package br.com.willian.repository; // Pacote da camada de repository

import br.com.willian.model.Order; // Entidade Order
import br.com.willian.model.enums.OrderStatus; // Enum de status do pedido
import org.springframework.data.domain.Page; // Página de resultados
import org.springframework.data.domain.Pageable; // Configuração de paginação
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA

// [REPO-ORDER-001] Interface repository para operações com Order
public interface OrderRepository extends JpaRepository<Order, Long> {

    // [REPO-ORDER-002] Busca pedidos por ID do cliente (paginado)
    Page<Order> findByCustomerId(Long customerId, Pageable pageable); // ID do cliente + paginação

    // [REPO-ORDER-003] Busca pedidos por status (paginado)
    Page<Order> findByStatus(OrderStatus status, Pageable pageable); // Status do pedido + paginação

    // [REPO-ORDER-004] Busca pedidos por ID do funcionário (paginado)
    Page<Order> findByEmployeeId(Long employeeId, Pageable pageable); // ID do funcionário + paginação
}