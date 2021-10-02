package org.codingspiderfox.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Stock.
 */
@Entity
@Table(name = "stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "stock")
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "added_timestamp", nullable = false)
    private ZonedDateTime addedTimestamp;

    @Column(name = "storage_location")
    private String storageLocation;

    @NotNull
    @Column(name = "calculated_expiry_timestamp", nullable = false)
    private ZonedDateTime calculatedExpiryTimestamp;

    @Column(name = "manual_set_expiry_timestamp")
    private ZonedDateTime manualSetExpiryTimestamp;

    @ManyToOne(optional = false)
    @NotNull
    private Product product;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Stock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getAddedTimestamp() {
        return this.addedTimestamp;
    }

    public Stock addedTimestamp(ZonedDateTime addedTimestamp) {
        this.setAddedTimestamp(addedTimestamp);
        return this;
    }

    public void setAddedTimestamp(ZonedDateTime addedTimestamp) {
        this.addedTimestamp = addedTimestamp;
    }

    public String getStorageLocation() {
        return this.storageLocation;
    }

    public Stock storageLocation(String storageLocation) {
        this.setStorageLocation(storageLocation);
        return this;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public ZonedDateTime getCalculatedExpiryTimestamp() {
        return this.calculatedExpiryTimestamp;
    }

    public Stock calculatedExpiryTimestamp(ZonedDateTime calculatedExpiryTimestamp) {
        this.setCalculatedExpiryTimestamp(calculatedExpiryTimestamp);
        return this;
    }

    public void setCalculatedExpiryTimestamp(ZonedDateTime calculatedExpiryTimestamp) {
        this.calculatedExpiryTimestamp = calculatedExpiryTimestamp;
    }

    public ZonedDateTime getManualSetExpiryTimestamp() {
        return this.manualSetExpiryTimestamp;
    }

    public Stock manualSetExpiryTimestamp(ZonedDateTime manualSetExpiryTimestamp) {
        this.setManualSetExpiryTimestamp(manualSetExpiryTimestamp);
        return this;
    }

    public void setManualSetExpiryTimestamp(ZonedDateTime manualSetExpiryTimestamp) {
        this.manualSetExpiryTimestamp = manualSetExpiryTimestamp;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Stock product(Product product) {
        this.setProduct(product);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        return id != null && id.equals(((Stock) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Stock{" +
            "id=" + getId() +
            ", addedTimestamp='" + getAddedTimestamp() + "'" +
            ", storageLocation='" + getStorageLocation() + "'" +
            ", calculatedExpiryTimestamp='" + getCalculatedExpiryTimestamp() + "'" +
            ", manualSetExpiryTimestamp='" + getManualSetExpiryTimestamp() + "'" +
            "}";
    }
}
