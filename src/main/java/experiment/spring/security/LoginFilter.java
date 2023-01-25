package experiment.spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import experiment.spring.domain.member.dto.LoginDto;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("인증 메서드가 지원되지 않습니다." + request.getMethod());
        }

        LoginDto loginDto;
        try {
            loginDto = new ObjectMapper().readValue(request.getInputStream(), LoginDto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("로그인을 실패했습니다.");
        }

        String loginId = loginDto.getLoginId();
        String password = loginDto.getPassword();

        if (loginId == null) {
            loginId = "";
        }

        if (password == null) {
            password = "";
        }

        loginId = loginId.trim();

        log.debug("loginId={}, password={}", loginId, password);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            loginId, password);

        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
