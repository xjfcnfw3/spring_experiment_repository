package experiment.spring.domain.Boaed.dto;

import experiment.spring.domain.Boaed.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse {

    private Long id;
    private String title;
    private String content;
    private Long views;

    public static BoardResponse of(Board board) {
        return new BoardResponse(
            board.getId(),
            board.getTitle(),
            board.getContent(),
            board.getViews());
    }
}
