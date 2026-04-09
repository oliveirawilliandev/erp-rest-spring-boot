package br.com.willian.file.exporter.impl; // Pacote da implementação de exportadores

import br.com.willian.dto.v1.EmployeesDTO; // DTO de funcionários
import br.com.willian.file.exporter.contract.EmployeesExporter; // Contrato para exportadores
import org.apache.commons.csv.CSVFormat; // Formatação CSV
import org.apache.commons.csv.CSVPrinter; // Impressão CSV
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.core.io.ByteArrayResource; // Recurso em memória
import org.springframework.core.io.Resource; // Representação de recurso
import org.springframework.stereotype.Component; // Componente Spring

import java.io.ByteArrayOutputStream; // Stream para escrita em memória
import java.io.IOException; // Exceção de I/O
import java.io.OutputStreamWriter; // Writer para UTF-8
import java.nio.charset.StandardCharsets; // Charset UTF-8
import java.util.List; // Interface List

@Component // Define a classe como um componente Spring
public class CsvExporter implements EmployeesExporter { // Implementa o contrato de exportação

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(CsvExporter.class); // Logger para rastreamento

    // [EMP-EXPORTER-CSV-001]
    // Gera arquivo CSV com todos os funcionários
    @Override // Sobrescreve método da interface
    public Resource exportEmployees(List<EmployeesDTO> employeesDTOList) throws Exception {

        logger.info("[EMP-EXPORTER-CSV-001] Exportação CSV iniciada | totalRegistros={}",
                employeesDTOList != null ? employeesDTOList.size() : 0); // Log da quantidade

        // Validação da lista
        if (employeesDTOList == null || employeesDTOList.isEmpty()) {
            logger.warn("[EMP-EXPORTER-CSV-001] Lista de funcionários vazia ou nula | retornando CSV vazio"); // Log de aviso
        }

        long startTime = System.currentTimeMillis(); // Inicia contagem

        // Stream para escrever o CSV em memória
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // Buffer em memória
        logger.debug("[EMP-EXPORTER-CSV-001] ByteArrayOutputStream criado"); // Log da criação

        // 👇 adiciona BOM UTF-8 para compatibilidade com Excel
        outputStream.write(0xEF); // Primeiro byte do BOM
        outputStream.write(0xBB); // Segundo byte do BOM
        outputStream.write(0xBF); // Terceiro byte do BOM
        logger.debug("[EMP-EXPORTER-CSV-001] BOM UTF-8 adicionado (EF BB BF)"); // Log do BOM

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8); // Writer UTF-8
        logger.debug("[EMP-EXPORTER-CSV-001] OutputStreamWriter criado | charset=UTF-8"); // Log do writer

        // Configura formato CSV: cabeçalho com todos os campos do DTO
        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader( // Define cabeçalho com todos os campos
                        "ID", "First Name", "Last Name", "CPF", "Email", "Gender",
                        "Phone", "Mobile Phone", "Zip Code", "Street", "Street Number",
                        "Address Complement", "Neighborhood", "City", "State",
                        "Job Title", "Department", "Active", "Birth Date",
                        "Hire Date", "Termination Date", "Created At", "Updated At"
                )
                .setSkipHeaderRecord(false) // não pula o cabeçalho (inclui no arquivo)
                .build();

        logger.debug("[EMP-EXPORTER-CSV-001] CSVFormat configurado | headerCount={}", 23); // Log do formato

        int registrosProcessados = 0; // Contador para log

        // CSVPrinter auto close: garante que writer seja fechado
        try(CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)){ // Try-with-resources

            logger.debug("[EMP-EXPORTER-CSV-001] CSVPrinter inicializado"); // Log de inicialização

            // Para cada funcionário, imprime uma linha no CSV
            for(EmployeesDTO employee : employeesDTOList){
                csvPrinter.printRecord( // Imprime uma linha
                        employee.getId(), // ID
                        employee.getFirstName(), // Primeiro nome
                        employee.getLastName(), // Último nome
                        employee.getCpf(), // CPF
                        employee.getEmail(), // Email
                        employee.getGender(), // Gênero
                        employee.getPhone(), // Telefone fixo
                        employee.getMobilePhone(), // Telefone celular
                        employee.getZipCode(), // CEP
                        employee.getStreet(), // Logradouro
                        employee.getStreetNumber(), // Número
                        employee.getAddressComplement(), // Complemento
                        employee.getNeighborhood(), // Bairro
                        employee.getCity(), // Cidade
                        employee.getState(), // Estado
                        employee.getJobTitle(), // Cargo
                        employee.getDepartment(), // Departamento
                        employee.getActive(), // boolean vira "true"/"false" no CSV
                        employee.getBirthDate(), // Data de nascimento
                        employee.getHireDate(), // Data de contratação
                        employee.getTerminationDate(), // pode ser null (campo vazio)
                        employee.getCreatedAt(), // Data de criação
                        employee.getUpdatedAt() // pode ser null (campo vazio)
                );
                registrosProcessados++; // Incrementa contador

                // Log a cada 100 registros para não poluir
                if (registrosProcessados % 100 == 0) {
                    logger.debug("[EMP-EXPORTER-CSV-001] Processados {} registros", registrosProcessados); // Log progresso
                }
            }

            csvPrinter.flush(); // Força flush
            logger.debug("[EMP-EXPORTER-CSV-001] CSVPrinter finalizado | totalRegistros={}", registrosProcessados); // Log de conclusão
        } catch (IOException e) {
            logger.error("[EMP-EXPORTER-CSV-001] Erro ao escrever CSV | erro={}", e.getMessage(), e); // Log de erro
            throw new Exception("Erro ao gerar arquivo CSV", e); // Exceção
        }

        byte[] bytes = outputStream.toByteArray(); // Converte para array de bytes
        logger.debug("[EMP-EXPORTER-CSV-001] Bytes gerados | tamanho={} bytes", bytes.length); // Log do tamanho

        long endTime = System.currentTimeMillis(); // Finaliza contagem
        long duration = endTime - startTime; // Calcula duração

        logger.info("[EMP-EXPORTER-CSV-001] Exportação CSV concluída | totalRegistros={} | tamanho={} bytes | tempo={}ms",
                registrosProcessados, bytes.length, duration); // Log de sucesso

        // Converte bytes para Resource e retorna
        return new ByteArrayResource(bytes); // Retorna recurso
    }

    // [EMP-EXPORTER-CSV-002]
    // Exporta um único funcionário (reaproveita método de lista)
    @Override // Sobrescreve método da interface
    public Resource exportEmployee(EmployeesDTO employee) throws Exception {

        logger.info("[EMP-EXPORTER-CSV-002] Exportação CSV de funcionário único | id={} | nome={} {}",
                employee.getId(), employee.getFirstName(), employee.getLastName()); // Log detalhado

        if (employee == null) {
            logger.error("[EMP-EXPORTER-CSV-002] Funcionário nulo recebido para exportação"); // Log de erro
            throw new IllegalArgumentException("Funcionário não pode ser nulo"); // Exceção
        }

        long startTime = System.currentTimeMillis(); // Inicia contagem

        // Reaproveita o método de lista
        Resource resource = exportEmployees(List.of(employee)); // Chama método com lista de um elemento

        long endTime = System.currentTimeMillis(); // Finaliza contagem
        long duration = endTime - startTime; // Calcula duração

        logger.info("[EMP-EXPORTER-CSV-002] Exportação CSV de funcionário único concluída | id={} | tempo={}ms",
                employee.getId(), duration); // Log de sucesso

        return resource; // Retorna recurso
    }

    // [EMP-EXPORTER-CSV-003]
    // Método auxiliar para obter o tipo de mídia
    public String getMediaType() {
        logger.debug("[EMP-EXPORTER-CSV-003] Obtendo media type para CSV"); // Log da operação
        return "text/csv"; // Retorna media type do CSV
    }
}