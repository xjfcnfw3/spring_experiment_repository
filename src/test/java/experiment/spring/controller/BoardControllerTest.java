package experiment.spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import experiment.spring.domain.Boaed.Board;
import experiment.spring.domain.Boaed.dto.BoardRequest;
import experiment.spring.domain.Boaed.dto.BoardResponse;
import experiment.spring.domain.member.Member;
import experiment.spring.security.WithMockCustomUser;
import experiment.spring.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = BoardController.class)
class BoardControllerTest {

    @MockBean
    private BoardService boardService;


    private MockMvc mvc;
    private BoardRequest boardRequest;
    private BoardResponse boardResponse;
    private Board board;
    private ObjectMapper objectMapper;

    @BeforeEach
    void init(WebApplicationContext context) {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        boardRequest = new BoardRequest("test", "this is Test");
        boardResponse = new BoardResponse(1L, "test", "this is Test", 1L);
        board = new Board(1L, "test", "this is Test", 1L, Member.builder().id(1L).build());
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockCustomUser
    void save() throws Exception {
        given(boardService.save(any(), any())).willReturn(board);
        mvc.perform(post("/api/board")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest)))
            .andExpect(status().isOk())
            .andDo(print());
        verify(boardService).save(any(), any());
    }

    @Test
    @WithMockCustomUser
    void getBoard() throws Exception {
        given(boardService.getBoard(any())).willReturn(board);
        mvc.perform(get("/api/board/{id}", 1L)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print());
        verify(boardService).getBoard(any());
    }

    @Test
    @WithMockCustomUser
    void updateBoard() throws Exception {
        BoardRequest request = new BoardRequest("This is updated");
        mvc.perform(put("/api/board/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print());
        verify(boardService).update(any(), any(), any());
    }

    @Test
    @WithMockCustomUser
    void deleteBoard() throws Exception {
        mvc.perform(delete("/api/board/{id}", 1L)
            .with(csrf()))
            .andExpect(status().isOk());
        verify(boardService).delete(any(), any());
    }
}
