package org.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import org.example.model.*;
import org.example.service.*;
import org.springframework.context.annotation.Scope;
import software.xdev.vaadin.grid_exporter.GridExporter;

import java.util.List;

@Route(value = "rewind", layout = MainLayout.class)
@PageTitle("Halfstock | Vector Hasselt")
@Scope("prototype")
@PermitAll
@SpringComponent
public class RewindView extends VerticalLayout {

    private Grid<RewindOrder> grid = new Grid<>(RewindOrder.class);
    private TextField filterText = new TextField();
    private RewindForm form;
    private RewindOrderService service;
    private CustomerService customerService;
    private DescriptionService descriptionService;
    private OldDescriptionService oldDescriptionService;
    private FooterRow footerRow;

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
        form.addSaveListener(this::saveRewindOrder);
        form.addDeleteListener(this::deleteRewindOrder);
        form.addCloseListener(e -> closeEditor());
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

        grid.setColumns("rack", "sevenNumber", "fourNumber");

        grid.getColumnByKey("rack").setHeader("Rack").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("sevenNumber").setHeader("7 Number").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("fourNumber").setHeader("4 Number").setResizable(true).setHeaderPartName("header");
        grid.addColumn(order -> order.getCustomer() != null ? order.getCustomer().getName() : "")
                .setHeader("Customer").setResizable(true).setHeaderPartName("header");
        grid.addColumn(order -> order.getDescription() != null ? order.getDescription().getName() : "")
                .setHeader("Description").setResizable(true).setHeaderPartName("header");
        grid.addColumn(order -> order.getOldDescription() != null ? order.getOldDescription().getName() : "")
                .setHeader("Old Description").setResizable(true).setHeaderPartName("header");

        grid.addColumn(RewindOrder::getBox)
                .setHeader("Box").setResizable(true).setHeaderPartName("header");
        grid.addColumn(RewindOrder::getRewinder)
                .setHeader("Rewinder").setResizable(true).setHeaderPartName("header");

        grid.addColumn(new ComponentRenderer<>(order -> {
            String formattedAmount = String.format("%,.0f", order.getAmount());
            return new Span(formattedAmount);
        })).setHeader("Amount").setKey("amount").setResizable(true).setHeaderPartName("header");

        grid.addColumn(new LocalDateRenderer<>(RewindOrder::getDate, "dd/MM/yyyy"))
                .setHeader("Date").setResizable(true).setHeaderPartName("header");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        footerRow = grid.appendFooterRow();

        updateFooter();

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
        updateFooter();
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

private void updateFooter() {
    List<RewindOrder> orders = service.findAllRewindOrders(filterText.getValue());
    double totalAmount = orders.stream()
            .mapToDouble(RewindOrder::getAmount)
            .sum();

    String formattedAmount = String.format("%,.0f", totalAmount);
    footerRow.getCell(this.grid.getColumnByKey("amount")).setText(formattedAmount);
}
    private void printGrid() {
        GridExporter
                .newWithDefaults(grid)
                .open();
    }
}