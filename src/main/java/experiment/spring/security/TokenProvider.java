package experiment.spring.security;

import experiment.spring.config.security.AuthProperties;
import experiment.spring.domain.member.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    private final AuthProperties authProperties;

    public String createToken(Member member) {
        log.info("secret={}", authProperties.getAuth().getTokenSecret());
        return Jwts.builder()
            .claim("member", member)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365)))
            .signWith(SignatureAlgorithm.HS256,
                authProperties.getAuth().getTokenSecret())
            .compact();
    }

    public Object getMember(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(authProperties.getAuth().getTokenSecret())
            .parseClaimsJws(token)
            .getBody();
        Object member = claims.get("member");
        log.info("member={}", member);
        return member;
    }
}
