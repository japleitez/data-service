package eu.europa.ec.eurostat.wihp.service.dto;

import java.util.UUID;

/**
 * A DTO representing a Crawler acquisition
 */
public class CreateAcquisitionDTO {

    String name;
    UUID uuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "AcquisitionDTO{" +
            "name='" + name + '\'' +
            ", uuid=" + uuid +
            '}';
    }

}
