package eu.europa.ec.eurostat.wihp.service.dto;

import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionStatusEnum;

public class StormReportDTO {

    private AcquisitionStatusEnum status;
    private String stormId;
    private String timestamp;

    public AcquisitionStatusEnum getStatus() {
        return status;
    }

    public StormReportDTO status(AcquisitionStatusEnum status) {
        this.status = status;
        return this;
    }

    public void setStatus(AcquisitionStatusEnum status) {
        this.status = status;
    }

    public String getStormId() {
        return stormId;
    }

    public StormReportDTO stormId(String stormId) {
        this.stormId = stormId;
        return this;
    }

    public void setStormId(String stormId) {
        this.stormId = stormId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public StormReportDTO timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "StormReport{" + "status=" + status + ", stormId='" + stormId + ", timestamp=" + timestamp + '}';
    }

    public String toJson() {
        return "{" + "status: \"" + status + "\"" + "storm_id: \"" + stormId + "\"" + "timestamp: \"" + timestamp + "\"" + '}';
    }
}
