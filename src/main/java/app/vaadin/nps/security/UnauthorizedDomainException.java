package app.vaadin.nps.security;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class UnauthorizedDomainException extends OAuth2AuthenticationException {
    public UnauthorizedDomainException(String message) {
        super(new OAuth2Error("unauthorized_domain"), message);
    }
}