package kr.taking.backend.service;

import kr.taking.backend.util.Security.AccessToken;
import kr.taking.backend.model.RoleEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface TokenService {
    AccessToken generateJwtToken(String userid, RoleEntity roles);
    boolean validateToken(AccessToken token);
    AccessToken resolveJwtToken(HttpServletRequest request);
    Authentication getAuthentication(AccessToken token);
}
