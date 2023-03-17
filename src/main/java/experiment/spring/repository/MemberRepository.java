package experiment.spring.repository;

import experiment.spring.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String username);

    @Query("select m from Member m join fetch m.boards")
    List<Member> findAllByBoards();
}
