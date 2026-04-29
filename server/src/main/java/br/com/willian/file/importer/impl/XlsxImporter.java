package br.com.willian.file.importer.impl; // Pacote da implementação de importadores

import br.com.willian.dto.v1.EmployeeDTO;
import br.com.willian.file.importer.contract.FileImporter; // Contrato para importadores
import org.apache.poi.ss.usermodel.Row; // Linha do Excel
import org.apache.poi.xssf.usermodel.XSSFSheet; // Aba do Excel
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Workbook Excel
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.stereotype.Component; // Componente Spring

import java.io.InputStream; // Stream para leitura de arquivos
import java.text.ParseException; // Exceção de parsing
import java.time.OffsetDateTime; // Data/hora com offset
import java.util.ArrayList; // Implementação de List
import java.util.Iterator; // Iterator para percorrer linhas
import java.util.List; // Interface List

@Component // Define a classe como um componente Spring
public class XlsxImporter extends AbstractFileImporter implements FileImporter { // Herda de AbstractFileImporter

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(XlsxImporter.class); // Logger para rastreamento

    // [EMP-IMPORTER-XLSX-015]
    // Importa dados de um arquivo XLSX
    @Override // Sobrescreve método da interface
    public List<EmployeeDTO> importFile(InputStream inputStream) throws Exception {

        logger.info("[EMP-IMPORTER-XLSX-015] Importação XLSX iniciada"); // Log de início

        if (inputStream == null) {
            logger.error("[EMP-IMPORTER-XLSX-015] InputStream nulo recebido"); // Log de erro
            throw new IllegalArgumentException("InputStream não pode ser nulo"); // Exceção
        }

        long startTime = System.currentTimeMillis(); // Inicia contagem

        // Carrega workbook Excel e fecha automaticamente (try-with-resources)
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            logger.debug("[EMP-IMPORTER-XLSX-015] Workbook carregado com sucesso"); // Log do carregamento

            XSSFSheet sheet = workbook.getSheetAt(0);  // primeira aba da planilha
            logger.debug("[EMP-IMPORTER-XLSX-015] Aba selecionada | nome={} | índice=0", sheet.getSheetName()); // Log da aba

            Iterator<Row> rowIterator = sheet.iterator(); // linhas da planilha
            logger.debug("[EMP-IMPORTER-XLSX-015] Iterator de linhas obtido"); // Log do iterator

            Row headerRow = null;
            int totalLinhas = 0;

            if (rowIterator.hasNext()) {
                headerRow = rowIterator.next(); // salva o cabeçalho em headerRow e pula o cabeçalho.
                logger.debug("[EMP-IMPORTER-XLSX-015] Cabeçalho processado | primeira célula='{}'",
                        formatter.formatCellValue(headerRow.getCell(0))); // Log do cabeçalho

                // Conta quantas linhas de dados existem (para log)
                Iterator<Row> countIterator = sheet.iterator();
                countIterator.next(); // pula cabeçalho
                while (countIterator.hasNext()) {
                    countIterator.next();
                    totalLinhas++;
                }
                logger.debug("[EMP-IMPORTER-XLSX-015] Total de linhas de dados detectado: {}", totalLinhas); // Log do total
            } else {
                logger.warn("[EMP-IMPORTER-XLSX-015] Planilha vazia | nenhuma linha encontrada"); // Log de aviso
            }

            // Processa linhas
            List<EmployeeDTO> result = parseRowsToEmployeesDtoList(rowIterator, headerRow); // processa linhas

            long endTime = System.currentTimeMillis(); // Finaliza contagem
            long duration = endTime - startTime; // Calcula duração

            logger.info("[EMP-IMPORTER-XLSX-015] Importação XLSX concluída | totalRegistros={} | linhosProcessadas={} | tempo={}ms",
                    result.size(), totalLinhas, duration); // Log de sucesso

            return result; // Retorna lista de DTOs
        } catch (Exception e) {
            logger.error("[EMP-IMPORTER-XLSX-015] Erro na importação XLSX | erro={}", e.getMessage(), e); // Log de erro
            throw e; // Relança exceção
        }
    }

    // [EMP-IMPORTER-XLSX-016]
    // Converte todas as linhas validas em DTOs
    private List<EmployeeDTO> parseRowsToEmployeesDtoList(Iterator<Row> rowIterator, Row headerRow) throws ParseException {

        logger.debug("[EMP-IMPORTER-XLSX-016] Iniciando conversão de linhas para DTOs"); // Log de início

        List<EmployeeDTO> employeeDTOS = new ArrayList<>(); // Lista de DTOs
        int linhaAtual = 1; // Contador de linhas (cabeçalho é linha 0)
        int registrosProcessados = 0; // Contador de registros processados
        int errosEncontrados = 0; // Contador de erros

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next(); // Próxima linha
            linhaAtual++; // Incrementa contador

            try {
                if (isRowValid(row)) { // metodo da classe pai: verifica se linha tem dados
                    logger.debug("[EMP-IMPORTER-XLSX-016] Processando linha {}", linhaAtual); // Log da linha

                    EmployeeDTO dto = parseRowsToEmployeesDto(row, headerRow); // Converte linha para DTO
                    employeeDTOS.add(dto); // Adiciona à lista
                    registrosProcessados++; // Incrementa contador de sucesso

                    logger.debug("[EMP-IMPORTER-XLSX-016] Linha {} processada com sucesso | email={}",
                            linhaAtual, dto.getEmail()); // Log de sucesso por linha

                    // Log a cada 100 registros para não poluir
                    if (registrosProcessados % 100 == 0) {
                        logger.debug("[EMP-IMPORTER-XLSX-016] Processados {} registros", registrosProcessados); // Log progresso
                    }
                } else {
                    logger.debug("[EMP-IMPORTER-XLSX-016] Linha {} ignorada (inválida ou vazia)", linhaAtual); // Log de linha ignorada
                }
            } catch (Exception e) {
                errosEncontrados++; // Incrementa contador de erro
                logger.error("[EMP-IMPORTER-XLSX-016] Erro ao processar linha {} | erro={}",
                        linhaAtual, e.getMessage(), e); // Log de erro detalhado
                // Continua com as próximas linhas mesmo com erro
            }
        }

        logger.debug("[EMP-IMPORTER-XLSX-016] Conversão concluída | registrosOK={} | erros={} | linhasProcessadas={}",
                registrosProcessados, errosEncontrados, linhaAtual); // Log de resumo

        if (errosEncontrados > 0) {
            logger.warn("[EMP-IMPORTER-XLSX-016] Importação concluída com erros | registrosOK={} | erros={}",
                    registrosProcessados, errosEncontrados); // Log de aviso
        }

        return employeeDTOS; // Retorna lista de DTOs
    }

    // [EMP-IMPORTER-XLSX-017]
    // Converte uma linha do Excel para EmployeeDTO
    private EmployeeDTO parseRowsToEmployeesDto(Row row, Row headerRow) throws ParseException {

        logger.debug("[EMP-IMPORTER-XLSX-017] Convertendo linha para DTO | row={}", row.getRowNum()); // Log da conversão

        EmployeeDTO employees = new EmployeeDTO(); // Cria novo DTO

        // Descobre se a primeira coluna é ID olhando o cabeçalho
        String primeiraColuna = formatter.formatCellValue(headerRow.getCell(0)).toLowerCase(); // Valor da primeira célula
        int offset = primeiraColuna.equals("id") ? 1 : 0; // se for ID, pula essa coluna
        logger.debug("[EMP-IMPORTER-XLSX-017] Offset calculado | primeiraColuna='{}' | offset={}", primeiraColuna, offset); // Log do offset

        // Usa formatter da classe pai para extrair valores (funciona para texto, numero, data)
        employees.setFirstName(formatter.formatCellValue(row.getCell(offset))); // Primeiro nome
        employees.setLastName(formatter.formatCellValue(row.getCell(1 + offset))); // Último nome
        employees.setCpf(formatter.formatCellValue(row.getCell(2 + offset))); // aceita CPF como numero ou string
        employees.setEmail(formatter.formatCellValue(row.getCell(3 + offset))); // Email

        String genderValue = formatter.formatCellValue(row.getCell(4 + offset)).toUpperCase(); // Gênero em maiúsculo
        logger.debug("[EMP-IMPORTER-XLSX-017] Validando gênero | gender={}", genderValue); // Log do gênero
        employees.setGender(validateGender(genderValue)); // Valida e define

        employees.setPhone(formatter.formatCellValue(row.getCell(5 + offset))); // Telefone fixo
        employees.setMobilePhone(formatter.formatCellValue(row.getCell(6 + offset))); // Telefone celular
        employees.setZipCode(formatter.formatCellValue(row.getCell(7 + offset))); // CEP
        employees.setStreet(formatter.formatCellValue(row.getCell(8 + offset))); // Logradouro
        employees.setStreetNumber(formatter.formatCellValue(row.getCell(9 + offset))); // numero vira string
        employees.setAddressComplement(formatter.formatCellValue(row.getCell(10 + offset))); // Complemento
        employees.setNeighborhood(formatter.formatCellValue(row.getCell(11 + offset))); // Bairro
        employees.setCity(formatter.formatCellValue(row.getCell(12 + offset))); // Cidade

        String stateValue = formatter.formatCellValue(row.getCell(13 + offset)).toUpperCase(); // Estado em maiúsculo
        employees.setState(stateValue); // Define estado

        employees.setJobTitle(formatter.formatCellValue(row.getCell(14 + offset))); // Cargo
        employees.setDepartment(formatter.formatCellValue(row.getCell(15 + offset))); // Departamento

        // Datas usam metodo da classe pai (getLocalDateFromCell)
        logger.debug("[EMP-IMPORTER-XLSX-017] Extraindo datas"); // Log de extração de datas

        employees.setBirthDate(getLocalDateFromCell(row.getCell(16 + offset))); // Data nascimento
        employees.setHireDate(getLocalDateFromCell(row.getCell(17 + offset))); // Data contratação
        employees.setTerminationDate(getLocalDateFromCell(row.getCell(18 + offset))); // Data desligamento

        employees.setCreatedAt(OffsetDateTime.now()); // data/hora atual

        String activeStr = formatter.formatCellValue(row.getCell(21 + offset)); // Status ativo
        employees.setActive(Boolean.parseBoolean(activeStr)); // "true"/"false"

        logger.debug("[EMP-IMPORTER-XLSX-017] DTO preenchido | email={} | active={}", employees.getEmail(), employees.getActive()); // Log do resultado

        return employees; // Retorna DTO
    }
}