package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.codingspiderfox.domain.FinanceTransactions} entity.
 */
public class FinanceTransactionsDTO implements Serializable {

    private String id;

    @NotNull
    private ZonedDateTime executionTimestamp;

    @NotNull
    private Double amountAddedToDestinationAccount;

    private String comment;

    private FinanceAccountDTO destinationAccount;

    private FinanceAccountDTO referenceAccount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ZonedDateTime getExecutionTimestamp() {
        return executionTimestamp;
    }

    public void setExecutionTimestamp(ZonedDateTime executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
    }

    public Double getAmountAddedToDestinationAccount() {
        return amountAddedToDestinationAccount;
    }

    public void setAmountAddedToDestinationAccount(Double amountAddedToDestinationAccount) {
        this.amountAddedToDestinationAccount = amountAddedToDestinationAccount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public FinanceAccountDTO getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(FinanceAccountDTO destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public FinanceAccountDTO getReferenceAccount() {
        return referenceAccount;
    }

    public void setReferenceAccount(FinanceAccountDTO referenceAccount) {
        this.referenceAccount = referenceAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FinanceTransactionsDTO)) {
            return false;
        }

        FinanceTransactionsDTO financeTransactionsDTO = (FinanceTransactionsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, financeTransactionsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FinanceTransactionsDTO{" +
            "id='" + getId() + "'" +
            ", executionTimestamp='" + getExecutionTimestamp() + "'" +
            ", amountAddedToDestinationAccount=" + getAmountAddedToDestinationAccount() +
            ", comment='" + getComment() + "'" +
            ", destinationAccount=" + getDestinationAccount() +
            ", referenceAccount=" + getReferenceAccount() +
            "}";
    }
}
