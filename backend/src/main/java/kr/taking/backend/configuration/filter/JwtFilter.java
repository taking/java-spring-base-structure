package kr.taking.backend.configuration.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.taking.backend.error.exception.CustomException;
import kr.taking.backend.service.TokenService;
import kr.taking.backend.util.Security.AccessToken;
import kr.taking.backend.error.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        AccessToken token = tokenService.resolveJwtToken(request);

        try {

            if (checkAccessToken(token)) {
                Authentication authentication = tokenService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.error("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());
                log.error("getAuthorities : {}", authentication.getAuthorities());
            }
            filterChain.doFilter(request, response);

        } catch (AuthenticationException e) {
            log.error("Cannot set user authentication: ", e);
            SecurityContextHolder.clearContext();
            throw new CustomException(ErrorCode.INVALID_USERNAME);
        }

    }

    private boolean checkAccessToken(AccessToken accessToken) {
        if (accessToken == null) return false;
        return tokenService.validateToken(accessToken);
    }
}