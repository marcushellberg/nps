package app.vaadin.nps.service;

import app.vaadin.nps.model.SurveyLink;

public record SurveyQuestionDTO(
    String question,
    boolean alreadyAnswered
) {
    public static SurveyQuestionDTO fromSurveyLink(SurveyLink link) {
        return new SurveyQuestionDTO(
            link.getSurvey().getQuestion(),
            link.getResponse() != null
        );
    }
}