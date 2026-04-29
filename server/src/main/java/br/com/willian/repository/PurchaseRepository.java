package br.com.willian.repository; // Pacote da camada de repository

import br.com.willian.model.Purchase; // Entidade Purchase
import br.com.willian.model.enums.PurchaseStatus; // Enum de status da compra
import org.springframework.data.domain.Page; // Página de resultados
import org.springframework.data.domain.Pageable; // Configuração de paginação
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA

// [REPO-PURCHASE-001] Interface repository para operações com Purchase
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // [REPO-PURCHASE-002] Busca compras por ID do fornecedor (paginado)
    Page<Purchase> findBySupplierId(Long supplierId, Pageable pageable); // ID do fornecedor + paginação

    // [REPO-PURCHASE-003] Busca compras por ID do funcionário responsável (paginado)
    Page<Purchase> findByEmployeeId(Long employeeId, Pageable pageable); // ID do funcionário + paginação

    // [REPO-PURCHASE-004] Busca compras por status (paginado)
    Page<Purchase> findByStatus(PurchaseStatus status, Pageable pageable); // Status da compra + paginação
}