package app.vaadin.nps.service;

import app.vaadin.nps.model.Survey;

public record SurveyFormDTO(
        String name,
        String question,
        String emailSubject,
        String emailBody,
        Long targetListId
) {

    public Survey toEntity() {
        Survey survey = new Survey();
        survey.setName(this.name);
        survey.setQuestion(this.question);
        survey.setEmailSubject(this.emailSubject);
        survey.setEmailBody(this.emailBody);
        return survey;
    }
}