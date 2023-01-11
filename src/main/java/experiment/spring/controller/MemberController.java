package experiment.spring.controller;

import experiment.spring.domain.member.Member;
import experiment.spring.repository.MemberRepository;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        Member member = new Member();
        member.setLoginId("test");
        member.setPassword("1234");
        member.setName("test member");
        memberRepository.addMember(member);
    }

    @PostMapping("/register")
    public Object register(@RequestBody Member member) {
        Member registerMember = memberRepository.addMember(member);
        log.info("registerMember={}", registerMember);
        return registerMember;
    }
}
