package app.vaadin.nps.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "surveys")
public class Survey extends AbstractEntity {

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "email_body", columnDefinition = "TEXT")
    private String emailBody;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "total_sent")
    private Integer totalSent = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_list_id")
    private TargetList targetList;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SurveyLink> surveyLinks = new HashSet<>();

    // Calculated property
    @Formula("(SELECT COUNT(*) FROM responses r " +
            "JOIN survey_links sl ON r.survey_link_id = sl.id " +
            "WHERE sl.survey_id = id)")
    private Integer totalResponses;

    // Calculated NPS score
    @Formula("(SELECT " +
            "CASE WHEN COUNT(*) > 0 THEN " +
            "((COUNT(CASE WHEN r.score >= 9 THEN 1 END) * 100.0 / COUNT(*)) - " +
            "(COUNT(CASE WHEN r.score <= 6 THEN 1 END) * 100.0 / COUNT(*))) " +
            "ELSE 0 END " +
            "FROM responses r " +
            "JOIN survey_links sl ON r.survey_link_id = sl.id " +
            "WHERE sl.survey_id = id)")
    private Double npsScore;

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getQuestion() {
        return question;
    }

    public void setQuestion(@NotNull String question) {
        this.question = question;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public LocalDateTime getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDateTime sendDate) {
        this.sendDate = sendDate;
    }

    public Integer getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(Integer totalSent) {
        this.totalSent = totalSent;
    }

    public TargetList getTargetList() {
        return targetList;
    }

    public void setTargetList(TargetList targetList) {
        this.targetList = targetList;
    }

    public Set<SurveyLink> getSurveyLinks() {
        return surveyLinks;
    }

    public void setSurveyLinks(Set<SurveyLink> surveyLinks) {
        this.surveyLinks = surveyLinks;
    }

    public Integer getTotalResponses() {
        return totalResponses;
    }

    public void setTotalResponses(Integer totalResponses) {
        this.totalResponses = totalResponses;
    }

    public Double getNpsScore() {
        return npsScore;
    }

    public void setNpsScore(Double npsScore) {
        this.npsScore = npsScore;
    }
}