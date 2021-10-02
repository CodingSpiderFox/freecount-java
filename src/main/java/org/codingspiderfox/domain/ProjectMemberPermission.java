package org.codingspiderfox.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.codingspiderfox.domain.enumeration.ProjectMemberPermissionEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProjectMemberPermission.
 */
@Entity
@Table(name = "project_member_permission")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "projectmemberpermission")
public class ProjectMemberPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_timestamp", nullable = false)
    private ZonedDateTime createdTimestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "project_member_permission", nullable = false)
    private ProjectMemberPermissionEnum projectMemberPermission;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectMemberPermission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public ProjectMemberPermission createdTimestamp(ZonedDateTime createdTimestamp) {
        this.setCreatedTimestamp(createdTimestamp);
        return this;
    }

    public void setCreatedTimestamp(ZonedDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public ProjectMemberPermissionEnum getProjectMemberPermission() {
        return this.projectMemberPermission;
    }

    public ProjectMemberPermission projectMemberPermission(ProjectMemberPermissionEnum projectMemberPermission) {
        this.setProjectMemberPermission(projectMemberPermission);
        return this;
    }

    public void setProjectMemberPermission(ProjectMemberPermissionEnum projectMemberPermission) {
        this.projectMemberPermission = projectMemberPermission;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMemberPermission)) {
            return false;
        }
        return id != null && id.equals(((ProjectMemberPermission) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberPermission{" +
            "id=" + getId() +
            ", createdTimestamp='" + getCreatedTimestamp() + "'" +
            ", projectMemberPermission='" + getProjectMemberPermission() + "'" +
            "}";
    }
}
