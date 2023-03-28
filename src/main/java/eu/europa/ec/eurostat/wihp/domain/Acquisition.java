package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionStatusEnum;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A Acquisition.
 */
@Entity
@Table(name = "acquisition")
public class Acquisition implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String UNDERSCORE = "_";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "workflow_id", nullable = false)
    private UUID workflowId;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AcquisitionStatusEnum status;

    @Column(name = "storm_id")
    private String stormId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquisition_id")
    @JsonIgnoreProperties(value = { "acquisition" }, allowSetters = true)
    private Set<Config> configs = new HashSet<>();

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "acquisition_id")
    @JsonIgnoreProperties(value = { "actionLogs", "acquisition" }, allowSetters = true)
    private Set<Action> actions = new HashSet<>();

    @OneToMany(mappedBy = "acquisition")
    @JsonIgnoreProperties(value = { "acquisition" }, allowSetters = true)
    private Set<Report> reports = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "crawler_id", nullable = false)
    @JsonIgnoreProperties(value = { "parserFilters", "acquisitions", "sources" }, allowSetters = true)
    private Crawler crawler;

    public Acquisition() {}

    public Acquisition(final UUID workflowId, final Crawler crawler) {
        this.workflowId = workflowId;
        this.crawler = crawler;
        this.startDate = Instant.now();
        this.lastUpdateDate = Instant.now();
        this.status = AcquisitionStatusEnum.PROVISIONING;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Acquisition id(Long id) {
        this.id = id;
        return this;
    }

    public UUID getWorkflowId() {
        return this.workflowId;
    }

    public Acquisition workflowId(UUID workflowId) {
        this.workflowId = workflowId;
        return this;
    }

    public void setWorkflowId(UUID workflowId) {
        this.workflowId = workflowId;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Acquisition startDate(Instant startDate) {
        this.startDate = startDate;
        return this;
    }

    public Instant getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public Acquisition lastUpdateDate(Instant lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        return this;
    }

    public void setLastUpdateDate(Instant lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public AcquisitionStatusEnum getStatus() {
        return this.status;
    }

    public Acquisition status(AcquisitionStatusEnum status) {
        this.status = status;
        return this;
    }

    public void updateStatus(AcquisitionStatusEnum status) {
        this.status = status;
        this.lastUpdateDate = Instant.now();
    }

    public String getStormId() {
        return this.stormId;
    }

    public Acquisition stormId(String stormId) {
        this.stormId = stormId;
        return this;
    }

    public void setStormId(String stormId) {
        this.stormId = stormId;
    }

    public Set<Config> getConfigs() {
        return this.configs;
    }

    public Acquisition configs(Set<Config> configs) {
        this.setConfigs(configs);
        return this;
    }

    public Acquisition addConfig(Config config) {
        this.configs.add(config);
        config.setAcquisition(this);
        return this;
    }

    public Acquisition removeConfig(Config config) {
        this.configs.remove(config);
        config.setAcquisition(null);
        return this;
    }

    public void setConfigs(Set<Config> configs) {
        if (this.configs != null) {
            this.configs.forEach(i -> i.setAcquisition(null));
        }
        if (configs != null) {
            configs.forEach(i -> i.setAcquisition(this));
        }
        this.configs = configs;
    }

    public Set<Action> getActions() {
        return this.actions;
    }

    public Acquisition actions(Set<Action> actions) {
        this.setActions(actions);
        return this;
    }

    public Acquisition addAction(Action action) {
        this.actions.add(action);
        action.setAcquisition(this);
        return this;
    }

    public Acquisition removeAction(Action action) {
        this.actions.remove(action);
        action.setAcquisition(null);
        return this;
    }

    public void setActions(Set<Action> actions) {
        if (this.actions != null) {
            this.actions.forEach(i -> i.setAcquisition(null));
        }
        if (actions != null) {
            actions.forEach(i -> i.setAcquisition(this));
        }
        this.actions = actions;
    }

    public Set<Report> getReports() {
        return this.reports;
    }

    public Acquisition reports(Set<Report> reports) {
        this.setReports(reports);
        return this;
    }

    public Acquisition addReport(Report report) {
        this.reports.add(report);
        report.setAcquisition(this);
        return this;
    }

    public Acquisition removeReport(Report report) {
        this.reports.remove(report);
        report.setAcquisition(null);
        return this;
    }

    public void setReports(Set<Report> reports) {
        if (this.reports != null) {
            this.reports.forEach(i -> i.setAcquisition(null));
        }
        if (reports != null) {
            reports.forEach(i -> i.setAcquisition(this));
        }
        this.reports = reports;
    }

    public Crawler getCrawler() {
        return this.crawler;
    }

    public Acquisition crawler(Crawler crawler) {
        this.setCrawler(crawler);
        return this;
    }

    public void setCrawler(Crawler crawler) {
        this.crawler = crawler;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Acquisition)) {
            return false;
        }
        return id != null && id.equals(((Acquisition) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Acquisition{" +
            "id=" + getId() +
            ", workflowId='" + getWorkflowId() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", lastUpdateDate='" + getLastUpdateDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", stormId='" + getStormId() + "'" +
            "}";
    }

    public String getTopologyName() {
        Objects.requireNonNull(this.id);
        Objects.requireNonNull(this.workflowId);
        Objects.requireNonNull(this.crawler);
        Objects.requireNonNull(this.crawler.getName());

        return this.crawler.getName().concat(UNDERSCORE).concat(this.workflowId.toString()).concat(UNDERSCORE).concat(this.id.toString());
    }
}
