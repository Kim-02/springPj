package com.dasolsystem.core.jwt.filter;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.vo.WhiteListVO;
import com.dasolsystem.core.enums.JwtCode;
import com.dasolsystem.core.auth.userdetail.service.CustomUserDetailsService;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.filter.dto.JwtTokenStrogeDto;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public static final String USER_NAME = "User-Name";

    private final JwtBuilder jwtBuilder;

    private final CustomUserDetailsService userDetailsService;

    //인증이 없이 들어가야하는 URI
    private static final List<String> WHITE_LIST = Arrays.asList(
            new WhiteListVO().getWhiteList()
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        log.info("✅ doFilterInternal");
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);
        String refreshTokenId = request.getHeader(REFRESH_AUTHORIZATION_HEADER);
        String username = request.getHeader(USER_NAME);
//        log.info("✅ accessToken "+accessToken);
//        log.info("✅ refreshTokenId "+refreshTokenId);
//        log.info("✅ username "+username);
        String accessTokenGoodHeader;
        String refreshToken;
        String requestURI = request.getRequestURI();
//        log.info("✅ requestURI "+ requestURI);
        if (WHITE_LIST.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
//            log.info(" ■ JWT filter : accessToken " + accessToken);
            log.info(" ■ JWT filter : accessToken " + accessToken.startsWith(BEARER_PREFIX));
        } catch (Exception e) {
        }
        try {
//            log.info(" ■ JWT filter : refreshToken " + refreshTokenId);
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
                JwtTokenStrogeDto jwtTokenStrogeDto = JwtTokenStrogeDto.builder()
                        .accessToken(accessTokenGoodHeader)
                        .refreshTokenId(refreshToken)
                        .build();
//                log.info(" ■ JWT filter : accessTokenHeader - " + accessTokenGoodHeader);
//                log.info(" ■ JWT filter : refreshTokenHeader - " + refreshToken);

                //유효성 검증
                JwtCode accessTokenStatus = jwtBuilder.validateToken(accessTokenGoodHeader);
                log.info(" ■ JWT filter : accessTokenStatus - " + accessTokenStatus);

                //잘못된 토큰
                switch (accessTokenStatus) {
                    case WRONG:
                        throw new AuthFailException(ApiState.ERROR_602, "Wrong Token");
                    case EXPIRE:
                        TokenIdAccesserDto accesserDto = TokenIdAccesserDto.builder()
                                .tokenId(jwtTokenStrogeDto.getRefreshTokenId())
                                .emailId(jwtBuilder.getRefreshTokenEmailId(jwtTokenStrogeDto.getRefreshTokenId()))
                                .build();
                        String newAccessToken = jwtBuilder.getNewAccessToken(accesserDto);
                        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken);
                        jwtTokenStrogeDto.setAccessToken(newAccessToken);
                        log.info("■ 새로운 Access Token 발급");
                        break;
                    case NOT_SUPPORT:
                        throw new AuthFailException(ApiState.ERROR_602, "Not Support Token");
                    case NOT_EXIST_CLAIMS:
                        throw new AuthFailException(ApiState.ERROR_602, "Not Exist Claims");
                    case OK:
                        log.info("■ JWT AccessToken success");
                        break;
                    default:
                        throw new AuthFailException(ApiState.ERROR_602, "Unknown Token");
                }
                log.info("■ accessToken User : "+ jwtBuilder.getAccessTokenPayload(jwtTokenStrogeDto.getAccessToken())
                        .get("EmailId").toString());
                UserDetails userDetails = userDetailsService.loadUserByUsername(
                        jwtBuilder.getAccessTokenPayload(jwtTokenStrogeDto.getAccessToken())
                        .get("EmailId").toString());
                log.info(userDetails.getUsername());
                log.info(userDetails.getAuthorities().toString());
                if(userDetails!=null) {

                    //security 접근 토큰 생성
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    log.info("■ 접근 토큰 생성 완료");
                    //접근 토큰 활성화
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    log.info("■ 접근 토큰 활성화: "+usernamePasswordAuthenticationToken.getAuthorities());
                }

            }
            else {
                throw new AuthFailException(ApiState.ERROR_602, "None AccessToken Please Login");
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
