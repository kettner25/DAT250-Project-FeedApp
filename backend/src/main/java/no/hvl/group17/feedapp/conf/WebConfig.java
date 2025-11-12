package no.hvl.group17.feedapp.conf;

import no.hvl.group17.feedapp.security.AnonCookieSigner;
import no.hvl.group17.feedapp.security.AnonIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public AnonCookieSigner anonCookieSigner() {
        return new AnonCookieSigner("super-secret-hmac-key-change-me"); // todo load from env/props in real usage
    }

    @Bean
    public FilterRegistrationBean<AnonIdFilter> anonIdFilter(AnonCookieSigner signer) {
        FilterRegistrationBean<AnonIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new AnonIdFilter(signer, false));  // todo secure=false in localhost; set true in prod behind HTTPS
        reg.setOrder(0);
        reg.addUrlPatterns("/*");
        return reg;
    }

}
