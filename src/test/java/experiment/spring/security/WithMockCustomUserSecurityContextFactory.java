package experiment.spring.security;

import experiment.spring.domain.member.Member;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser member) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + member.role().name()));
        Member detailsMember = getMember(member);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            detailsMember, detailsMember.getPassword(), grantedAuthorities);
        context.setAuthentication(token);
        return context;
    }

    private Member getMember(WithMockCustomUser member) {
        return Member.builder().loginId(member.username())
            .password(member.password())
            .role(member.role())
            .build();
    }
}
