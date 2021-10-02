package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.codingspiderfox.domain.Product} entity.
 */
public class ProductDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String scannerId;

    @NotNull
    private Duration usualDurationFromBuyTillExpire;

    private Boolean expireMeansBad;

    private String y;

    private String h;

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

    public String getScannerId() {
        return scannerId;
    }

    public void setScannerId(String scannerId) {
        this.scannerId = scannerId;
    }

    public Duration getUsualDurationFromBuyTillExpire() {
        return usualDurationFromBuyTillExpire;
    }

    public void setUsualDurationFromBuyTillExpire(Duration usualDurationFromBuyTillExpire) {
        this.usualDurationFromBuyTillExpire = usualDurationFromBuyTillExpire;
    }

    public Boolean getExpireMeansBad() {
        return expireMeansBad;
    }

    public void setExpireMeansBad(Boolean expireMeansBad) {
        this.expireMeansBad = expireMeansBad;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", scannerId='" + getScannerId() + "'" +
            ", usualDurationFromBuyTillExpire='" + getUsualDurationFromBuyTillExpire() + "'" +
            ", expireMeansBad='" + getExpireMeansBad() + "'" +
            ", y='" + getY() + "'" +
            ", h='" + getH() + "'" +
            "}";
    }
}
