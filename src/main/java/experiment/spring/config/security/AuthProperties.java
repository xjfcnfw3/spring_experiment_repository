package experiment.spring.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "secret")
public class AuthProperties {

    private Auth auth = new Auth();

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Auth {
        private String tokenSecret;
        private String refreshHeader;
        private String accessHeader;

        public String getRefreshHeader() {
            return refreshHeader;
        }

        public void setRefreshHeader(String refreshHeader) {
            this.refreshHeader = refreshHeader;
        }

        public String getAccessHeader() {
            return accessHeader;
        }

        public void setAccessHeader(String accessHeader) {
            this.accessHeader = accessHeader;
        }

        public String getTokenSecret() {
            return tokenSecret;
        }

        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }
    }
}
