package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;
import org.codingspiderfox.domain.enumeration.ProjectMemberPermissionEnum;

/**
 * A DTO for the {@link org.codingspiderfox.domain.ProjectMemberPermission} entity.
 */
public class ProjectMemberPermissionDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime createdTimestamp;

    @NotNull
    private ProjectMemberPermissionEnum projectMemberPermission;

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

    public ProjectMemberPermissionEnum getProjectMemberPermission() {
        return projectMemberPermission;
    }

    public void setProjectMemberPermission(ProjectMemberPermissionEnum projectMemberPermission) {
        this.projectMemberPermission = projectMemberPermission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMemberPermissionDTO)) {
            return false;
        }

        ProjectMemberPermissionDTO projectMemberPermissionDTO = (ProjectMemberPermissionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectMemberPermissionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberPermissionDTO{" +
            "id=" + getId() +
            ", createdTimestamp='" + getCreatedTimestamp() + "'" +
            ", projectMemberPermission='" + getProjectMemberPermission() + "'" +
            "}";
    }
}
