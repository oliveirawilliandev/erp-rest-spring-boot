package br.com.willian.file.importer.impl; // Pacote da implementação de importadores

import br.com.willian.dto.v1.EmployeesDTO; // DTO de funcionários
import br.com.willian.file.importer.contract.FileImporter; // Contrato para importadores
import br.com.willian.model.enums.Gender; // Enum de gêneros válidos
import org.apache.commons.csv.CSVFormat; // Formatação CSV
import org.apache.commons.csv.CSVRecord; // Registro CSV
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.stereotype.Component; // Componente Spring

import java.io.InputStream; // Stream para leitura de arquivos
import java.io.InputStreamReader; // Reader para UTF-8
import java.text.ParseException; // Exceção de parsing
import java.time.*; // Classes de data e hora
import java.util.ArrayList; // Implementação de List
import java.util.List; // Interface List

@Component // Define a classe como um componente Spring
public class CsvImporter extends AbstractFileImporter implements FileImporter { // Herda de AbstractFileImporter

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(CsvImporter.class); // Logger para rastreamento

    // [EMP-IMPORTER-CSV-001]
    // Importa dados de um arquivo CSV
    @Override // Sobrescreve método da interface
    public List<EmployeesDTO> importFile(InputStream inputStream) throws Exception {

        logger.info("[EMP-IMPORTER-CSV-001] Importação CSV iniciada"); // Log de início

        if (inputStream == null) {
            logger.error("[EMP-IMPORTER-CSV-001] InputStream nulo recebido"); // Log de erro
            throw new IllegalArgumentException("InputStream não pode ser nulo"); // Exceção
        }

        long startTime = System.currentTimeMillis(); // Inicia contagem

        // Configura o parser CSV: primeira linha como cabeçalho, ignora linhas vazias
        logger.debug("[EMP-IMPORTER-CSV-001] Configurando formato CSV"); // Log de configuração

        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()              // primeira linha vira cabeçalho
                .setSkipHeaderRecord(true)  // pula a linha do cabeçalho na leitura
                .setIgnoreEmptyLines(true)  // ignora linhas em branco
                .setTrim(true)              // remove espaços extras
                .build();

        logger.debug("[EMP-IMPORTER-CSV-001] Formato CSV configurado | skipHeader=true | ignoreEmpty=true | trim=true"); // Log do formato

        // Le o arquivo e converte para registros CSV
        logger.debug("[EMP-IMPORTER-CSV-001] Iniciando parsing do arquivo CSV"); // Log de início do parsing

        Iterable<CSVRecord> records = format.parse(new InputStreamReader(inputStream)); // Parse do arquivo

        // Converte registros para lista de DTOs
        List<EmployeesDTO> result = parseRecordsToEmployeesDTO(records); // Chama método de conversão

        long endTime = System.currentTimeMillis(); // Finaliza contagem
        long duration = endTime - startTime; // Calcula duração

        logger.info("[EMP-IMPORTER-CSV-001] Importação CSV concluída | totalRegistros={} | tempo={}ms",
                result.size(), duration); // Log de sucesso

        return result; // Retorna lista de DTOs
    }

    // [EMP-IMPORTER-CSV-002]
    // Converte cada linha do CSV para EmployeesDTO
    private List<EmployeesDTO> parseRecordsToEmployeesDTO(Iterable<CSVRecord> records) throws ParseException {

        logger.debug("[EMP-IMPORTER-CSV-002] Iniciando conversão de registros CSV para DTOs"); // Log de início

        List<EmployeesDTO> employeesDTOS = new ArrayList<>(); // Lista de DTOs
        int linhaAtual = 1; // Contador de linhas (começa em 1 pois cabeçalho é linha 0)
        int registrosProcessados = 0; // Contador de registros processados
        int errosEncontrados = 0; // Contador de erros

        for (CSVRecord record : records) { // para cada linha do arquivo
            linhaAtual++; // Incrementa contador de linha

            try {
                logger.debug("[EMP-IMPORTER-CSV-002] Processando linha {}", linhaAtual); // Log da linha atual

                EmployeesDTO employees = new EmployeesDTO(); // Cria novo DTO

                // Mapeia colunas do CSV pelos nomes do cabeçalho
                employees.setFirstName(record.get("firstName")); // Primeiro nome
                employees.setLastName(record.get("lastName")); // Último nome
                employees.setCpf(record.get("cpf")); // CPF
                employees.setEmail(record.get("email")); // Email

                // Valida e define gênero
                String genderValue = record.get("gender").toUpperCase(); // Gênero em maiúsculo
                logger.debug("[EMP-IMPORTER-CSV-002] Validando gênero | linha={} | gender={}", linhaAtual, genderValue); // Log do gênero
                employees.setGender(validateGender(genderValue)); // Valida e define

                employees.setPhone(record.get("phone")); // Telefone fixo
                employees.setMobilePhone(record.get("mobilePhone")); // Telefone celular
                employees.setZipCode(record.get("zipCode")); // CEP
                employees.setStreet(record.get("street")); // Logradouro
                employees.setStreetNumber(record.get("streetNumber")); // Número
                employees.setAddressComplement(record.get("addressComplement")); // Complemento
                employees.setNeighborhood(record.get("neighborhood")); // Bairro
                employees.setCity(record.get("city")); // Cidade

                String stateValue = record.get("state").toUpperCase(); // Estado em maiúsculo
                employees.setState(stateValue); // Define estado

                employees.setJobTitle(record.get("jobTitle")); // Cargo
                employees.setDepartment(record.get("department")); // Departamento

                // Converte datas usando constantes da classe pai
                logger.debug("[EMP-IMPORTER-CSV-002] Convertendo datas | linha={}", linhaAtual); // Log de conversão

                String birthDateStr = record.get("birthDate"); // Data de nascimento
                employees.setBirthDate(LocalDate.parse(birthDateStr, DATE_FORMAT)); // Converte com formato específico

                String hireDateStr = record.get("hireDate"); // Data de contratação
                employees.setHireDate(LocalDate.parse(hireDateStr)); // formato padrão ISO

                String terminationDateStr = record.get("terminationDate"); // Data de desligamento
                employees.setTerminationDate(LocalDate.parse(terminationDateStr, DATE_FORMAT)); // Converte com formato específico

                employees.setCreatedAt(OffsetDateTime.now()); // data atual

                String activeStr = record.get("active"); // Status ativo
                employees.setActive(Boolean.parseBoolean(activeStr)); // string "true"/"false"

                employeesDTOS.add(employees); // Adiciona à lista
                registrosProcessados++; // Incrementa contador de sucesso

                logger.debug("[EMP-IMPORTER-CSV-002] Linha {} processada com sucesso | email={}",
                        linhaAtual, employees.getEmail()); // Log de sucesso por linha

            } catch (Exception e) {
                errosEncontrados++; // Incrementa contador de erro
                logger.error("[EMP-IMPORTER-CSV-002] Erro ao processar linha {} | erro={}",
                        linhaAtual, e.getMessage(), e); // Log de erro detalhado
                // Continua com as próximas linhas mesmo com erro
            }
        }

        logger.debug("[EMP-IMPORTER-CSV-002] Conversão concluída | totalRegistros={} | erros={}",
                registrosProcessados, errosEncontrados); // Log de resumo

        if (errosEncontrados > 0) {
            logger.warn("[EMP-IMPORTER-CSV-002] Importação concluída com erros | registrosOK={} | erros={}",
                    registrosProcessados, errosEncontrados); // Log de aviso
        }

        return employeesDTOS; // Retorna lista de DTOs
    }
}