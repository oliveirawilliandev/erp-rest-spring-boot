package br.com.willian.dto.v1;

import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

public class IngredientDTO extends RepresentationModel<IngredientDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    // =====================================================
    // INFORMAÇÕES BÁSICAS
    // =====================================================

    @NotBlank(message = "Ingredient name is required")
    @Size(max = 150, message = "Ingredient name must be up to 150 characters")
    private String name;

    private String description;

    // =====================================================
    // PREÇOS E ESTOQUE
    // =====================================================

    @NotNull(message = "Purchase price is required")
    @Positive(message = "Purchase price must be positive")
    private Double purchasePrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Minimum stock is required")
    @Min(value = 0, message = "Minimum stock cannot be negative")
    private Integer minimumStock;

    @NotBlank(message = "Unit of measure is required")
    @Size(max = 20, message = "Unit of measure must be up to 20 characters")
    private String unitOfMeasure; // kg, g, L, mL, unidade, pacote, etc.

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

    // =====================================================
    // RELACIONAMENTOS
    // =====================================================

    private Long preferredSupplierId;
    private String preferredSupplierName;

    // Constructors
    public IngredientDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Integer minimumStock) { this.minimumStock = minimumStock; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public String getBarCode() { return barCode; }
    public void setBarCode(String barCode) { this.barCode = barCode; }

    public Long getPreferredSupplierId() { return preferredSupplierId; }
    public void setPreferredSupplierId(Long preferredSupplierId) { this.preferredSupplierId = preferredSupplierId; }

    public String getPreferredSupplierName() { return preferredSupplierName; }
    public void setPreferredSupplierName(String preferredSupplierName) { this.preferredSupplierName = preferredSupplierName; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IngredientDTO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(purchasePrice, that.purchasePrice) &&
                Objects.equals(stockQuantity, that.stockQuantity) &&
                Objects.equals(minimumStock, that.minimumStock) &&
                Objects.equals(unitOfMeasure, that.unitOfMeasure) &&
                Objects.equals(active, that.active) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(photoUrl, that.photoUrl) &&
                Objects.equals(qrCode, that.qrCode) &&
                Objects.equals(barCode, that.barCode) &&
                Objects.equals(preferredSupplierId, that.preferredSupplierId) &&
                Objects.equals(preferredSupplierName, that.preferredSupplierName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, description, purchasePrice,
                stockQuantity, minimumStock, unitOfMeasure, active,
                createdAt, updatedAt, photoUrl, qrCode, barCode,
                preferredSupplierId, preferredSupplierName);
    }
}