package org.codingspiderfox.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Bill.
 */
@Entity
@Table(name = "bill")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "closed_timestamp")
    private ZonedDateTime closedTimestamp;

    @Column(name = "final_amount")
    private Double finalAmount;

    @ManyToOne(optional = false)
    @NotNull
    private Project project;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Bill id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Bill title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ZonedDateTime getClosedTimestamp() {
        return this.closedTimestamp;
    }

    public Bill closedTimestamp(ZonedDateTime closedTimestamp) {
        this.setClosedTimestamp(closedTimestamp);
        return this;
    }

    public void setClosedTimestamp(ZonedDateTime closedTimestamp) {
        this.closedTimestamp = closedTimestamp;
    }

    public Double getFinalAmount() {
        return this.finalAmount;
    }

    public Bill finalAmount(Double finalAmount) {
        this.setFinalAmount(finalAmount);
        return this;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Bill project(Project project) {
        this.setProject(project);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bill)) {
            return false;
        }
        return id != null && id.equals(((Bill) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bill{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", closedTimestamp='" + getClosedTimestamp() + "'" +
            ", finalAmount=" + getFinalAmount() +
            "}";
    }
}
