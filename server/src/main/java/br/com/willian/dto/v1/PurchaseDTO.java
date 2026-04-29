package br.com.willian.dto.v1;

import br.com.willian.model.enums.PurchaseStatus;
import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PurchaseDTO extends RepresentationModel<PurchaseDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    // =====================================================
    // RELACIONAMENTOS (IDs)
    // =====================================================

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    // =====================================================
    // VALORES DA COMPRA
    // =====================================================

    @PositiveOrZero(message = "Total amount must be zero or positive")
    private Double totalAmount;

    private Instant purchaseDate;

    @NotNull(message = "Status is required")
    private PurchaseStatus status;

    // =====================================================
    // ENTIDADES RELACIONADAS (para respostas detalhadas)
    // =====================================================

    private SupplierDTO supplier;
    private EmployeeDTO employee;

    // =====================================================
    // ITENS DA COMPRA (ALTERADO de Product para Ingredient)
    // =====================================================

    private List<PurchaseItemDTO> items = new ArrayList<>();

    // Constructors
    public PurchaseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Instant getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Instant purchaseDate) { this.purchaseDate = purchaseDate; }

    public PurchaseStatus getStatus() { return status; }
    public void setStatus(PurchaseStatus status) { this.status = status; }

    public SupplierDTO getSupplier() { return supplier; }
    public void setSupplier(SupplierDTO supplier) { this.supplier = supplier; }

    public EmployeeDTO getEmployee() { return employee; }
    public void setEmployee(EmployeeDTO employee) { this.employee = employee; }

    public List<PurchaseItemDTO> getItems() { return items; }
    public void setItems(List<PurchaseItemDTO> items) { this.items = items; }

    // Método auxiliar para calcular o total da compra baseado nos itens
    public Double calculateTotalFromItems() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PurchaseDTO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(supplierId, that.supplierId) &&
                Objects.equals(employeeId, that.employeeId) &&
                Objects.equals(totalAmount, that.totalAmount) &&
                Objects.equals(purchaseDate, that.purchaseDate) &&
                status == that.status &&
                Objects.equals(supplier, that.supplier) &&
                Objects.equals(employee, that.employee) &&
                Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, supplierId, employeeId, totalAmount, purchaseDate, status, supplier, employee, items);
    }
}