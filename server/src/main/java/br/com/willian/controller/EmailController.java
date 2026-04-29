package br.com.willian.controller; // Pacote da camada de controle/API

import br.com.willian.controller.docs.EmailControllerDocs; // Interface de documentação Swagger/OpenAPI
import br.com.willian.dto.v1.request.EmailRequestDTO; // DTO para requisição de email
import br.com.willian.dto.v1.request.SendVerificationCodeRequestDTO;
import br.com.willian.dto.v1.request.VerifyEmailCodeRequestDTO;
import br.com.willian.service.EmailService; // Serviço de email
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.http.HttpStatus; // Códigos de status HTTP
import org.springframework.http.MediaType; // Constantes para tipos de mídia
import org.springframework.http.ResponseEntity; // Entidade de resposta HTTP
import org.springframework.web.bind.annotation.*; // Anotações Spring para REST controllers
import org.springframework.web.multipart.MultipartFile; // Representação de arquivo upload

@RestController // Indica que é um controller REST com retorno direto
@RequestMapping("/api/email/v1") // Mapeia as requisições para esta URL base
public class EmailController implements EmailControllerDocs { // Implementa a interface de documentação

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class); // Logger para rastreamento

    @Autowired
    private EmailService emailService; // Serviço para envio de emails

    // [EMAIL-CTRL-001]
    // Endpoint para envio de email simples (sem anexo)
    @PostMapping // Mapeia requisições POST para URL base
    @Override // Sobrescreve método da interface
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {

        logger.debug("[EMAIL-CTRL-001] Requisição de envio de email simples recebida | to={} | subject={} | bodyLength={}",
                emailRequestDTO.getTo(), emailRequestDTO.getSubject(),
                emailRequestDTO.getBody() != null ? emailRequestDTO.getBody().length() : 0); // Log da requisição


        try {
            // Validação básica
            if (emailRequestDTO == null) {
                logger.error("[EMAIL-CTRL-001] EmailRequestDTO nulo recebido"); // Log de erro
                return new ResponseEntity<>("Dados de email não podem ser nulos", HttpStatus.BAD_REQUEST); // Retorna erro
            }

            // Chama serviço para enviar email simples
            emailService.sendSimpleMail(emailRequestDTO);

            return new ResponseEntity<>("Email send with success!", HttpStatus.OK); // Retorna sucesso

        } catch (IllegalArgumentException e) {
            logger.error("[EMAIL-CTRL-001] Erro de validação no envio de email | erro={}", e.getMessage(), e); // Log de erro
            return new ResponseEntity<>("Erro de validação: " + e.getMessage(), HttpStatus.BAD_REQUEST); // Retorna erro
        } catch (Exception e) {
            logger.error("[EMAIL-CTRL-001] Erro ao enviar email simples | to={} | subject={} | erro={}",
                    emailRequestDTO.getTo(), emailRequestDTO.getSubject(), e.getMessage(), e); // Log de erro
            return new ResponseEntity<>("Erro ao enviar email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // Retorna erro
        }
    }

    // [EMAIL-CTRL-002]
    // Endpoint para envio de email com anexo
    @PostMapping(value = "/withAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Mapeia POST multipart
    @Override // Sobrescreve método da interface
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("emailRequest") String emailRequest, // JSON da requisição
            @RequestParam("attachment") MultipartFile[] attachments) { // Arquivo anexo
        int totalAttachments = attachments != null ? attachments.length : 0;
        logger.debug("[EMAIL-CTRL-002] Requisição de envio de email com anexos recebida | totalAttachments={}",totalAttachments);

        try {
            // Validação do JSON
            if (emailRequest == null || emailRequest.trim().isEmpty()) {
                logger.error("[EMAIL-CTRL-002] EmailRequest vazio ou nulo");
                return new ResponseEntity<>("Dados de email não podem ser vazios", HttpStatus.BAD_REQUEST);
            }

            // Log detalhado dos anexos
            if (attachments != null && attachments.length > 0) {
                for (MultipartFile file : attachments) {
                    if (file != null && !file.isEmpty()) { logger.debug("[EMAIL-CTRL-002] Anexo recebido | nome={} | tamanho={} bytes", file.getOriginalFilename(), file.getSize() ); }
                }
            } else {
                logger.warn("[EMAIL-CTRL-002] Nenhum anexo enviado");
            }
            logger.debug("[EMAIL-CTRL-002] Processando email | emailRequestLength={} | totalAttachments={}", emailRequest.length(), totalAttachments);

            // Chama serviço
            emailService.sendEmailWithAttachment(emailRequest, attachments);

            return new ResponseEntity<>("Email with attachment sent successfully!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("[EMAIL-CTRL-002] Erro de validação no envio de email com anexo | erro={}", e.getMessage(),e );
            return new ResponseEntity<>("Erro de validação: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("[EMAIL-CTRL-002] Erro ao enviar email com anexos | erro={}",e.getMessage(),e);
            return new ResponseEntity<>("Erro ao enviar email com anexos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // [EMAIL-CTRL-003]
    // Endpoint para enviar código de verificação
    @PostMapping("/verify/send")
    @Override // Sobrescreve método da interface
    public ResponseEntity<String> sendVerificationCode(@RequestBody() SendVerificationCodeRequestDTO emailRequest) {

        logger.debug("[EMAIL-CTRL-003] Solicitação de envio de código | email={}", emailRequest.email());

        try {

            emailService.sendVerificationCode(emailRequest.email());

            return new ResponseEntity<>(
                    "Verification code sent successfully!",
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException e) {

            logger.error("[EMAIL-CTRL-003] Erro de validação | email={} | erro={}", emailRequest.email(), e.getMessage(), e);

            return new ResponseEntity<>(
                    "Validation error: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception e) {

            logger.error("[EMAIL-CTRL-003] Erro ao enviar código | email={} | erro={}", emailRequest.email(), e.getMessage(), e);

            return new ResponseEntity<>(
                    "Error sending verification code",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // [EMAIL-CTRL-004]
    // Endpoint para validar código de verificação
    @PostMapping("/verify/validate")
    @Override // Sobrescreve método da interface
    public ResponseEntity<String> validateVerificationCode(
            @RequestBody VerifyEmailCodeRequestDTO emailRequest) {

        logger.debug("[EMAIL-CTRL-004] Validação de código | email={} | code={}", emailRequest.email(), emailRequest.code());

        try {

            boolean valid = emailService.validateCode(emailRequest.email(), emailRequest.code());

            if (!valid) {

                return new ResponseEntity<>(
                        "Invalid or expired code",
                        HttpStatus.BAD_REQUEST
                );
            }

            return new ResponseEntity<>(
                    "Code validated successfully!",
                    HttpStatus.OK
            );

        } catch (Exception e) {

            logger.error("[EMAIL-CTRL-004] Erro na validação | email={} | erro={}", emailRequest.email(), e.getMessage(), e);

            return new ResponseEntity<>(
                    "Error validating code",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}