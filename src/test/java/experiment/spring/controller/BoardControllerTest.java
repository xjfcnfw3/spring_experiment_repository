package experiment.spring.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import experiment.spring.domain.Boaed.Board;
import experiment.spring.domain.Boaed.dto.BoardRequest;
import experiment.spring.domain.member.Member;
import experiment.spring.domain.member.Role;
import experiment.spring.security.WithMockCustomUser;
import experiment.spring.service.BoardService;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Transactional
@SpringBootTest
class BoardControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BoardService boardService;

    private MockMvc mvc;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    @WithMockCustomUser(role = Role.USER, username = "test", password = "1234")
    void save() throws Exception {
        BoardRequest boardDto = new BoardRequest("this is test", "test1");
        mvc.perform(post("/api/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(boardDto)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, username = "test", password = "1234")
    void updateBoard() throws Exception {
        Member member = getMember();
        Board board = boardService.save(member, new BoardRequest("test", "this is Test"));
        String beforeContent = board.getContent();

        BoardRequest boardRequest = new BoardRequest();
        boardRequest.setContent("updated");
        mvc.perform(put("/api/board/" + board.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(boardRequest)))
            .andExpect(status().isOk());
        Board updatedBoard = boardService.getBoard(board.getId());
        assertThat(beforeContent).isNotEqualTo(updatedBoard.getContent());
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Member) authentication.getPrincipal();
    }
}
