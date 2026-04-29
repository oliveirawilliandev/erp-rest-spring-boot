package br.com.willian.repository; // Pacote da camada de repository

import br.com.willian.model.Supplier; // Entidade Supplier
import org.springframework.data.domain.Page; // Página de resultados
import org.springframework.data.domain.Pageable; // Configuração de paginação
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA
import org.springframework.data.jpa.repository.Modifying; // Anotação para operações de modificação
import org.springframework.data.jpa.repository.Query; // Anotação para queries customizadas
import org.springframework.data.repository.query.Param; // Anotação para parâmetros de query
import org.springframework.transaction.annotation.Transactional; // Controle transacional

import java.util.Optional; // Container opcional

// [REPO-SUPP-001] Interface repository para operações com Supplier
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // [REPO-SUPP-002] Desativa um fornecedor por ID (active = false)
    @Modifying(clearAutomatically = true) // Indica operação de modificação, limpa o cache automaticamente
    @Query("UPDATE Supplier s SET s.active = false WHERE s.id = :id") // Query JPQL para desativação
    void deactivateSupplier(@Param("id") Long id); // ID do fornecedor a ser desativado

    // [REPO-SUPP-003] Ativa um fornecedor por ID (active = true)
    @Modifying(clearAutomatically = true) // Indica operação de modificação, limpa o cache
    @Transactional // Executa dentro de uma transação
    @Query("UPDATE Supplier s SET s.active = true WHERE s.id = :id") // Query JPQL para ativação
    void activateSupplier(@Param("id") Long id); // ID do fornecedor a ser ativado

    // [REPO-SUPP-004] Busca fornecedores por nome (case-insensitive, paginado)
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))") // Query JPQL com busca parcial
    Page<Supplier> findByNameContainingIgnoreCase(
            @Param("name") String name, // Nome do fornecedor (parcial)
            Pageable pageable // Configuração de paginação
    );

    // [REPO-SUPP-005] Busca fornecedor por documento (CNPJ/CPF)
    Optional<Supplier> findByDocument(String document); // Documento do fornecedor

    // [REPO-SUPP-006] Verifica se já existe um documento cadastrado
    boolean existsByDocument(String document); // Documento a ser verificado

    // [REPO-SUPP-007] Verifica se já existe um email cadastrado
    boolean existsByEmail(String email); // Email a ser verificado
}