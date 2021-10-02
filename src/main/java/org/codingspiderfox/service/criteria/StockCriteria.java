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
 * Criteria class for the {@link org.codingspiderfox.domain.Stock} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.StockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class StockCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter addedTimestamp;

    private StringFilter storageLocation;

    private ZonedDateTimeFilter calculatedExpiryTimestamp;

    private ZonedDateTimeFilter manualSetExpiryTimestamp;

    private LongFilter productId;

    private Boolean distinct;

    public StockCriteria() {}

    public StockCriteria(StockCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.addedTimestamp = other.addedTimestamp == null ? null : other.addedTimestamp.copy();
        this.storageLocation = other.storageLocation == null ? null : other.storageLocation.copy();
        this.calculatedExpiryTimestamp = other.calculatedExpiryTimestamp == null ? null : other.calculatedExpiryTimestamp.copy();
        this.manualSetExpiryTimestamp = other.manualSetExpiryTimestamp == null ? null : other.manualSetExpiryTimestamp.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StockCriteria copy() {
        return new StockCriteria(this);
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

    public ZonedDateTimeFilter getAddedTimestamp() {
        return addedTimestamp;
    }

    public ZonedDateTimeFilter addedTimestamp() {
        if (addedTimestamp == null) {
            addedTimestamp = new ZonedDateTimeFilter();
        }
        return addedTimestamp;
    }

    public void setAddedTimestamp(ZonedDateTimeFilter addedTimestamp) {
        this.addedTimestamp = addedTimestamp;
    }

    public StringFilter getStorageLocation() {
        return storageLocation;
    }

    public StringFilter storageLocation() {
        if (storageLocation == null) {
            storageLocation = new StringFilter();
        }
        return storageLocation;
    }

    public void setStorageLocation(StringFilter storageLocation) {
        this.storageLocation = storageLocation;
    }

    public ZonedDateTimeFilter getCalculatedExpiryTimestamp() {
        return calculatedExpiryTimestamp;
    }

    public ZonedDateTimeFilter calculatedExpiryTimestamp() {
        if (calculatedExpiryTimestamp == null) {
            calculatedExpiryTimestamp = new ZonedDateTimeFilter();
        }
        return calculatedExpiryTimestamp;
    }

    public void setCalculatedExpiryTimestamp(ZonedDateTimeFilter calculatedExpiryTimestamp) {
        this.calculatedExpiryTimestamp = calculatedExpiryTimestamp;
    }

    public ZonedDateTimeFilter getManualSetExpiryTimestamp() {
        return manualSetExpiryTimestamp;
    }

    public ZonedDateTimeFilter manualSetExpiryTimestamp() {
        if (manualSetExpiryTimestamp == null) {
            manualSetExpiryTimestamp = new ZonedDateTimeFilter();
        }
        return manualSetExpiryTimestamp;
    }

    public void setManualSetExpiryTimestamp(ZonedDateTimeFilter manualSetExpiryTimestamp) {
        this.manualSetExpiryTimestamp = manualSetExpiryTimestamp;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public LongFilter productId() {
        if (productId == null) {
            productId = new LongFilter();
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
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
        final StockCriteria that = (StockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(addedTimestamp, that.addedTimestamp) &&
            Objects.equals(storageLocation, that.storageLocation) &&
            Objects.equals(calculatedExpiryTimestamp, that.calculatedExpiryTimestamp) &&
            Objects.equals(manualSetExpiryTimestamp, that.manualSetExpiryTimestamp) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addedTimestamp, storageLocation, calculatedExpiryTimestamp, manualSetExpiryTimestamp, productId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (addedTimestamp != null ? "addedTimestamp=" + addedTimestamp + ", " : "") +
            (storageLocation != null ? "storageLocation=" + storageLocation + ", " : "") +
            (calculatedExpiryTimestamp != null ? "calculatedExpiryTimestamp=" + calculatedExpiryTimestamp + ", " : "") +
            (manualSetExpiryTimestamp != null ? "manualSetExpiryTimestamp=" + manualSetExpiryTimestamp + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
