package experiment.spring.service;

import experiment.spring.domain.Boaed.Board;
import experiment.spring.domain.Boaed.dto.BoardRequest;
import experiment.spring.domain.member.Member;
import experiment.spring.repository.BoardRepository;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;

    @Transactional
    public Board save(Member member, BoardRequest boardDto) {
        Board board = boardDto.toEntity();
        board.setMember(member);
        board.setViews(0L);
        return repository.save(board);
    }

    @Transactional
    public void delete(Member member, Long id) {
        Board board = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (member.getId().equals(board.getMember().getId())) {
            repository.delete(board);
        } else {
            throw new AuthorizationServiceException("You are not Owner!");
        }
    }

    @Transactional
    public Board getBoard(Long id) {
        Board board = repository
            .findBoardById(id).orElseThrow(EntityNotFoundException::new);
        board.increaseView();
        return board;
    }

    @Transactional
    public void update(Member member, BoardRequest boardDto, Long boardId) {
        Board board = repository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        if (member.getId().equals(board.getMember().getId())) {
            String content = boardDto.getContent();
            board.setContent(content);
            repository.save(board);
        } else {
            throw new AuthorizationServiceException("You are not Owner!");
        }
    }
}
