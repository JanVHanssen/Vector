package org.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.security.PermitAll;
import org.example.model.Customer;
import org.example.model.Description;
import org.example.service.CustomerService;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "customers", layout = MainLayout.class)
@PageTitle("Customers | Vector Hasselt")
public class CustomerView extends VerticalLayout {
    private Grid<Customer> grid = new Grid<>(Customer.class);
    private TextField name = new TextField("Name");
    private TextField email = new TextField("Email");
    private TextField phone = new TextField("Phone");
    private CustomerService customerService;

    public CustomerView(CustomerService customerService) {
        this.customerService = customerService;
        addClassName("customer-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("customer-grid");
        grid.setSizeFull();
        grid.setColumns("name", "email", "phone");

        grid.asSingleSelect().addValueChangeListener(event -> editCustomer(event.getValue()));
    }

    private void configureForm() {
        HorizontalLayout formLayout = new HorizontalLayout(name, email, phone);

        Button save = new Button("Save");
        Button update = new Button("Update");
        Button delete = new Button("Delete");

        save.addClickListener(e -> saveCustomer());
        update.addClickListener(e -> updateCustomer());
        delete.addClickListener(e -> deleteCustomer());

        HorizontalLayout buttonsLayout = new HorizontalLayout(save, update, delete);
        add(formLayout, buttonsLayout);
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        grid.setItems(customerService.findAllCustomers());
    }

    private void addCustomer() {
        grid.asSingleSelect().clear();
        editCustomer(new Customer());
    }

    private void editCustomer(Customer customer) {
        if (customer == null) {
            closeEditor();
        } else {
            name.setValue(customer.getName());
            email.setValue(customer.getEmail());
            phone.setValue(customer.getPhone());
        }
    }

    private void saveCustomer() {
        Customer customer = new Customer();
        customer.setName(name.getValue());
        customer.setEmail(email.getValue());
        customer.setPhone(phone.getValue());
        customerService.saveCustomer(customer);
        updateList();
        closeEditor();
    }

    private void updateCustomer() {
        Customer selectedCustomer = grid.asSingleSelect().getValue();
        if (selectedCustomer != null) {
            selectedCustomer.setName(name.getValue());
            selectedCustomer.setEmail(email.getValue());
            selectedCustomer.setPhone(phone.getValue());
            customerService.saveCustomer(selectedCustomer);
            updateList();
            closeEditor();
        }
    }

    private void deleteCustomer() {
        Customer selectedCustomer = grid.asSingleSelect().getValue();
        if (selectedCustomer != null) {
            customerService.deleteCustomer(selectedCustomer);
            updateList();
            closeEditor();
        }
    }

    private void closeEditor() {
        name.clear();
        email.clear();
        phone.clear();
    }
}