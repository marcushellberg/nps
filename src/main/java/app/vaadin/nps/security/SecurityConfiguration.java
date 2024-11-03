package app.vaadin.nps.security;

import app.vaadin.nps.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    private static final String LOGIN_URL = "/login";

    @Autowired
    private VaadinOAuth2UserService oAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configure static resources with public access
        http.authorizeHttpRequests(auth ->
            auth.requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll());

        // Configure security settings for Vaadin
        super.configure(http);

        // Configure OAuth2 login with custom user service
        http.oauth2Login(c -> {
            c.loginPage(LOGIN_URL)
                .permitAll()
                .userInfoEndpoint(u -> {
                    u.userService(oAuth2UserService);
                });
            
        });

        // Set login view
        setLoginView(http, LoginView.class);
    }
}