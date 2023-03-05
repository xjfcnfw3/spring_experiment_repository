package experiment.spring.controller;

import experiment.spring.domain.Boaed.Board;
import experiment.spring.domain.Boaed.BoardDto;
import experiment.spring.domain.member.Member;
import experiment.spring.security.LoginMember;
import experiment.spring.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public Board save(@RequestBody BoardDto boardDto, @LoginMember Member member) {
        return boardService.save(member, boardDto);
    }

    @GetMapping("/{id}")
    public Board getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable Long id) {
        boardService.delete(id);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public void updateBoard(@LoginMember Member member, @RequestBody BoardDto boardDto, @PathVariable Long id) {
        boardService.update(member, boardDto, id);
    }
}
