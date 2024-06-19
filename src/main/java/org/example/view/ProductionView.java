package org.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import jakarta.annotation.security.PermitAll;
import org.example.model.*;

import java.util.List;

import jakarta.annotation.PostConstruct;

import org.example.service.CustomerService;
import org.example.service.DescriptionService;
import org.example.service.OldDescriptionService;
import org.example.service.ProductionOrderService;


import org.springframework.beans.factory.annotation.Autowired;
import software.xdev.vaadin.grid_exporter.GridExporter;

@UIScope
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
    private FooterRow footerRow;

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
        grid.addClassName("styling");
        grid.setColumnReorderingAllowed(true);
        grid.setSizeFull();

        grid.setColumns(
                "sequence", "sevenNumber", "fourNumber",
                "salesOrder", "soLine", "ml",
                "reelLength", "infosheets", "catalog"
        );

        grid.getColumnByKey("sequence").setHeader("Sequence").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("sevenNumber").setHeader("7 Number").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("fourNumber").setHeader("4 Number").setResizable(true).setHeaderPartName("header");
        grid.addColumn(order -> order.getDescription() != null ? order.getDescription().getName() : "")
                .setHeader("Description").setResizable(true).setHeaderPartName("header");
        grid.addColumn(new ComponentRenderer<>(order -> {
            String formattedAmount = String.format("%,.0f", order.getAmount());
            return new Span(formattedAmount);
        })).setHeader("Amount").setKey("amount").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("salesOrder").setHeader("Sales Order").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("soLine").setHeader("SO Line").setResizable(true).setHeaderPartName("header");
        grid.addColumn(new LocalDateRenderer<>(ProductionOrder::getRequestedDate, "dd/MM/yyyy"))
                .setHeader("Requested Date").setResizable(true).setHeaderPartName("header");
        grid.addColumn(new LocalDateRenderer<>(ProductionOrder::getPromisedDate, "dd/MM/yyyy"))
                .setHeader("Promised Date").setResizable(true).setHeaderPartName("header");
        grid.addColumn(order -> order.getCustomer() != null ? order.getCustomer().getName() : "")
                .setHeader("Customer").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("ml").setHeader("ML").setResizable(true).setHeaderPartName("header");
        grid.addColumn(order -> order.getOldDescription() != null ? order.getOldDescription().getName() : "")
                .setHeader("Old Description").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("reelLength").setHeader("Reel Length").setResizable(true).setHeaderPartName("header");
        grid.addColumn(new LocalDateRenderer<>(ProductionOrder::getConversionDate, "dd/MM/yyyy"))
                .setHeader("Conversion Date").setResizable(true).setHeaderPartName("header");
        grid.addColumn(new LocalDateRenderer<>(ProductionOrder::getCustomerDate, "dd/MM/yyyy"))
                .setHeader("Customer Date").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("infosheets").setHeader("Infosheets").setResizable(true).setHeaderPartName("header");
        grid.getColumnByKey("catalog").setHeader("Catalog").setResizable(true).setHeaderPartName("header");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        footerRow = grid.appendFooterRow();

        updateFooter();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);

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
        updateFooter();
    }

    private void addBackgroundColor() {
        grid.setPartNameGenerator(order -> {
            if (order.getColor() != null) {
                String color = order.getColor().toString();
                System.out.println("Production order: " + order.toString() + " color: " + color);
                return color;
            }
            return null;
        });
    }

    private void updateFooter() {
        List<ProductionOrder> orders = service.findAllProductionOrders(filterText.getValue());
        double totalAmount = orders.stream()
                .mapToDouble(ProductionOrder::getAmount)
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

