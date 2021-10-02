package org.codingspiderfox.domain;

import java.io.Serializable;
import java.time.Duration;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "scanner_id", nullable = false)
    private String scannerId;

    @NotNull
    @Column(name = "usual_duration_from_buy_till_expire", nullable = false)
    private Duration usualDurationFromBuyTillExpire;

    @Column(name = "expire_means_bad")
    private Boolean expireMeansBad;

    @Column(name = "y")
    private String y;

    @Column(name = "h")
    private String h;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Product title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScannerId() {
        return this.scannerId;
    }

    public Product scannerId(String scannerId) {
        this.setScannerId(scannerId);
        return this;
    }

    public void setScannerId(String scannerId) {
        this.scannerId = scannerId;
    }

    public Duration getUsualDurationFromBuyTillExpire() {
        return this.usualDurationFromBuyTillExpire;
    }

    public Product usualDurationFromBuyTillExpire(Duration usualDurationFromBuyTillExpire) {
        this.setUsualDurationFromBuyTillExpire(usualDurationFromBuyTillExpire);
        return this;
    }

    public void setUsualDurationFromBuyTillExpire(Duration usualDurationFromBuyTillExpire) {
        this.usualDurationFromBuyTillExpire = usualDurationFromBuyTillExpire;
    }

    public Boolean getExpireMeansBad() {
        return this.expireMeansBad;
    }

    public Product expireMeansBad(Boolean expireMeansBad) {
        this.setExpireMeansBad(expireMeansBad);
        return this;
    }

    public void setExpireMeansBad(Boolean expireMeansBad) {
        this.expireMeansBad = expireMeansBad;
    }

    public String getY() {
        return this.y;
    }

    public Product y(String y) {
        this.setY(y);
        return this;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getH() {
        return this.h;
    }

    public Product h(String h) {
        this.setH(h);
        return this;
    }

    public void setH(String h) {
        this.h = h;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
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
