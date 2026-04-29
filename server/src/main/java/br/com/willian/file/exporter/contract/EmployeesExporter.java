package br.com.willian.file.exporter.contract;

import br.com.willian.dto.v1.EmployeeDTO;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Contrato para exportadores de dados de funcionários.
 *
 * Implementações: PDF, Excel, CSV, etc.
 */
public interface EmployeesExporter {

    /**
     * Exporta lista de funcionários para arquivo.
     *
     * @param employeeDTOList Lista de funcionários
     * @return Resource contendo arquivo gerado
     */
    Resource exportEmployees(List<EmployeeDTO> employeeDTOList) throws Exception;

    /**
     * Exporta um único funcionário para arquivo.
     *
     * @param employeeDTO Funcionário a ser exportado
     * @return Resource contendo arquivo gerado
     */
    Resource exportEmployee(EmployeeDTO employeeDTO) throws Exception;
}