package br.com.willian.controller.docs;

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

    /**
     * [CTRL-TRACE: FILE-CTRL-004]
     *
     * Realiza o upload da foto de perfil do usuário.
     *
     * FLUXO:
     * 1. Recebe o arquivo de foto via multipart/form-data
     * 2. VALIDA se o arquivo é uma imagem JPG/JPEG
     * 3. Persiste a foto no diretório específico de fotos de usuário
     * 4. Renomeia o arquivo para o padrão: {id}.jpg
     * 5. Atualiza o campo photo_url do usuário no banco de dados
     *
     * REGRAS:
     * - ACEITA APENAS: arquivos JPG ou JPEG
     * - Tamanho máximo: (configure no application.properties)
     * - O arquivo é renomeado para o ID do usuário (ex: 1.jpg, 2.jpg)
     * - Se o usuário já tiver foto, a anterior é substituída
     *
     * RESPOSTAS:
     * - 204: Foto enviada e associada com sucesso
     * - 400: Arquivo vazio, inválido ou não é JPG
     * - 404: Usuário não encontrado
     *
     * EXEMPLO DE REQUISIÇÃO:
     * - POST /api/file/v1/users/1/photo
     * - Body: form-data com key "file" e valor (arquivo .jpg)
     */
    @Operation(
            summary = "Upload user profile photo",
            description = "Uploads a profile photo (JPG only) and associates it with the user",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Photo updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Empty file, invalid file or not a JPG image"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    ResponseEntity<Void> uploadUserPhoto(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file
    );

    /**
     * [CTRL-TRACE: FILE-CTRL-007]
     *
     * Realiza o download da foto de perfil do usuário.
     *
     * FLUXO:
     * 1. Recebe o nome do arquivo como parâmetro
     * 2. Localiza o arquivo no diretório de fotos de usuário (C:/Code/UserPhoto)
     * 3. Retorna o arquivo como attachment para download
     *
     * EXEMPLOS:
     * - GET /api/file/v1/downloadUserPhoto/1.jpg → Baixa a foto 1.jpg
     * - GET /api/file/v1/downloadUserPhoto/2.jpg → Baixa a foto 2.jpg
     *
     * RESPOSTAS:
     * - 200: Foto encontrada e baixada com sucesso
     * - 404: Arquivo não encontrado no diretório de fotos
     */
    @Operation(
            summary = "Download user photo by filename",
            description = "Downloads a user's profile photo (JPG format) from the user photo directory",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Photo downloaded successfully"),
                    @ApiResponse(responseCode = "404", description = "Photo file not found")
            }
    )
    ResponseEntity<Resource> downloadUserPhoto(
            @PathVariable("fileName") String fileName,
            HttpServletRequest request
    );

}

