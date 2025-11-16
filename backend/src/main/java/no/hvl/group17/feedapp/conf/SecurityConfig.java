package no.hvl.group17.feedapp.conf;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(           // Allow UI
                                "/",
                                "/index.html",
                                "/static/**",
                                "/build/**",
                                "/css/**",
                                "/js/**",
                                "/favicon.ico",
                                "/assets/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/polls",          // GET all polls
                                "/api/polls/*",        // GET single poll
                                "/api/polls/*/count"   // GET vote count
                        ).permitAll()
                        .requestMatchers(
                                "/api/polls/*/votes",      // POST vote
                                "/api/polls/*/votes/*"     // DELETE unvote
                        ).permitAll()
                        .requestMatchers("/api/me/**").hasRole("USER")      // USER endpoints
                        .requestMatchers("/api/users/**").hasRole("ADMIN")  // ADMIN endpoints
                        .anyRequest().authenticated()   // Else authenticated
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new HashSet<>(grantedAuthoritiesConverter.convert(jwt));
            Collection<? extends GrantedAuthority> baseAuthorities = grantedAuthoritiesConverter.convert(jwt);
            authorities.addAll(baseAuthorities);

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null) {
                Object rolesObj = realmAccess.get("roles");
                if (rolesObj instanceof Collection<?> roles) {
                    roles.stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .forEach(authorities::add);
                }
            }

            return authorities;
        });

        return converter;
    }
}
