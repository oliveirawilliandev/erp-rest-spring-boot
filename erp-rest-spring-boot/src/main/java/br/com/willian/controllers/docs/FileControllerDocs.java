package br.com.willian.controllers.docs;

import br.com.willian.dto.v1.UploadFileResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * FileControllerDocs
 *
 * Contrato de documentação dos endpoints de arquivos.
 *
 * RESPONSABILIDADE:
 * - Definir o comportamento esperado da API
 * - Servir como referência para Swagger/OpenAPI
 * - Facilitar manutenção e evolução futura
 *
 * IMPORTANTE:
 * - Esta interface NÃO contém lógica
 * - Qualquer alteração aqui impacta o contrato público da API
 */
@Tag(
        name = "Files",
        description = "Endpoints responsible for file upload and download operations"
)
public interface FileControllerDocs {

    /**
     * [CTRL-TRACE: FILE-CTRL-001]
     *
     * Realiza o upload de um único arquivo.
     *
     * FLUXO:
     * 1. Recebe arquivo via multipart/form-data
     * 2. Persiste o arquivo em storage físico/lógico
     * 3. Retorna metadados do arquivo salvo
     *
     * REGRAS:
     * - O nome do arquivo pode ser alterado internamente
     * - O retorno SEMPRE contém a URL de download
     *
     * RISCOS DE MANUTENÇÃO:
     * - Alterar o path quebra clientes externos
     * - Alterar o DTO quebra contratos
     */
    @Operation(
            summary = "Upload a single file",
            description = "Uploads a single file and returns its metadata",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "File uploaded successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UploadFileResponseDTO.class)
                            )
                    )
            }
    )
    UploadFileResponseDTO uploadFile(
            @RequestParam("file") MultipartFile file
    );

    /**
     * [CTRL-TRACE: FILE-CTRL-002]
     *
     * Realiza o upload de múltiplos arquivos.
     *
     * FLUXO:
     * - Executa internamente o mesmo processo do upload unitário
     *
     * OBSERVAÇÃO:
     * - Este endpoint depende diretamente do método uploadFile(...)
     *
     * RISCO:
     * - Alterar uploadFile impacta este endpoint automaticamente
     */
    @Operation(
            summary = "Upload multiple files",
            description = "Uploads multiple files and returns metadata for each one"
    )
    List<UploadFileResponseDTO> uploadMultipleFile(
            @RequestParam("files") MultipartFile[] files
    );

    /**
     * [CTRL-TRACE: FILE-CTRL-003]
     *
     * Realiza o download de um arquivo previamente armazenado.
     *
     * FLUXO:
     * 1. Localiza o arquivo pelo nome
     * 2. Resolve o Content-Type dinamicamente
     * 3. Retorna o arquivo como attachment
     *
     * REGRAS:
     * - O arquivo SEMPRE será retornado como download
     * - Caso o tipo não seja identificado, application/octet-stream será usado
     *
     * SEGURANÇA:
     * - Não expõe caminhos físicos do servidor
     */
    @Operation(
            summary = "Download file",
            description = "Downloads a file by its name"
    )
    ResponseEntity<Resource> downloadFile(
            @PathVariable("fileName") String fileName,
            HttpServletRequest request
    );
}

