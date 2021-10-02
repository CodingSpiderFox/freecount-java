package org.codingspiderfox.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link org.codingspiderfox.domain.FinanceTransactions} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.FinanceTransactionsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /finance-transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FinanceTransactionsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private StringFilter id;

    private ZonedDateTimeFilter executionTimestamp;

    private DoubleFilter amountAddedToDestinationAccount;

    private StringFilter comment;

    private StringFilter destinationAccountId;

    private StringFilter referenceAccountId;

    private Boolean distinct;

    public FinanceTransactionsCriteria() {}

    public FinanceTransactionsCriteria(FinanceTransactionsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.executionTimestamp = other.executionTimestamp == null ? null : other.executionTimestamp.copy();
        this.amountAddedToDestinationAccount =
            other.amountAddedToDestinationAccount == null ? null : other.amountAddedToDestinationAccount.copy();
        this.comment = other.comment == null ? null : other.comment.copy();
        this.destinationAccountId = other.destinationAccountId == null ? null : other.destinationAccountId.copy();
        this.referenceAccountId = other.referenceAccountId == null ? null : other.referenceAccountId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public FinanceTransactionsCriteria copy() {
        return new FinanceTransactionsCriteria(this);
    }

    public StringFilter getId() {
        return id;
    }

    public StringFilter id() {
        if (id == null) {
            id = new StringFilter();
        }
        return id;
    }

    public void setId(StringFilter id) {
        this.id = id;
    }

    public ZonedDateTimeFilter getExecutionTimestamp() {
        return executionTimestamp;
    }

    public ZonedDateTimeFilter executionTimestamp() {
        if (executionTimestamp == null) {
            executionTimestamp = new ZonedDateTimeFilter();
        }
        return executionTimestamp;
    }

    public void setExecutionTimestamp(ZonedDateTimeFilter executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
    }

    public DoubleFilter getAmountAddedToDestinationAccount() {
        return amountAddedToDestinationAccount;
    }

    public DoubleFilter amountAddedToDestinationAccount() {
        if (amountAddedToDestinationAccount == null) {
            amountAddedToDestinationAccount = new DoubleFilter();
        }
        return amountAddedToDestinationAccount;
    }

    public void setAmountAddedToDestinationAccount(DoubleFilter amountAddedToDestinationAccount) {
        this.amountAddedToDestinationAccount = amountAddedToDestinationAccount;
    }

    public StringFilter getComment() {
        return comment;
    }

    public StringFilter comment() {
        if (comment == null) {
            comment = new StringFilter();
        }
        return comment;
    }

    public void setComment(StringFilter comment) {
        this.comment = comment;
    }

    public StringFilter getDestinationAccountId() {
        return destinationAccountId;
    }

    public StringFilter destinationAccountId() {
        if (destinationAccountId == null) {
            destinationAccountId = new StringFilter();
        }
        return destinationAccountId;
    }

    public void setDestinationAccountId(StringFilter destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public StringFilter getReferenceAccountId() {
        return referenceAccountId;
    }

    public StringFilter referenceAccountId() {
        if (referenceAccountId == null) {
            referenceAccountId = new StringFilter();
        }
        return referenceAccountId;
    }

    public void setReferenceAccountId(StringFilter referenceAccountId) {
        this.referenceAccountId = referenceAccountId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FinanceTransactionsCriteria that = (FinanceTransactionsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(executionTimestamp, that.executionTimestamp) &&
            Objects.equals(amountAddedToDestinationAccount, that.amountAddedToDestinationAccount) &&
            Objects.equals(comment, that.comment) &&
            Objects.equals(destinationAccountId, that.destinationAccountId) &&
            Objects.equals(referenceAccountId, that.referenceAccountId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            executionTimestamp,
            amountAddedToDestinationAccount,
            comment,
            destinationAccountId,
            referenceAccountId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FinanceTransactionsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (executionTimestamp != null ? "executionTimestamp=" + executionTimestamp + ", " : "") +
            (amountAddedToDestinationAccount != null ? "amountAddedToDestinationAccount=" + amountAddedToDestinationAccount + ", " : "") +
            (comment != null ? "comment=" + comment + ", " : "") +
            (destinationAccountId != null ? "destinationAccountId=" + destinationAccountId + ", " : "") +
            (referenceAccountId != null ? "referenceAccountId=" + referenceAccountId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
