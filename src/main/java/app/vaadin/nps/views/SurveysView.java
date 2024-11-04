package app.vaadin.nps.views;

import app.vaadin.nps.service.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@Route("")
@PermitAll
@PageTitle("Surveys")
@Menu(title = "Surveys", icon = "vaadin:dashboard")
@RegisterReflectionForBinding(SurveyFormDTO.class)
public class SurveysView extends HorizontalLayout implements HasGlobalAction {

    private final SurveyService surveyService;
    private final TargetListService targetListService;
    private final Button newButton;
    private final Grid<SurveyDTO> list;
    private final SurveyForm form;

    @Override
    public Component getGlobalActionComponent() {
        return newButton;
    }

    public SurveysView(SurveyService surveyService, TargetListService targetListService) {
        this.surveyService = surveyService;
        this.targetListService = targetListService;

        setSizeFull();
        setSpacing(false);

        // Initialize components
        newButton = new Button("New survey");
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        list = createGrid();
        form = new SurveyForm();
        form.setVisible(false);

        // Setup listeners
        newButton.addClickListener(e -> createNewSurvey());
        setupGridSelection();

        add(list, form);
    }

    private Grid<SurveyDTO> createGrid() {
        var grid = new Grid<SurveyDTO>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addColumn(SurveyDTO::name).setHeader("Name");
        grid.addColumn(SurveyDTO::totalResponses).setHeader("Answered");
        grid.addColumn(SurveyDTO::totalSent).setHeader("Sent");
        grid.addColumn(SurveyDTO::npsScore).setHeader("NPS");
        grid.setItems(surveyService.findAll());
        grid.setSizeFull();
        return grid;
    }

    private void setupGridSelection() {
        list.addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isPresent()) {
                form.setSurvey(event.getFirstSelectedItem().get());
                form.setVisible(true);
            } else {
                form.setVisible(false);
            }
        });
    }

    private void createNewSurvey() {
        list.deselectAll();
        form.setVisible(true);
        form.createNew();
    }

    private void refreshGrid() {
        list.setItems(surveyService.findAll());
    }

    class SurveyForm extends VerticalLayout {
        private final Binder<SurveyFormDTO> binder =
            new BeanValidationBinder<>(SurveyFormDTO.class);
        private SurveyDTO currentSurvey;

        H3 npsScore = new H3();
        TextField name = new TextField("Survey name");
        TextArea question = new TextArea("NPS question");
        ComboBox<TargetListDTO> targetList = new ComboBox<>("Target list");
        TextField emailSubject = new TextField("Email subject");
        TextArea emailBody = new TextArea("Email body");
        Button delete = new Button("Delete");
        Button save = new Button("Save");
        Button cancel = new Button("Cancel");
        Button sendEmail = new Button("Send email");

        public SurveyForm() {
            setWidth("40%");
            setMaxWidth("500px");
            setupTargetListComboBox();
            setupButtons();
            setupLayout();
            setupBinder();
        }

        private void setupTargetListComboBox() {
            targetList.setItems(targetListService.findAll());
            targetList.setItemLabelGenerator(TargetListDTO::name);
        }

        private void setupButtons() {
            sendEmail.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            sendEmail.addClickListener(e -> sendEmail());

            save.addClickListener(e -> save());

            cancel.addClickListener(e -> cancel());

            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> delete());
        }

        private void setupLayout() {
            var buttonLayout = new HorizontalLayout();
            buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            buttonLayout.setWidthFull();
            buttonLayout.add(new HorizontalLayout(delete, cancel, save), sendEmail);

            add(
                npsScore,
                name,
                question,
                targetList,
                emailSubject,
                emailBody,
                buttonLayout);

            setAlignItems(Alignment.STRETCH);
        }

        private void setupBinder() {
            binder.bindInstanceFields(this);
            // Special handling for targetList
            binder.forField(targetList)
                .withConverter(
                    targetListDTO -> targetListDTO != null ? targetListDTO.id() : null,
                    targetListId -> targetList.getListDataView().getItems()
                        .filter(dto -> dto.id().equals(targetListId))
                        .findFirst()
                        .orElse(null))
                .bind("targetListId");
        }

        public void setSurvey(SurveyDTO survey) {
            this.currentSurvey = survey;
            npsScore.setVisible(true);
            npsScore.setText("NPS score: " + survey.npsScore());
            delete.setVisible(true);
            sendEmail.setVisible(true);

            targetList.setValue(targetList.getListDataView().getItems()
                .filter(dto -> dto.id().equals(survey.targetListId()))
                .findFirst()
                .orElse(null));

            binder.readBean(survey.toFormDTO());
        }

        public void createNew() {
            currentSurvey = null;
            npsScore.setVisible(false);
            sendEmail.setVisible(false);
            delete.setVisible(false);

            binder.readBean(new SurveyFormDTO("", "", "", "", null));
        }

        private void save() {
            try {
                var formData = binder.writeRecord();
                if (currentSurvey == null) {
                    setSurvey(surveyService.createSurvey(formData));
                } else {
                    setSurvey(surveyService.updateSurvey(currentSurvey.id(), formData));
                }
                refreshGrid();
                Notification.show("Survey saved successfully");
            } catch (Exception e) {
                Notification.show("Failed to save survey: " + e.getMessage());
            }
        }

        private void cancel() {
            setVisible(false);
        }

        private void sendEmail() {
            try {
                save(); // Save any pending changes first
                surveyService.sendSurvey(currentSurvey.id());
                refreshGrid();
                Notification.show("Survey sent successfully");
            } catch (Exception e) {
                Notification.show("Failed to send survey: " + e.getMessage());
            }
        }

        private void delete() {
            try {
                surveyService.deleteSurvey(currentSurvey.id());
                refreshGrid();
                setVisible(false);
            } catch (Exception e) {
                Notification.show("Failed to delete survey: " + e.getMessage());
            }
        }
    }
}
