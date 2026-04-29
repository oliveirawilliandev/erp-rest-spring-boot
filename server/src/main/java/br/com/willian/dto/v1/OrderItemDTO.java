package br.com.willian.dto.v1;

import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemDTO extends RepresentationModel<OrderItemDTO> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(OrderItemDTO.class.getName());

    // [ORDER-ITEM-DTO-001] Identificador único do item do pedido
    private Long id;

// =====================================================
// [ORDER-ITEM-DTO-002] RELACIONAMENTOS (IDs)
// =====================================================

    // [ORDER-ITEM-DTO-003] ID do pedido ao qual este item pertence (obrigatório)
    @NotNull(message = "Order ID is required") // Validação: campo não pode ser nulo
    private Long orderId;

    // [ORDER-ITEM-DTO-004] ID do produto (obrigatório)
    @NotNull(message = "Product ID is required") // Validação: campo não pode ser nulo
    private Long productId;

// =====================================================
// [ORDER-ITEM-DTO-005] VALORES DO ITEM
// =====================================================

    // [ORDER-ITEM-DTO-006] Quantidade do produto (obrigatório, mínimo 1)
    @NotNull(message = "Quantity is required") // Validação: campo não pode ser nulo
    @Min(value = 1, message = "Quantity must be at least 1") // Validação: quantidade deve ser ≥ 1
    private Integer quantity;

    // [ORDER-ITEM-DTO-007] Preço unitário no momento do pedido (obrigatório, positivo)
    @NotNull(message = "Unit price is required") // Validação: campo não pode ser nulo
    @Positive(message = "Unit price must be positive") // Validação: preço deve ser maior que zero
    private Double unitPrice;

// =====================================================
// [ORDER-ITEM-DTO-008] ENTIDADE RELACIONADA
// =====================================================

    // [ORDER-ITEM-DTO-009] Dados completos do produto (opcional, para respostas detalhadas)
    private ProductDTO product;

    // Constructors
    public OrderItemDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderItemDTO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, that.id) && Objects.equals(orderId, that.orderId) && Objects.equals(productId, that.productId) && Objects.equals(quantity, that.quantity) && Objects.equals(unitPrice, that.unitPrice) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, orderId, productId, quantity, unitPrice, product);
    }
}