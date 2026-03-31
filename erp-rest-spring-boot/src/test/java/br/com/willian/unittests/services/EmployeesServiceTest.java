package br.com.willian.unittests.services;

// DTO usado nos testes de retorno do serviço
import br.com.willian.dto.v1.EmployeesDTO;

// Exceção lançada quando um objeto obrigatório é nulo
import br.com.willian.exception.RequiredObjectIsNullException;

// Mapper responsável por converter Entity <-> DTO
import br.com.willian.mapper.EmployeesMapper;

// Entidade JPA
import br.com.willian.model.Employees;

// Repositório JPA mockado
import br.com.willian.repository.EmployeesRepository;

// Serviço que está sendo testado
import br.com.willian.services.EmployeesService;

// Classe utilitária de mocks (somente para testes)
import br.com.willian.unittests.mapper.MockEmployees;

// Dependências de paginação do Spring
import org.springframework.data.domain.*;

// JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

// Mockito
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

// HATEOAS
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

// Java
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Assertions e Mockito helpers
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// Define que os mocks vivem durante todo o ciclo da classe
// Isso evita recriação excessiva dos mocks
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

// Integra Mockito com JUnit 5
@ExtendWith(MockitoExtension.class)
class EmployeesServiceTest {

    // Classe auxiliar responsável por criar entidades e DTOs mockados
    // NÃO é um mapper real, apenas um gerador de dados fake
    private MockEmployees mockEmployees;

    // Assembler usado para montar respostas paginadas com HATEOAS
    @Mock
    private PagedResourcesAssembler<EmployeesDTO> assembler;

    // Repositório mockado (nenhuma chamada real ao banco)
    @Mock
    private EmployeesRepository employeesRepository;

    // Serviço real sendo testado, com dependências injetadas pelos mocks
    @InjectMocks
    private EmployeesService employeesService;

    // Mapper mockado para controlar conversões Entity <-> DTO
    @Mock
    private EmployeesMapper employeesMapper;

    // Executado antes de cada teste
    @BeforeEach
    void setUp() {
        // Inicializa os dados fake
        mockEmployees = new MockEmployees();

        // Inicializa os mocks do Mockito
        MockitoAnnotations.openMocks(this);
    }

    // Testa a busca de funcionário por ID
    @Test
    void findById() {

        // Cria entidade e DTO fake
        Employees entityEmployeesMock = mockEmployees.mockEntity(1);
        EmployeesDTO dtoEmployeesMock = mockEmployees.mockDTO(1);

        // Simula retorno do banco
        when(employeesRepository.findById(1L))
                .thenReturn(Optional.of(entityEmployeesMock));

        // Simula conversão Entity -> DTO
        when(employeesMapper.toDTO(entityEmployeesMock))
                .thenReturn(dtoEmployeesMock);

        // Executa o método real do serviço
        var result = employeesService.findById(1L);

        // Valida retorno básico
        assertNotNull(result);
        assertNotNull(result.getId());

        // Valida que os links HATEOAS foram criados
        assertNotNull(result.getLinks());

        // Valida link self
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().contains("/api/employee/v1/1")
                        && link.getType().equals("GET")));

        // Valida link findAll
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("GET")));

        // Valida link findByName
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findByName")
                        && link.getHref().contains("/api/employee/v1/findEmployeeByName")
                        && link.getType().equals("GET")));

        // Valida link create
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("POST")));

        // Valida link massCreation
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("massCreation")
                        && link.getHref().contains("/api/employee/v1/massCreation")
                        && link.getType().equals("POST")));

        // Valida link update
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("PUT")));

        // Valida link delete
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().contains("/api/employee/v1/1")
                        && link.getType().equals("DELETE")));

        // Valida dados do DTO
        assertEquals(1L, result.getId());
        assertEquals("First Name Test 1", result.getFirstName());
        assertEquals("Last Name Test 1", result.getLastName());
        assertEquals("12345678901", result.getCpf());
        assertEquals("employee1@email.com", result.getEmail());
        assertEquals("MALE",result.getGender());
        assertEquals("1133330001", result.getPhone());
        assertEquals("11999990001", result.getMobilePhone());
        assertEquals("01000-001", result.getZipCode());
        assertEquals("Test Street 1", result.getStreet());
        assertEquals("101", result.getStreetNumber());
        assertEquals("Apt 1", result.getAddressComplement());
        assertEquals("Test Neighborhood", result.getNeighborhood());
        assertEquals("São Paulo", result.getCity());
        assertEquals("SP", result.getState());
        assertEquals("Software Engineer", result.getJobTitle());
        assertEquals("Technology", result.getDepartment());
        assertEquals(true, result.getActive());

        // Datas obrigatórias
        assertNotNull(result.getBirthDate());
        assertNotNull(result.getHireDate());

        // TerminationDate deve ser nula para funcionário ativo
        assertNull(result.getTerminationDate());

        // Datas de auditoria
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    // Testa criação de funcionário
    @Test
    void create() {

        // Cria mocks
        Employees entityEmployeesMock = mockEmployees.mockEntity(1);
        EmployeesDTO dtoEmployeesMock = mockEmployees.mockDTO(1);

        // Simula fluxo:
        // DTO -> Entity -> Save -> DTO
        when(employeesMapper.toEntity(dtoEmployeesMock))
                .thenReturn(entityEmployeesMock);

        when(employeesRepository.save(entityEmployeesMock))
                .thenReturn(entityEmployeesMock);

        when(employeesMapper.toDTO(entityEmployeesMock))
                .thenReturn(dtoEmployeesMock);

        // Executa criação
        var result = employeesService.create(dtoEmployeesMock);

        // Valida retorno
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
    }

    // Testa remoção de funcionário
    @Test
    void delete() {

        // Cria entidade mockada
        Employees entityEmployeesMock = mockEmployees.mockEntity(1);

        // Simula busca no banco
        when(employeesRepository.findById(1L))
                .thenReturn(Optional.of(entityEmployeesMock));

        // Executa delete
        employeesService.delete(entityEmployeesMock.getId());

        // Verifica se findById foi chamado uma única vez
        verify(employeesRepository, times(1))
                .findById(anyLong());

        // Verifica se delete foi chamado uma única vez
        verify(employeesRepository, times(1))
                .delete(any(Employees.class));

        // Garante que não houve interações extras
        verifyNoMoreInteractions(employeesRepository);
    }

    // Testa listagem paginada com HATEOAS
    @Test
    void findAll() {

        // Cria listas mockadas
        List<Employees> entityEmployeesMockList = mockEmployees.mockEntityList();
        List<EmployeesDTO> dtoEmployeesMockList = mockEmployees.mockDTOList();

        // Configura paginação
        Pageable pageable = PageRequest.of(0, 10);

        // Página de entidades
        Page<Employees> pageEmployees =
                new PageImpl<>(entityEmployeesMockList, pageable, entityEmployeesMockList.size());

        // Página de DTOs
        Page<EmployeesDTO> pageEmployeesDTO =
                new PageImpl<>(dtoEmployeesMockList, pageable, entityEmployeesMockList.size());

        // Simula repository e mapper
        when(employeesRepository.findAll(pageable))
                .thenReturn(pageEmployees);

        when(employeesMapper.toDTOPage(pageEmployees))
                .thenReturn(pageEmployeesDTO);

        // Cria EntityModels (HATEOAS)
        List<EntityModel<EmployeesDTO>> entityModels =
                dtoEmployeesMockList.stream()
                        .map(EntityModel::of)
                        .collect(Collectors.toList());

        // Metadata da página
        PagedModel.PageMetadata pageMetadata =
                new PagedModel.PageMetadata(
                        pageEmployees.getSize(),
                        pageEmployees.getNumber(),
                        pageEmployees.getTotalElements(),
                        pageEmployees.getTotalPages()
                );

        // Modelo paginado HATEOAS
        PagedModel<EntityModel<EmployeesDTO>> mockPagedModel =
                PagedModel.of(entityModels, pageMetadata);

        when(assembler.toModel(any(Page.class), any(Link.class)))
                .thenReturn(mockPagedModel);

        // Executa o método real
        PagedModel<EntityModel<EmployeesDTO>> result =
                employeesService.findAll(pageable);

        // Valida retorno
        assertNotNull(result);
        assertEquals(14, result.getContent().size());
    }

    // Testa regra de negócio: não permitir criação com objeto nulo
    @Test
    void testCreateWithNullEmployees() {

        Exception exception = assertThrows(
                RequiredObjectIsNullException.class,
                () -> employeesService.create(null)
        );

        assertTrue(exception.getMessage()
                .contains("It is not allowed to persist a null object"));
    }

    // Testa update com objeto nulo
    @Test
    void testUpdateWithNullEmployees() {

        Exception exception = assertThrows(
                RequiredObjectIsNullException.class,
                () -> employeesService.create(null)
        );

        assertTrue(exception.getMessage()
                .contains("It is not allowed to persist a null object"));
    }

    // Testes desabilitados (funcionalidade ainda não implementada)

    @Disabled("Reason: still under development exportEmployees")
    @Test
    void exportEmployees() {}

    @Disabled("Reason: still under development exportPage")
    @Test
    void exportPage() {}

    @Disabled("Reason: still under development massCreation")
    @Test
    void massCreation() {}

    @Disabled("Reason: still under development disableEmployees")
    @Test
    void disableEmployees() {}
}
