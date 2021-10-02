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
 * A ProjectMemberRoleAssignment.
 */
@Entity
@Table(name = "project_member_role_assignment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "projectmemberroleassignment")
public class ProjectMemberRoleAssignment implements Serializable {

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
        name = "rel_project_member_role_assignment__project_member_role",
        joinColumns = @JoinColumn(name = "project_member_role_assignment_id"),
        inverseJoinColumns = @JoinColumn(name = "project_member_role_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ProjectMemberRole> projectMemberRoles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectMemberRoleAssignment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getAssignmentTimestamp() {
        return this.assignmentTimestamp;
    }

    public ProjectMemberRoleAssignment assignmentTimestamp(ZonedDateTime assignmentTimestamp) {
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

    public ProjectMemberRoleAssignment projectMember(ProjectMember projectMember) {
        this.setProjectMember(projectMember);
        return this;
    }

    public Set<ProjectMemberRole> getProjectMemberRoles() {
        return this.projectMemberRoles;
    }

    public void setProjectMemberRoles(Set<ProjectMemberRole> projectMemberRoles) {
        this.projectMemberRoles = projectMemberRoles;
    }

    public ProjectMemberRoleAssignment projectMemberRoles(Set<ProjectMemberRole> projectMemberRoles) {
        this.setProjectMemberRoles(projectMemberRoles);
        return this;
    }

    public ProjectMemberRoleAssignment addProjectMemberRole(ProjectMemberRole projectMemberRole) {
        this.projectMemberRoles.add(projectMemberRole);
        return this;
    }

    public ProjectMemberRoleAssignment removeProjectMemberRole(ProjectMemberRole projectMemberRole) {
        this.projectMemberRoles.remove(projectMemberRole);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMemberRoleAssignment)) {
            return false;
        }
        return id != null && id.equals(((ProjectMemberRoleAssignment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberRoleAssignment{" +
            "id=" + getId() +
            ", assignmentTimestamp='" + getAssignmentTimestamp() + "'" +
            "}";
    }
}
