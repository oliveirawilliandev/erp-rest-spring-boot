package br.com.willian.services;

import br.com.willian.dto.v1.EmployeesDTO;
import br.com.willian.exception.RequiredObjectIsNullException;
import br.com.willian.mapper.EmployeesMapper;
import br.com.willian.model.Employees;
import br.com.willian.repository.EmployeesRepository;
import br.com.willian.unitetests.mapper.MockEmployees;
import org.springframework.data.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

// os mock so vão dura nessa classe caso precise testa outra classe vai ser criado outro mock.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class EmployeesServiceTest {
    //classe moke de employees da pasta test não é o converto para entity to dto
    private MockEmployees mockEmployees;
    @Mock
    private PagedResourcesAssembler<EmployeesDTO> assembler;

    @Mock
    private EmployeesRepository employeesRepository;

    @InjectMocks
    private EmployeesService employeesService;

    @Mock
    private EmployeesMapper employeesMapper;
    @BeforeEach
    void setUp() {
        mockEmployees = new MockEmployees();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Employees entityEmployeesMock = mockEmployees.mockEntity(1);
        EmployeesDTO dtoEmployeesMock = mockEmployees.mockDTO(1);

        when(employeesRepository.findById(1L)).thenReturn(Optional.of(entityEmployeesMock));
        when(employeesMapper.toDTO(entityEmployeesMock)).thenReturn(dtoEmployeesMock);
        var result = employeesService.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().contains("/api/employee/v1/1")
                        && link.getType().equals("GET")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("GET")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findByName")
                        && link.getHref().contains("/api/employee/v1/findEmployeeByName")
                        && link.getType().equals("GET")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("POST")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("massCreation")
                        && link.getHref().contains("/api/employee/v1/massCreation")
                        && link.getType().equals("POST")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("PUT")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().contains("/api/employee/v1/1")
                        && link.getType().equals("DELETE")));

        assertEquals(1L, result.getId());
        assertEquals("First Name Test 1", result.getFirstName());
        assertEquals("Last Name Test 1", result.getLastName());
        assertEquals("12345678901", result.getCpf());
        assertEquals("employee1@email.com", result.getEmail());
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
        assertNotNull(result.getBirthDate());
        assertNotNull(result.getHireDate());
        assertNull(result.getTerminationDate());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void create() {

        Employees etityEmployeesMock = mockEmployees.mockEntity(1);
        EmployeesDTO dtoEmployeesMock = mockEmployees.mockDTO(1);

        Employees entityPersistedMock = etityEmployeesMock;
        EmployeesDTO dtoPersistedEmployeesMock = dtoEmployeesMock;


        when(employeesMapper.toEntity(dtoEmployeesMock)).thenReturn(etityEmployeesMock); //1
        when(employeesRepository.save(etityEmployeesMock)).thenReturn(entityPersistedMock);//2
        when(employeesMapper.toDTO(etityEmployeesMock)).thenReturn(dtoPersistedEmployeesMock); //3

        var result = employeesService.create(dtoEmployeesMock);


        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().contains("/api/employee/v1/1")
                        && link.getType().equals("GET")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("GET")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findByName")
                        && link.getHref().contains("/api/employee/v1/findEmployeeByName")
                        && link.getType().equals("GET")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("POST")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("massCreation")
                        && link.getHref().contains("/api/employee/v1/massCreation")
                        && link.getType().equals("POST")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("PUT")));

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().contains("/api/employee/v1/1")
                        && link.getType().equals("DELETE")));

        assertEquals(1L, result.getId());
        assertEquals("First Name Test 1", result.getFirstName());
        assertEquals("Last Name Test 1", result.getLastName());
        assertEquals("12345678901", result.getCpf());
        assertEquals("employee1@email.com", result.getEmail());
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
        assertNotNull(result.getBirthDate());
        assertNotNull(result.getHireDate());
        assertNull(result.getTerminationDate());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        
    }

    @Test
    void delete() {

        Employees etityEmployeesMock = mockEmployees.mockEntity(1);

        when(employeesRepository.findById(1L)).thenReturn(Optional.of(etityEmployeesMock));
        employeesService.delete(etityEmployeesMock.getId());
        //verifica que findbyId foi chamado so uma vez
        verify(employeesRepository, times(1)).findById(anyLong());        //verifica que findbyId foi chamado so uma vez
        //verifica que delete foi chamado so uma vez
        verify(employeesRepository, times(1)).delete(any(Employees.class));
        //verifica que nao teve nenhuma mais interação
        verifyNoMoreInteractions(employeesRepository);
    }


    @Test
    void findAll() {

        List<Employees> entityEmployeesMockList = mockEmployees.mockEntityList();
        List<EmployeesDTO> dtoEmployeesMockList = mockEmployees.mockDTOList();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Employees> pageEmployees =
                new PageImpl<>(entityEmployeesMockList, pageable, entityEmployeesMockList.size());

        Page<EmployeesDTO> pageEmployeesDTO =
                new PageImpl<>(dtoEmployeesMockList, pageable, entityEmployeesMockList.size());


        when(employeesRepository.findAll(pageable)).thenReturn(pageEmployees);
        when(employeesMapper.toDTOPage(pageEmployees)).thenReturn(pageEmployeesDTO);

        List<EntityModel<EmployeesDTO>> entityModels = dtoEmployeesMockList.stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                pageEmployees.getSize(),
                pageEmployees.getNumber(),
                pageEmployees.getTotalElements(),
                pageEmployees.getTotalPages()
        );

        PagedModel<EntityModel<EmployeesDTO>> mockPagedModel = PagedModel.of(entityModels, pageMetadata);
        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(mockPagedModel);


        PagedModel<EntityModel<EmployeesDTO>> listDtoMockEmployees =
                employeesService.findAll(pageable);
        assertNotNull(listDtoMockEmployees);
        assertEquals(14, listDtoMockEmployees.getContent().size());

        List<EntityModel<EmployeesDTO>> contentList =
                new ArrayList<>(listDtoMockEmployees.getContent());

        EntityModel<EmployeesDTO> entityModelOne = contentList.get(1);
        EmployeesDTO employeesOne = entityModelOne.getContent();



        assertNotNull(employeesOne);
        assertNotNull(employeesOne.getId());
        assertNotNull(employeesOne.getLinks());
        assertTrue(employeesOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().contains("/api/employee/v1/1")
                        && link.getType().equals("GET")));

        assertTrue(employeesOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("GET")));

        assertTrue(employeesOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findByName")
                        && link.getHref().contains("/api/employee/v1/findEmployeeByName")
                        && link.getType().equals("GET")));

        assertTrue(employeesOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("POST")));

        assertTrue(employeesOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("massCreation")
                        && link.getHref().contains("/api/employee/v1/massCreation")
                        && link.getType().equals("POST")));

        assertTrue(employeesOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("PUT")));

        assertTrue(employeesOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().contains("/api/employee/v1/1")
                        && link.getType().equals("DELETE")));

        assertEquals(1L, employeesOne.getId());
        assertEquals("First Name Test 1", employeesOne.getFirstName());
        assertEquals("Last Name Test 1", employeesOne.getLastName());
        assertEquals("12345678901", employeesOne.getCpf());
        assertEquals("employee1@email.com", employeesOne.getEmail());
        assertEquals("1133330001", employeesOne.getPhone());
        assertEquals("11999990001", employeesOne.getMobilePhone());
        assertEquals("01000-001", employeesOne.getZipCode());
        assertEquals("Test Street 1", employeesOne.getStreet());
        assertEquals("101", employeesOne.getStreetNumber());
        assertEquals("Apt 1", employeesOne.getAddressComplement());
        assertEquals("Test Neighborhood", employeesOne.getNeighborhood());
        assertEquals("São Paulo", employeesOne.getCity());
        assertEquals("SP", employeesOne.getState());
        assertEquals("Software Engineer", employeesOne.getJobTitle());
        assertEquals("Technology", employeesOne.getDepartment());
        assertEquals(true, employeesOne.getActive());
        assertNotNull(employeesOne.getBirthDate());
        assertNotNull(employeesOne.getHireDate());
        assertNull(employeesOne.getTerminationDate());
        assertNotNull(employeesOne.getCreatedAt());
        assertNotNull(employeesOne.getUpdatedAt());

        EntityModel<EmployeesDTO> entityModelSeven = contentList.get(7);
        EmployeesDTO employeesSeven = entityModelSeven.getContent();

        assertNotNull(employeesSeven);
        assertNotNull(employeesSeven.getId());
        assertNotNull(employeesSeven.getLinks());
        assertTrue(employeesSeven.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().contains("/api/employee/v1/7")
                        && link.getType().equals("GET")));

        assertTrue(employeesSeven.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("GET")));

        assertTrue(employeesSeven.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findByName")
                        && link.getHref().contains("/api/employee/v1/findEmployeeByName")
                        && link.getType().equals("GET")));

        assertTrue(employeesSeven.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("POST")));

        assertTrue(employeesSeven.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("massCreation")
                        && link.getHref().contains("/api/employee/v1/massCreation")
                        && link.getType().equals("POST")));

        assertTrue(employeesSeven.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("PUT")));

        assertTrue(employeesSeven.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().contains("/api/employee/v1/7")
                        && link.getType().equals("DELETE")));

        assertEquals(7L, employeesSeven.getId());
        assertEquals("First Name Test 7", employeesSeven.getFirstName());
        assertEquals("Last Name Test 7", employeesSeven.getLastName());
        assertEquals("12345678907", employeesSeven.getCpf());
        assertEquals("employee7@email.com", employeesSeven.getEmail());
        assertEquals("1133330007", employeesSeven.getPhone());
        assertEquals("11999990007", employeesSeven.getMobilePhone());
        assertEquals("01000-007", employeesSeven.getZipCode());
        assertEquals("Test Street 7", employeesSeven.getStreet());
        assertEquals("107", employeesSeven.getStreetNumber());
        assertEquals("Apt 7", employeesSeven.getAddressComplement());
        assertEquals("Test Neighborhood", employeesSeven.getNeighborhood());
        assertEquals("São Paulo", employeesSeven.getCity());
        assertEquals("SP", employeesSeven.getState());
        assertEquals("Software Engineer", employeesSeven.getJobTitle());
        assertEquals("Technology", employeesSeven.getDepartment());
        assertEquals(true, employeesSeven.getActive());
        assertNotNull(employeesSeven.getBirthDate());
        assertNotNull(employeesSeven.getHireDate());
        assertNull(employeesSeven.getTerminationDate());
        assertNotNull(employeesSeven.getCreatedAt());
        assertNotNull(employeesSeven.getUpdatedAt());

        EntityModel<EmployeesDTO> entityModelTwelve = contentList.get(12);
        EmployeesDTO employeesTwelve = entityModelTwelve.getContent();
        //var employeesTwelve = listDtoMockEmployees.get(12);

        assertNotNull(employeesTwelve);
        assertNotNull(employeesTwelve.getId());
        assertNotNull(employeesTwelve.getLinks());
        assertTrue(employeesTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().contains("/api/employee/v1/12")
                        && link.getType().equals("GET")));

        assertTrue(employeesTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("GET")));

        assertTrue(employeesTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findByName")
                        && link.getHref().contains("/api/employee/v1/findEmployeeByName")
                        && link.getType().equals("GET")));

        assertTrue(employeesTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("POST")));

        assertTrue(employeesTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("massCreation")
                        && link.getHref().contains("/api/employee/v1/massCreation")
                        && link.getType().equals("POST")));

        assertTrue(employeesTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().contains("/api/employee/v1")
                        && link.getType().equals("PUT")));

        assertTrue(employeesTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().contains("/api/employee/v1/12")
                        && link.getType().equals("DELETE")));

        assertEquals(12L, employeesTwelve.getId());
        assertEquals("First Name Test 12", employeesTwelve.getFirstName());
        assertEquals("Last Name Test 12", employeesTwelve.getLastName());
        assertEquals("123456789012", employeesTwelve.getCpf());
        assertEquals("employee12@email.com", employeesTwelve.getEmail());
        assertEquals("11333300012", employeesTwelve.getPhone());
        assertEquals("119999900012", employeesTwelve.getMobilePhone());
        assertEquals("01000-0012", employeesTwelve.getZipCode());
        assertEquals("Test Street 12", employeesTwelve.getStreet());
        assertEquals("1012", employeesTwelve.getStreetNumber());
        assertEquals("Apt 12", employeesTwelve.getAddressComplement());
        assertEquals("Test Neighborhood", employeesTwelve.getNeighborhood());
        assertEquals("São Paulo", employeesTwelve.getCity());
        assertEquals("SP", employeesTwelve.getState());
        assertEquals("Software Engineer", employeesTwelve.getJobTitle());
        assertEquals("Technology", employeesTwelve.getDepartment());
        assertEquals(true, employeesTwelve.getActive());
        assertNotNull(employeesTwelve.getBirthDate());
        assertNotNull(employeesTwelve.getHireDate());
        assertNull(employeesTwelve.getTerminationDate());
        assertNotNull(employeesTwelve.getCreatedAt());
        assertNotNull(employeesTwelve.getUpdatedAt());


    }

    @Test
    void testCreateWithNullEmployees() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            employeesService.create(null);
        });
        String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void testUpdateWithNullEmployees() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            employeesService.create(null);
        });
        String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Disabled("Reason: still under development exportEmployees")
    @Test
    void exportEmployees() {
    }
    @Disabled("Reason: still under development exportPage")
    @Test
    void exportPage() {

    }
    @Disabled("Reason: still under development massCreation")
    @Test
    void massCreation() {

    }
    @Disabled("Reason: still under development disableEmployees")
    @Test
    void disableEmployees() {

    }

}