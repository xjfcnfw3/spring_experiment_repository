package experiment.spring.service;

import experiment.spring.domain.Boaed.Board;
import experiment.spring.domain.Boaed.BoardDto;
import experiment.spring.domain.member.Member;
import experiment.spring.repository.BoardRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;

    public Board save(Member member, BoardDto boardDto) {
        Board board = boardDto.toEntity();
        board.setMember(member);
        log.info("board ={}", board);
        return repository.save(board);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Board getBoard(Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void update(Member member, BoardDto boardDto, Long boardId) {
        if (member.getId().equals(boardId)) {
            String content = boardDto.getContent();
            Board board = repository.findById(boardId).orElseThrow(EntityNotFoundException::new);
            board.setContent(content);
            repository.save(board);
        } else {
            throw new AuthorizationServiceException("You are not Owner!");
        }
    }
}
