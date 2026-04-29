package br.com.willian.model;

import br.com.willian.model.enums.PurchaseStatus;
import br.com.willian.model.enums.PurchaseType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "purchase_date")
    private Instant purchaseDate;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @OneToMany(mappedBy = "purchaseId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PurchaseItem> items = new ArrayList<>();

    public Purchase() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<PurchaseItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Purchase purchase)) return false;
        return Objects.equals(id, purchase.id) && Objects.equals(supplierId, purchase.supplierId) && Objects.equals(employeeId, purchase.employeeId) && Objects.equals(totalAmount, purchase.totalAmount) && Objects.equals(purchaseDate, purchase.purchaseDate) && status == purchase.status && Objects.equals(supplier, purchase.supplier) && Objects.equals(employee, purchase.employee) && Objects.equals(items, purchase.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, supplierId, employeeId, totalAmount, purchaseDate, status, supplier, employee, items);
    }
}

