package br.com.willian.file.exporter.factory;

import br.com.willian.exception.BadRequestException;
import br.com.willian.file.exporter.MediaTypes;
import br.com.willian.file.exporter.contract.EmployeesExporter;
import br.com.willian.file.exporter.impl.CsvExporter;
import br.com.willian.file.exporter.impl.PdfExporter;
import br.com.willian.file.exporter.impl.XlsxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
/**
 * Fábrica responsável por retornar o importador adequado baseado na extensão do arquivo.
 *
 * Formatos suportados:
 * - .xlsx -> XlsxImporter (Excel)
 * - .csv  -> CsvImporter
 */

@Component
public class FileExporterFactory {
    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);
    @Autowired
    private ApplicationContext context;

    /**
     * Retorna o importador correspondente à extensão do arquivo.
     *
     * param fileName Nome do arquivo (ex: dados.csv, planilha.xlsx)
     * return FileImporter para processar o arquivo
     * throws BadRequestException Se a extensão não for .xlsx ou .csv
     */
    public EmployeesExporter getExporter(String acceptHeader) throws Exception{
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) {
            return context.getBean(XlsxExporter.class);
           // return new XlsxImporter();
        }else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return context.getBean(CsvExporter.class);
           // return new CsvImporter();
        }else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
            return context.getBean(PdfExporter.class);
           // return new PdfImporter();
        }else {
            throw new BadRequestException();
        }
    }

}
