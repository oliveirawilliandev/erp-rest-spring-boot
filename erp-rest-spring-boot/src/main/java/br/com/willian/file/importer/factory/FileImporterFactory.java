package br.com.willian.file.importer.factory;

import br.com.willian.exception.BadRequestException;
import br.com.willian.file.importer.contract.FileImporter;
import br.com.willian.file.importer.impl.CsvImporter;
import br.com.willian.file.importer.impl.XlsxImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Fábrica para retornar o importador correto baseado na extensão do arquivo.
 */
@Component
public class FileImporterFactory {
    private Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);

    @Autowired
    private ApplicationContext context;

    /**
     * Retorna o importador adequado para o arquivo.
     *
     * @param fileName Nome do arquivo com extensão
     * @return FileImporter para processar o arquivo
     * @throws BadRequestException se extensão não for suportada
     */
    public FileImporter getImporter(String fileName) throws Exception{
        if (fileName.endsWith(".xlsx")) {
            return context.getBean(XlsxImporter.class);
        }else if (fileName.endsWith(".csv")) {
            return context.getBean(CsvImporter.class);
        }else {
            throw new BadRequestException("Invalid File Format");
        }
    }
}