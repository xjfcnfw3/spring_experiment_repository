package experiment.spring.security;

import experiment.spring.config.security.AuthProperties;
import experiment.spring.domain.member.Member;
import experiment.spring.repository.MemberRepository;
import experiment.spring.security.token.TokenProvider;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private AuthProperties authProperties;
    @Autowired
    private MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            authenticate(request, response);
        } catch (Exception e) {
            log.error("Error = ", e);
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

    private void authenticate(HttpServletRequest request, HttpServletResponse response) {
        String token = resolveToken(request);
        if (StringUtils.hasText(token)) {
            Authentication authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            reIssue(request, response);
        }
    }

    private void reIssue(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getRequestURI().equals("/api/token/refresh") || !request.getMethod().equals("POST")) {
            return;
        }
        String refreshToken = getRefreshToken(request);
        String accessToken = tokenProvider.generateAccessToken(refreshToken);
        Cookie cookie = getRefreshTokenCookie(refreshToken);
        response.addHeader(authProperties.getAuth().getAccessHeader(), accessToken);
        response.addCookie(cookie);
    }

    private String getRefreshToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
            .filter(cookie -> cookie.getName().equals(authProperties.getAuth().getRefreshHeader()))
            .findFirst()
            .map(Cookie::getValue)
            .orElse(null);
    }

    private Cookie getRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh", refreshToken);
        cookie.setMaxAge(432000);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/token/refresh");
        return cookie;
    }

    private String resolveToken(HttpServletRequest request) {
        String accessToken = request.getHeader(authProperties.getAuth().getAccessHeader());
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        return null;
    }
}
