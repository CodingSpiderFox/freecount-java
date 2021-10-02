package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;
import org.codingspiderfox.domain.enumeration.ProjectMemberRoleEnum;

/**
 * A DTO for the {@link org.codingspiderfox.domain.ProjectMemberRole} entity.
 */
public class ProjectMemberRoleDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime createdTimestamp;

    @NotNull
    private ProjectMemberRoleEnum projectMemberRole;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(ZonedDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public ProjectMemberRoleEnum getProjectMemberRole() {
        return projectMemberRole;
    }

    public void setProjectMemberRole(ProjectMemberRoleEnum projectMemberRole) {
        this.projectMemberRole = projectMemberRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMemberRoleDTO)) {
            return false;
        }

        ProjectMemberRoleDTO projectMemberRoleDTO = (ProjectMemberRoleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectMemberRoleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberRoleDTO{" +
            "id=" + getId() +
            ", createdTimestamp='" + getCreatedTimestamp() + "'" +
            ", projectMemberRole='" + getProjectMemberRole() + "'" +
            "}";
    }
}
