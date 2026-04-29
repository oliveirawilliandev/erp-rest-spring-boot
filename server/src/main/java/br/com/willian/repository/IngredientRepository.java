package br.com.willian.repository;

import br.com.willian.model.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * [REPO-ING-001] Desativa um insumo por ID
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Ingredient i SET i.active = false WHERE i.id = :id")
    void deactivateIngredient(@Param("id") Long id);

    /**
     * [REPO-ING-002] Ativa um insumo por ID
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Ingredient i SET i.active = true WHERE i.id = :id")
    void activateIngredient(@Param("id") Long id);

    /**
     * [REPO-ING-003] Atualiza a quantidade em estoque de um insumo
     */
    @Modifying
    @Transactional
    @Query("UPDATE Ingredient i SET i.stockQuantity = :quantity WHERE i.id = :id")
    void updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * [REPO-ING-004] Busca insumos por nome (case-insensitive)
     */
    @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Ingredient> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * [REPO-ING-005] Busca insumos por status ativo/inativo
     */
    Page<Ingredient> findByActive(Boolean active, Pageable pageable);

    /**
     * [REPO-ING-006] Busca insumos com estoque abaixo do mínimo
     */
    @Query("SELECT i FROM Ingredient i WHERE i.stockQuantity < i.minimumStock")
    Page<Ingredient> findLowStock(Pageable pageable);

    /**
     * [REPO-ING-007] Busca insumos com estoque abaixo do limite informado
     */
    Page<Ingredient> findByStockQuantityLessThan(Integer threshold, Pageable pageable);

    /**
     * [REPO-ING-008] Verifica se já existe um insumo com o nome informado
     */
    boolean existsByName(String name);

    /**
     * [REPO-ING-009] Busca insumos por fornecedor preferencial
     */
    Page<Ingredient> findByPreferredSupplierId(Long supplierId, Pageable pageable);

    /**
     * [REPO-ING-010] Busca insumos com estoque crítico (abaixo de 20% do mínimo)
     */
    @Query("SELECT i FROM Ingredient i WHERE i.stockQuantity < (i.minimumStock * 0.2)")
    Page<Ingredient> findCriticalStock(Pageable pageable);
}