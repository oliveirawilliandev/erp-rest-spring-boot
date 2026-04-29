package br.com.willian.repository;

import br.com.willian.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// Repository responsável pelo acesso aos dados da entidade Employee.
// Estende JpaRepository para herdar operações CRUD básicas.
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // [REPO-EMP-001] Desativa logicamente um funcionário
    // Não remove o registro do banco, apenas seta active = false.
    //
    // IMPORTANTE:
    // - Este metodo executa um UPDATE direto no banco.
    // - Precisa ser chamado dentro de uma transação.
    // - O @Transactional deve estar no Service que chama este metodo.
    //
    // clearAutomatically = true:
    // - Limpa o contexto de persistência após o UPDATE.
    // - Evita que entidades em cache fiquem com dados desatualizados.
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Employee e SET e.active = false WHERE e.id = :id")
    void disableEmployee(@Param("id") Long id);

    // [REPO-EMP-002] Ativa um funcionário por ID (active = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Employee e SET e.active = true WHERE e.id = :id")
    void activateEmployee(@Param("id") Long id);

    // [REPO-EMP-003] Busca funcionário por CPF
    Optional<Employee> findByCpf(String cpf);

    // [REPO-EMP-004] Busca funcionário por email
    Optional<Employee> findByEmail(String email);

    // [REPO-EMP-005] Verifica se já existe um CPF cadastrado
    boolean existsByCpf(String cpf);

    // [REPO-EMP-006] Verifica se já existe um email cadastrado
    boolean existsByEmail(String email);

    // [REPO-EMP-007] Busca funcionários por termo textual (primeiro nome, sobrenome ou nome completo)
    //
    // Detalhes da consulta:
    // - Usa LIKE para permitir busca por parte do termo.
    // - LOWER garante busca case-insensitive.
    // - CONCAT permite montar o padrão %termo%.
    //
    // Exemplo:
    // name = "jo"
    // Retorna: João Silva, JONAS Souza, Maria Joana
    //
    // Pageable permite:
    // - Paginação (page, size)
    // - Ordenação (sort)
    @Query("""
    SELECT e
    FROM Employee e
    WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
       OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
       OR LOWER(CONCAT(e.firstName, ' ', e.lastName))
          LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    Page<Employee> findEmployeesByName(
            @Param("name") String name,
            Pageable pageable
    );

    // [REPO-EMP-008] Busca funcionários ativos
    Page<Employee> findByActiveTrue(Pageable pageable);

    // [REPO-EMP-009] Busca funcionários inativos
    Page<Employee> findByActiveFalse(Pageable pageable);

    // [REPO-EMP-010] Busca funcionários por departamento
    Page<Employee> findByDepartmentIgnoreCase(String department, Pageable pageable);

    // [REPO-EMP-011] Busca funcionários por cargo
        Page<Employee> findByJobTitleIgnoreCase(String jobTitle, Pageable pageable);


}