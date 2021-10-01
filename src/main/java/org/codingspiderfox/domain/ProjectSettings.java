package org.codingspiderfox.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProjectSettings.
 */
@Entity
@Table(name = "project_settings")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "projectsettings")
public class ProjectSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "must_provide_bill_copy_by_default", nullable = false)
    private Boolean mustProvideBillCopyByDefault;

    @OneToOne(optional = false)
    @NotNull
    @MapsId
    @JoinColumn(name = "id")
    private Project project;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectSettings id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getMustProvideBillCopyByDefault() {
        return this.mustProvideBillCopyByDefault;
    }

    public ProjectSettings mustProvideBillCopyByDefault(Boolean mustProvideBillCopyByDefault) {
        this.setMustProvideBillCopyByDefault(mustProvideBillCopyByDefault);
        return this;
    }

    public void setMustProvideBillCopyByDefault(Boolean mustProvideBillCopyByDefault) {
        this.mustProvideBillCopyByDefault = mustProvideBillCopyByDefault;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectSettings project(Project project) {
        this.setProject(project);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectSettings)) {
            return false;
        }
        return id != null && id.equals(((ProjectSettings) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectSettings{" +
            "id=" + getId() +
            ", mustProvideBillCopyByDefault='" + getMustProvideBillCopyByDefault() + "'" +
            "}";
    }
}
