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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(AUTHORIZATION_HEADER).substring(BEARER_PREFIX.length());
        String refreshTokenId = request.getHeader(REFRESH_AUTHORIZATION_HEADER).substring(BEARER_PREFIX.length());
        String username = request.getHeader(USER_NAME);
        String requestURI = request.getRequestURI();
        if (WHITE_LIST.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            //만약 토큰이 있다면
            if (StringUtils.hasText(accessToken) && StringUtils.hasText(refreshTokenId)) {
                Boolean isBlacklisted = redisTemplate.hasKey("blacklist:access:" + accessToken);
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    SecurityContextHolder.clearContext();
                    throw new AuthFailException(ApiState.ERROR_606, "logout token, please login first");
                }
                //유효성 검증
                TokenResponseDto responseToken = securityGuardian.tokenValidator(accessToken, refreshTokenId);
                UserDetails userDetails = userDetailsService.loadUserByUsername(securityGuardian.getStudentId(responseToken.getAccessToken()));
                if(userDetails!=null) {

                    //security 접근 토큰 생성
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    //접근 토큰 활성화
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }

            }
            else {
                throw new AuthFailException(ApiState.ERROR_602, "None AccessToken Please Login");
            }
        } catch (AuthFailException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
