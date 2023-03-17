package experiment.spring.security;

import experiment.spring.domain.member.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser withMember) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member member = new Member();
        member.setLoginId(withMember.loginId());
        member.setPassword(withMember.password());
        member.setName(withMember.name());
        member.setRole(withMember.role());
        member.setId(withMember.id());
        MemberDetails memberDetails = new MemberDetails(member);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            memberDetails, memberDetails.getPassword(),
            memberDetails.getAuthorities());
        context.setAuthentication(token);
        return context;
    }
}
