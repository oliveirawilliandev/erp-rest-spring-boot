package br.com.willian.unitetests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import br.com.willian.dto.v1.EmployeesDTO;
import br.com.willian.mapper.EmployeesMapper;
import br.com.willian.model.Employees;
import br.com.willian.unitetests.mapper.MockEmployees;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
// Classe mock utilizada exclusivamente para testes do MapStruct.
// Seu objetivo é validar se as conversões entre Employees e EmployeesDTO
// estão sendo realizadas corretamente pelo mapper.

public class TestsMapStruct {

    MockEmployees inputObject;
    EmployeesMapper mapStruct;

    @BeforeEach
    public void setUp() {
        inputObject = new MockEmployees();
        mapStruct = Mappers.getMapper(EmployeesMapper.class);
    }

    @Test
    public void parseEntityToDTOTest() {
        EmployeesDTO employeesDTO = mapStruct.toDTO(inputObject.mockEntity());

        assertNotNull(employeesDTO);
        assertEquals(0L, employeesDTO.getId());
        assertEquals("First Name Test 0", employeesDTO.getFirstName());
        assertEquals("Last Name Test 0", employeesDTO.getLastName());
        assertEquals("12345678900", employeesDTO.getCpf());
        assertEquals("employee0@email.com", employeesDTO.getEmail());
        assertEquals("1133330000", employeesDTO.getPhone());
        assertEquals("11999990000", employeesDTO.getMobilePhone());
        assertEquals("01000-000", employeesDTO.getZipCode());
        assertEquals("Test Street 0", employeesDTO.getStreet());
        assertEquals("100", employeesDTO.getStreetNumber());
        assertEquals("Apt 0", employeesDTO.getAddressComplement());
        assertEquals("Test Neighborhood", employeesDTO.getNeighborhood());
        assertEquals("São Paulo", employeesDTO.getCity());
        assertEquals("SP", employeesDTO.getState());
        assertEquals("Software Engineer", employeesDTO.getJobTitle());
        assertEquals("Technology", employeesDTO.getDepartment());
        assertEquals(true, employeesDTO.getActive());
        assertNotNull(employeesDTO.getBirthDate());
        assertNotNull(employeesDTO.getHireDate());
        assertNull(employeesDTO.getTerminationDate());
        assertNotNull(employeesDTO.getCreatedAt());
        assertNotNull(employeesDTO.getUpdatedAt());
    }

    @Test
    public void parseEntityListToDTOListTest() {

        List<EmployeesDTO> outputList = mapStruct.toDTOList(inputObject.mockEntityList());

        // Teste para posição 0
        EmployeesDTO outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());
        assertEquals("First Name Test 0", outputZero.getFirstName());
        assertEquals("Last Name Test 0", outputZero.getLastName());
        assertEquals("12345678900", outputZero.getCpf());
        assertEquals("employee0@email.com", outputZero.getEmail());
        assertEquals("1133330000", outputZero.getPhone());
        assertEquals("11999990000", outputZero.getMobilePhone());
        assertEquals("01000-000", outputZero.getZipCode());
        assertEquals("Test Street 0", outputZero.getStreet());
        assertEquals("100", outputZero.getStreetNumber());
        assertEquals("Apt 0", outputZero.getAddressComplement());
        assertEquals("Test Neighborhood", outputZero.getNeighborhood());
        assertEquals("São Paulo", outputZero.getCity());
        assertEquals("SP", outputZero.getState());
        assertEquals("Software Engineer", outputZero.getJobTitle());
        assertEquals("Technology", outputZero.getDepartment());
        assertEquals(true, outputZero.getActive());
        assertNotNull(outputZero.getBirthDate());
        assertNotNull(outputZero.getHireDate());
        assertNull(outputZero.getTerminationDate());
        assertNotNull(outputZero.getCreatedAt());
        assertNotNull(outputZero.getUpdatedAt());

        // Teste para posição 7 com TODOS os atributos
        EmployeesDTO outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
        assertEquals("First Name Test 7", outputSeven.getFirstName());
        assertEquals("Last Name Test 7", outputSeven.getLastName());
        assertEquals("12345678907", outputSeven.getCpf());
        assertEquals("employee7@email.com", outputSeven.getEmail());
        assertEquals("1133330007", outputSeven.getPhone());
        assertEquals("11999990007", outputSeven.getMobilePhone());
        assertEquals("01000-007", outputSeven.getZipCode());
        assertEquals("Test Street 7", outputSeven.getStreet());
        assertEquals("107", outputSeven.getStreetNumber());
        assertEquals("Apt 7", outputSeven.getAddressComplement());
        assertEquals("Test Neighborhood", outputSeven.getNeighborhood());
        assertEquals("São Paulo", outputSeven.getCity());
        assertEquals("SP", outputSeven.getState());
        assertEquals("Software Engineer", outputSeven.getJobTitle());
        assertEquals("Technology", outputSeven.getDepartment());
        assertEquals(true, outputSeven.getActive());
        assertNotNull(outputSeven.getBirthDate());
        assertNotNull(outputSeven.getHireDate());
        assertNull(outputSeven.getTerminationDate());
        assertNotNull(outputSeven.getCreatedAt());
        assertNotNull(outputSeven.getUpdatedAt());

        // Teste para posição 12 com TODOS os atributos
        EmployeesDTO outputTwelve = outputList.get(12);
        assertEquals(12L, outputTwelve.getId());
        assertEquals("First Name Test 12", outputTwelve.getFirstName());
        assertEquals("Last Name Test 12", outputTwelve.getLastName());
        assertEquals("123456789012", outputTwelve.getCpf());
        assertEquals("employee12@email.com", outputTwelve.getEmail());
        assertEquals("11333300012", outputTwelve.getPhone());
        assertEquals("119999900012", outputTwelve.getMobilePhone());
        assertEquals("01000-0012", outputTwelve.getZipCode());
        assertEquals("Test Street 12", outputTwelve.getStreet());
        assertEquals("1012", outputTwelve.getStreetNumber());
        assertEquals("Apt 12", outputTwelve.getAddressComplement());
        assertEquals("Test Neighborhood", outputTwelve.getNeighborhood());
        assertEquals("São Paulo", outputTwelve.getCity());
        assertEquals("SP", outputTwelve.getState());
        assertEquals("Software Engineer", outputTwelve.getJobTitle());
        assertEquals("Technology", outputTwelve.getDepartment());
        assertEquals(true, outputTwelve.getActive());
        assertNotNull(outputTwelve.getBirthDate());
        assertNotNull(outputTwelve.getHireDate());
        assertNull(outputTwelve.getTerminationDate());
        assertNotNull(outputTwelve.getCreatedAt());
        assertNotNull(outputTwelve.getUpdatedAt());
    }

    @Test
    public void parseDTOToEntityTest() {
        Employees output = mapStruct.toEntity(inputObject.mockDTO());

        assertNotNull(output);
        assertEquals(0L, output.getId());
        assertEquals("First Name Test 0", output.getFirstName());
        assertEquals("Last Name Test 0", output.getLastName());
        assertEquals("12345678900", output.getCpf());
        assertEquals("employee0@email.com", output.getEmail());
        assertEquals("1133330000", output.getPhone());
        assertEquals("11999990000", output.getMobilePhone());
        assertEquals("01000-000", output.getZipCode());
        assertEquals("Test Street 0", output.getStreet());
        assertEquals("100", output.getStreetNumber());
        assertEquals("Apt 0", output.getAddressComplement());
        assertEquals("Test Neighborhood", output.getNeighborhood());
        assertEquals("São Paulo", output.getCity());
        assertEquals("SP", output.getState());
        assertEquals("Software Engineer", output.getJobTitle());
        assertEquals("Technology", output.getDepartment());
        assertEquals(true, output.getActive());
        assertNotNull(output.getBirthDate());
        assertNotNull(output.getHireDate());
        assertNull(output.getTerminationDate());
        assertNotNull(output.getCreatedAt());
        assertNotNull(output.getUpdatedAt());
    }

    @Test
    public void parserDTOListToEntityListTest() {

        List<Employees> outputList = mapStruct.toEntityList(inputObject.mockDTOList());

        // Teste para posição 0
        Employees outputZero = outputList.get(0);
        assertEquals(0L, outputZero.getId());
        assertEquals("First Name Test 0", outputZero.getFirstName());
        assertEquals("Last Name Test 0", outputZero.getLastName());
        assertEquals("12345678900", outputZero.getCpf());
        assertEquals("employee0@email.com", outputZero.getEmail());
        assertEquals("1133330000", outputZero.getPhone());
        assertEquals("11999990000", outputZero.getMobilePhone());
        assertEquals("01000-000", outputZero.getZipCode());
        assertEquals("Test Street 0", outputZero.getStreet());
        assertEquals("100", outputZero.getStreetNumber());
        assertEquals("Apt 0", outputZero.getAddressComplement());
        assertEquals("Test Neighborhood", outputZero.getNeighborhood());
        assertEquals("São Paulo", outputZero.getCity());
        assertEquals("SP", outputZero.getState());
        assertEquals("Software Engineer", outputZero.getJobTitle());
        assertEquals("Technology", outputZero.getDepartment());
        assertEquals(true, outputZero.getActive());
        assertNotNull(outputZero.getBirthDate());
        assertNotNull(outputZero.getHireDate());
        assertNull(outputZero.getTerminationDate());
        assertNotNull(outputZero.getCreatedAt());
        assertNotNull(outputZero.getUpdatedAt());

        // Teste para posição 7 com TODOS os atributos
        Employees outputSeven = outputList.get(7);
        assertEquals(7L, outputSeven.getId());
        assertEquals("First Name Test 7", outputSeven.getFirstName());
        assertEquals("Last Name Test 7", outputSeven.getLastName());
        assertEquals("12345678907", outputSeven.getCpf());
        assertEquals("employee7@email.com", outputSeven.getEmail());
        assertEquals("1133330007", outputSeven.getPhone());
        assertEquals("11999990007", outputSeven.getMobilePhone());
        assertEquals("01000-007", outputSeven.getZipCode());
        assertEquals("Test Street 7", outputSeven.getStreet());
        assertEquals("107", outputSeven.getStreetNumber());
        assertEquals("Apt 7", outputSeven.getAddressComplement());
        assertEquals("Test Neighborhood", outputSeven.getNeighborhood());
        assertEquals("São Paulo", outputSeven.getCity());
        assertEquals("SP", outputSeven.getState());
        assertEquals("Software Engineer", outputSeven.getJobTitle());
        assertEquals("Technology", outputSeven.getDepartment());
        assertEquals(true, outputSeven.getActive());
        assertNotNull(outputSeven.getBirthDate());
        assertNotNull(outputSeven.getHireDate());
        assertNull(outputSeven.getTerminationDate());
        assertNotNull(outputSeven.getCreatedAt());
        assertNotNull(outputSeven.getUpdatedAt());

        // Teste para posição 12 com TODOS os atributos
        Employees outputTwelve = outputList.get(12);
        assertEquals(12L, outputTwelve.getId());
        assertEquals("First Name Test 12", outputTwelve.getFirstName());
        assertEquals("Last Name Test 12", outputTwelve.getLastName());
        assertEquals("123456789012", outputTwelve.getCpf());
        assertEquals("employee12@email.com", outputTwelve.getEmail());
        assertEquals("11333300012", outputTwelve.getPhone());
        assertEquals("119999900012", outputTwelve.getMobilePhone());
        assertEquals("01000-0012", outputTwelve.getZipCode());
        assertEquals("Test Street 12", outputTwelve.getStreet());
        assertEquals("1012", outputTwelve.getStreetNumber());
        assertEquals("Apt 12", outputTwelve.getAddressComplement());
        assertEquals("Test Neighborhood", outputTwelve.getNeighborhood());
        assertEquals("São Paulo", outputTwelve.getCity());
        assertEquals("SP", outputTwelve.getState());
        assertEquals("Software Engineer", outputTwelve.getJobTitle());
        assertEquals("Technology", outputTwelve.getDepartment());
        assertEquals(true, outputTwelve.getActive());
        assertNotNull(outputTwelve.getBirthDate());
        assertNotNull(outputTwelve.getHireDate());
        assertNull(outputTwelve.getTerminationDate());
        assertNotNull(outputTwelve.getCreatedAt());
        assertNotNull(outputTwelve.getUpdatedAt());
    }
}