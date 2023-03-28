package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A Report.
 */
@Entity
@Table(name = "report")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Type(type = "json")
    @Column(name = "content", columnDefinition = "jsonb", nullable = false)
    private JsonNode content;

    @ManyToOne
    @JsonIgnoreProperties(value = {"configs", "actions", "reports", "crawler"}, allowSetters = true)
    private Acquisition acquisition;

    public Report() {
    }

    public Report(Acquisition acquisition, JsonNode content) {
        this.acquisition = acquisition;
        this.content = content;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Report id(Long id) {
        this.id = id;
        return this;
    }

    public JsonNode getContent() {
        return this.content;
    }

    public Report content(JsonNode content) {
        this.content = content;
        return this;
    }

    public void setContent(JsonNode content) {
        this.content = content;
    }

    public Acquisition getAcquisition() {
        return this.acquisition;
    }

    public Report acquisition(Acquisition acquisition) {
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
        if (!(o instanceof Report)) {
            return false;
        }
        return id != null && id.equals(((Report) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            "}";
    }
}
