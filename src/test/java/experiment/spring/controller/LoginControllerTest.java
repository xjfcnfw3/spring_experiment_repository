package experiment.spring.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import experiment.spring.domain.member.dto.LoginDto;
import experiment.spring.domain.member.dto.LoginResponse;
import experiment.spring.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LoginControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private LoginController controller;

    @Autowired
    private TokenProvider provider;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .build();
    }

    @Test
    void login() throws Exception {
        LoginResponse response = loginTest("/api/auth/login");
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
    }

    @Test
    void checkToken() throws Exception {
        LoginResponse response = loginTest("/api/auth/login");
        String accessToken = response.getAccessToken();
        provider.validateToken(accessToken);
        String loginId = provider.getAttribute(accessToken);
        assertThat(loginId).isEqualTo("test");
    }

    private LoginResponse loginTest(String pathName) throws Exception {
        LoginDto login = LoginDto.builder()
            .loginId("test")
            .password("1234")
            .build();
        String url = "http://localhost:" + port + pathName;
        MvcResult result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(login)))
            .andReturn();
        String content = result.getResponse().getContentAsString();
        return new ObjectMapper().readValue(content, LoginResponse.class);
    }
}
