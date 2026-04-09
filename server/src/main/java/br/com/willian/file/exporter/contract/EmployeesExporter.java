package br.com.willian.file.exporter.contract;

import br.com.willian.dto.v1.EmployeesDTO;
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
     * @param employeesDTOList Lista de funcionários
     * @return Resource contendo arquivo gerado
     */
    Resource exportEmployees(List<EmployeesDTO> employeesDTOList) throws Exception;

    /**
     * Exporta um único funcionário para arquivo.
     *
     * @param employeesDTO Funcionário a ser exportado
     * @return Resource contendo arquivo gerado
     */
    Resource exportEmployee(EmployeesDTO employeesDTO) throws Exception;
}