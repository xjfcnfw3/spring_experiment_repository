package experiment.spring.security;

import experiment.spring.config.security.AuthProperties;
import experiment.spring.domain.member.Member;
import experiment.spring.domain.token.RefreshToken;
import experiment.spring.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;

    private final AuthProperties authProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public String generateRefreshToken(Authentication authentication) {
        String loginId = null;
        if (authentication.getPrincipal() instanceof  MemberDetails) {
            MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
            loginId = memberDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof Member) {
            Member member = (Member) authentication.getPrincipal();
            loginId = member.getLoginId();
        }
        String uuid = UUID.randomUUID().toString();
        refreshTokenRepository.save(new RefreshToken(uuid, loginId));
        return uuid;
    }

    public String generateAccessToken(String refreshToken) {
        String loginId = refreshTokenRepository.findById(refreshToken)
            .orElseThrow(() -> new JwtException("refresh token does not exist"))
            .getLoginId();

        return Jwts.builder()
            .setSubject(loginId)
            .setExpiration(new Date(System.currentTimeMillis() + (MINUTE * 60L)))
            .signWith(SignatureAlgorithm.HS512, authProperties.getAuth().getTokenSecret())
            .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parser().setSigningKey(authProperties.getAuth().getTokenSecret())
                .parseClaimsJws(accessToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public boolean validateRefreshToken(String refreshToken) {
        return refreshTokenRepository.findById(refreshToken).isPresent();
    }

    public Object getAttribute(String accessToken) {
        return Jwts.parser().setSigningKey(authProperties.getAuth().getTokenSecret())
            .parseClaimsJws(accessToken)
            .getBody()
            .getSubject();
    }
}
