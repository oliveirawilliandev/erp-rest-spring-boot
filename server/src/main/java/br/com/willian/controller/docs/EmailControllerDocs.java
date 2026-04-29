package br.com.willian.controller.docs;

import br.com.willian.dto.v1.request.EmailRequestDTO;
import br.com.willian.dto.v1.request.SendVerificationCodeRequestDTO;
import br.com.willian.dto.v1.request.VerifyEmailCodeRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * EmailControllerDocs
 *
 * Contrato de documentação dos endpoints responsáveis pelo envio de e-mails.
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
        name = "e-Mail",
        description = "Endpoints responsible for sending emails"
)
public interface EmailControllerDocs {

    /**
     * [CTRL-TRACE: EMAIL-CTRL-001]
     *
     * Realiza o envio de um e-mail simples (sem anexos).
     *
     * FLUXO:
     * 1. Recebe os dados do e-mail via JSON
     * 2. Converte automaticamente para EmailRequestDTO
     * 3. Encaminha os dados para o EmailService
     * 4. O serviço realiza o envio do e-mail
     *
     * REGRAS:
     * - O envio depende da configuração SMTP do sistema
     * - Campos obrigatórios devem ser validados no DTO
     *
     * RISCOS DE MANUTENÇÃO:
     * - Alterar o DTO quebra contratos da API
     * - Alterar o path do endpoint pode impactar clientes externos
     */
    @Operation(
            summary = "Send simple email",
            description = "Sends an email without attachments",
            tags = {"e-Mail"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<String> sendEmail(
            @RequestBody EmailRequestDTO emailRequestDTO
    );

    /**
     * [CTRL-TRACE: EMAIL-CTRL-002]
     *
     * Realiza o envio de um e-mail com anexo.
     *
     * FLUXO:
     * 1. Recebe dados do e-mail via multipart/form-data
     * 2. O campo emailRequest contém os dados do e-mail em formato JSON
     * 3. O campo attachment contém o arquivo anexado
     * 4. O serviço processa o JSON e envia o e-mail com o anexo
     *
     * REGRAS:
     * - O arquivo é enviado como MultipartFile
     * - O JSON precisa ser convertido internamente para EmailRequestDTO
     *
     * SEGURANÇA:
     * - Validar tamanho máximo do arquivo
     * - Validar tipos permitidos de anexo
     *
     * RISCOS DE MANUTENÇÃO:
     * - Alterar o nome dos parâmetros quebra clientes
     * - Alterar o consumo multipart pode impactar integrações
     */
    @Operation(
            summary = "Send email with attachment",
            description = "Sends an email including a file attachment",
            tags = {"e-Mail"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
            }

    )
    ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("emailRequest") String emailRequest,
            @RequestParam("attachment") MultipartFile[] attachments
    );
    /**
     * [CTRL-TRACE: EMAIL-CTRL-003]
     *
     * Envia um código de verificação para um endereço de e-mail.
     *
     * FLUXO:
     * 1. Recebe o email como parâmetro
     * 2. O sistema gera um código aleatório
     * 3. O código é salvo no banco com tempo de expiração
     * 4. O código é enviado para o e-mail informado
     *
     * REGRAS:
     * - O código expira após 15 minutos
     * - Pode existir controle de limite de envio
     *
     * SEGURANÇA:
     * - Evitar envio massivo de códigos
     * - Validar formato do email
     */
    @Operation(
            summary = "Send verification code",
            description = "Generates and sends a verification code to the provided email",
            tags = {"e-Mail"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<String> sendVerificationCode(
            @RequestBody() SendVerificationCodeRequestDTO emailRequest
    );

    /**
     * [CTRL-TRACE: EMAIL-CTRL-004]
     *
     * Valida um código de verificação enviado por e-mail.
     *
     * FLUXO:
     * 1. Recebe o email e o código informado
     * 2. Busca o código correspondente no banco de dados
     * 3. Verifica se o código existe
     * 4. Verifica se o código não expirou
     * 5. Marca o código como utilizado
     *
     * REGRAS:
     * - O código só pode ser usado uma vez
     * - O código deve estar dentro do tempo de expiração
     *
     * SEGURANÇA:
     * - Evitar tentativa de força bruta
     * - Limitar número de validações
     */
    @Operation(
            summary = "Validate verification code",
            description = "Validates the verification code sent to the user's email",
            tags = {"e-Mail"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal server error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<String> validateVerificationCode(
            @RequestBody VerifyEmailCodeRequestDTO emailRequest
    );
}

