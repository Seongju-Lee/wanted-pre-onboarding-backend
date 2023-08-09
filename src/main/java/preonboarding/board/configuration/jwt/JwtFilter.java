package preonboarding.board.configuration.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private static final String BEARER_TYPE = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER_TYPE)) {
            request.setAttribute("exception", TokenError.MISSING);
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorization.substring(BEARER_TYPE.length());

        try {
            jwtProvider.validationToken(token);
        } catch (SignatureException e) {
            request.setAttribute("exception", TokenError.NO_SIGNATURE);
            filterChain.doFilter(request, response);
            return;
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", TokenError.EXPIRED);
            filterChain.doFilter(request, response);
            return;
        } catch (MalformedJwtException e) {
            request.setAttribute("exception", TokenError.MALFORMED);
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            request.setAttribute("exception", TokenError.INVALID);
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtProvider.getClaims(token);
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}