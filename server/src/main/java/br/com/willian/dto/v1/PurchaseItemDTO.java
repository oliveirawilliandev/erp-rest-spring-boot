package br.com.willian.dto.v1;

import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Objects;

public class PurchaseItemDTO extends RepresentationModel<PurchaseItemDTO> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(PurchaseItemDTO.class.getName());

    // [PURCHASE-ITEM-DTO-001] Identificador único do item da compra
    private Long id;

// =====================================================
// [PURCHASE-ITEM-DTO-002] RELACIONAMENTOS (IDs)
// =====================================================

    // [PURCHASE-ITEM-DTO-003] ID da compra à qual este item pertence (obrigatório)
    @NotNull(message = "Purchase ID is required")
    private Long purchaseId;

    // [PURCHASE-ITEM-DTO-004] ID do insumo (obrigatório) - ALTERADO de productId para ingredientId
    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;

// =====================================================
// [PURCHASE-ITEM-DTO-005] VALORES DO ITEM
// =====================================================

    // [PURCHASE-ITEM-DTO-006] Quantidade comprada (obrigatório, mínimo 1)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // [PURCHASE-ITEM-DTO-007] Preço unitário no momento da compra (obrigatório, positivo)
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private Double unitPrice;

// =====================================================
// [PURCHASE-ITEM-DTO-008] ENTIDADE RELACIONADA
// =====================================================

    // [PURCHASE-ITEM-DTO-009] Dados completos do insumo (opcional, para respostas detalhadas) - ALTERADO de Product para Ingredient
    private IngredientDTO ingredient;

    // [PURCHASE-ITEM-DTO-010] Campo calculado: valor total do item (quantidade * preço unitário)
    private Double totalValue;

    // Constructors
    public PurchaseItemDTO() {}

    // Método auxiliar para calcular o valor total
    public Double getTotalValue() {
        if (quantity != null && unitPrice != null) {
            return quantity * unitPrice;
        }
        return 0.0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public IngredientDTO getIngredient() {
        return ingredient;
    }

    public void setIngredient(IngredientDTO ingredient) {
        this.ingredient = ingredient;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PurchaseItemDTO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(purchaseId, that.purchaseId) &&
                Objects.equals(ingredientId, that.ingredientId) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(unitPrice, that.unitPrice) &&
                Objects.equals(ingredient, that.ingredient) &&
                Objects.equals(totalValue, that.totalValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, purchaseId, ingredientId, quantity, unitPrice, ingredient, totalValue);
    }
}