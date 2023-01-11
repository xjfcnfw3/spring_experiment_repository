package experiment.spring.repository;

import experiment.spring.domain.member.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member addMember(Member member);
    Member findById(Long memberId);
    Optional<Member> findByLoginId(String loginId);
    List<Member> findAll();
}
