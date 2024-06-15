package org.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import org.example.model.*;
import org.example.service.*;
import org.springframework.context.annotation.Scope;

import java.util.List;

@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "rewind", layout = MainLayout.class)
@PageTitle("Halfstock | Vector Hasselt")
public class RewindView extends VerticalLayout {

    private Grid<RewindOrder> grid = new Grid<>(RewindOrder.class);
    TextField filterText = new TextField();
    RewindForm form;
    RewindOrderService service;
    private CustomerService customerService;
    private DescriptionService descriptionService;
    private OldDescriptionService oldDescriptionService;

    public RewindView(RewindOrderService service, CustomerService customerService, DescriptionService descriptionService, OldDescriptionService oldDescriptionService) {
        this.service = service;
        this.customerService = customerService;
        this.descriptionService = descriptionService;
        this.oldDescriptionService = oldDescriptionService;
        addClassName("rewind-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        addBackgroundColor();
        updateList();
        closeEditor();
    }
    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        List<Customer> customers = customerService.findAllCustomers();
        List<Description> descriptions = descriptionService.findAllDescriptions();
        List<OldDescription> oldDescriptions = oldDescriptionService.findAllOldDescriptions();
        form = new RewindForm(service.findAllRewindOrders(), customers, descriptions, oldDescriptions);
        form.setWidth("25em");
        form.addSaveListener(this::saveRewindOrder); // <1>
        form.addDeleteListener(this::deleteRewindOrder); // <2>
        form.addCloseListener(e -> closeEditor()); // <3>
    }

    private void saveRewindOrder(RewindForm.SaveEvent event) {
        service.saveRewindOrder(event.getRewindOrder());
        updateList();
        closeEditor();
    }

    private void deleteRewindOrder(RewindForm.DeleteEvent event) {
        service.deleteRewindOrder(event.getRewindOrder());
        updateList();
        closeEditor();
    }

    @PostConstruct
    public void configureGrid() {
        grid.addClassName("styling");
        grid.setColumnReorderingAllowed(true);
        grid.setSizeFull();
        grid.setColumns("sevenNumber", "fourNumber", "rack", "box", "rewinder");

        grid.getColumnByKey("sevenNumber").setHeader("7 Number").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("fourNumber").setHeader("4 Number").setResizable(true).setHeaderPartName("header");
        grid.addColumn(order -> order.getCustomer() != null ? order.getCustomer().getName() : "")
                .setHeader("Customer").setResizable(true).setHeaderPartName("header");
        grid.addColumn(order -> order.getDescription() != null ? order.getDescription().getName() : "")
                .setHeader("Description").setResizable(true).setHeaderPartName("header");
        grid.addColumn(order -> order.getOldDescription() != null ? order.getOldDescription().getName() : "")
                .setHeader("Old Description").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("rack").setHeader("Rack").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("box").setHeader("Box").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("rewinder").setHeader("Rewinder").setResizable(true).setHeaderPartName("header");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.asSingleSelect().addValueChangeListener(event ->
                editRewindOrder(event.getValue()));


    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addRewindOrderButton = new Button("Add rewind order");
        addRewindOrderButton.addClickListener(click -> addRewindOrder());

        Button printGridButton = new Button("Print");
        printGridButton.addClickListener(click -> printGrid());

        var toolbar = new HorizontalLayout(filterText, addRewindOrderButton, printGridButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void editRewindOrder(RewindOrder rewindOrder) {
        if (rewindOrder == null) {
            closeEditor();
        } else {
            form.setRewindOrder(rewindOrder);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setRewindOrder(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addRewindOrder() {
        grid.asSingleSelect().clear();
        editRewindOrder(new RewindOrder());
    }


    private void updateList() {
        grid.setItems(service.findAllRewindOrders(filterText.getValue()));
    }

    private void addBackgroundColor() {
        grid.setPartNameGenerator(order -> {
            if (order.getColor() != null) {
                String color = order.getColor().toString();
                System.out.println("Rewind order: " + order.toString() + " color: " + color);
                return color;
            }
            return null;
        });
    }

    private void printGrid() {
        // Logic for printing the grid
        Notification.show("Printing grid...");
        Page page = this.getUI().get().getPage();
        page.executeJs("window.print()");
    }
}
