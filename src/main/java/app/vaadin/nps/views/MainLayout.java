package app.vaadin.nps.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Layout
@PermitAll
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private final H1 title;
    private final Div globalActionContainer;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        DrawerToggle toggle = new DrawerToggle();

        title = new H1("");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);


        addToDrawer(getSideNav());

        globalActionContainer = new Div();
        var header = new HorizontalLayout();
        header.addClassNames(LumoUtility.Padding.Right.SMALL);
        header.setWidthFull();
        header.add(toggle, title, globalActionContainer);
        header.expand(title);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        addToNavbar(header);
    }

    private Component getSideNav() {
        var appName = new H2("Vaadin NPS");
        appName.addClassNames(LumoUtility.FontSize.XLARGE);

        var sideNav = new SideNav();

        MenuConfiguration.getMenuEntries().forEach(menuEntry -> {
            sideNav.addItem(new SideNavItem(menuEntry.title(),
                menuEntry.path(), new Icon(menuEntry.icon())));
        });

        return new VerticalLayout(appName, new Scroller(sideNav) {{
            setWidthFull();
        }});
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        Class<?> viewClass = getContent().getClass();
        PageTitle pageTitle = viewClass.getAnnotation(PageTitle.class);
        title.setText(pageTitle != null ? pageTitle.value() : "");

        globalActionContainer.removeAll();

        if (getContent() instanceof HasGlobalAction) {
            var globalAction = ((HasGlobalAction) getContent()).getGlobalActionComponent();
            globalActionContainer.add(globalAction);
        }

    }
}