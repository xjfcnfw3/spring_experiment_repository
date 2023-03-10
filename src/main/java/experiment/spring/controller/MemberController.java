package experiment.spring.controller;

import experiment.spring.domain.member.Member;
import experiment.spring.domain.member.Role;
import experiment.spring.domain.member.dto.LoginResponse;
import experiment.spring.repository.MemberRepository;
import experiment.spring.security.LoginMember;
import experiment.spring.security.MemberDetails;
import experiment.spring.security.token.TokenProvider;
import javax.annotation.PostConstruct;
import javax.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
//@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        Member member = Member.builder()
            .loginId("test")
            .password(passwordEncoder.encode("1234"))
            .name("test member")
            .role(Role.USER)
            .build();
        memberRepository.save(member);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public Object register(@RequestBody Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        Member registerMember = memberRepository.save(member);

        log.info("registerMember={}", registerMember);
        return registerMember;
    }

    @GetMapping
    public Object getMember(@LoginMember Member member) {
        log.info("detail = {}", member);
        return member;
    }

    @GetMapping("test")
    public String test(@LoginMember Member member) {
        return "ok";
    }
}
