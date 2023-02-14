package experiment.spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import experiment.spring.domain.member.Member;
import experiment.spring.repository.MemberRepository;
import experiment.spring.security.token.TokenProvider;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private MemberRepository memberRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String jwtAccessToken = null;
        String refreshToken = null;

        if (request.getRequestURI().equals("/api/token/refresh") && request.getMethod().equals("POST")) {
            refreshToken = resolveToken(request);
            jwtAccessToken = tokenProvider.generateAccessToken(refreshToken);
            log.info("jwtAccessToken={}", jwtAccessToken);
        } else {
            jwtAccessToken = resolveToken(request);
        }

        if (StringUtils.hasText(jwtAccessToken) && tokenProvider.validateAccessToken(jwtAccessToken)) {
            Authentication authentication = getAuthentication(jwtAccessToken);
            log.info("auth={}", authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }

    private Authentication getAuthentication(String token) {
        String loginId = (String) tokenProvider.getAttribute(token);
        Member member = memberRepository.findByLoginId(loginId)
            .orElseThrow(NoSuchElementException::new);
        Collection<? extends GrantedAuthority> authorities = Collections
            .singletonList(new SimpleGrantedAuthority(member.getRole().name()));
        return new UsernamePasswordAuthenticationToken(member, token, authorities);
    }

    private String resolveToken(HttpServletRequest request) {
        String accessToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        return null;
    }
}
