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
import org.example.model.OldDescription;
import org.example.service.DescriptionService;
import org.example.service.OldDescriptionService;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope("prototype")
@PermitAll
@Route(value = "descriptions", layout = MainLayout.class)
@PageTitle("Descriptions | Vector Hasselt")
public class DescriptionView extends VerticalLayout {
    private Grid<Description> grid = new Grid<>(Description.class);
    private TextField name = new TextField("Name");
    private DescriptionService descriptionService;

    public DescriptionView(DescriptionService descriptionService) {
        this.descriptionService = descriptionService;
        addClassName("description-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("description-grid");
        grid.setSizeFull();
        grid.setColumns("name");

        grid.asSingleSelect().addValueChangeListener(event -> editDescription(event.getValue()));
    }

    private void configureForm() {
        HorizontalLayout formLayout = new HorizontalLayout(name);
        Button save = new Button("Save");
        Button update = new Button("Update");
        Button delete = new Button("Delete");

        save.addClickListener(e -> saveDescription());
        update.addClickListener(e -> updateDescription());
        delete.addClickListener(e -> deleteDescription());

        HorizontalLayout buttonsLayout = new HorizontalLayout(save, update, delete);
        add(formLayout, buttonsLayout);
    }
    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        grid.setItems(descriptionService.findAllDescriptions());
    }


    private void editDescription(Description description) {
        if (description == null) {
            closeEditor();
        } else {
            name.setValue(description.getName());
        }
    }

    private void saveDescription() {
        Description description = new Description();
        description.setName(name.getValue());
        descriptionService.saveDescription(description);
        updateList();
        closeEditor();
    }

    private void updateDescription() {
        Description selectedDescription = grid.asSingleSelect().getValue();
        if (selectedDescription != null) {
            selectedDescription.setName(name.getValue());
            descriptionService.saveDescription(selectedDescription);
            updateList();
            closeEditor();
        }
    }

    private void deleteDescription() {
        Description selectedDescription = grid.asSingleSelect().getValue();
        if (selectedDescription != null) {
            descriptionService.deleteDescription(selectedDescription);
            updateList();
            closeEditor();
        }
    }

    private void closeEditor() {
        name.clear();
    }
}