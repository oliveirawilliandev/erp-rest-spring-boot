package br.com.willian.dto.v1;

import br.com.willian.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderDTO extends RepresentationModel<OrderDTO> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(OrderDTO.class.getName());

    // [ORDERS-DTO-001] Identificador único do pedido
    private Long id;

// =====================================================
// [ORDERS-DTO-002] RELACIONAMENTOS (IDs)
// =====================================================

    // [ORDERS-DTO-003] ID do funcionário responsável pelo pedido (obrigatório)
    @NotNull(message = "Employee ID is required") // Validação: campo não pode ser nulo
    private Long employeeId;

    // [ORDERS-DTO-004] ID do cliente que fez o pedido (obrigatório)
    @NotNull(message = "Customer ID is required") // Validação: campo não pode ser nulo
    private Long customerId;

// =====================================================
// [ORDERS-DTO-005] VALORES
// =====================================================

    // [ORDERS-DTO-006] Valor total do pedido (obrigatório, positivo)
    @NotNull(message = "Total amount is required") // Validação: campo não pode ser nulo
    @Positive(message = "Total amount must be positive") // Validação: valor deve ser maior que zero
    private BigDecimal totalAmount;

    // [ORDERS-DTO-007] Status atual do pedido (obrigatório)
    @NotNull(message = "Status is required") // Validação: campo não pode ser nulo
    private OrderStatus status; // Valores: PENDING, PROCESSING, COMPLETED, CANCELLED

// =====================================================
// [ORDERS-DTO-008] DATAS DE AUDITORIA
// =====================================================

    // [ORDERS-DTO-009] Data de criação do pedido (com timezone)
    private OffsetDateTime createdAt;

    // [ORDERS-DTO-010] Data da última atualização do pedido (com timezone)
    private OffsetDateTime updatedAt;

// =====================================================
// [ORDERS-DTO-011] CÓDIGOS DO PEDIDO
// =====================================================

    // [ORDERS-DTO-012] QR Code para consulta/rastreamento
    private String qrCode;

    // [ORDERS-DTO-013] Código de barras do pedido
    private String barCode;

// =====================================================
// [ORDERS-DTO-014] ITENS DO PEDIDO
// =====================================================

    // [ORDERS-DTO-015] Lista de itens do pedido (inicializada vazia)
    private List<OrderItemDTO> items = new ArrayList<>();

// =====================================================
// [ORDERS-DTO-016] ENTIDADES RELACIONADAS (respostas detalhadas)
// =====================================================

    // [ORDERS-DTO-017] Dados completos do cliente (opcional, para respostas detalhadas)
    private CustomerDTO customer;

    // [ORDERS-DTO-018] Dados completos do funcionário (opcional, para respostas detalhadas)
    private EmployeeDTO employee;

    // Constructors
    public OrderDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderDTO orderDTO)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, orderDTO.id) && Objects.equals(employeeId, orderDTO.employeeId) && Objects.equals(customerId, orderDTO.customerId) && Objects.equals(totalAmount, orderDTO.totalAmount) && status == orderDTO.status && Objects.equals(createdAt, orderDTO.createdAt) && Objects.equals(updatedAt, orderDTO.updatedAt) && Objects.equals(qrCode, orderDTO.qrCode) && Objects.equals(barCode, orderDTO.barCode) && Objects.equals(items, orderDTO.items) && Objects.equals(customer, orderDTO.customer) && Objects.equals(employee, orderDTO.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, employeeId, customerId, totalAmount, status, createdAt, updatedAt, qrCode, barCode, items, customer, employee);
    }
}