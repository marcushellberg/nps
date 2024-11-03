package app.vaadin.nps.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "responses")
public class Response extends AbstractEntity {

    @NotNull
    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_link_id", unique = true)
    private SurveyLink surveyLink;

    public @NotNull Integer getScore() {
        return score;
    }

    public void setScore(@NotNull Integer score) {
        this.score = score;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public SurveyLink getSurveyLink() {
        return surveyLink;
    }

    public void setSurveyLink(SurveyLink surveyLink) {
        this.surveyLink = surveyLink;
    }
}