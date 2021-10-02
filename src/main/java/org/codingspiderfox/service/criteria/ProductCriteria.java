package org.codingspiderfox.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.DurationFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link org.codingspiderfox.domain.Product} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter scannerId;

    private DurationFilter usualDurationFromBuyTillExpire;

    private BooleanFilter expireMeansBad;

    private StringFilter y;

    private StringFilter h;

    private Boolean distinct;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.scannerId = other.scannerId == null ? null : other.scannerId.copy();
        this.usualDurationFromBuyTillExpire =
            other.usualDurationFromBuyTillExpire == null ? null : other.usualDurationFromBuyTillExpire.copy();
        this.expireMeansBad = other.expireMeansBad == null ? null : other.expireMeansBad.copy();
        this.y = other.y == null ? null : other.y.copy();
        this.h = other.h == null ? null : other.h.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
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

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getScannerId() {
        return scannerId;
    }

    public StringFilter scannerId() {
        if (scannerId == null) {
            scannerId = new StringFilter();
        }
        return scannerId;
    }

    public void setScannerId(StringFilter scannerId) {
        this.scannerId = scannerId;
    }

    public DurationFilter getUsualDurationFromBuyTillExpire() {
        return usualDurationFromBuyTillExpire;
    }

    public DurationFilter usualDurationFromBuyTillExpire() {
        if (usualDurationFromBuyTillExpire == null) {
            usualDurationFromBuyTillExpire = new DurationFilter();
        }
        return usualDurationFromBuyTillExpire;
    }

    public void setUsualDurationFromBuyTillExpire(DurationFilter usualDurationFromBuyTillExpire) {
        this.usualDurationFromBuyTillExpire = usualDurationFromBuyTillExpire;
    }

    public BooleanFilter getExpireMeansBad() {
        return expireMeansBad;
    }

    public BooleanFilter expireMeansBad() {
        if (expireMeansBad == null) {
            expireMeansBad = new BooleanFilter();
        }
        return expireMeansBad;
    }

    public void setExpireMeansBad(BooleanFilter expireMeansBad) {
        this.expireMeansBad = expireMeansBad;
    }

    public StringFilter getY() {
        return y;
    }

    public StringFilter y() {
        if (y == null) {
            y = new StringFilter();
        }
        return y;
    }

    public void setY(StringFilter y) {
        this.y = y;
    }

    public StringFilter getH() {
        return h;
    }

    public StringFilter h() {
        if (h == null) {
            h = new StringFilter();
        }
        return h;
    }

    public void setH(StringFilter h) {
        this.h = h;
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
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(scannerId, that.scannerId) &&
            Objects.equals(usualDurationFromBuyTillExpire, that.usualDurationFromBuyTillExpire) &&
            Objects.equals(expireMeansBad, that.expireMeansBad) &&
            Objects.equals(y, that.y) &&
            Objects.equals(h, that.h) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, scannerId, usualDurationFromBuyTillExpire, expireMeansBad, y, h, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (scannerId != null ? "scannerId=" + scannerId + ", " : "") +
            (usualDurationFromBuyTillExpire != null ? "usualDurationFromBuyTillExpire=" + usualDurationFromBuyTillExpire + ", " : "") +
            (expireMeansBad != null ? "expireMeansBad=" + expireMeansBad + ", " : "") +
            (y != null ? "y=" + y + ", " : "") +
            (h != null ? "h=" + h + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
