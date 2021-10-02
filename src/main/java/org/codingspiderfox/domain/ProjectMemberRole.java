package org.codingspiderfox.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.codingspiderfox.domain.enumeration.ProjectMemberRoleEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProjectMemberRole.
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "project_member_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "projectmemberrole")
public class ProjectMemberRole implements Serializable {

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
    @Column(name = "project_member_role", nullable = false)
    private ProjectMemberRoleEnum projectMemberRole;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectMemberRole id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public ProjectMemberRole createdTimestamp(ZonedDateTime createdTimestamp) {
        this.setCreatedTimestamp(createdTimestamp);
        return this;
    }

    public void setCreatedTimestamp(ZonedDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public ProjectMemberRoleEnum getProjectMemberRole() {
        return this.projectMemberRole;
    }

    public ProjectMemberRole projectMemberRole(ProjectMemberRoleEnum projectMemberRole) {
        this.setProjectMemberRole(projectMemberRole);
        return this;
    }

    public void setProjectMemberRole(ProjectMemberRoleEnum projectMemberRole) {
        this.projectMemberRole = projectMemberRole;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMemberRole)) {
            return false;
        }
        return id != null && id.equals(((ProjectMemberRole) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberRole{" +
            "id=" + getId() +
            ", createdTimestamp='" + getCreatedTimestamp() + "'" +
            ", projectMemberRole='" + getProjectMemberRole() + "'" +
            "}";
    }
}
