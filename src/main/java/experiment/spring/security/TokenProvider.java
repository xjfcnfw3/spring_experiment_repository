package experiment.spring.security;

import experiment.spring.config.security.AuthProperties;
import experiment.spring.domain.member.Member;
import experiment.spring.domain.token.RefreshToken;
import experiment.spring.repository.MemberRepository;
import experiment.spring.repository.RefreshTokenRepository;
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
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;

    private final AuthProperties authProperties;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public String generateRefreshToken(String loginId) throws AuthException {
        Member member = memberRepository.findByLoginId(loginId)
            .orElseThrow(AuthException::new);
        RefreshToken refreshToken = new RefreshToken(member.getId(), UUID.randomUUID().toString());
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public String createToken(String refreshToken, Member member) {
        String validToken = refreshTokenRepository.findById(member.getId().toString())
            .orElseThrow(() -> new JwtException("refresh token does not exist"))
            .getToken();

        if (!refreshToken.equals(validToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        return Jwts.builder()
            .setSubject(member.getLoginId())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + (SECOND * 20L * 2)))
            .signWith(SignatureAlgorithm.HS256,
                authProperties.getAuth().getTokenSecret())
            .compact();
    }

    public boolean validateToken(String accessToken) {
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

    public String getAttribute(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(authProperties.getAuth().getTokenSecret())
            .parseClaimsJws(token)
            .getBody();
        log.info("claims = {}", claims);
        return claims.getSubject();
    }
}
