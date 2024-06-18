package org.example.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.security.PermitAll;
import org.example.model.OldDescription;
import org.example.service.OldDescriptionService;
import org.springframework.context.annotation.Scope;

@PermitAll
@Route(value = "oldDescriptions", layout = MainLayout.class)
@PageTitle("Old Descriptions | Vector Hasselt")
public class OldDescriptionView extends VerticalLayout {
    private Grid<OldDescription> grid = new Grid<>(OldDescription.class);
    private TextField name = new TextField("Name");
    private OldDescriptionService oldDescriptionService;
    private OldDescription currentOldDescription;

    public OldDescriptionView(OldDescriptionService oldDescriptionService) {
        this.oldDescriptionService = oldDescriptionService;
        addClassName("old-description-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("old-description-grid");
        grid.setSizeFull();
        grid.setColumns("name");

        grid.asSingleSelect().addValueChangeListener(event -> editOldDescription(event.getValue()));
    }

    private void configureForm() {
        HorizontalLayout formLayout = new HorizontalLayout(name);
        Button save = new Button("Save");
        Button update = new Button("Update");
        Button delete = new Button("Delete");
        Button cancel = new Button("Cancel");

        save.addClickListener(e -> saveOldDescription());
        update.addClickListener(e -> updateOldDescription());
        delete.addClickListener(e -> deleteOldDescription());
        cancel.addClickListener(e -> closeEditor());

        HorizontalLayout buttonsLayout = new HorizontalLayout(save, update, delete, cancel);
        add(formLayout, buttonsLayout);
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        grid.setItems(oldDescriptionService.findAllOldDescriptions());
    }

    private void editOldDescription(OldDescription oldDescription) {
        if (oldDescription == null) {
            closeEditor();
        } else {
            currentOldDescription = oldDescription;
            name.setValue(oldDescription.getName());
        }
    }

    private void saveOldDescription() {
        if (currentOldDescription == null) {
            currentOldDescription = new OldDescription();
        }
        currentOldDescription.setName(name.getValue());
        try {
            oldDescriptionService.saveOldDescription(currentOldDescription);
            updateList();
            closeEditor();
        } catch (IllegalArgumentException e) {
            Notification.show("Old name already exists", 3000, Notification.Position.MIDDLE);
        }
    }

    private void updateOldDescription() {
        if (currentOldDescription != null) {
            currentOldDescription.setName(name.getValue());
            try {
                oldDescriptionService.saveOldDescription(currentOldDescription);
                updateList();
                closeEditor();
            } catch (IllegalArgumentException e) {
                Notification.show("Old name already exists", 3000, Notification.Position.MIDDLE);
            }
        }
    }

    private void deleteOldDescription() {
        OldDescription selectedOldDescription = grid.asSingleSelect().getValue();
        if (selectedOldDescription != null) {
            try {
                oldDescriptionService.deleteOldDescription(selectedOldDescription);
                updateList();
                closeEditor();
            } catch (IllegalStateException e) {
                Notification.show("Cannot delete old description with existing orders", 3000, Notification.Position.MIDDLE);
            }
        }
    }

    private void closeEditor() {
        name.clear();
        currentOldDescription = null;
    }
}