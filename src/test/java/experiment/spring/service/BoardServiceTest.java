package experiment.spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import experiment.spring.domain.Boaed.Board;
import experiment.spring.domain.Boaed.BoardDto;
import experiment.spring.domain.member.Member;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AuthorizationServiceException;

@Transactional
@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Test
    void update() {
        Member member = Member.builder().id(1L).build();
        BoardDto boardDto = new BoardDto("test", "this is test");
        Board savedBoard = boardService.save(member, boardDto);

        BoardDto updateDto = new BoardDto("test", "this is test2");
        boardService.update(member, updateDto, savedBoard.getId());

        Board board = boardService.getBoard(savedBoard.getId());
        assertThat(board.getContent()).isNotEqualTo(boardDto.getContent());
    }

    @Test
    void updateNotOwner() {
        Member notOwner = Member.builder().id(2L).build();
        Board savedBoard = saveBoard();

        BoardDto updateDto = new BoardDto("test", "this is test2");
        assertThatThrownBy(() -> boardService.update(notOwner, updateDto, savedBoard.getId()))
            .isInstanceOf(AuthorizationServiceException.class);
    }

    private Board saveBoard() {
        Member member = Member.builder().id(1L).build();
        BoardDto boardDto = new BoardDto("test", "this is test");
        return boardService.save(member, boardDto);
    }


}
