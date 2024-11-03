package app.vaadin.nps.views;

import app.vaadin.nps.service.TargetListDTO;
import app.vaadin.nps.service.TargetListFormDTO;
import app.vaadin.nps.service.TargetListService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("targets")
@PageTitle("Targets")
@PermitAll
@Menu(title = "Targets", icon = "vaadin:users")
public class TargetListsView extends HorizontalLayout implements HasGlobalAction {

    private final TargetListService service;
    private final Button newButton;
    private final Grid<TargetListDTO> list;
    private final TargetListForm form;

    @Override
    public Component getGlobalActionComponent() {
        return newButton;
    }

    public TargetListsView(TargetListService service) {
        this.service = service;

        setSizeFull();
        setSpacing(false);

        newButton = new Button("New target list");
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        list = createGrid();
        form = new TargetListForm();
        form.setVisible(false);

        newButton.addClickListener(e -> createNew());
        setupGridSelection();

        add(list, form);
    }

    private Grid<TargetListDTO> createGrid() {
        var grid = new Grid<TargetListDTO>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addColumn(TargetListDTO::name).setHeader("Name");
        grid.addColumn(TargetListDTO::emailCount).setHeader("Emails");
        grid.setItems(service.findAll());
        grid.setSizeFull();
        return grid;
    }

    private void setupGridSelection() {
        list.addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isPresent()) {
                form.setTargetList(event.getFirstSelectedItem().get());
                form.setVisible(true);
            } else {
                form.setVisible(false);
            }
        });
    }

    private void createNew() {
        list.deselectAll();
        form.setVisible(true);
        form.createNew();
    }

    private void refreshGrid() {
        list.setItems(service.findAll());
    }

    class TargetListForm extends VerticalLayout {
        private final Binder<TargetListFormDTO> binder = new BeanValidationBinder<>(TargetListFormDTO.class);
        private TargetListDTO currentTargetList;
        private MemoryBuffer fileBuffer;
        private Upload upload;

        TextField name = new TextField("Name");
        Button delete = new Button("Delete");
        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        public TargetListForm() {
            setWidth("40%");
            setMaxWidth("500px");
            setupUpload();
            setupLayout();
            setupButtons();
            setupBinder();
        }

        private void setupUpload() {
            fileBuffer = new MemoryBuffer();
            upload = new Upload(fileBuffer);
            upload.setAcceptedFileTypes(".txt", "text/plain");
            upload.setDropLabel(new Span("File with one email address per line"));

            // Clear upload when successful
            upload.addSucceededListener(event -> {
                Notification.show("File uploaded: " + event.getFileName());
            });

            upload.addFailedListener(event -> {
                Notification.show("Upload failed: " + event.getReason());
            });
        }

        private void setupLayout() {
            var buttonLayout = new HorizontalLayout();
            buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            buttonLayout.setWidthFull();

            var leftButtons = new HorizontalLayout(delete);
            var rightButtons = new HorizontalLayout(cancel, save);
            buttonLayout.add(leftButtons, rightButtons);

            add(
                name,
                upload,
                buttonLayout
            );

            setAlignItems(Alignment.STRETCH);
        }

        private void setupButtons() {
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            save.addClickListener(e -> save());

            cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            cancel.addClickListener(e -> cancel());

            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> delete());
        }

        private void setupBinder() {
            binder.bindInstanceFields(this);
        }

        public void setTargetList(TargetListDTO targetList) {
            this.currentTargetList = targetList;
            delete.setVisible(true);
            binder.readBean(TargetListFormDTO.fromDTO(targetList));
        }

        public void createNew() {
            this.currentTargetList = null;
            delete.setVisible(false);
            upload.clearFileList();
            binder.readBean(new TargetListFormDTO(null, ""));
        }

        private void save() {
            try {
                var formData = new TargetListFormDTO(
                    currentTargetList != null ? currentTargetList.id() : null,
                    name.getValue()
                );

                if (fileBuffer.getInputStream().available() == 0) {
                    throw new IllegalStateException("Please upload a CSV file with email addresses");
                }

                if (currentTargetList == null) {
                    service.create(formData, fileBuffer.getInputStream());
                } else {
                    service.update(currentTargetList.id(), formData, fileBuffer.getInputStream());
                }

                refreshGrid();
                setVisible(false);
                upload.clearFileList();
                Notification.show("Target list saved successfully");
            } catch (Exception e) {
                Notification.show("Failed to save target list: " + e.getMessage());
            }
        }

        private void cancel() {
            setVisible(false);
        }

        private void delete() {
            try {
                service.delete(currentTargetList.id());
                refreshGrid();
                setVisible(false);
                Notification.show("Target list deleted");
            } catch (Exception e) {
                Notification.show("Failed to delete target list: " + e.getMessage());
            }
        }
    }
}