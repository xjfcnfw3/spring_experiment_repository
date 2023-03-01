package experiment.spring.service;

import experiment.spring.domain.Boaed.Board;
import experiment.spring.domain.Boaed.BoardDto;
import experiment.spring.repository.BoardRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;

    public Board save(BoardDto boardDto) {
        Board board = boardDto.toEntity();
        log.info("board ={}", board);
        return repository.save(board);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Board getBoard(Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
