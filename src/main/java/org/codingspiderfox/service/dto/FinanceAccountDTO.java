package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.codingspiderfox.domain.FinanceAccount} entity.
 */
public class FinanceAccountDTO implements Serializable {

    private String id;

    @NotNull
    private String title;

    @NotNull
    private Double currentBalance;

    private UserDTO owner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public UserDTO getOwner() {
        return owner;
    }

    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FinanceAccountDTO)) {
            return false;
        }

        FinanceAccountDTO financeAccountDTO = (FinanceAccountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, financeAccountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FinanceAccountDTO{" +
            "id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", currentBalance=" + getCurrentBalance() +
            ", owner=" + getOwner() +
            "}";
    }
}
