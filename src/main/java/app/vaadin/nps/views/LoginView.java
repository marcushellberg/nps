package app.vaadin.nps.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", autoLayout = false)
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private static final String OAUTH_URL = "/oauth2/authorization/google";
    private final Div errorMessage;

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Create error message div (hidden by default)
        errorMessage = new Div();
        errorMessage.setClassName("error-message");
        errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMessage.getStyle().set("margin-bottom", "1em");
        errorMessage.setVisible(false);

        // Create login link
        Anchor loginLink = new Anchor(OAUTH_URL, "Login with Google");
        loginLink.setRouterIgnore(true);

        add(
            new H1("NPS Admin"),
            errorMessage,
            loginLink
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if there are any error parameters
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        if (queryParameters.getParameters().containsKey("error")) {
            errorMessage.setText("Only @vaadin.com email addresses are allowed to login.");
            errorMessage.setVisible(true);
        }
    }
}