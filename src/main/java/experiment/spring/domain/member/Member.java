package experiment.spring.domain.member;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Member {
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private Role role;
}
