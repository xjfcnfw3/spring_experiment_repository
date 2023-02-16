package experiment.spring.controller;

import experiment.spring.domain.member.dto.LoginResponse;
import experiment.spring.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class AuthController {

    private final TokenProvider tokenProvider;

    @PostMapping("/refresh")
    public LoginResponse refresh(Authentication authentication) {
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        String accessToken = tokenProvider.generateAccessToken(refreshToken);
        return new LoginResponse(accessToken, refreshToken);
    }
}
