package experiment.spring.controller;

import experiment.spring.domain.member.Member;
import experiment.spring.domain.member.Role;
import experiment.spring.repository.MemberRepository;
import experiment.spring.security.LoginMember;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        Member member = Member.builder()
            .loginId("init")
            .password(passwordEncoder.encode("1234"))
            .name("init member")
            .role(Role.USER)
            .build();
        memberRepository.save(member);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public Object register(@RequestBody Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    @GetMapping
    public Object getMember(@LoginMember Member member) {
        return member.getName();
    }

    @GetMapping("test")
    public String test(@LoginMember Member member) {
        return "ok";
    }
}
