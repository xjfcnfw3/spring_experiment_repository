package experiment.spring.domain.Boaed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private String title;
    private String content;

    public Board toEntity() {
        return Board.builder()
            .title(title)
            .content(content)
            .build();
    }
}
