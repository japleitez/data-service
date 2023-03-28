package eu.europa.ec.eurostat.wihp.service.dto;

import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionStatusEnum;
import java.time.Instant;
import java.util.UUID;

public class AcquisitionDTO {

    Long id;
    UUID workflowId;
    String crawlerName;
    Instant startDate;
    Instant lastUpdateDate;
    AcquisitionStatusEnum status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(UUID workflowId) {
        this.workflowId = workflowId;
    }

    public void setCrawlerName(String crawlerName) {
        this.crawlerName = crawlerName;
    }

    public String getCrawlerName() {return crawlerName;}

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Instant lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public AcquisitionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AcquisitionStatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return (
            "AcquisitionDTO{" +
            "id=" +
            id +
            ", workflowId=" +
            workflowId +
            ", crawlerName=" +
            crawlerName +
            ", startDate=" +
            startDate +
            ", lastUpdateDate=" +
            lastUpdateDate +
            ", status=" +
            status +
            '}'
        );
    }
}
