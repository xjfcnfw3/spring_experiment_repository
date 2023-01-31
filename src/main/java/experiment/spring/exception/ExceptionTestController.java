package experiment.spring.exception;

import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/ex")
public class ExceptionTestController {


    @PostMapping("/{id}")
    public ResponseEntity<Void> getException(@PathVariable String id) {
        log.info("id={}", id);
        if (id.equals("entity")) {
            throw new EntityNotFoundException("해당 엔티티는 찾을 수 없습니다.");
        }
        return ResponseEntity.ok().build();
    }
}
