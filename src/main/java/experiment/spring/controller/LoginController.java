package experiment.spring.controller;

import experiment.spring.domain.member.LoginDto;
import experiment.spring.domain.member.LoginResponse;
import experiment.spring.domain.member.Member;
import experiment.spring.security.TokenProvider;
import experiment.spring.service.LoginService;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController {
    private final LoginService loginService;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public Object login(@RequestBody LoginDto loginDto) {
        Member loginMember = loginService.login(loginDto.getLoginId(), loginDto.getPassword());
        log.info("loginMember={}", loginMember);
        String token = tokenProvider.createToken(loginMember);
        return LoginResponse.builder().token(token).build();
    }

    @GetMapping("/jwt-member")
    public Object getSecretMember(@RequestBody MemberToken memberToken) {
        String token = memberToken.getToken();
        log.info("token={}", token);
        return tokenProvider.getMember(token);
    }

    @Data
    static class MemberToken {
        private String token;
    }
}
