package app.vaadin.nps.security;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class VaadinOAuth2UserService extends DefaultOAuth2UserService {

    private static final String ALLOWED_DOMAIN = "vaadin.com";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Load the user using the default service
        OAuth2User user = super.loadUser(userRequest);

        // Get the email address
        String email = user.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("invalid_email"),
                "Email not provided by OAuth2 provider"
            );
        }

        // Validate the domain
        if (!email.endsWith("@" + ALLOWED_DOMAIN)) {
            throw new UnauthorizedDomainException(
                "Only @" + ALLOWED_DOMAIN + " email addresses are allowed. " +
                    "Got: " + email
            );
        }

        return user;
    }
}