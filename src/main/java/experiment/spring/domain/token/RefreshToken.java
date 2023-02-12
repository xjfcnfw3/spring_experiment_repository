package experiment.spring.domain.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Getter
@RedisHash(value = "refreshToken", timeToLive = 300)
public class RefreshToken {

    @Id
    private String token;
    private String loginId;
}
