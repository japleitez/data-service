package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A Actions.
 */
@Entity
@Table(name = "action")
public class Action implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @NotNull
    @Column(name = "success", nullable = false)
    private Boolean success;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AcquisitionAction action;

    @OneToMany(mappedBy = "action", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "action" }, allowSetters = true)
    private Set<ActionLog> actionLogs = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "acquisition_id")
    @JsonIgnoreProperties(value = { "configs", "actions", "crawler" }, allowSetters = true)
    private Acquisition acquisition;

    public Action() {
        this.date = Instant.now();
        this.success = false;
        this.action = AcquisitionAction.SUBMITTING;
    }

    public Action(Acquisition acquisition, AcquisitionAction action) {
        this.acquisition = acquisition;
        this.action = action;
        this.date = Instant.now();
        this.success = false;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Action id(Long id) {
        this.id = id;
        return this;
    }

    public Instant getDate() {
        return this.date;
    }

    public Action date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public Action success(Boolean success) {
        this.success = success;
        return this;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public AcquisitionAction getAction() {
        return this.action;
    }

    public Action action(AcquisitionAction action) {
        this.action = action;
        return this;
    }

    public void setAction(AcquisitionAction action) {
        this.action = action;
    }

    public Set<ActionLog> getActionLogs() {
        return this.actionLogs;
    }

    public Action actionLogs(Set<ActionLog> actionLogs) {
        this.setActionLogs(actionLogs);
        return this;
    }

    public Action addActionLogs(ActionLog actionLog) {
        this.actionLogs.add(actionLog);
        actionLog.setAction(this);
        return this;
    }

    public void setActionLogs(Set<ActionLog> actionLogs) {
        if (this.actionLogs != null) {
            this.actionLogs.forEach(i -> i.setAction(null));
        }
        if (actionLogs != null) {
            actionLogs.forEach(i -> i.setAction(this));
        }
        this.actionLogs = actionLogs;
    }

    public Acquisition getAcquisition() {
        return this.acquisition;
    }

    public Action acquisition(Acquisition acquisition) {
        this.setAcquisition(acquisition);
        return this;
    }

    public void setAcquisition(Acquisition acquisition) {
        this.acquisition = acquisition;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Action)) {
            return false;
        }
        return id != null && id.equals(((Action) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Actions{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", success='" + getSuccess() + "'" +
            ", action='" + getAction() + "'" +
            "}";
    }
}
