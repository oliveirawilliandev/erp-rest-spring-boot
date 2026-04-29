package br.com.willian.repository;

import br.com.willian.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.willian.model.Employee;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class) // Habilita integração do JUnit 5 com o contexto do Spring
@DataJpaTest // Configura teste focado na camada JPA (Repositories)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usa o banco real (Testcontainers)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Garante execução dos testes na ordem definida
class EmployeeRepositoryTest extends AbstractIntegrationTest { // Base comum de testes com container

    @Autowired
    EmployeeRepository employeesRepository; // Repositório JPA a ser testado

    private static Employee employee; // Entidade compartilhada entre os testes

    @BeforeAll
    static void setUp() {
        employee = new Employee(); // Inicializa objeto base antes da execução dos testes
    }

    @Test
    @Order(1)
    void findEmployeesByName() {

        Pageable pageable = PageRequest.of(
                0,                                  // Página inicial (index começa em 0)
                4,                                  // Quantidade máxima de registros por página
                Sort.by(Sort.Direction.ASC,         // Ordenação crescente
                        "firstName")                // Campo usado na ordenação
        );

        // Executa busca paginada por funcionários cujo nome contenha "a"
        employee = employeesRepository
                .findEmployeesByName("a", pageable) // Chamada ao método customizado do repositório
                .getContent()                        // Obtém o conteúdo da página
                .get(0);                             // Recupera o primeiro registro

        // ============================
        // Validações do funcionário
        // ============================

        assertNotNull(employee);                    // Funcionário não deve ser nulo
        assertNotNull(employee.getId());            // ID deve existir
        assertEquals("Ana", employee.getFirstName());
        assertEquals("Pereira", employee.getLastName());
        assertEquals("São Paulo", employee.getCity());
        assertEquals("Assistente", employee.getJobTitle());
        assertTrue(employee.getActive());            // Funcionário deve estar ativo
    }

    @Test
    @Order(2)
    void disableEmployees() {

        Long id = employee.getId();                  // Recupera ID do funcionário criado no teste anterior

        employeesRepository.disableEmployee(id);      // Executa atualização lógica (disable)

        var result = employeesRepository.findById(id); // Busca novamente o funcionário no banco
        employee = result.get();                      // Obtém entidade atualizada

        // ============================
        // Validações após o disable
        // ============================

        assertNotNull(employee);
        assertNotNull(employee.getId());
        assertEquals("Ana", employee.getFirstName());
        assertEquals("Pereira", employee.getLastName());
        assertEquals("São Paulo", employee.getCity());
        assertEquals("Assistente", employee.getJobTitle());

        assertFalse(employee.getActive());            // Funcionário deve estar desativado
    }
}
