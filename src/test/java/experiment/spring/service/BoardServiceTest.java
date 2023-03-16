package experiment.spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import experiment.spring.domain.Boaed.Board;
import experiment.spring.domain.Boaed.dto.BoardRequest;
import experiment.spring.domain.member.Member;
import experiment.spring.repository.BoardRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AuthorizationServiceException;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    private Member member;

    private Board board;

    private BoardRequest boardRequest;

    @BeforeEach
    void setUp() {
        boardService = new BoardService(boardRepository);

        member = Member.builder()
            .id(1L)
            .loginId("test")
            .password("1234")
            .build();

        board = Board
            .builder()
            .id(1L)
            .title("test")
            .content("This is test")
            .member(member)
            .views(0L)
            .build();

        boardRequest = new BoardRequest("test", "This is test");
    }

    @DisplayName("특정 유저의 게시판의 생성")
    @Test
    void createBoard() {
        when(boardRepository.save(any())).thenReturn(board);
        boardService.save(member, boardRequest);
        verify(boardRepository).save(any());
    }

    @DisplayName("게시판을 조회한다.")
    @Test
    void getBoard() {
        when(boardRepository.findBoardById(any())).thenReturn(Optional.ofNullable(board));
        Board board = boardService.getBoard(any());
        assertThat(board.getViews()).isGreaterThan(0L);
        verify(boardRepository).findBoardById(any());
    }

    @DisplayName("게시판을 업데이트 한다.")
    @Test
    void update() {
        Member otherMember = Member.builder()
            .id(2L)
            .loginId("test2")
            .password("1234")
            .build();
        BoardRequest updateRequest = new BoardRequest("This is updated!");

        when(boardRepository.save(any())).thenReturn(board);
        when(boardRepository.findById(any())).thenReturn(Optional.ofNullable(board));

        boardService.save(member, boardRequest);
        boardService.update(member, updateRequest, board.getId());

        assertThatThrownBy(() -> boardService.update(otherMember, updateRequest, board.getId()))
            .isInstanceOf(AuthorizationServiceException.class);
        assertThat(board.getContent()).isEqualTo(updateRequest.getContent());
        verify(boardRepository, times(2)).save(any());
        verify(boardRepository, times(2)).findById(any());
    }

    @Test
    void delete() {
        Member otherMember = Member.builder()
            .id(2L)
            .loginId("test2")
            .password("1234")
            .build();

        when(boardRepository.findById(any())).thenReturn(Optional.ofNullable(board));

        assertThatThrownBy(() -> boardService.delete(otherMember, board.getId()))
            .isInstanceOf(AuthorizationServiceException.class);
        boardService.delete(member, board.getId());
        verify(boardRepository, times(2)).findById(any());
        verify(boardRepository).delete(any());
    }
}
