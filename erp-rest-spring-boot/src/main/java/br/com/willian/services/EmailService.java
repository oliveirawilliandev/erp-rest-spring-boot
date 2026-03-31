package br.com.willian.services; // Pacote da camada de serviço

import br.com.willian.config.EmailConfig; // Configurações de e-mail (SMTP)
import br.com.willian.dto.v1.request.EmailRequestDTO; // DTO de requisição de e-mail
import br.com.willian.mail.EmailSender; // Classe responsável pelo envio de e-mails
import br.com.willian.model.EmailVerification;
import br.com.willian.repository.EmailVerificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException; // Exceção de parse JSON
import com.fasterxml.jackson.databind.ObjectMapper; // Conversor JSON → Objeto
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; // Marca a classe como Service
import org.springframework.web.multipart.MultipartFile; // Arquivo enviado via upload

import java.io.File; // Representação de arquivo
import java.io.IOException; // Exceção de I/O
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // Serviço gerenciado pelo Spring
public class EmailService {

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class); // Logger para rastreamento

    // Componente responsável pelo envio do e-mail
    @Autowired 
    private EmailSender emailSender;
    // Componente responsavel por salva no banco de dados
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    // Configurações SMTP do e-mail
    @Autowired 
    private EmailConfig emailConfig;

    // [SERVICE-TRACE: EMAIL-SRV-001]
    // Envia e-mail simples (sem anexo)
    public void sendSimpleMail(EmailRequestDTO emailRequestDTO) {

        logger.info("[EMAIL-SRV-001] Envio de e-mail simples solicitado | to={} | subject={} | bodyLength={}",
                emailRequestDTO.getTo(), emailRequestDTO.getSubject(),
                emailRequestDTO.getBody() != null ? emailRequestDTO.getBody().length() : 0); // Log da solicitação

        // Validações básicas
        if (emailRequestDTO.getTo() == null || emailRequestDTO.getTo().trim().isEmpty()) {
            logger.error("[EMAIL-SRV-001] Destinatário inválido | to={}", emailRequestDTO.getTo()); // Log de erro
            throw new IllegalArgumentException("Destinatário não pode ser vazio"); // Exceção
        }

        if (emailRequestDTO.getSubject() == null || emailRequestDTO.getSubject().trim().isEmpty()) {
            logger.warn("[EMAIL-SRV-001] Assunto vazio | to={}", emailRequestDTO.getTo()); // Log de aviso
        }

        if (emailRequestDTO.getBody() == null || emailRequestDTO.getBody().trim().isEmpty()) {
            logger.warn("[EMAIL-SRV-001] Corpo do e-mail vazio | to={}", emailRequestDTO.getTo()); // Log de aviso
        }

//        logger.debug("[EMAIL-SRV-001] Configurações SMTP | host={} | port={} | username={} | auth={} | starttls={}",
//                emailConfig.getHost(), emailConfig.getPort(), emailConfig.getUsername(),
//                emailConfig.isAuth(), emailConfig.isStarttls()); // Log das configurações



        try {
            emailSender
                    .to(emailRequestDTO.getTo())              // Destinatário
                    .withSubject(emailRequestDTO.getSubject())// Assunto
                    .withMessage(emailRequestDTO.getBody())   // Corpo do e-mail
                    .buildEmailFooter()                       //Footer padrao
                    .send(emailConfig);                       // Envia usando as configurações



            logger.debug("[EMAIL-SRV-001] E-mail simples enviado com sucesso | to={} | subject={}",
                    emailRequestDTO.getTo(), emailRequestDTO.getSubject() ); // Log de sucesso

        } catch (Exception e) {
            logger.error("[EMAIL-SRV-001] Erro ao enviar e-mail simples | to={} | subject={} | erro={}",
                    emailRequestDTO.getTo(), emailRequestDTO.getSubject(), e.getMessage(), e); // Log de erro
            throw new RuntimeException("Falha ao enviar e-mail para: " + emailRequestDTO.getTo(), e); // Exceção
        }
    }

    // [SERVICE-TRACE: EMAIL-SRV-002]
    // Envia e-mail com anexos
    public void sendEmailWithAttachment(String emailRequestjson,
                                        MultipartFile[] attachments) {

        logger.info("[EMAIL-SRV-002] Envio de e-mail com anexos solicitado | jsonLength={} | totalAttachments={}",
                emailRequestjson != null ? emailRequestjson.length() : 0,
                attachments != null ? attachments.length : 0); // Log da solicitação

        List<File> tempFiles = new ArrayList<>(); // Lista de arquivos temporários para anexos

        try {
            // Converte o JSON da requisição em objeto
            logger.debug("[EMAIL-SRV-002] Convertendo JSON para EmailRequestDTO | json={}", emailRequestjson); // Log do JSON

            EmailRequestDTO emailRequestDTO =new ObjectMapper().readValue(emailRequestjson, EmailRequestDTO.class);

            logger.debug("[EMAIL-SRV-002] JSON convertido com sucesso | to={} | subject={}", emailRequestDTO.getTo(), emailRequestDTO.getSubject()); // Log da conversão

            // Validações básicas
            if (emailRequestDTO.getTo() == null || emailRequestDTO.getTo().trim().isEmpty()) {
                logger.error("[EMAIL-SRV-002] Destinatário inválido | to={}", emailRequestDTO.getTo()); // Log de erro
                throw new IllegalArgumentException("Destinatário não pode ser vazio"); // Exceção
            }

            // Verifica se existem anexos
            if (attachments == null || attachments.length == 0) {
                logger.warn("[EMAIL-SRV-002] Nenhum anexo enviado"); // Log de aviso
            } else {

                // Processa cada anexo recebido
                for (MultipartFile file : attachments) {
                    if (file == null || file.isEmpty()) {
                        logger.warn("[EMAIL-SRV-002] Anexo vazio ou nulo ignorado");
                        continue;
                    }

                    String attachmentName = file.getOriginalFilename();

                    // Cria arquivo temporário
                    File tempFile = File.createTempFile("attachment_", "_" + attachmentName);

                    logger.debug("[EMAIL-SRV-002] Arquivo temporário criado | path={}",
                            tempFile.getAbsolutePath()); // Log do arquivo

                    // Transfere o conteúdo do MultipartFile para o arquivo temporário
                    file.transferTo(tempFile);

                    logger.debug("[EMAIL-SRV-002] Conteúdo transferido para arquivo temporário | nome={} | tamanho={} bytes",
                            attachmentName, tempFile.length()); // Log da transferência

                    tempFiles.add(tempFile); // Adiciona à lista
                }
            }

            // Configura envio do email
            EmailSender sender = emailSender
                    .to(emailRequestDTO.getTo())               // Destinatário
                    .withSubject(emailRequestDTO.getSubject()) // Assunto
                    .withMessage(emailRequestDTO.getBody())   // Corpo
                    .buildEmailFooter();                      // Footer padrao

            // Adiciona todos os anexos
            for (File file : tempFiles) {
                sender.attach(file.getAbsolutePath()); // Adiciona anexo
            }

            // Envia o e-mail
            sender.send(emailConfig); // Envio

            logger.debug("[EMAIL-SRV-002] E-mail com anexos enviado com sucesso | to={} | subject={} | totalAttachments={}",
                    emailRequestDTO.getTo(),
                    emailRequestDTO.getSubject(),
                    tempFiles.size()); // Log de sucesso

        } catch (JsonProcessingException e) {
            // Erro ao converter JSON para objeto
            logger.error("[EMAIL-SRV-002] Erro ao parsear JSON | json={} | erro={}",
                    emailRequestjson, e.getMessage(), e); // Log de erro
            throw new RuntimeException("Error parsing email request JSON", e); // Exceção

        } catch (IOException e) {
            // Erro ao processar anexos
            logger.error("[EMAIL-SRV-002] Erro ao processar anexos | erro={}",
                    e.getMessage(), e); // Log de erro
            throw new RuntimeException("Error processing attachments", e); // Exceção

        } catch (Exception e) {
            logger.error("[EMAIL-SRV-002] Erro inesperado ao enviar e-mail | erro={}",
                    e.getMessage(), e); // Log de erro
            throw new RuntimeException("Erro inesperado ao enviar e-mail", e); // Exceção

        } finally {

            // Remove os arquivos temporários após o envio
            for (File file : tempFiles) {

                if (file != null && file.exists()) {

                    boolean deleted = file.delete();

                    logger.debug("[EMAIL-SRV-002] Arquivo temporário removido | path={} | sucesso={}",
                            file.getAbsolutePath(), deleted); // Log da remoção
                }
            }
        }
    }
    // [SERVICE-TRACE: EMAIL-SRV-003]
    // Envia código de verificação por e-mail
    public String sendVerificationCode(String email) {

        logger.info("[EMAIL-SRV-003] Envio de código de verificação solicitado | email={}", email); // Log da solicitação

        try {
            // Validação do email
            if (email == null || email.trim().isEmpty()) {
                logger.error("[EMAIL-SRV-003] Email inválido | email fornecido={}", email); // Log de erro
                throw new IllegalArgumentException("Email não pode ser vazio"); // Exceção
            }

            String code = generateVerificationCode(); // Gera código aleatório de 6 caracteres
            logger.debug("[EMAIL-SRV-003] Código gerado | code={}", code); // Log do código gerado (apenas em DEBUG)


            EmailVerification verification = new EmailVerification(); // Cria nova entidade
            verification.setEmail(email); // Define o email do destinatário
            verification.setCode(code); // Define o código gerado
            verification.setCreatedAt(LocalDateTime.now()); // define a data da criação do codigo
            verification.setExpiresAt(LocalDateTime.now().plusMinutes(15)); // Define expiração em 15 minutos
            verification.setUsed(false); // Marca como não utilizado
            emailVerificationRepository.save(verification); // Persiste no banco

            // Corpo do email em HTML
            String body = bodyEmail(code); // Template HTML com o código

            logger.debug("[EMAIL-SRV-003] Corpo do email preparado | bodyLength={} caracteres", body.length()); // Log do corpo

            // Envia o email usando o EmailSender
            emailSender
                    .to(email) // Destinatário
                    .withSubject("Código de verificação") // Assunto
                    .withMessage(body) // Corpo em HTML
                    .buildEmailFooter() // Adiciona rodapé padrão
                    .send(emailConfig); // Envia com as configurações

            logger.debug("[EMAIL-SRV-003] Código enviado com sucesso | email={} ", email); // Log de sucesso

            return code; // Retorna o código gerado

        } catch (IllegalArgumentException e) {
            logger.error("[EMAIL-SRV-003] Erro de validação no envio de código | email={} | erro={}",
                    email, e.getMessage(), e); // Log de erro
            throw e; // Relança exceção
        } catch (Exception e) {
            logger.error("[EMAIL-SRV-003] Erro ao enviar código de verificação | email={} | erro={}",
                    email, e.getMessage(), e); // Log de erro
            throw new RuntimeException("Erro ao enviar código de verificação para: " + email, e); // Exceção
        }
    }


    // [SERVICE-TRACE: EMAIL-SRV-004]
    // Valida se um código de verificação é válido para o email informado
    public boolean validateCode(String email, String code) {

        logger.info("[EMAIL-SRV-004] Validando código | email={}", email);

        // Validações básicas
        if (email == null || email.trim().isEmpty()) {
            logger.error("[EMAIL-SRV-004] Email inválido");
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        if (code == null || code.trim().isEmpty()) {
            logger.error("[EMAIL-SRV-004] Código inválido");
            throw new IllegalArgumentException("Código não pode ser vazio");
        }

        // Busca código não utilizado
        Optional<EmailVerification> verification =
                emailVerificationRepository
                        .findByEmailAndCodeAndUsedFalse(email, code);

        if (verification.isEmpty()) {
            logger.debug("[EMAIL-SRV-004] Código inválido | email={}", email);
            return false;
        }

        EmailVerification record = verification.get();

        // Verifica expiração
        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.info("[EMAIL-SRV-004] Código expirado | email={}", email);
            return false;
        }

        // Marca como utilizado
        record.setUsed(true);
        emailVerificationRepository.save(record);

        logger.debug("[EMAIL-SRV-004] Código validado | email={}", email);
        return true;
    }

    // [EMAIL-SRV-INTERNAL-001: GERAR CODE]
    // Gera código aleatório de 6 caracteres (letras maiúsculas e números)
    private String generateVerificationCode() {

        logger.debug("[EMAIL-SRV-INTERNAL-001] Gerando código de verificação aleatório"); // Log de início

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // Caracteres permitidos
        StringBuilder code = new StringBuilder(); // Construtor de string

        int length = 6; // Tamanho do código
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length()); // Índice aleatório
            char selectedChar = chars.charAt(index); // Caractere selecionado
            code.append(selectedChar); // Adiciona ao código

            logger.trace("[EMAIL-SRV-INTERNAL-001] Posição {} | caractere='{}' | índice={}", i + 1, selectedChar, index); // Log trace (bem detalhado)
        }

        String generatedCode = code.toString(); // Converte para string
        logger.debug("[EMAIL-SRV-INTERNAL-001] Código gerado | code={} | length={}", generatedCode, generatedCode.length()); // Log do código

        return generatedCode; // Retorna o código
    }

    // [EMAIL-SRV-INTERNAL-002: GERAR CORPO DO EMAIL]
    // Gera o corpo em HTML do email com o codigo.
    private static String bodyEmail(String code) {
        logger.debug("[EMAIL-SRV-INTERNAL-002: GERAR CORPO DO EMAIL] Gerando corpo do email"); // Log de início
        return """
                <h2>Código de Verificação</h2>
                <p>Seu código é:</p>
                <h1 style="font-size: 32px; letter-spacing: 5px; background-color: #f0f0f0; padding: 10px; text-align: center;">%s</h1>
                <p>Este código expira em <b>15 minutos</b>.</p>
                <hr>
                <p style="color: #666; font-size: 12px;">Se você não solicitou este código, ignore este e-mail.</p>
                """.formatted(code);
    }

}