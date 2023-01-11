package experiment.spring.repository.implement;

import experiment.spring.domain.member.Member;
import experiment.spring.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static final Map<Long, Member> store = new ConcurrentHashMap<>();
    private static Long sequence = 0L;

    @Override
    public Member addMember(Member member) {
        member.setId(++sequence);
        log.info("member={}", member);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream()
            .filter(m -> m.getLoginId().equals(loginId))
            .findFirst();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clear() {
        store.clear();
    }
}
