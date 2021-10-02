package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.codingspiderfox.domain.Stock} entity.
 */
public class StockDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime addedTimestamp;

    private String storageLocation;

    @NotNull
    private ZonedDateTime calculatedExpiryTimestamp;

    private ZonedDateTime manualSetExpiryTimestamp;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getAddedTimestamp() {
        return addedTimestamp;
    }

    public void setAddedTimestamp(ZonedDateTime addedTimestamp) {
        this.addedTimestamp = addedTimestamp;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public ZonedDateTime getCalculatedExpiryTimestamp() {
        return calculatedExpiryTimestamp;
    }

    public void setCalculatedExpiryTimestamp(ZonedDateTime calculatedExpiryTimestamp) {
        this.calculatedExpiryTimestamp = calculatedExpiryTimestamp;
    }

    public ZonedDateTime getManualSetExpiryTimestamp() {
        return manualSetExpiryTimestamp;
    }

    public void setManualSetExpiryTimestamp(ZonedDateTime manualSetExpiryTimestamp) {
        this.manualSetExpiryTimestamp = manualSetExpiryTimestamp;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockDTO)) {
            return false;
        }

        StockDTO stockDTO = (StockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockDTO{" +
            "id=" + getId() +
            ", addedTimestamp='" + getAddedTimestamp() + "'" +
            ", storageLocation='" + getStorageLocation() + "'" +
            ", calculatedExpiryTimestamp='" + getCalculatedExpiryTimestamp() + "'" +
            ", manualSetExpiryTimestamp='" + getManualSetExpiryTimestamp() + "'" +
            ", product=" + getProduct() +
            "}";
    }
}
