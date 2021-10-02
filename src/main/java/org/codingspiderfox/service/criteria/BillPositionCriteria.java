package org.codingspiderfox.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link org.codingspiderfox.domain.Bill} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.BillResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /bills?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BillPositionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private LongFilter billId;

    private DoubleFilter cost;

    private Boolean distinct;

    public BillPositionCriteria() {}

    public BillPositionCriteria(BillPositionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.billId = other.billId == null ? null : other.billId.copy();
        this.cost = other.cost == null ? null : other.cost.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BillPositionCriteria copy() {
        return new BillPositionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public DoubleFilter getCost() {
        return cost;
    }

    public void setCost(DoubleFilter cost) {
        this.cost = cost;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
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
        final BillPositionCriteria that = (BillPositionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(billId, that.billId) &&
            Objects.equals(cost, that.cost) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, billId, cost, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BillCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (billId != null ? "billId=" + billId + ", " : "") +
            (cost != null ? "cost=" + cost + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }

    public LongFilter getBillId() {
        return billId;
    }

    public void setBillId(LongFilter billId) {
        this.billId = billId;
    }
}
