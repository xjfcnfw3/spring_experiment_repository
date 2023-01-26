package experiment.spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import experiment.spring.domain.member.dto.LoginResponse;
import java.io.IOException;
import javax.security.auth.message.AuthException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {
            LoginResponse loginResponse = makeTokens(extractMemberLoginId(authentication));
            new ObjectMapper().writeValue(response.getWriter(), loginResponse);
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
    }

    private LoginResponse makeTokens(String loginId) throws AuthException {
        String refreshToken = tokenProvider.generateRefreshToken(loginId);
        String accessToken = tokenProvider.createToken(refreshToken, loginId);
        return new LoginResponse(accessToken, refreshToken);
    }

    private String extractMemberLoginId(Authentication authentication) {
        MemberDetails details = (MemberDetails) authentication.getPrincipal();
        return details.getUsername();
    }
}
