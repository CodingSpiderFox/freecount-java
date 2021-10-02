package org.codingspiderfox.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProjectMemberPermissionAssignment.
 */
@Entity
@Table(name = "proj_member_permiss_assign")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "projectmemberpermissionassignment")
public class ProjectMemberPermissionAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "assignment_timestamp", nullable = false)
    private ZonedDateTime assignmentTimestamp;

    @JsonIgnoreProperties(value = { "user", "project" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @MapsId
    @JoinColumn(name = "id")
    private ProjectMember projectMember;

    @ManyToMany
    @NotNull
    @JoinTable(
        name = "rel_proj_member_permiss_assign__project_member_permission",
        joinColumns = @JoinColumn(name = "proj_member_permiss_assign_id"),
        inverseJoinColumns = @JoinColumn(name = "project_member_permission_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ProjectMemberPermission> projectMemberPermissions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectMemberPermissionAssignment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getAssignmentTimestamp() {
        return this.assignmentTimestamp;
    }

    public ProjectMemberPermissionAssignment assignmentTimestamp(ZonedDateTime assignmentTimestamp) {
        this.setAssignmentTimestamp(assignmentTimestamp);
        return this;
    }

    public void setAssignmentTimestamp(ZonedDateTime assignmentTimestamp) {
        this.assignmentTimestamp = assignmentTimestamp;
    }

    public ProjectMember getProjectMember() {
        return this.projectMember;
    }

    public void setProjectMember(ProjectMember projectMember) {
        this.projectMember = projectMember;
    }

    public ProjectMemberPermissionAssignment projectMember(ProjectMember projectMember) {
        this.setProjectMember(projectMember);
        return this;
    }

    public Set<ProjectMemberPermission> getProjectMemberPermissions() {
        return this.projectMemberPermissions;
    }

    public void setProjectMemberPermissions(Set<ProjectMemberPermission> projectMemberPermissions) {
        this.projectMemberPermissions = projectMemberPermissions;
    }

    public ProjectMemberPermissionAssignment projectMemberPermissions(Set<ProjectMemberPermission> projectMemberPermissions) {
        this.setProjectMemberPermissions(projectMemberPermissions);
        return this;
    }

    public ProjectMemberPermissionAssignment addProjectMemberPermission(ProjectMemberPermission projectMemberPermission) {
        this.projectMemberPermissions.add(projectMemberPermission);
        return this;
    }

    public ProjectMemberPermissionAssignment removeProjectMemberPermission(ProjectMemberPermission projectMemberPermission) {
        this.projectMemberPermissions.remove(projectMemberPermission);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMemberPermissionAssignment)) {
            return false;
        }
        return id != null && id.equals(((ProjectMemberPermissionAssignment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberPermissionAssignment{" +
            "id=" + getId() +
            ", assignmentTimestamp='" + getAssignmentTimestamp() + "'" +
            "}";
    }
}
