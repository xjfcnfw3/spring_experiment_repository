package experiment.spring.security;

import experiment.spring.domain.member.Role;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    long id() default 1L;
    String loginId() default "user";
    String password() default "1234";
    String name() default "tester";
    Role role() default Role.USER;
}
