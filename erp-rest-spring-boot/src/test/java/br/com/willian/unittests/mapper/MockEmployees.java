package br.com.willian.unittests.mapper;

import br.com.willian.dto.v1.EmployeesDTO;
import br.com.willian.model.Employees;
import br.com.willian.model.enums.Gender;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Classe mock utilizada exclusivamente para testes do MapStruct.
// Seu objetivo é validar se as conversões entre Employees e EmployeesDTO
// estão sendo realizadas corretamente pelo mapper.

public class MockEmployees {

    public Employees mockEntity() {
        return mockEntity(0);
    }

    public EmployeesDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Employees> mockEntityList() {
        List<Employees> employees = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            employees.add(mockEntity(i));
        }
        return employees;
    }

    public List<EmployeesDTO> mockDTOList() {
        List<EmployeesDTO> employees = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            employees.add(mockDTO(i));
        }
        return employees;
    }

    public Employees mockEntity(Integer number) {
        Employees employees = new Employees();

        employees.setId(number.longValue());
        employees.setFirstName("First Name Test " + number);
        employees.setLastName("Last Name Test " + number);
        employees.setCpf("1234567890" + number);
        employees.setEmail("employee" + number + "@email.com");
        employees.setGender(Gender.MALE);
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
        employees.setCreatedAt(Instant.now());
        employees.setUpdatedAt(Instant.now());

        return employees;
    }

    public EmployeesDTO mockDTO(Integer number) {
        EmployeesDTO employees = new EmployeesDTO();

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
