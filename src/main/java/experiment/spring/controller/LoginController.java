package experiment.spring.controller;

import experiment.spring.domain.member.LoginDto;
import experiment.spring.domain.member.Member;
import experiment.spring.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/login")
    public Object login(@RequestBody LoginDto loginDto) {
        Member loginMember = loginService.login(loginDto.getLoginId(), loginDto.getPassword());
        log.info("loginMember={}", loginMember);
        return loginMember;
    }
}
