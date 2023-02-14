package experiment.spring.controller;


import experiment.spring.domain.member.Member;
import experiment.spring.domain.member.dto.LoginResponse;
import experiment.spring.security.LoginMember;
import experiment.spring.security.MemberDetails;
import experiment.spring.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class AuthController {

    private final TokenProvider tokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(Authentication authentication) {
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        String accessToken = tokenProvider.generateAccessToken(refreshToken);
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }
}
