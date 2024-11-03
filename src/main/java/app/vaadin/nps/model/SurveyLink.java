package app.vaadin.nps.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "survey_links",
        indexes = {
                @Index(name = "idx_survey_link_token", columnList = "token"),
                @Index(name = "idx_survey_link_email", columnList = "email_address")
        })
public class SurveyLink extends AbstractEntity {

    @NotNull
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @NotNull
    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @OneToOne(mappedBy = "surveyLink", cascade = CascadeType.ALL, orphanRemoval = true)
    private Response response;

    public @NotNull String getToken() {
        return token;
    }

    public void setToken(@NotNull String token) {
        this.token = token;
    }

    public @NotNull String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(@NotNull String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @PrePersist
    protected void onCreate() {
        if (token == null) {
            token = generateUniqueToken();
        }
    }

    private String generateUniqueToken() {
        return UUID.randomUUID().toString();
    }
}