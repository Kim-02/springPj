package com.dasolsystem.core.jwt;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.Enum.JwtCode;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.dto.ResponsesignInJwtDto;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.post.Dto.ResponseJson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_AUTHORIZATION_HEADER = "rAuthorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String USER_NAME = "User-Name";

    private final JwtBuilder jwtBuilder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        String refreshToken = request.getHeader(REFRESH_AUTHORIZATION_HEADER);
        String username = request.getHeader(USER_NAME);
        String DBrefreshToken;
        String accessTokenGoodHeader;
        String refreshTokenGoodHeader;
        try {
            log.info(" ■ JWT filter : accessToken " + accessToken);
            log.info(" ■ JWT filter : accessToken " + accessToken.startsWith(BEARER_PREFIX));
        } catch (Exception e) {
        }
        try {
            log.info(" ■ JWT filter : refreshToken " + refreshToken);
            log.info(" ■ JWT filter : refreshToken " + refreshToken.startsWith(BEARER_PREFIX));
        } catch (Exception e) {
        }
        try {
            log.info(" ■ JWT filter : username " + username);
        } catch (Exception e) {
        }

        try {
            //만약 토큰이 있다면
            if ((StringUtils.hasText(accessToken) && accessToken.startsWith(BEARER_PREFIX)) && (username != null) && (StringUtils.hasText(refreshToken) && refreshToken.startsWith(BEARER_PREFIX))) {
                log.info(" ■ JWT filter : refreshToken, accessToken 전송");
                //실제 토큰만 추출
                accessTokenGoodHeader = accessToken.substring(BEARER_PREFIX.length());
                refreshTokenGoodHeader = refreshToken.substring(BEARER_PREFIX.length());
                log.info(" ■ JWT filter : accessTokenHeader - " + accessTokenGoodHeader);
                log.info(" ■ JWT filter : refreshTokenHeader - " + refreshTokenGoodHeader);
                //유요성 검증
                JwtCode accessTokenStatus = jwtBuilder.validateToken(accessTokenGoodHeader);
                JwtCode refreshTokenStatus = jwtBuilder.validateToken(refreshTokenGoodHeader);
                log.info(" ■ JWT filter : accessTokenStatus - " + accessTokenStatus);
                log.info(" ■ JWT filter : refreshTokenStatus - " + refreshTokenStatus);
                //잘못된 토큰
                if (refreshTokenStatus.equals(JwtCode.WRONG) || accessTokenStatus.equals(JwtCode.WRONG))
                    throw new AuthFailException(ApiState.ERROR_602, "Wrong Token");
                //refresh 토큰이 만료됨
                if (refreshTokenStatus.equals(JwtCode.EXPIRE))
                    throw new AuthFailException(ApiState.ERROR_602, "refresh token Expired");

                //refresh는 괜찮고 access토큰이 만료됨.
                if (refreshTokenStatus.equals(JwtCode.OK) && accessTokenStatus.equals(JwtCode.EXPIRE)) {
                    ResponsesignInJwtDto responsesignInJwtDto = jwtBuilder.getRefreshTokenByName(username);
                    DBrefreshToken = responsesignInJwtDto.getRtoken();
                    log.info(DBrefreshToken);
                    log.info(" ■ JWT filter : refreshTokenEquals - " + DBrefreshToken.equals(refreshTokenGoodHeader));
                    //DB에 있는 리프레시 토큰과 동일하면 새로운 AccessToken 발급
                    if (DBrefreshToken.equals(refreshTokenGoodHeader)) {
                        String newAccessToken = jwtBuilder.generateAccessToken(username);
                        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken);
                        log.info("■ 새로운 Access Token 발급");
                    } else {
                        throw new AuthFailException(ApiState.ERROR_602, "refresh token is not equals");
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
