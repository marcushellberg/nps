package app.vaadin.nps;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.menu.RouteParamType;
import com.vaadin.flow.theme.Theme;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("my-theme")
@RegisterReflectionForBinding(RouteParamType.class)
public class NpsApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(NpsApplication.class, args);
    }
}
