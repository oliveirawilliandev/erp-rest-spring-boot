package br.com.willian.dto.v1;

import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

public class ProductDTO extends RepresentationModel<ProductDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    // =====================================================
    // INFORMAÇÕES BÁSICAS
    // =====================================================

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must be up to 150 characters")
    private String name;

    private String description;

    // =====================================================
    // PREÇOS
    // =====================================================

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Starting price is required")
    @PositiveOrZero(message = "Starting price must be zero or positive")
    private Double startingPrice;

    // =====================================================
    // ESTOQUE
    // =====================================================

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    // =====================================================
    // STATUS E AUDITORIA
    // =====================================================

    private Boolean active;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    // =====================================================
    // FOTOS E CÓDIGOS
    // =====================================================

    private String photoUrl;

    private String qrCode;

    private String barCode;

    public ProductDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductDTO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(price, that.price)
                && Objects.equals(startingPrice, that.startingPrice)
                && Objects.equals(stockQuantity, that.stockQuantity)
                && Objects.equals(active, that.active)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt)
                && Objects.equals(photoUrl, that.photoUrl)
                && Objects.equals(qrCode, that.qrCode)
                && Objects.equals(barCode, that.barCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                id,
                name,
                description,
                price,
                startingPrice,
                stockQuantity,
                active,
                createdAt,
                updatedAt,
                photoUrl,
                qrCode,
                barCode
        );
    }
}