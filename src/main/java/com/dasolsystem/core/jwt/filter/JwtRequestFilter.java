package com.dasolsystem.core.jwt.filter;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.vo.WhiteListVO;
import com.dasolsystem.core.enums.JwtCode;
import com.dasolsystem.core.auth.userdetail.service.CustomUserDetailsService;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.guardian.TokenResponseDto;
import com.dasolsystem.core.jwt.filter.dto.JwtTokenStrogeDto;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_AUTHORIZATION_HEADER = "rAuthorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String USER_NAME = "userName";
    private final RedisTemplate<Object, Object> redisTemplate;

    private final CustomUserDetailsService userDetailsService;

    private final SecurityGuardian securityGuardian;
    //인증이 없이 들어가야하는 URI
    private static final List<String> WHITE_LIST = Arrays.asList(
            new WhiteListVO().getWhiteList()
    );

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return WHITE_LIST.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("인증 로직에 진입");
            String accessToken = request.getHeader(AUTHORIZATION_HEADER);
            if (accessToken == null || !accessToken.startsWith(BEARER_PREFIX)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("로그인 후에 이용할 수 있는 서비스입니다.");
                return;
            }
            accessToken = accessToken.substring(BEARER_PREFIX.length());

            String refreshTokenId = request.getHeader(REFRESH_AUTHORIZATION_HEADER);
            if (refreshTokenId == null || !refreshTokenId.startsWith(BEARER_PREFIX)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("로그인 후에 이용할 수 있는 서비스입니다.");
                return;
            }
            refreshTokenId = refreshTokenId.substring(BEARER_PREFIX.length());


            try {
                //만약 토큰이 있다면
                if (StringUtils.hasText(accessToken) && StringUtils.hasText(refreshTokenId)) {
                    Boolean isBlacklisted = redisTemplate.hasKey("blacklist:access:" + accessToken);
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        SecurityContextHolder.clearContext();
                        throw new AuthFailException(ApiState.ERROR_700, "logout token, please login first");
                    }
                    //유효성 검증
                    TokenResponseDto responseToken = securityGuardian.tokenValidator(accessToken, refreshTokenId);
                    if(responseToken.getJwtCode() != JwtCode.OK){
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("토큰이 유효하지 않습니다.");
                        return;
                    }
                    String finalAccessToken = responseToken.getAccessToken();
                    UserDetails userDetails = userDetailsService.loadUserByUsername(securityGuardian.getStudentId(finalAccessToken));
                    if (userDetails != null) {
                        log.info("유효성 검증 진입");
                        //security 접근 토큰 생성
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        //접근 토큰 활성화
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        if (!finalAccessToken.equals(accessToken)) {
                            response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + finalAccessToken);
                            response.setHeader(REFRESH_AUTHORIZATION_HEADER, BEARER_PREFIX + responseToken.getRefreshToken());
                        }
                    }

                } else {
                    throw new AuthFailException(ApiState.ERROR_700, "None AccessToken Please Login");
                }
                filterChain.doFilter(request, response);
            } catch (AuthFailException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(e.getMessage());
                return;
            }


        }catch (Exception e) {
            throw new ServletException(e.getMessage(), e.getCause());
        }
    }

}
