package experiment.spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import experiment.spring.config.security.AuthProperties;
import experiment.spring.domain.member.dto.LoginResponse;
import experiment.spring.security.token.TokenProvider;
import java.io.IOException;
import javax.security.auth.message.AuthException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final AuthProperties authProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        String accessToken = tokenProvider.generateAccessToken(refreshToken);
        Cookie cookie = getRefreshTokenCookie(refreshToken);
        showLog(cookie, accessToken);
        insertToken(response, cookie, accessToken);
    }

    private void showLog(Cookie cookie, String accessToken) {
        log.info("cookie={}, accessToken={}", cookie, accessToken);
    }

    private void insertToken(HttpServletResponse response, Cookie cookie, String accessToken) {
        response.setHeader(authProperties.getAuth().getAccessHeader(), accessToken);
        response.addCookie(cookie);
    }

    private Cookie getRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh", refreshToken);
        cookie.setMaxAge(432000);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/token/refresh");
        return cookie;
    }
}
