package org.codingspiderfox.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link org.codingspiderfox.domain.ProjectMember} entity.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateProjectMemberDTO implements Serializable {

    private Long id;

    private String userId;

    private Long projectId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateProjectMemberDTO)) {
            return false;
        }

        CreateProjectMemberDTO projectMemberDTO = (CreateProjectMemberDTO) o;
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
            ", userId=" + getUserId() +
            ", projectId=" + getProjectId() +
            "}";
    }
}
