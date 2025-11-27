package no.hvl.group17.feedapp.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtAuthFilter() {
        this.algorithm = Algorithm.HMAC256("super-secret-key"); // TODO: move to config/env
        this.verifier = JWT.require(algorithm).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                DecodedJWT jwt = verifier.verify(token);

                String username = jwt.getSubject();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JWTVerificationException e) {
                System.out.println("Invalid JWT: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
