package br.com.willian.file.exporter.impl; // Pacote da implementação de exportadores

import br.com.willian.dto.v1.EmployeeDTO;
import br.com.willian.file.exporter.contract.EmployeesExporter; // Contrato para exportadores
import org.apache.poi.ss.usermodel.*; // Classes do Apache POI para Excel
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Implementação para Excel XLSX
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.core.io.ByteArrayResource; // Recurso em memória
import org.springframework.core.io.Resource; // Representação de recurso
import org.springframework.stereotype.Component; // Componente Spring

import java.io.ByteArrayOutputStream; // Stream para escrita em memória
import java.util.List; // Interface List

@Component // Define a classe como um componente Spring
public class XlsxExporter implements EmployeesExporter { // Implementa o contrato de exportação

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(XlsxExporter.class); // Logger para rastreamento

    // [EMP-EXPORTER-XLSX-006]
    // Gera arquivo Excel com todos os funcionários
    @Override // Sobrescreve método da interface
    public Resource exportEmployees(List<EmployeeDTO> employeeDTOList) throws Exception {

        logger.info("[EMP-EXPORTER-XLSX-006] Exportação XLSX iniciada | totalRegistros={}",
                employeeDTOList != null ? employeeDTOList.size() : 0); // Log da quantidade

        // Validação da lista
        if (employeeDTOList == null || employeeDTOList.isEmpty()) {
            logger.warn("[EMP-EXPORTER-XLSX-006] Lista de funcionários vazia ou nula | gerando XLSX vazio"); // Log de aviso
        }

        long startTime = System.currentTimeMillis(); // Inicia contagem

        try (Workbook workbook = new XSSFWorkbook()) { // cria workbook Excel (try-with-resources)

            logger.debug("[EMP-EXPORTER-XLSX-006] Workbook XSSF criado"); // Log da criação

            Sheet sheet = workbook.createSheet("Employee"); // cria aba chamada "Employee"
            logger.debug("[EMP-EXPORTER-XLSX-006] Sheet 'Employee' criada"); // Log da aba

            // Cabeçalho com todos os campos do DTO
            String[] headers = {
                    "ID", "First Name", "Last Name", "CPF", "Email", "GenderType", "Phone", "Mobile Phone",
                    "Zip Code", "Street", "Street Number", "Address Complement", "Neighborhood",
                    "City", "State", "Job Title", "Department", "Active", "Birth Date",
                    "Hire Date", "Termination Date", "Created At", "Updated At"
            };
            logger.debug("[EMP-EXPORTER-XLSX-006] Cabeçalho definido | totalColunas={}", headers.length); // Log do cabeçalho

            // Linha do cabeçalho
            Row headerRow = sheet.createRow(0); // Primeira linha (índice 0)
            CellStyle headerStyle = createHeaderCellStyle(workbook); // Estilo do cabeçalho

            // Preenche células do cabeçalho
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i); // Cria célula
                cell.setCellValue(headers[i]); // Define valor
                cell.setCellStyle(headerStyle); // Aplica estilo
            }
            logger.debug("[EMP-EXPORTER-XLSX-006] Cabeçalho preenchido"); // Log de conclusão

            // Preenche dados dos funcionários
            int rowIndex = 1; // Começa na linha 1 (após cabeçalho)
            int registrosProcessados = 0; // Contador para log

            for (EmployeeDTO employee : employeeDTOList) {
                Row row = sheet.createRow(rowIndex++); // Cria nova linha

                // Mapeia campos do DTO para colunas
                row.createCell(0).setCellValue(employee.getId()); // ID (Long)
                row.createCell(1).setCellValue(employee.getFirstName()); // Primeiro nome
                row.createCell(2).setCellValue(employee.getLastName()); // Último nome
                row.createCell(3).setCellValue(employee.getCpf()); // CPF
                row.createCell(4).setCellValue(employee.getEmail()); // Email
                row.createCell(5).setCellValue(employee.getGender()); // Gênero
                row.createCell(6).setCellValue(employee.getPhone()); // Telefone fixo
                row.createCell(7).setCellValue(employee.getMobilePhone()); // Telefone celular
                row.createCell(8).setCellValue(employee.getZipCode()); // CEP
                row.createCell(9).setCellValue(employee.getStreet()); // Logradouro
                row.createCell(10).setCellValue(employee.getStreetNumber()); // Número
                row.createCell(11).setCellValue(employee.getAddressComplement()); // Complemento
                row.createCell(12).setCellValue(employee.getNeighborhood()); // Bairro
                row.createCell(13).setCellValue(employee.getCity()); // Cidade
                row.createCell(14).setCellValue(employee.getState()); // Estado
                row.createCell(15).setCellValue(employee.getJobTitle()); // Cargo
                row.createCell(16).setCellValue(employee.getDepartment()); // Departamento

                // Tratamento especial para booleano (Active)
                row.createCell(17).setCellValue(Boolean.TRUE.equals(employee.getActive()) ? "Yes" : "No"); // Yes/No em vez de true/false

                // Tratamento para datas nulas (evita NullPointerException)
                row.createCell(18).setCellValue(employee.getBirthDate() != null ? employee.getBirthDate().toString() : "" ); // Data nascimento
                row.createCell(19).setCellValue(employee.getHireDate() != null ? employee.getHireDate().toString() : ""); // Data contratação
                row.createCell(20).setCellValue(employee.getTerminationDate() != null ? employee.getTerminationDate().toString() : ""); // Data desligamento
                row.createCell(21).setCellValue(employee.getCreatedAt() != null ? employee.getCreatedAt().toString()  : ""); // Data criação
                row.createCell(22).setCellValue(employee.getUpdatedAt() != null ? employee.getUpdatedAt().toString() : ""); // Data atualização

                registrosProcessados++; // Incrementa contador

                // Log a cada 100 registros para não poluir
                if (registrosProcessados % 100 == 0) {
                    logger.debug("[EMP-EXPORTER-XLSX-006] Processados {} registros", registrosProcessados); // Log progresso
                }
            }

            logger.debug("[EMP-EXPORTER-XLSX-006] Dados preenchidos | totalRegistros={}", registrosProcessados); // Log de conclusão

            // Ajusta largura das colunas automaticamente
            logger.debug("[EMP-EXPORTER-XLSX-006] Ajustando largura das colunas..."); // Log de início
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i); // Auto-ajuste baseado no conteúdo
            }
            logger.debug("[EMP-EXPORTER-XLSX-006] Largura das colunas ajustada"); // Log de conclusão

            // Converte para Resource e retorna
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // Stream em memória
            workbook.write(outputStream); // Escreve workbook no stream

            byte[] bytes = outputStream.toByteArray(); // Converte para array de bytes
            logger.debug("[EMP-EXPORTER-XLSX-006] XLSX gerado | tamanho={} bytes", bytes.length); // Log do tamanho

            long endTime = System.currentTimeMillis(); // Finaliza contagem
            long duration = endTime - startTime; // Calcula duração

            logger.info("[EMP-EXPORTER-XLSX-006] Exportação XLSX concluída | totalRegistros={} | tamanho={} bytes | tempo={}ms",
                    registrosProcessados, bytes.length, duration); // Log de sucesso

            return new ByteArrayResource(bytes); // Retorna recurso

        } catch (Exception e) {
            logger.error("[EMP-EXPORTER-XLSX-006] Erro ao gerar arquivo XLSX | erro={}", e.getMessage(), e); // Log de erro
            throw new Exception("Erro ao gerar arquivo Excel", e); // Exceção
        }
    }

    // [EMP-EXPORTER-XLSX-007]
    // Exporta um único funcionário (reaproveita o método de lista)
    @Override // Sobrescreve método da interface
    public Resource exportEmployee(EmployeeDTO employee) throws Exception {

        logger.info("[EMP-EXPORTER-XLSX-007] Exportação XLSX de funcionário único | id={} | nome={} {}",
                employee.getId(), employee.getFirstName(), employee.getLastName()); // Log detalhado

        if (employee == null) {
            logger.error("[EMP-EXPORTER-XLSX-007] Funcionário nulo recebido para exportação"); // Log de erro
            throw new IllegalArgumentException("Funcionário não pode ser nulo"); // Exceção
        }

        long startTime = System.currentTimeMillis(); // Inicia contagem

        // Reaproveita o método de lista
        Resource resource = exportEmployees(List.of(employee)); // Chama método com lista de um elemento

        long endTime = System.currentTimeMillis(); // Finaliza contagem
        long duration = endTime - startTime; // Calcula duração

        logger.info("[EMP-EXPORTER-XLSX-007] Exportação XLSX de funcionário único concluída | id={} | tempo={}ms",
                employee.getId(), duration); // Log de sucesso

        return resource; // Retorna recurso
    }

    // [EMP-EXPORTER-XLSX-008]
    // Estilo negrito centralizado para cabeçalho
    private CellStyle createHeaderCellStyle(Workbook workbook) {
        logger.debug("[EMP-EXPORTER-XLSX-008] Criando estilo para cabeçalho"); // Log da criação

        CellStyle style = workbook.createCellStyle(); // Cria estilo
        Font font = workbook.createFont(); // Cria fonte
        font.setBold(true); // Define negrito
        style.setFont(font); // Aplica fonte
        style.setAlignment(HorizontalAlignment.CENTER); // Alinhamento centralizado

        logger.debug("[EMP-EXPORTER-XLSX-008] Estilo de cabeçalho criado | bold=true | alignment=CENTER"); // Log de conclusão

        return style; // Retorna estilo
    }
}