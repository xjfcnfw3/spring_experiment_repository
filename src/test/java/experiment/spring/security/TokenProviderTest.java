package experiment.spring.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import experiment.spring.config.security.AuthProperties;
import experiment.spring.config.security.AuthProperties.Auth;
import experiment.spring.domain.member.Member;
import experiment.spring.domain.member.Role;
import experiment.spring.domain.token.RefreshToken;
import experiment.spring.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private AuthProperties authProperties;

    private TokenProvider tokenProvider;
    private String refreshToken;
    private String accessToken;
    private MemberDetails memberDetails;
    private Auth auth;

    @BeforeEach
    void init() {
        tokenProvider = new TokenProvider(authProperties, refreshTokenRepository);
        refreshToken = UUID.randomUUID().toString();

        Member member = Member.builder()
            .id(1L)
            .loginId("test")
            .password("1234")
            .role(Role.USER)
            .build();
        memberDetails = new MemberDetails(member);

        auth = new Auth();
        String secret = UUID.randomUUID().toString();
        auth.setTokenSecret(secret);

        accessToken = Jwts.builder()
            .setSubject("test")
            .setExpiration(new Date(System.currentTimeMillis() + 1000L))
            .signWith(SignatureAlgorithm.HS512, auth.getTokenSecret())
            .compact();
    }

    @Test
    void generateRefreshToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails,
            memberDetails.getPassword(), memberDetails.getAuthorities());
        String generateRefreshToken = tokenProvider.generateRefreshToken(authentication);
        assertThat(generateRefreshToken).isNotNull();
    }

    @Test
    void generateAccessToken() {
        when(refreshTokenRepository.findById(any(String.class)))
            .thenReturn(Optional.of(new RefreshToken(refreshToken, "test")));
        when(authProperties.getAuth()).thenReturn(auth);
        String accessToken = tokenProvider.generateAccessToken(refreshToken);
        assertThat(accessToken).isNotNull();
        System.out.println("accessToken = " + accessToken);
    }

    @Test
    void failReIssue() {
        when(refreshTokenRepository.findById(any(String.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tokenProvider.generateAccessToken(refreshToken))
            .isInstanceOf(JwtException.class);
    }

    @Test
    void validateAccessToken() {
        String token = Jwts.builder()
            .setSubject("test")
            .setExpiration(new Date(System.currentTimeMillis() + 1000L))
            .signWith(SignatureAlgorithm.HS512, auth.getTokenSecret())
            .compact();
        when(authProperties.getAuth()).thenReturn(auth);
        boolean result = tokenProvider.validateAccessToken(token);
        assertThat(result).isTrue();
    }

    @Test
    void expiredToken() throws InterruptedException {
        when(authProperties.getAuth()).thenReturn(auth);
        Thread.sleep(2000);
        boolean result = tokenProvider.validateAccessToken(accessToken);
        assertThat(result).isFalse();
    }

    @Test
    void signatureError() {
        Auth otherAuth = new Auth();
        otherAuth.setTokenSecret("not Key");
        when(authProperties.getAuth()).thenReturn(otherAuth);
        boolean result = tokenProvider.validateAccessToken(accessToken);
        assertThat(result).isFalse();
    }

    @Test
    void inputNotToken() {
        when(authProperties.getAuth()).thenReturn(auth);
        boolean result = tokenProvider.validateAccessToken("token");
        assertThat(result).isFalse();
    }

    @Test
    void inputBlank() {
        when(authProperties.getAuth()).thenReturn(auth);
        boolean result = tokenProvider.validateAccessToken("");
        assertThat(result).isFalse();
    }

    @Test
    void validateRefreshToken() {
        when(refreshTokenRepository.findById(any(String.class)))
            .thenReturn(Optional.of(new RefreshToken(refreshToken, "test")));
        boolean result = tokenProvider.validateRefreshToken(refreshToken);
        assertThat(result).isTrue();
    }

    @Test
    void getAttribute() {
        when(authProperties.getAuth()).thenReturn(auth);
        String attribute = (String) tokenProvider.getAttribute(accessToken);
        assertThat(attribute).isEqualTo("test");
    }
}
