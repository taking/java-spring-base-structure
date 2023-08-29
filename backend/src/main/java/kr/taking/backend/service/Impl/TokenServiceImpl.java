package kr.taking.backend.service.Impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import kr.taking.backend.service.security.CustomUserDetailsService;
import kr.taking.backend.util.Security.AccessToken;
import kr.taking.backend.util.Security.SecretKey;
import kr.taking.backend.model.RoleEntity;
import kr.taking.backend.model.UserEntity;
import kr.taking.backend.service.TokenService;
import kr.taking.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${lab.cherry.nw.jwtSecret}")
    private String jwtSecretKey;

    @Value("${lab.cherry.nw.jwtExpirationMs}")
    private int jwtExpirationMs;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    public AccessToken generateJwtToken(String userid, RoleEntity role) {
        
        SecretKey secretKey = new SecretKey(jwtSecretKey, jwtExpirationMs);
        UserEntity user = userService.findByUserId(userid);
        
        Claims claims = Jwts.claims().setSubject(userid);
        claims.put("id", user.getUserid());
        claims.put("name", user.getUsername());
        claims.put("roles",new SimpleGrantedAuthority(role.getName()));

        Instant issuedAt = Instant.now();
        Instant validUntil = issuedAt.plusMillis(secretKey.getExpirationInMiliseconds());
        
        String token = Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(validUntil))
                .signWith(getSignInKey(secretKey))
                .compact();
        
        return new AccessToken(token);
    }

    public boolean validateToken(AccessToken token) {
        SecretKey secretKey = new SecretKey(jwtSecretKey,jwtExpirationMs);
        
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(secretKey))
                .build()
                .parseClaimsJws(token.getAccessToken());
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
        
    }

    public AccessToken resolveJwtToken(HttpServletRequest request) {
        final String headerAuth = request.getHeader(AUTHORIZATION_HEADER);

        if (headerAuth == null) return null;
        if (!headerAuth.startsWith("Bearer ")) return null;

        return new AccessToken(headerAuth.substring(7));
    }

    public Authentication getAuthentication(AccessToken token) {
        SecretKey secretKey = new SecretKey(jwtSecretKey, jwtExpirationMs);
        String username = Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(secretKey))
                .build()
                .parseClaimsJws(token.getAccessToken())
                .getBody()
                .getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
    
    private Key getSignInKey(SecretKey secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
}
