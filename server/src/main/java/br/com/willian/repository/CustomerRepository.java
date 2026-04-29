package br.com.willian.repository; // Pacote da camada de repository

import br.com.willian.model.Customer; // Entidade Customer
import org.springframework.data.domain.Page; // Página de resultados
import org.springframework.data.domain.Pageable; // Configuração de paginação
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA
import org.springframework.data.jpa.repository.Modifying; // Anotação para operações de modificação
import org.springframework.data.jpa.repository.Query; // Anotação para queries customizadas
import org.springframework.data.repository.query.Param; // Anotação para parâmetros de query
import org.springframework.transaction.annotation.Transactional; // Controle transacional

import java.util.Optional; // Container opcional

// [REPO-CUST-001] Interface repository para operações com Customer
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // [REPO-CUST-002] Desativa um cliente por ID (active = false)
    @Modifying(clearAutomatically = true) // Indica operação de modificação, limpa o cache automaticamente
    @Query("UPDATE Customer c SET c.active = false WHERE c.id = :id") // Query JPQL para desativação
    void deactivateCustomer(@Param("id") Long id); // ID do cliente a ser desativado

    // [REPO-CUST-003] Busca clientes por nome (case-insensitive, paginado)
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))") // Query JPQL com busca parcial
    Page<Customer> findByNameContainingIgnoreCase(
            @Param("name") String name, // Nome do cliente (parcial)
            Pageable pageable // Configuração de paginação
    );

    // [REPO-CUST-004] Busca cliente por email
    Optional<Customer> findByEmail(String email); // Email do cliente

    // [REPO-CUST-005] Busca cliente por documento (CPF/CNPJ)
    Optional<Customer> findByDocument(String document); // Documento do cliente

    // [REPO-CUST-006] Ativa um cliente por ID (active = true)
    @Modifying // Indica operação de modificação
    @Transactional // Executa dentro de uma transação
    @Query("UPDATE Customer c SET c.active = true WHERE c.id = :id") // Query JPQL para ativação
    void activateCustomer(Long id); // ID do cliente a ser ativado

    // [REPO-CUST-007] Verifica se já existe um documento cadastrado
    boolean existsByDocument(String document); // Documento a ser verificado

    // [REPO-CUST-008] Verifica se já existe um email cadastrado
    boolean existsByEmail(String email); // Email a ser verificado
}