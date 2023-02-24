package experiment.spring.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import experiment.spring.config.security.AuthProperties;
import experiment.spring.domain.member.Member;
import experiment.spring.domain.member.Role;
import experiment.spring.domain.member.dto.LoginDto;
import experiment.spring.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@SpringBootTest
@Transactional
public class SecurityTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthProperties authProperties;

    private MockMvc mvc;

    @BeforeEach
    public void setup(){
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        Member member = Member.builder().role(Role.USER)
                .loginId("tester").password(passwordEncoder.encode("1234")).build();
        memberRepository.save(member);
    }

    @Test
    void checkSecurityChain() throws Exception {
        MockHttpServletResponse response = login();

        String refreshToken = response.getCookie(authProperties.getAuth().getRefreshHeader()).getValue();
        String accessToken = response.getHeader(authProperties.getAuth().getAccessHeader());

        log.info("accessToken={}", accessToken);
        log.info("refreshToken={}", refreshToken);

        assertThat(refreshToken).isNotNull();
        assertThat(accessToken).isNotNull();
    }

    private MockHttpServletResponse login() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = new LoginDto("tester", "1234");
        return mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
    }
}

