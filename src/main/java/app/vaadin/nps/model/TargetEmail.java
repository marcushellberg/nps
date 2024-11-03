package app.vaadin.nps.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "target_emails",
        indexes = @Index(name = "idx_target_email_address", columnList = "email_address"))
public class TargetEmail extends AbstractEntity {

    @NotNull
    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_list_id")
    private TargetList targetList;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public TargetList getTargetList() {
        return targetList;
    }

    public void setTargetList(TargetList targetList) {
        this.targetList = targetList;
    }
}