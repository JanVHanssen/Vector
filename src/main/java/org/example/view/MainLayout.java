package org.example.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.text.NumberFormat;
import java.util.Locale;

public class MainLayout extends AppLayout {

    public MainLayout() {
        UI.getCurrent().getPage().addStyleSheet("/frontend/grid-styling.css");
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Vector Hasselt");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        var header = new HorizontalLayout(new DrawerToggle(), logo);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private void createDrawer() {
        addToDrawer(new VerticalLayout(
                new RouterLink("Planning", ProductionView.class),
                new RouterLink("Halfstock", RewindView.class),
                new RouterLink("Customers", CustomerView.class),
                new RouterLink("Descriptions", DescriptionView.class),
                new RouterLink("Old Descriptions", OldDescriptionView.class)
        ));
    }
}