package org.codingspiderfox.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.codingspiderfox.domain.BillPosition} entity.
 */
public class BillPositionDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Double cost;

    @NotNull
    private Integer order;

    private BillDTO bill;

    private ProductDTO product;

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

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public BillDTO getBill() {
        return bill;
    }

    public void setBill(BillDTO bill) {
        this.bill = bill;
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
        if (!(o instanceof BillPositionDTO)) {
            return false;
        }

        BillPositionDTO billPositionDTO = (BillPositionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, billPositionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BillPositionDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", cost=" + getCost() +
            ", order=" + getOrder() +
            ", bill=" + getBill() +
            ", product=" + getProduct() +
            "}";
    }
}
