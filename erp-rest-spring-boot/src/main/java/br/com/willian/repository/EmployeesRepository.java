package br.com.willian.repository;

import br.com.willian.model.Employees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Repository responsável pelo acesso aos dados da entidade Employees.
// Estende JpaRepository para herdar operações CRUD básicas.
@Repository
public interface EmployeesRepository extends JpaRepository<Employees, Long> {

    // Desativa logicamente um funcionário
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
    @Query("UPDATE Employees e SET e.active = false WHERE e.id = :id")
    void disableEmployee(@Param("id") Long id);

    // Busca funcionários pelo primeiro nome de forma parcial.
    //
    // Detalhes da consulta:
    // - Usa LIKE para permitir busca por parte do nome.
    // - LOWER garante busca case-insensitive.
    // - CONCAT permite montar o padrão %nome%.
    //
    // Exemplo:
    // firstName = "jo"
    // Retorna: João, JONAS, joana
    //
    // Pageable permite:
    // - Paginação (page, size)
    // - Ordenação (sort)
    @Query("SELECT e FROM Employees e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    Page<Employees> findEmployeesByName(
            @Param("firstName") String firstName,
            Pageable pageable
    );

}
