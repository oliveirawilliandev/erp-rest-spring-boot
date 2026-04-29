package br.com.willian.model;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "ingredients")
@DynamicInsert
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "purchase_price", nullable = false)
    private Double purchasePrice;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock = 0;

    @Column(name = "unit_of_measure", length = 20, nullable = false)
    private String unitOfMeasure;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "qr_code", length = 255)
    private String qrCode;

    @Column(name = "bar_code", length = 255)
    private String barCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "preferred_supplier_id")
    private Supplier preferredSupplier;

    public Ingredient() {}

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

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
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

    public Supplier getPreferredSupplier() {
        return preferredSupplier;
    }

    public void setPreferredSupplier(Supplier preferredSupplier) {
        this.preferredSupplier = preferredSupplier;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ingredient that)) return false;
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
                Objects.equals(preferredSupplier, that.preferredSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name,
                description,
                purchasePrice,
                stockQuantity,
                minimumStock,
                unitOfMeasure,
                active,
                createdAt,
                updatedAt,
                photoUrl,
                qrCode,
                barCode,
                preferredSupplier
        );
    }
}