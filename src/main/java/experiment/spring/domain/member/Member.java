package experiment.spring.domain.member;

import lombok.Data;

@Data
public class Member {
    private Long id;
    private String loginId;
    private String password;
    private String name;
}
