package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.codingspiderfox.domain.Bill} entity.
 */
public class BillDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private ZonedDateTime closedTimestamp;

    private Double finalAmount;

    private ProjectDTO project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ZonedDateTime getClosedTimestamp() {
        return closedTimestamp;
    }

    public void setClosedTimestamp(ZonedDateTime closedTimestamp) {
        this.closedTimestamp = closedTimestamp;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
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
        if (!(o instanceof BillDTO)) {
            return false;
        }

        BillDTO billDTO = (BillDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, billDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BillDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", closedTimestamp='" + getClosedTimestamp() + "'" +
            ", finalAmount=" + getFinalAmount() +
            ", project=" + getProject() +
            "}";
    }
}
