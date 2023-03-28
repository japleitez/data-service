package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Source.
 */
@Entity
@Table(name = "source")
public class Source implements Serializable {

    public static final String NAME_REGEX = "^(?=.*[a-zA-Z\\d].*)[a-zA-Z\\d _.]{1,}$";
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    @Pattern(regexp = NAME_REGEX)
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @NotNull
    @URL
    @Size(min = 1)
    @Column(name = "url", nullable = false)
    private String url;

    @ManyToMany(mappedBy = "sources",fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sources" }, allowSetters = true)
    private Set<Crawler> crawlers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Source id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Source name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public Source url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<Crawler> getCrawlers() {
        return this.crawlers;
    }

    public Source crawlers(Set<Crawler> crawlers) {
        this.setCrawlers(crawlers);
        return this;
    }

    public void setCrawlers(Set<Crawler> crawlers) {
        if (this.crawlers != null) {
            this.crawlers.forEach(i -> i.removeSource(this));
        }
        if (crawlers != null) {
            crawlers.forEach(i -> i.addSource(this));
        }
        this.crawlers = crawlers;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Source)) {
            return false;
        }
        return id != null && id.equals(((Source) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Source{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
