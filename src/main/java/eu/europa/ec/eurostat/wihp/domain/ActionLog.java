package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A ActionLogs.
 */
@Entity
@Table(name = "action_log")
public class ActionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "log_text", nullable = false)
    private String logText;

    public ActionLog() {}

    public ActionLog(Action action, String title, String logText) {
        this.action = action;
        this.title = title;
        this.logText = logText;
    }

    @ManyToOne
    @JsonIgnoreProperties(value = { "actionLogs", "acquisition" }, allowSetters = true)
    private Action action;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActionLog id(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public ActionLog title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogText() {
        return this.logText;
    }

    public ActionLog logText(String logText) {
        this.logText = logText;
        return this;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public Action getAction() {
        return this.action;
    }

    public ActionLog action(Action action) {
        this.setAction(action);
        return this;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionLog)) {
            return false;
        }
        return id != null && id.equals(((ActionLog) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActionLogs{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", logText='" + getLogText() + "'" +
            "}";
    }
}
