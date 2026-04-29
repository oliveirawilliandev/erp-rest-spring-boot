package br.com.willian.repository; // Pacote da camada de repository

import br.com.willian.model.OrderItem; // Entidade OrderItem
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA

import java.util.List; // Interface List

// [REPO-ORDER-ITEM-001] Interface repository para operações com OrderItem
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // [REPO-ORDER-ITEM-002] Busca todos os itens de um pedido específico
    List<OrderItem> findByOrderId(Long id); // ID do pedido

    // [REPO-ORDER-ITEM-003] Remove todos os itens de um pedido específico
    void deleteByOrderId(Long id); // ID do pedido
}