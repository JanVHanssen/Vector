package org.example.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.example.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@SpringComponent
@UIScope
public class ProductionForm extends FormLayout {
    private TextField sequence = new TextField("Sequence");
    private TextField sevenNumber = new TextField("Seven Number");
    private TextField fourNumber = new TextField("Four Number");
    private ComboBox<Description> description = new ComboBox<>("Description");
    private TextField amount = new TextField("Amount");
    private TextField salesOrder = new TextField("Sales Order");
    private TextField soLine = new TextField("SO Line");
    private DatePicker requestedDate = new DatePicker("Requested date");
    private DatePicker promisedDate = new DatePicker("Promised date");
    private ComboBox<Customer> customer = new ComboBox<>("Customer");
    private ComboBox<MLOption> ml = new ComboBox<>("ML");
    private ComboBox<OldDescription> oldDescription = new ComboBox<>("Old Description");
    private TextField reelLength = new TextField("Reel Length");
    private DatePicker conversionDate = new DatePicker("Conversion date");
    private DatePicker customerDate = new DatePicker("Customer date");
    private ComboBox<InfosheetsOption> infosheets = new ComboBox<>("Infosheets");
    private TextField catalog = new TextField("Catalog");
    ComboBox<ProductionOrder> productionOrder = new ComboBox<>("Production Order");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<ProductionOrder> binder = new BeanValidationBinder<>(ProductionOrder.class);

    @Autowired
    public ProductionForm(List<ProductionOrder> productionOrders, List<Customer> customers,
                          List<Description> descriptions, List<OldDescription> oldDescriptions) {
        addClassName("production-form");
        binder.bindInstanceFields(this);

        description.setItems(descriptions);
        description.setItemLabelGenerator(Description::getName);
        add(description);

        oldDescription.setItems(oldDescriptions);
        oldDescription.setItemLabelGenerator(OldDescription::getName);
        add(oldDescription);

        // Initialize the ComboBox with the list of customers
        customer.setItems(customers);
        customer.setItemLabelGenerator(Customer::getName);
        add(customer);

        ml.setItems(Arrays.asList(MLOption.values())); // Set enum values as items
        ml.setLabel("ML"); // Set label for the ComboBox
        ml.setItemLabelGenerator(MLOption::toString); // Use the enum's toString method for display
        add(ml);

        infosheets.setItems(Arrays.asList(InfosheetsOption.values())); // Set enum values as items
        infosheets.setLabel("Infosheets"); // Set label for the ComboBox
        infosheets.setItemLabelGenerator(InfosheetsOption::toString); // Use the enum's toString method for display
        add(infosheets);

        productionOrder.setItems(productionOrders);
        productionOrder.setItemLabelGenerator(order -> String.valueOf(order.getSevenNumber()));

        add(sequence,
                sevenNumber,
                fourNumber,
                description,
                amount,
                salesOrder,
                soLine,
                requestedDate,
                promisedDate,
                customer,
                ml,
                oldDescription,
                reelLength,
                conversionDate,
                customerDate,
                infosheets,
                catalog,
                createButtonsLayout());

        binder.forField(description).bind(ProductionOrder::getDescription, ProductionOrder::setDescription);
        binder.forField(customer).bind(ProductionOrder::getCustomer, ProductionOrder::setCustomer);
        binder.forField(oldDescription).bind(ProductionOrder::getOldDescription, ProductionOrder::setOldDescription);
    }
    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new ProductionForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new ProductionForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new ProductionForm.SaveEvent(this, binder.getBean()));
        }
    }

    public void setProductionOrder(ProductionOrder productionOrder) {
        binder.setBean(productionOrder);
    }

    // Events
    public static abstract class ProductionFormEvent extends ComponentEvent<ProductionForm> {
        private ProductionOrder productionOrder;

        protected ProductionFormEvent(ProductionForm source, ProductionOrder productionOrder) {
            super(source, false);
            this.productionOrder = productionOrder;
        }

        public ProductionOrder getProductionOrder() {
            return productionOrder;
        }
    }

    public static class SaveEvent extends ProductionForm.ProductionFormEvent {
        SaveEvent(ProductionForm source, ProductionOrder productionOrder) {
            super(source, productionOrder);
        }
    }

    public static class DeleteEvent extends ProductionForm.ProductionFormEvent {
        DeleteEvent(ProductionForm source, ProductionOrder productionOrder) {
            super(source, productionOrder);
        }

    }

    public static class CloseEvent extends ProductionForm.ProductionFormEvent {
        CloseEvent(ProductionForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<ProductionForm.DeleteEvent> listener) {
        return addListener(ProductionForm.DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<ProductionForm.SaveEvent> listener) {
        return addListener(ProductionForm.SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<ProductionForm.CloseEvent> listener) {
        return addListener(ProductionForm.CloseEvent.class, listener);
    }
}