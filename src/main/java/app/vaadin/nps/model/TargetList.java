package app.vaadin.nps.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "target_lists")
public class TargetList extends AbstractEntity {

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    // For large email lists, we store them in a separate table
    @OneToMany(mappedBy = "targetList", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TargetEmail> emails = new HashSet<>();

    @OneToMany(mappedBy = "targetList")
    private Set<Survey> surveys = new HashSet<>();

    @Formula("(SELECT COUNT(*) FROM target_emails te WHERE te.target_list_id = id)")
    private Integer emailCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TargetEmail> getEmails() {
        return emails;
    }

    public void setEmails(Set<TargetEmail> emails) {
        this.emails = emails;
    }

    public Set<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(Set<Survey> surveys) {
        this.surveys = surveys;
    }

    public Integer getEmailCount() {
        return emailCount;
    }

    public void setEmailCount(Integer emailCount) {
        this.emailCount = emailCount;
    }
}