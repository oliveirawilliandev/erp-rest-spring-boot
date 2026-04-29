package br.com.willian.file.importer.contract;

import br.com.willian.dto.v1.EmployeeDTO;

import java.io.InputStream;
import java.util.List;
// Contrato para implementação de importadores de arquivos.
// As implementações desta interface devem processar diferentes formatos de arquivo [CSV, Excel]

public interface FileImporter {
    /**
     * Processa o arquivo de entrada e retorna os dados convertidos.
     *
     * @param inputStream Stream do arquivo a ser processado
     * @return Lista de EmployeeDTO com os dados do arquivo
     * @throws Exception Erro durante o processamento do arquivo
     */
    List<EmployeeDTO> importFile(InputStream inputStream) throws Exception;
}
