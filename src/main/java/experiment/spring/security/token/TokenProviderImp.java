package experiment.spring.security.token;

import experiment.spring.config.security.AuthProperties;
import experiment.spring.domain.member.Member;
import experiment.spring.domain.token.RefreshToken;
import experiment.spring.repository.MemberRepository;
import experiment.spring.repository.RefreshTokenRepository;
import experiment.spring.security.MemberDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import java.util.UUID;
import javax.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProviderImp implements TokenProvider {

    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;

    private final AuthProperties authProperties;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String generateRefreshToken(Authentication authentication) {
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        String loginId = memberDetails.getUsername();
        RefreshToken refreshToken = new RefreshToken(loginId, UUID.randomUUID().toString());
        refreshTokenRepository.save(refreshToken);
        return Jwts.builder()
            .setSubject(refreshToken.getLoginId())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + (SECOND * 60L)))
            .signWith(SignatureAlgorithm.HS256,
                authProperties.getAuth().getTokenSecret())
            .compact();
    }

    @Override
    public String generateAccessToken(String refreshToken) {
        String loginId = Jwts.parser()
            .setSigningKey(authProperties.getAuth().getTokenSecret())
            .parseClaimsJws(refreshToken)
            .getBody().getSubject();

        String validToken = refreshTokenRepository.findById(loginId)
            .orElseThrow(() -> new JwtException("refresh token does not exist"))
            .getToken();

//        if (!refreshToken.equals(validToken)) {
//            throw new IllegalArgumentException("Invalid token");
//        }


        return Jwts.builder()
            .setSubject(loginId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + (SECOND * 20L * 2)))
            .signWith(SignatureAlgorithm.HS256,
                authProperties.getAuth().getTokenSecret())
            .compact();
    }

    @Override
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

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return false;
    }

    public String getAttribute(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(authProperties.getAuth().getTokenSecret())
            .parseClaimsJws(token)
            .getBody();
        log.info("claims = {}", claims);
        return claims.getSubject();
    }
}
