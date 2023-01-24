package experiment.spring.controller;

import experiment.spring.domain.member.Member;
import experiment.spring.domain.member.Role;
import experiment.spring.repository.MemberRepository;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberRepository memberRepository;

    @PostConstruct
    private void init() {
        Member member = Member.builder()
            .loginId("test")
            .password("1234")
            .name("test member")
            .role(Role.USER)
            .build();
        memberRepository.save(member);
    }

    @PostMapping("/register")
    public Object register(@RequestBody Member member) {
        Member registerMember = memberRepository.save(member);
        log.info("registerMember={}", registerMember);
        return registerMember;
    }

    @GetMapping
    public Object getMember(Authentication authentication) {
        log.info("detail = {}", authentication);
        return authentication.getPrincipal();
    }
}
