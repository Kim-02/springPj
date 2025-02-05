package com.dasolsystem.core.jwt.filter;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.Enum.JwtCode;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.dto.TokenIdAccesserDto;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_AUTHORIZATION_HEADER = "rAuthorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String USER_NAME = "User-Name";

    private final JwtBuilder jwtBuilder;

    private static final List<String> WHITE_LIST = Arrays.asList(
            "/",
            "/test/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        String refreshTokenId = request.getHeader(REFRESH_AUTHORIZATION_HEADER);
        String username = request.getHeader(USER_NAME);
//        String DBrefreshToken;
        String accessTokenGoodHeader;
        String refreshToken;
        String requestURI = request.getRequestURI();

        if(WHITE_LIST.stream().anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            log.info(" ■ JWT filter : accessToken " + accessToken);
            log.info(" ■ JWT filter : accessToken " + accessToken.startsWith(BEARER_PREFIX));
        } catch (Exception e) {
        }
        try {
            log.info(" ■ JWT filter : refreshToken " + refreshTokenId);
            log.info(" ■ JWT filter : refreshToken " + refreshTokenId.startsWith(BEARER_PREFIX));
        } catch (Exception e) {
        }
        try {
            log.info(" ■ JWT filter : username " + username);
        } catch (Exception e) {
        }

        try {
            //만약 토큰이 있다면
            if (StringUtils.hasText(accessToken) && accessToken.startsWith(BEARER_PREFIX)) {
                log.info(" ■ JWT filter : refreshToken, accessToken 전송");
                //실제 토큰만 추출
                accessTokenGoodHeader = accessToken.substring(BEARER_PREFIX.length());
                refreshToken = refreshTokenId.substring(BEARER_PREFIX.length());
                log.info(" ■ JWT filter : accessTokenHeader - " + accessTokenGoodHeader);
                log.info(" ■ JWT filter : refreshTokenHeader - " + refreshToken);
                //유요성 검증
                JwtCode accessTokenStatus = jwtBuilder.validateToken(accessTokenGoodHeader);
                log.info(" ■ JWT filter : accessTokenStatus - " + accessTokenStatus);
                //잘못된 토큰
                if (accessTokenStatus.equals(JwtCode.WRONG))
                    throw new AuthFailException(ApiState.ERROR_602, "Wrong Token");

                //access토큰이 만료됨.
                if (accessTokenStatus.equals(JwtCode.EXPIRE)) {
                    TokenIdAccesserDto accesserDto = TokenIdAccesserDto.builder()
                            .tokenId(refreshToken)
                            .name(username)
                            .build();
                    String newAccessToken = jwtBuilder.getNewAccessToken(accesserDto);
                    response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken);
                    accessToken = newAccessToken;
                    log.info("■ 새로운 Access Token 발급");
                }
                if(SecurityContextHolder.getContext().getAuthentication() == null){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, accessToken,
                                    Collections.singletonList(new SimpleGrantedAuthority(jwtBuilder.getAccessTokenPayload(accessToken))));
                    try {
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } catch (Exception e) {
                        log.error("❗ JWT 인증 실패: {}", e.getMessage());
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("❌ JWT 인증 실패: " + e.getMessage());
                        return;
                    }
                }


            }
            else {
                throw new AuthFailException(ApiState.ERROR_602, "null token or username");
            }
        } catch (AuthFailException e) {
            log.error("인증실패");
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
