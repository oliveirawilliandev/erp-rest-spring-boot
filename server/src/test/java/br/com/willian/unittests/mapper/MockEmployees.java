package br.com.willian.unittests.mapper;

import br.com.willian.dto.v1.EmployeeDTO;
import br.com.willian.model.Employee;
import br.com.willian.model.enums.GenderType;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

// Classe mock utilizada exclusivamente para testes do MapStruct.
// Seu objetivo é validar se as conversões entre Employee e EmployeeDTO
// estão sendo realizadas corretamente pelo mapper.

public class MockEmployees {

    public Employee mockEntity() {
        return mockEntity(0);
    }

    public EmployeeDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Employee> mockEntityList() {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            employees.add(mockEntity(i));
        }
        return employees;
    }

    public List<EmployeeDTO> mockDTOList() {
        List<EmployeeDTO> employees = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            employees.add(mockDTO(i));
        }
        return employees;
    }

    public Employee mockEntity(Integer number) {
        Employee employee = new Employee();

        employee.setId(number.longValue());
        employee.setFirstName("First Name Test " + number);
        employee.setLastName("Last Name Test " + number);
        employee.setCpf("1234567890" + number);
        employee.setEmail("employee" + number + "@email.com");
        employee.setGender(GenderType.MALE);
        employee.setPhone("113333000" + number);
        employee.setMobilePhone("1199999000" + number);

        // Endereço
        employee.setZipCode("01000-00" + number);
        employee.setStreet("Test Street " + number);
        employee.setStreetNumber("10" + number);
        employee.setAddressComplement("Apt " + number);
        employee.setNeighborhood("Test Neighborhood");
        employee.setCity("São Paulo");
        employee.setState("SP");

        // Profissional
        employee.setJobTitle("Software Engineer");
        employee.setDepartment("Technology");
        employee.setActive(true);

        // Datas
        employee.setBirthDate(LocalDate.of(1990,1,1)); // 01/01/1990
        employee.setHireDate(LocalDate.now());
        employee.setTerminationDate(null);
        employee.setCreatedAt(Instant.now());
        employee.setUpdatedAt(Instant.now());

        return employee;
    }

    public EmployeeDTO mockDTO(Integer number) {
        EmployeeDTO employees = new EmployeeDTO();

        employees.setId(number.longValue());
        employees.setFirstName("First Name Test " + number);
        employees.setLastName("Last Name Test " + number);
        employees.setCpf("1234567890" + number);
        employees.setEmail("employee" + number + "@email.com");
        employees.setGender("MALE");
        employees.setPhone("113333000" + number);
        employees.setMobilePhone("1199999000" + number);

        // Endereço
        employees.setZipCode("01000-00" + number);
        employees.setStreet("Test Street " + number);
        employees.setStreetNumber("10" + number);
        employees.setAddressComplement("Apt " + number);
        employees.setNeighborhood("Test Neighborhood");
        employees.setCity("São Paulo");
        employees.setState("SP");

        // Profissional
        employees.setJobTitle("Software Engineer");
        employees.setDepartment("Technology");
        employees.setActive(true);

        // Datas
        employees.setBirthDate(LocalDate.of(1990,1,1)); // 01/01/1990
        employees.setHireDate(LocalDate.now());
        employees.setTerminationDate(null);
        employees.setCreatedAt(OffsetDateTime.now());
        employees.setUpdatedAt(OffsetDateTime.now());

        return employees;
    }
}
