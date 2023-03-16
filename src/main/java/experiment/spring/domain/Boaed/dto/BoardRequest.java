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
public class BoardRequest {
    private String title;
    private String content;

    public BoardRequest(String content) {
        this.content = content;
    }

    public Board toEntity() {
        return Board.builder()
            .title(title)
            .content(content)
            .build();
    }
}
