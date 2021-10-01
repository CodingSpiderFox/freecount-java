package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import org.codingspiderfox.domain.enumeration.ProjectPermission;

/**
 * A DTO for the {@link org.codingspiderfox.domain.ProjectMember} entity.
 */
public class ProjectMemberDTO implements Serializable {

    private Long id;

    @NotNull
    private ProjectPermission additionalProjectPermissions;

    private UserDTO user;

    private ProjectDTO project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProjectPermission getAdditionalProjectPermissions() {
        return additionalProjectPermissions;
    }

    public void setAdditionalProjectPermissions(ProjectPermission additionalProjectPermissions) {
        this.additionalProjectPermissions = additionalProjectPermissions;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMemberDTO)) {
            return false;
        }

        ProjectMemberDTO projectMemberDTO = (ProjectMemberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectMemberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberDTO{" +
            "id=" + getId() +
            ", additionalProjectPermissions='" + getAdditionalProjectPermissions() + "'" +
            ", user=" + getUser() +
            ", project=" + getProject() +
            "}";
    }
}
