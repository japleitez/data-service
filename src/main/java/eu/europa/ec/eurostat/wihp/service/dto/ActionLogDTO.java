package eu.europa.ec.eurostat.wihp.service.dto;

import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * A DTO for the {@link ActionLog} entity.
 */
public class ActionLogDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String logText;

    private Long actionId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionLogDTO)) {
            return false;
        }

        ActionLogDTO actionLogDTO = (ActionLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, actionLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActionLogsDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", logText='" + getLogText() + "'" +
            ", actionId=" + getActionId() +
            "}";
    }
}
