package experiment.spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import experiment.spring.domain.Boaed.Board;
import experiment.spring.domain.Boaed.BoardDto;
import experiment.spring.domain.member.Member;
import experiment.spring.repository.BoardRepository;
import experiment.spring.repository.MemberRepository;
import java.util.List;
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

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

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

    @Test
    void NPlusTest1() {
        for (int i = 1; i <= 10; i++) {
            Member member = Member.builder().name("member" + i).build();
            memberRepository.save(member);
            BoardDto boardDto = new BoardDto("hello member" + i, "This is test" + i);
            boardService.save(member, boardDto);
        }
        List<Board> boards = boardRepository.findAll();
        assertThat(boards.size()).isEqualTo(10);
    }

    @Test
    void NPlusTest2() {
        Member member = Member.builder().name("member1").build();
        for (int i = 1; i <= 10; i++) {
            BoardDto boardDto = new BoardDto("hello member" + i, "This is test" + i);
            boardService.save(member, boardDto);
        }
        List<Board> boards = boardRepository.findAll();
        assertThat(boards.size()).isEqualTo(10);
    }
}
