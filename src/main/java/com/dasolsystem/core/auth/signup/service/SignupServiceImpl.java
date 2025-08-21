package com.dasolsystem.core.auth.signup.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.config.excption.MailFailException;
import com.dasolsystem.core.auth.repository.RoleRepository;
import com.dasolsystem.core.auth.signup.dto.RequestSignupDto;
import com.dasolsystem.core.auth.signup.dto.ResponseSavedNameDto;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.RedisJwtId;
import com.dasolsystem.core.entity.RoleCode;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.redis.reopsitory.RedisJwtRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;
    private final SecureRandom random = new  SecureRandom();
    private final RedisJwtRepository redisJwtRepository;


    //email
    private final JavaMailSender mailSender = new JavaMailSenderImpl();

    @Value("${mail.from}")
    private String from;

    @Description("회원 가입")
    @Transactional
    public ResponseSavedNameDto signup(RequestSignupDto request) {
        if(userRepository.existsBystudentId(request.getStudentId()))
            throw new AuthFailException(ApiState.ERROR_700,"이미 가입되었습니다. 관리자에게 문의하세요.");
        if(!verifyEmail(request.getEmail(),request.getVerificationCode())){
            throw new AuthFailException(ApiState.ERROR_700,"이메일 인증이 완료되지 않았습니다.");
        }
        RoleCode default_role = roleRepository.findById(100L);
        Member user = Member.builder()
                .studentId(request.getStudentId())
                .enterYear(request.getStudentId().substring(0,4))
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .name(request.getName())
                .role(default_role)
                .personalInfoYn(request.getPersonalInfoYn())
                .emailCheckYn(true)
                .build();

        Member savedUsers = userRepository.save(user);
        return ResponseSavedNameDto.builder()
                .userName(savedUsers.getName())
                .message("Signup Success")
                .build();
    }

    //이메일 인증용 코드를 발급
    @Transactional
    public void emailVerificationCode(String email) {
        String key = "verify:email:" + email;
        String code = String.format("%08d",random.nextInt(100_000_000));
        RedisJwtId redisJwtId = RedisJwtId.builder()
                .id(Long.valueOf(UUID.randomUUID().toString()))
                .jti(key)
                .jwtToken(code)
                .ttl(120L)
                .build();
        redisJwtRepository.save(redisJwtId);
        emailSender(email,code,Duration.ofMinutes(2));
    }

    private void emailSender(String to, String code, Duration ttl){
        String subject = "이메일 인증 코드";
        String html = """
            <html><body>
            <h2>이메일 인증</h2>
            <p>인증코드: <b>%s</b></p>
            <p>유효시간: %d분</p>
            </body></html>
            """.formatted(code, ttl.toMinutes());
        sendHtml(to, subject, html);
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new MailFailException(ApiState.ERROR_900,"메일 전송 실패"+e.getMessage());
        }
    }

    private Boolean verifyEmail(String email,String inputCode){
        String key = "verify:email:" + email;
        RedisJwtId e = redisJwtRepository.findByJti(key).orElseThrow(
                () -> new DBFaillException(ApiState.ERROR_500,"코드 만료 또는 없음")
        );
        if(!MessageDigest.isEqual(e.getJwtToken().getBytes(StandardCharsets.UTF_8),
                inputCode.getBytes(StandardCharsets.UTF_8))){
            throw new AuthFailException(ApiState.ERROR_700,"코드 불일치");
        }

        redisJwtRepository.deleteById(e.getId());
        return true;
    }
}
