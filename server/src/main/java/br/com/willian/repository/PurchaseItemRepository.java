package br.com.willian.repository; // Pacote da camada de repository

import br.com.willian.model.PurchaseItem; // Entidade PurchaseItem
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA

import java.util.List; // Interface List

// [REPO-PURCHASE-ITEM-001] Interface repository para operações com PurchaseItem
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    // [REPO-PURCHASE-ITEM-002] Busca todos os itens de uma compra específica
    List<PurchaseItem> findByPurchaseId(Long id); // ID da compra

    // [REPO-PURCHASE-ITEM-003] Remove todos os itens de uma compra específica
    void deleteByPurchaseId(Long id); // ID da compra
}