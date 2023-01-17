package experiment.spring.domain.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Member {
    private Long id;
    private String loginId;
    private String password;
    private String name;
}
