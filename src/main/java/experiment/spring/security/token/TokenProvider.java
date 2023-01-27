package experiment.spring.security.token;

import org.springframework.security.core.Authentication;

public interface TokenProvider {
    String generateRefreshToken(Authentication  authentication);
    String generateAccessToken(String refreshToken);
    boolean validateAccessToken(String accessToken);
    boolean validateRefreshToken(String refreshToken);
    Object getAttribute(String accessToken);
}
