package experiment.spring.controller;

import experiment.spring.domain.member.dto.LoginDto;
import experiment.spring.domain.member.dto.LoginResponse;
import experiment.spring.domain.member.Member;
import experiment.spring.security.TokenProvider;
import experiment.spring.service.LoginService;
import javax.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    private final LoginService loginService;
    private final TokenProvider tokenProvider;

    public LoginController(LoginService loginService, TokenProvider tokenProvider) {
        this.loginService = loginService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginDto loginDto) throws AuthException {
        try {
            return getTokens(loginDto);
        } catch (AuthException ex) {
            log.error("invalid login Member");
            throw ex;
        }
    }

    private LoginResponse getTokens(LoginDto loginDto) throws AuthException {
        Member member = loginService.login(loginDto.getLoginId(), loginDto.getPassword());
        String refreshToken = tokenProvider.generateRefreshToken(member.getLoginId());
        String accessToken = tokenProvider.createToken(refreshToken, member);
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
