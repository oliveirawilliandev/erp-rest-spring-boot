package br.com.willian.file.exporter;

/**
 * Constantes para os Media Types utilizados na exportação de arquivos.
 *
 * Formatos suportados:
 * - XLSX: Planilha Excel
 * - PDF: Documento PDF
 * - CSV: Arquivo texto separado por vírgulas
 */
public interface MediaTypes {
    String APPLICATION_XLSX_VALUE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    String APPLICATION_PDF_VALUE = "application/pdf";
    String APPLICATION_CSV_VALUE = "text/csv";
}