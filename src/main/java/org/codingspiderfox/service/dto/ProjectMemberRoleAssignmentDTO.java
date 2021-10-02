package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.codingspiderfox.domain.ProjectMemberRoleAssignment} entity.
 */
public class ProjectMemberRoleAssignmentDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime assignmentTimestamp;

    private ProjectMemberDTO projectMember;

    private Set<ProjectMemberRoleDTO> projectMemberRoles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getAssignmentTimestamp() {
        return assignmentTimestamp;
    }

    public void setAssignmentTimestamp(ZonedDateTime assignmentTimestamp) {
        this.assignmentTimestamp = assignmentTimestamp;
    }

    public ProjectMemberDTO getProjectMember() {
        return projectMember;
    }

    public void setProjectMember(ProjectMemberDTO projectMember) {
        this.projectMember = projectMember;
    }

    public Set<ProjectMemberRoleDTO> getProjectMemberRoles() {
        return projectMemberRoles;
    }

    public void setProjectMemberRoles(Set<ProjectMemberRoleDTO> projectMemberRoles) {
        this.projectMemberRoles = projectMemberRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMemberRoleAssignmentDTO)) {
            return false;
        }

        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = (ProjectMemberRoleAssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectMemberRoleAssignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberRoleAssignmentDTO{" +
            "id=" + getId() +
            ", assignmentTimestamp='" + getAssignmentTimestamp() + "'" +
            ", projectMember=" + getProjectMember() +
            ", projectMemberRoles=" + getProjectMemberRoles() +
            "}";
    }
}
