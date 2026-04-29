package br.com.willian.repository; // Pacote da camada de repository

import br.com.willian.model.Product; // Entidade Product
import org.springframework.data.domain.Page; // Página de resultados
import org.springframework.data.domain.Pageable; // Configuração de paginação
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA
import org.springframework.data.jpa.repository.Modifying; // Anotação para operações de modificação
import org.springframework.data.jpa.repository.Query; // Anotação para queries customizadas
import org.springframework.data.repository.query.Param; // Anotação para parâmetros de query
import org.springframework.transaction.annotation.Transactional; // Controle transacional

// [REPO-PROD-001] Interface repository para operações com Product
public interface ProductRepository extends JpaRepository<Product, Long> {

    // [REPO-PROD-002] Desativa um produto por ID (active = false)
    @Modifying(clearAutomatically = true) // Indica operação de modificação, limpa o cache automaticamente
    @Query("UPDATE Product p SET p.active = false WHERE p.id = :id") // Query JPQL para desativação
    void deactivateProduct(@Param("id") Long id); // ID do produto a ser desativado

    // [REPO-PROD-003] Ativa um produto por ID (active = true)
    @Modifying(clearAutomatically = true) // Indica operação de modificação, limpa o cache
    @Transactional // Executa dentro de uma transação
    @Query("UPDATE Product p SET p.active = true WHERE p.id = :id") // Query JPQL para ativação
    void activateProduct(@Param("id") Long id); // ID do produto a ser ativado

    // [REPO-PROD-004] Atualiza a quantidade em estoque de um produto
    @Modifying // Indica operação de modificação
    @Transactional // Executa dentro de uma transação
    @Query("UPDATE Product p SET p.stockQuantity = :quantity WHERE p.id = :id") // Query JPQL para atualização de estoque
    void updateStock(
            @Param("id") Long id, // ID do produto
            @Param("quantity") Integer quantity // Nova quantidade em estoque
    );

    // [REPO-PROD-005] Busca produtos por nome (case-insensitive, paginado)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))") // Query JPQL com busca parcial
    Page<Product> findByNameContainingIgnoreCase(
            @Param("name") String name, // Nome do produto (parcial)
            Pageable pageable // Configuração de paginação
    );

    // [REPO-PROD-006] Busca produtos por status ativo/inativo (paginado)
    Page<Product> findByActive(Boolean active, Pageable pageable); // Status (true/false) + paginação

    // [REPO-PROD-007] Busca produtos com estoque abaixo do limite (paginado)
    Page<Product> findByStockQuantityLessThan(Integer threshold, Pageable pageable); // Limite mínimo de estoque + paginação

    // [REPO-PROD-008] Verifica se já existe um produto com o nome informado
    boolean existsByName(String name); // Nome do produto a ser verificado
}