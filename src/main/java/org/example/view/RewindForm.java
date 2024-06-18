package org.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.ButtonVariant;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.example.model.*;

import java.util.Arrays;
import java.util.List;

@SpringComponent
@UIScope
public class RewindForm extends FormLayout {

    private TextField sevenNumber = new TextField("Seven Number");
    private TextField fourNumber = new TextField("Four Number");
    private ComboBox<Customer> customer = new ComboBox<>("Customer");
    private ComboBox<Description> description = new ComboBox<>("Description");
    private ComboBox<OldDescription> oldDescription = new ComboBox<>("Old Description");
    private TextField amount = new TextField("Amount");
    private TextField rack = new TextField("Rack");
    private DatePicker date = new DatePicker("Date");
    private ComboBox<BoxOption> box = new ComboBox<>("Box", Arrays.asList(BoxOption.values()));
    private ComboBox<RewinderOption> rewindComboBox = new ComboBox<>("Rewinder", Arrays.asList(RewinderOption.values()));
    private ComboBox<ColorOption> color = new ComboBox<>("Color");
    ComboBox<RewindOrder> rewindOrder = new ComboBox<>("Rewind Order");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<RewindOrder> binder = new BeanValidationBinder<>(RewindOrder.class);
    public RewindForm(List<RewindOrder> rewindOrders, List<Customer> customers, List<Description> descriptions, List<OldDescription> oldDescriptions) {
        addClassName("rewind-form");
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

        color.setItems(Arrays.asList(ColorOption.values()));
        color.setLabel("Color");
        color.setItemLabelGenerator(ColorOption::toString);
        add(color);

        binder.forField(rewindComboBox)
                .bind(RewindOrder::getRewinder, RewindOrder::setRewinder);
        rewindOrder.setItems(rewindOrders);
        rewindOrder.setItemLabelGenerator(order -> String.valueOf(order.getSevenNumber()));

        add(sevenNumber,
                fourNumber,
                customer,
                description,
                oldDescription,
                amount,
                rack,
                box,
                date,
                rewindComboBox,
                color,
                createButtonsLayout());

        // Bind the customer field to the ProductionOrder
        binder.forField(customer).bind(RewindOrder::getCustomer, RewindOrder::setCustomer);
        binder.forField(description).bind(RewindOrder::getDescription, RewindOrder::setDescription);
        binder.forField(oldDescription).bind(RewindOrder::getOldDescription, RewindOrder::setOldDescription);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave()); // <1>
        delete.addClickListener(event -> fireEvent(new RewindForm.DeleteEvent(this, binder.getBean()))); // <2>
        close.addClickListener(event -> fireEvent(new RewindForm.CloseEvent(this))); // <3>

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); // <4>
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new RewindForm.SaveEvent(this, binder.getBean())); // <6>
        }
    }


    public void setRewindOrder(RewindOrder rewindOrder) {
        binder.setBean(rewindOrder); // <1>
    }

    // Events
    public static abstract class RewindFormEvent extends ComponentEvent<RewindForm> {
        private RewindOrder rewindOrder;

        protected RewindFormEvent(RewindForm source, RewindOrder rewindOrder) {
            super(source, false);
            this.rewindOrder = rewindOrder;
        }

        public RewindOrder getRewindOrder() {
            return rewindOrder;
        }
    }

    public static class SaveEvent extends RewindForm.RewindFormEvent {
        SaveEvent(RewindForm source, RewindOrder rewindOrder) {
            super(source, rewindOrder);
        }
    }

    public static class DeleteEvent extends RewindForm.RewindFormEvent {
        DeleteEvent(RewindForm source, RewindOrder rewindOrder) {
            super(source, rewindOrder);
        }

    }

    public static class CloseEvent extends RewindForm.RewindFormEvent {
        CloseEvent(RewindForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<RewindForm.DeleteEvent> listener) {
        return addListener(RewindForm.DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<RewindForm.SaveEvent> listener) {
        return addListener(RewindForm.SaveEvent.class, listener);
    }
    public Registration addCloseListener(ComponentEventListener<RewindForm.CloseEvent> listener) {
        return addListener(RewindForm.CloseEvent.class, listener);
    }

}
