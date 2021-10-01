package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.codingspiderfox.domain.ProjectSettings} entity.
 */
public class ProjectSettingsDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean mustProvideBillCopyByDefault;

    private ProjectDTO project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getMustProvideBillCopyByDefault() {
        return mustProvideBillCopyByDefault;
    }

    public void setMustProvideBillCopyByDefault(Boolean mustProvideBillCopyByDefault) {
        this.mustProvideBillCopyByDefault = mustProvideBillCopyByDefault;
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
        if (!(o instanceof ProjectSettingsDTO)) {
            return false;
        }

        ProjectSettingsDTO projectSettingsDTO = (ProjectSettingsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectSettingsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectSettingsDTO{" +
            "id=" + getId() +
            ", mustProvideBillCopyByDefault='" + getMustProvideBillCopyByDefault() + "'" +
            ", project=" + getProject() +
            "}";
    }
}
