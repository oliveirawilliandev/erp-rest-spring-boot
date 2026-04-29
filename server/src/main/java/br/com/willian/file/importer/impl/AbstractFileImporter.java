package br.com.willian.file.importer.impl; // Pacote da implementação de importadores

import br.com.willian.exception.InvalidGenderException; // Exceção para gênero inválido
import br.com.willian.model.enums.GenderType; // Enum de gêneros válidos
import org.apache.poi.ss.usermodel.Cell; // Célula do Excel
import org.apache.poi.ss.usermodel.CellType; // Tipo de célula do Excel
import org.apache.poi.ss.usermodel.DataFormatter; // Formatador de dados do Excel
import org.apache.poi.ss.usermodel.Row; // Linha do Excel
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers

import java.time.*; // Classes de data e hora
import java.time.format.DateTimeFormatter; // Formatador de datas
import java.util.Arrays; // Utilitário para arrays
import java.util.stream.Collectors; // Utilitário para streams

/**
 * Classe base abstrata para importadores de arquivos (CSV, XLSX).
 * Fornece métodos utilitários comuns para manipulação de datas e células.
 */
public abstract class AbstractFileImporter {

    // Logger para rastreamento (usando nome da classe atual para melhor contexto)
    private static final Logger logger = LoggerFactory.getLogger(AbstractFileImporter.class); // Logger para rastreamento

    // [EMP-IMPORTER-ABSTRACT-004]
    // Formato: yyyy-MM-dd
    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Formato para datas sem hora

    // [EMP-IMPORTER-ABSTRACT-005]
    // Formato: aceita data com ou sem hora
    protected static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd[ H:mm[:ss]]"); // Formato flexível

    // [EMP-IMPORTER-ABSTRACT-006]
    // Utilitario POI para extrair valor de celulas Excel
    protected static final DataFormatter formatter = new DataFormatter(); // Formatador para células Excel

    // [EMP-IMPORTER-ABSTRACT-007]
    // Fuso padrao Brasil (pode ser alterado)
    protected ZoneId currentZone = ZoneId.of("America/Sao_Paulo"); // Fuso horário padrão

    // [EMP-IMPORTER-ABSTRACT-008]
    // Permite trocar o fuso antes da importacao
    public void setCurrentZone(ZoneId zone) {
        logger.debug("[EMP-IMPORTER-ABSTRACT-008] Alterando fuso horário | novoFuso={} | anterior={}",
                zone, this.currentZone); // Log da alteração

        if (zone != null) {
            this.currentZone = zone; // Atualiza fuso
            logger.info("[EMP-IMPORTER-ABSTRACT-008] Fuso horário alterado com sucesso | novoFuso={}", zone); // Log de sucesso
        } else {
            logger.warn("[EMP-IMPORTER-ABSTRACT-008] Tentativa de alterar fuso com valor nulo | fuso mantido={}",
                    this.currentZone); // Log de aviso
        }
    }

    // [EMP-IMPORTER-ABSTRACT-009]
    public ZoneId getCurrentZone() {
        logger.debug("[EMP-IMPORTER-ABSTRACT-009] Obtendo fuso horário atual | fuso={}", currentZone); // Log da consulta
        return currentZone; // Retorna fuso atual
    }

    // [EMP-IMPORTER-ABSTRACT-010]
    // Converte string CSV para OffsetDateTime usando fuso atual
    protected OffsetDateTime parseCsvDate(String value) {
        logger.debug("[EMP-IMPORTER-ABSTRACT-010] Parseando data CSV | value={} | fuso={}", value, currentZone); // Log da operação

        if (value == null || value.isBlank()) {
            logger.debug("[EMP-IMPORTER-ABSTRACT-010] Valor nulo ou vazio, retornando null"); // Log de valor vazio
            return null; // Retorna nulo para valores vazios
        }

        try {
            if (value.contains(" ")) {
                // Data com hora
                logger.debug("[EMP-IMPORTER-ABSTRACT-010] Detectado formato com hora"); // Log do formato
                LocalDateTime ldt = LocalDateTime.parse(value, CSV_DATE_FORMAT); // Parse com formato flexível
                // return ldt.atZone(ZoneId.systemDefault()).toOffsetDateTime(); // em produção precisa verificar isso melhor.
                OffsetDateTime result = ldt.atZone(this.currentZone).toOffsetDateTime(); // Converte usando fuso configurado
                logger.debug("[EMP-IMPORTER-ABSTRACT-010] Data com hora parseada | result={}", result); // Log do resultado
                return result; // Retorna OffsetDateTime
            }

            // Data sem hora
            logger.debug("[EMP-IMPORTER-ABSTRACT-010] Detectado formato sem hora"); // Log do formato
            LocalDate ld = LocalDate.parse(value, DATE_FORMAT); // Parse com formato de data
            //return ld.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
            OffsetDateTime result = ld.atStartOfDay(this.currentZone).toOffsetDateTime(); // Início do dia no fuso configurado
            logger.debug("[EMP-IMPORTER-ABSTRACT-010] Data sem hora parseada | result={}", result); // Log do resultado
            return result; // Retorna OffsetDateTime

        } catch (Exception e) {
            logger.error("[EMP-IMPORTER-ABSTRACT-010] Erro ao parsear data CSV | value={} | erro={}",
                    value, e.getMessage(), e); // Log de erro
            throw new IllegalArgumentException("Erro ao parsear data: " + value, e); // Exceção
        }
    }

    // [EMP-IMPORTER-ABSTRACT-011]
    // Extrai LocalDate de celula Excel (sem hora)
    protected LocalDate getLocalDateFromCell(Cell cell) {
        logger.debug("[EMP-IMPORTER-ABSTRACT-011] Extraindo LocalDate de célula Excel"); // Log da operação

        if (cell == null) {
            logger.error("[EMP-IMPORTER-ABSTRACT-011] Célula nula fornecida"); // Log de erro
            throw new IllegalArgumentException("The date cell is empty."); // Exceção
        }

        logger.debug("[EMP-IMPORTER-ABSTRACT-011] Célula | row={} | col={} | type={}",
                cell.getRowIndex(), cell.getColumnIndex(), cell.getCellType()); // Log da posição da célula

        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            LocalDate result = cell.getDateCellValue() // Obtém valor da data
                    .toInstant() // Converte para Instant
                    .atZone(ZoneId.systemDefault()) // Converte para ZonedDateTime com fuso do sistema
                    .toLocalDate(); // Extrai LocalDate

            logger.debug("[EMP-IMPORTER-ABSTRACT-011] LocalDate extraído com sucesso | result={}", result); // Log do resultado
            return result; // Retorna LocalDate
        }

        logger.error("[EMP-IMPORTER-ABSTRACT-011] Célula não contém data válida | valor={}", formatter.formatCellValue(cell)); // Log de erro
        throw new IllegalArgumentException("The cell does not contain a valid date: " + cell); // Exceção
    }

    // [EMP-IMPORTER-ABSTRACT-012]
    // Extrai OffsetDateTime de celula Excel (com hora)
    protected OffsetDateTime getOffsetDateTimeFromCell(Cell cell) {
        logger.debug("[EMP-IMPORTER-ABSTRACT-012] Extraindo OffsetDateTime de célula Excel"); // Log da operação

        if (cell == null) {
            logger.error("[EMP-IMPORTER-ABSTRACT-012] Célula nula fornecida"); // Log de erro
            throw new IllegalArgumentException("The date cell is empty."); // Exceção
        }

        logger.debug("[EMP-IMPORTER-ABSTRACT-012] Célula | row={} | col={} | type={}",
                cell.getRowIndex(), cell.getColumnIndex(), cell.getCellType()); // Log da posição da célula

        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            OffsetDateTime result = cell.getDateCellValue() // Obtém valor da data
                    .toInstant() // Converte para Instant
                    .atZone(ZoneId.systemDefault()) // Converte para ZonedDateTime com fuso do sistema
                    .toOffsetDateTime(); // Converte para OffsetDateTime

            logger.debug("[EMP-IMPORTER-ABSTRACT-012] OffsetDateTime extraído com sucesso | result={}", result); // Log do resultado
            return result; // Retorna OffsetDateTime
        }

        logger.error("[EMP-IMPORTER-ABSTRACT-012] Célula não contém data válida | valor={}", formatter.formatCellValue(cell)); // Log de erro
        throw new IllegalArgumentException("The cell does not contain a valid date: " + cell); // Exceção
    }

    // [EMP-IMPORTER-ABSTRACT-013]
    // Verifica se linha tem dados (primeira celula nao vazia)
    protected static boolean isRowValid(Row row) {
        logger.debug("[EMP-IMPORTER-ABSTRACT-013] Verificando validade da linha | rowIndex={}",
                row != null ? row.getRowNum() : null); // Log da verificação

        if (row == null) {
            logger.warn("[EMP-IMPORTER-ABSTRACT-013] Linha nula recebida"); // Log de aviso
            return false; // Linha nula é inválida
        }

        Cell firstCell = row.getCell(0); // Primeira célula
        boolean isValid = firstCell != null && firstCell.getCellType() != CellType.BLANK; // Verifica se existe e não está vazia

        logger.debug("[EMP-IMPORTER-ABSTRACT-013] Resultado da validação | row={} | isValid={}",
                row.getRowNum(), isValid); // Log do resultado

        return isValid; // Retorna resultado
    }

    /**
     * [EMP-IMPORTER-ABSTRACT-014]
     * Valida se o gênero informado é um valor válido do enum GenderType
     * @param gender Gênero a ser validado (case insensitive)
     * @throws InvalidGenderException se o gênero for nulo ou inválido
     */
    protected static String validateGender(String gender) {
        logger.debug("[EMP-IMPORTER-ABSTRACT-014] Validando gênero | gender={}", gender); // Log da validação

        try {
            if (gender == null) {
                logger.error("[EMP-IMPORTER-ABSTRACT-014] Gênero nulo"); // Log de erro
                throw new InvalidGenderException("Gênero não pode ser nulo"); // Exceção
            }

            GenderType.valueOf(gender.toUpperCase()); // Tenta converter para enum
            logger.debug("[EMP-IMPORTER-ABSTRACT-014] Gênero validado com sucesso: {}", gender); // Log de sucesso
            return gender; // Retorna gênero

        } catch (Exception e) {
            logger.error("[EMP-IMPORTER-ABSTRACT-014] Tentativa de usar gênero inválido: {}", gender); // Log de erro

            String allowedValues = Arrays.stream(GenderType.values()) // Stream dos valores do enum
                    .map(Enum::name) // Extrai nomes
                    .collect(Collectors.joining(", ")); // Junta com vírgula

            throw new InvalidGenderException( // Lança exceção personalizada
                    String.format("Gênero '%s' inválido. Valores permitidos: %s",
                            gender, allowedValues) // Mensagem formatada
            );
        }
    }
}