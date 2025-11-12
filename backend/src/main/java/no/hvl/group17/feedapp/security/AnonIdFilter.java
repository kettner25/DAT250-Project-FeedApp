package no.hvl.group17.feedapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class AnonIdFilter extends OncePerRequestFilter {
    public static final String COOKIE_NAME = "anonId";
    public static final String REQ_ATTR = "anonId";
    private final AnonCookieSigner signer;
    private final boolean secureCookies;

    public AnonIdFilter(AnonCookieSigner signer, boolean secureCookies) {
        this.signer = signer;
        this.secureCookies = secureCookies;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String raw = getCookie(req).orElse(null);
        String anonId = null;

        if (raw != null && raw.contains(".")) {
            String[] parts = raw.split("\\.", 2);
            String id = parts[0];
            String sig = parts[1];
            if (signer.verify(id, sig)) {
                anonId = id;
            }
        }

        if (anonId == null) {
            anonId = UUID.randomUUID().toString();
            String value = anonId + "." + signer.sign(anonId);

            Cookie c = new Cookie(COOKIE_NAME, value);
            c.setHttpOnly(true);
            c.setSecure(secureCookies);
            c.setPath("/");
            c.setMaxAge(60 * 60 * 24 * 180);
            c.setAttribute("SameSite", "Lax");
            res.addCookie(c);
        }

        req.setAttribute(REQ_ATTR, anonId);
        chain.doFilter(req, res);
    }

    private Optional<String> getCookie(HttpServletRequest req) {
        if (req.getCookies() == null) return Optional.empty();
        return Arrays.stream(req.getCookies())
                .filter(c -> AnonIdFilter.COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
