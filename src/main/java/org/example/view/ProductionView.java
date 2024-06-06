package org.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import org.example.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import jakarta.annotation.PostConstruct;

import org.example.service.CustomerService;
import org.example.service.DescriptionService;
import org.example.service.OldDescriptionService;
import org.example.service.ProductionOrderService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Planning | Vector Hasselt")
public class ProductionView extends VerticalLayout {

    private Grid<ProductionOrder> grid = new Grid<>(ProductionOrder.class);
    TextField filterText = new TextField();
    ProductionForm form;
    ProductionOrderService service;
    private CustomerService customerService;
    private DescriptionService descriptionService;
    private OldDescriptionService oldDescriptionService;

    @Autowired
    public ProductionView(ProductionOrderService service, CustomerService customerService, DescriptionService descriptionService, OldDescriptionService oldDescriptionService) {
        this.service = service;
        this.customerService = customerService;
        this.descriptionService = descriptionService;
        this.oldDescriptionService = oldDescriptionService;
        addClassName("production-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
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
        form = new ProductionForm(service.findAllProductionOrders(), customers, descriptions, oldDescriptions);
        form.setWidth("25em");
        form.addSaveListener(this::saveProductionOrder);
        form.addDeleteListener(this::deleteProductionOrder);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveProductionOrder(ProductionForm.SaveEvent event) {
        service.saveProductionOrder(event.getProductionOrder());
        updateList();
        closeEditor();
    }

    private void deleteProductionOrder(ProductionForm.DeleteEvent event) {
        service.deleteProductionOrder(event.getProductionOrder());
        updateList();
        closeEditor();
    }

    @PostConstruct
    public void configureGrid() {
        grid.addClassNames("production-grid");
        grid.setSizeFull();

        // Set columns including all fields
        grid.setColumns(
                "sequence", "sevenNumber", "fourNumber", "amount",
                "salesOrder", "soLine", "ml",
                "reelLength", "infosheets", "catalog"
        );

        // Set headers for non-date fields
        grid.getColumnByKey("sequence").setHeader("Sequence");
        grid.getColumnByKey("sevenNumber").setHeader("7 Number");
        grid.getColumnByKey("fourNumber").setHeader("4 Number");
        grid.addColumn(order -> order.getDescription() != null ? order.getDescription().getName() : "")
                .setHeader("Description");
        grid.getColumnByKey("amount").setHeader("Amount");
        grid.getColumnByKey("salesOrder").setHeader("Sales Order");
        grid.getColumnByKey("soLine").setHeader("SO Line");
        grid.addColumn(new LocalDateRenderer<>(ProductionOrder::getRequestedDate, "dd/MM/yyyy"))
                .setHeader("Requested Date");
        grid.addColumn(new LocalDateRenderer<>(ProductionOrder::getPromisedDate, "dd/MM/yyyy"))
                .setHeader("Promised Date");
        grid.addColumn(order -> order.getCustomer() != null ? order.getCustomer().getName() : "")
                .setHeader("Customer");
        grid.getColumnByKey("ml").setHeader("ML");
        grid.addColumn(order -> order.getOldDescription() != null ? order.getOldDescription().getName() : "")
                .setHeader("Old Description");
        grid.getColumnByKey("reelLength").setHeader("Reel Length");
        grid.addColumn(new LocalDateRenderer<>(ProductionOrder::getConversionDate, "dd/MM/yyyy"))
                .setHeader("Conversion Date");
        grid.addColumn(new LocalDateRenderer<>(ProductionOrder::getCustomerDate, "dd/MM/yyyy"))
                .setHeader("Customer Date");
        grid.getColumnByKey("infosheets").setHeader("Infosheets");
        grid.getColumnByKey("catalog").setHeader("Catalog");


        // Set auto-width for columns
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        // Add value change listener
        grid.asSingleSelect().addValueChangeListener(event ->
                editProductionOrder(event.getValue()));
    }
    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addProductionOrderButton = new Button("Add production order");
        addProductionOrderButton.addClickListener(click -> addProductionOrder());

        Button printGridButton = new Button("Print");
        printGridButton.addClickListener(click -> printGrid());

        var toolbar = new HorizontalLayout(filterText, addProductionOrderButton, printGridButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void editProductionOrder(ProductionOrder productionOrder) {
        if (productionOrder == null) {
            closeEditor();
        } else {
            form.setProductionOrder(productionOrder);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setProductionOrder(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addProductionOrder() {
        grid.asSingleSelect().clear();
        editProductionOrder(new ProductionOrder());
    }

    private void updateList() {
        grid.setItems(service.findAllProductionOrders(filterText.getValue()));
    }

    private void printGrid() {
        // Logic for printing the grid
        Notification.show("Printing grid...");
        Page page = this.getUI().get().getPage();
        page.executeJs("window.print()");
    }
}


