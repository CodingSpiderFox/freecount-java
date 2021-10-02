package org.codingspiderfox.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FinanceTransactions.
 */
@Entity
@Table(name = "finance_transactions")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "financetransactions")
public class FinanceTransactions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "execution_timestamp", nullable = false)
    private ZonedDateTime executionTimestamp;

    @NotNull
    @Column(name = "amount_added_to_destination_account", nullable = false)
    private Double amountAddedToDestinationAccount;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "owner" }, allowSetters = true)
    private FinanceAccount destinationAccount;

    @JsonIgnoreProperties(value = { "owner" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @MapsId
    @JoinColumn(name = "id")
    private FinanceAccount referenceAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public FinanceTransactions id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ZonedDateTime getExecutionTimestamp() {
        return this.executionTimestamp;
    }

    public FinanceTransactions executionTimestamp(ZonedDateTime executionTimestamp) {
        this.setExecutionTimestamp(executionTimestamp);
        return this;
    }

    public void setExecutionTimestamp(ZonedDateTime executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
    }

    public Double getAmountAddedToDestinationAccount() {
        return this.amountAddedToDestinationAccount;
    }

    public FinanceTransactions amountAddedToDestinationAccount(Double amountAddedToDestinationAccount) {
        this.setAmountAddedToDestinationAccount(amountAddedToDestinationAccount);
        return this;
    }

    public void setAmountAddedToDestinationAccount(Double amountAddedToDestinationAccount) {
        this.amountAddedToDestinationAccount = amountAddedToDestinationAccount;
    }

    public String getComment() {
        return this.comment;
    }

    public FinanceTransactions comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public FinanceAccount getDestinationAccount() {
        return this.destinationAccount;
    }

    public void setDestinationAccount(FinanceAccount financeAccount) {
        this.destinationAccount = financeAccount;
    }

    public FinanceTransactions destinationAccount(FinanceAccount financeAccount) {
        this.setDestinationAccount(financeAccount);
        return this;
    }

    public FinanceAccount getReferenceAccount() {
        return this.referenceAccount;
    }

    public void setReferenceAccount(FinanceAccount financeAccount) {
        this.referenceAccount = financeAccount;
    }

    public FinanceTransactions referenceAccount(FinanceAccount financeAccount) {
        this.setReferenceAccount(financeAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FinanceTransactions)) {
            return false;
        }
        return id != null && id.equals(((FinanceTransactions) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FinanceTransactions{" +
            "id=" + getId() +
            ", executionTimestamp='" + getExecutionTimestamp() + "'" +
            ", amountAddedToDestinationAccount=" + getAmountAddedToDestinationAccount() +
            ", comment='" + getComment() + "'" +
            "}";
    }
}
