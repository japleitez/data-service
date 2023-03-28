package eu.europa.ec.eurostat.wihp.service.dto;

import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;

/**
 * A DTO for the {@link Action} entity.
 */
public class ActionDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant date;

    @NotNull
    private Boolean success;

    @NotNull
    private AcquisitionAction action;

    private Set<ActionLog> actionLogs = new HashSet<>();

    private Long acquisitionId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public AcquisitionAction getAction() {
        return action;
    }

    public void setAction(AcquisitionAction action) {
        this.action = action;
    }

    public Set<ActionLog> getActionLogs() {return actionLogs;}

    public void setActionLogs(Set<ActionLog> actionLogs) {this.actionLogs = actionLogs;}

    public Long getAcquisitionId() {
        return acquisitionId;
    }

    public void setAcquisitionId(Long acquisitionId) {
        this.acquisitionId = acquisitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionDTO)) {
            return false;
        }

        ActionDTO actionDTO = (ActionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, actionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "ActionDTO{" +
            "id=" +
            id +
            ", date=" +
            date +
            ", success=" +
            success +
            ", action=" +
            action +
            ", acquisitionId=" +
            acquisitionId +
            '}'
        );
    }
}
