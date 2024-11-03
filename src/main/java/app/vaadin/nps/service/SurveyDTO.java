package app.vaadin.nps.service;

import app.vaadin.nps.model.Survey;

import java.time.LocalDateTime;

public record SurveyDTO(
        Long id,
        String name,
        String question,
        String emailSubject,
        String emailBody,
        LocalDateTime sendDate,
        Integer totalSent,
        Integer totalResponses,
        Long targetListId,
        String targetListName,
        Double npsScore
) {

    public static SurveyDTO fromEntity(Survey survey) {
        return new SurveyDTO(
                survey.getId(),
                survey.getName(),
                survey.getQuestion(),
                survey.getEmailSubject(),
                survey.getEmailBody(),
                survey.getSendDate(),
                survey.getTotalSent(),
                survey.getTotalResponses(),
                survey.getTargetList() != null ? survey.getTargetList().getId() : null,
                survey.getTargetList() != null ? survey.getTargetList().getName() : null,
                survey.getNpsScore()
        );
    }

    /**
     * Converts this DTO to the form DTO for editing
     */
    public SurveyFormDTO toFormDTO() {
        return new SurveyFormDTO(
                name,
                question,
                emailSubject,
                emailBody,
                targetListId
        );
    }
}