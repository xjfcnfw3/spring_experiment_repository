package experiment.spring.controller;

import experiment.spring.domain.member.dto.LoginDto;
import experiment.spring.domain.member.dto.LoginResponse;
import experiment.spring.domain.member.Member;
import experiment.spring.domain.member.dto.RefreshRequest;
import experiment.spring.domain.member.dto.RefreshResponse;
import experiment.spring.repository.MemberRepository;
import experiment.spring.security.TokenProvider;
import experiment.spring.service.LoginService;
import javax.security.auth.message.AuthException;
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
    private final MemberRepository memberRepository;

//    @PostMapping("/login")
    public Object login(@RequestBody LoginDto loginDto) throws AuthException {
        try {
            return getTokens(loginDto);
        } catch (AuthException ex) {
            log.error("invalid login Member");
            throw ex;
        }
    }


    @GetMapping("/refresh")
    public Object getAccessToken(@RequestBody RefreshRequest request) throws AuthException {
        String token = tokenProvider.createToken(request.getRefreshToken(), request.getLoginId());
        return new RefreshResponse(token);
    }

    private LoginResponse getTokens(LoginDto loginDto) throws AuthException {
        Member member = loginService.login(loginDto.getLoginId(), loginDto.getPassword());
        String refreshToken = tokenProvider.generateRefreshToken(member.getLoginId());
        String accessToken = tokenProvider.createToken(refreshToken, member.getLoginId());
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
