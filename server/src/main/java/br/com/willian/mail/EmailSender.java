package br.com.willian.mail; // Pacote da camada de email

import br.com.willian.config.EmailConfig; // Configurações de email
import jakarta.mail.MessagingException; // Exceção de mensagem
import jakarta.mail.internet.AddressException; // Exceção de endereço
import jakarta.mail.internet.InternetAddress; // Endereço de email
import jakarta.mail.internet.MimeMessage; // Mensagem MIME
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.mail.javamail.JavaMailSender; // Envio de email JavaMail
import org.springframework.mail.javamail.MimeMessageHelper; // Helper para mensagens MIME
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component; // Componente Spring

import java.io.File; // Representação de arquivo
import java.io.Serializable; // Serializable
import java.util.ArrayList; // Implementação de List
import java.util.List;
import java.util.StringTokenizer; // Tokenizador de strings

@Component // Define a classe como um componente Spring
public class EmailSender implements Serializable { // Implementa Serializable

    // Logger para rastreamento
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class); // Logger para rastreamento

    private final JavaMailSender javaMailSender; // Serviço de envio de email
    private String to; // Destinatário(s) em formato string
    private String subject; // Assunto do email
    private String body; // Corpo do email
    private List<InternetAddress> recipients = new ArrayList<>(); // Lista de destinatários
    private List<File> attachments = new ArrayList<>();; // Arquivo anexo
    private String emailFooter;

    // [EMP-MAIL-001]
    // Construtor com injeção do JavaMailSender
    public EmailSender(JavaMailSender javaMailSender) {
        logger.info("[EMP-MAIL-001] Inicializando EmailSender"); // Log de inicialização
        this.javaMailSender = javaMailSender; // Atribui sender
        logger.debug("[EMP-MAIL-001] JavaMailSender injetado com sucesso"); // Log de sucesso
    }

    // [EMP-MAIL-002]
    // Define o(s) destinatário(s) do email
    public EmailSender to(String to) {
        logger.debug("[EMP-MAIL-002] Definindo destinatário(s) | to='{}'", to); // Log da definição

        if (to == null || to.trim().isEmpty()) {
            logger.error("[EMP-MAIL-002] Lista de destinatários vazia ou nula"); // Log de erro
            throw new IllegalArgumentException("Destinatário não pode ser vazio"); // Exceção
        }

        this.to = to; // Armazena string original
        this.recipients = getRecipients(to); // Converte para lista de endereços

        logger.debug("[EMP-MAIL-002] Destinatários processados | quantidade={}", recipients.size()); // Log da quantidade
        return this; // Retorna própria instância (fluent interface)
    }

    // [EMP-MAIL-003]
    // Converte string de destinatários (separados por ;) em lista de InternetAddress
    private ArrayList<InternetAddress> getRecipients(String to) {
        logger.debug("[EMP-MAIL-003] Processando destinatários | entrada='{}'", to); // Log do processamento

        String toWithoutSpaces = to.replaceAll("\\s", ""); // Remove espaços
        StringTokenizer tok = new StringTokenizer(toWithoutSpaces, ";"); // Tokeniza por ;
        ArrayList<InternetAddress> recipientsList = new ArrayList<>(); // Lista de endereços

        int count = 0; // Contador para log
        while (tok.hasMoreTokens()) {
            String token = tok.nextElement().toString(); // Próximo token
            try {
                InternetAddress address = new InternetAddress(token); // Cria endereço
                recipientsList.add(address); // Adiciona à lista
                count++; // Incrementa contador
                logger.debug("[EMP-MAIL-003] Endereço adicionado | token='{}' | posição={}", token, count); // Log por endereço
            } catch (AddressException e) {
                logger.error("[EMP-MAIL-003] Erro ao processar endereço | token='{}' | erro={}",
                        token, e.getMessage(), e); // Log de erro
                throw new RuntimeException("Erro ao processar endereço de email: " + token, e); // Exceção
            }
        }

        logger.debug("[EMP-MAIL-003] Processamento concluído | totalEnderecos={}", recipientsList.size()); // Log de conclusão
        return recipientsList; // Retorna lista
    }

    // [EMP-MAIL-004]
    // Define o assunto do email
    public EmailSender withSubject(String subject) {
        logger.debug("[EMP-MAIL-004] Definindo assunto | subject='{}'", subject); // Log da definição
        this.subject = subject; // Armazena assunto
        return this; // Retorna própria instância (fluent interface)
    }

    // [EMP-MAIL-005]
    // Define o corpo do email
    public EmailSender withMessage(String body) {
        logger.debug("[EMP-MAIL-005] Definindo corpo do email | bodyLength={} caracteres",
                body != null ? body.length() : 0); // Log do tamanho
        this.body = body; // Armazena corpo
        return this; // Retorna própria instância (fluent interface)
    }

    // [EMP-MAIL-006]
    // Define um arquivo anexo
    public EmailSender attach(String fileDir) {

        logger.info("[EMP-MAIL-006] Definindo anexo | fileDir='{}'", fileDir); // Log da definição

        if (fileDir == null || fileDir.trim().isEmpty()) {
            logger.warn("[EMP-MAIL-006] Caminho de anexo vazio ou nulo");
            return this;
        }

        File file = new File(fileDir);

        if (!file.exists()) {
            logger.warn("[EMP-MAIL-006] Arquivo anexo não encontrado | path='{}'", fileDir);
        } else {

            attachments.add(file);

            logger.debug(
                    "[EMP-MAIL-006] Arquivo anexo adicionado | nome='{}' | tamanho={} bytes | totalAnexos={}",
                    file.getName(),
                    file.length(),
                    attachments.size()
            );
        }

        return this;
    }

    // [EMP-MAIL-007]
    // Envia o email com as configurações fornecidas
    public void send(EmailConfig config) {
        logger.debug("[EMP-MAIL-007] Iniciando envio de email"); // Log de início

        // Validações básicas
        if (recipients == null || recipients.isEmpty()) {
            logger.error("[EMP-MAIL-007] Lista de destinatários vazia"); // Log de erro
            throw new IllegalStateException("Nenhum destinatário definido"); // Exceção
        }

        if (subject == null || subject.trim().isEmpty()) {
            logger.warn("[EMP-MAIL-007] Assunto vazio"); // Log de aviso
        }

        if (body == null || body.trim().isEmpty()) {
            logger.warn("[EMP-MAIL-007] Corpo do email vazio"); // Log de aviso
        }

        long startTime = System.currentTimeMillis(); // Inicia contagem

        MimeMessage message = javaMailSender.createMimeMessage(); // Cria mensagem vazia
        logger.debug("[EMP-MAIL-007] Mensagem MIME criada"); // Log da criação

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // Helper com suporte a anexos
            logger.debug("[EMP-MAIL-007] MimeMessageHelper criado | multipart=true"); // Log do helper

            helper.setFrom(config.getUsername()); // Remetente
            helper.setTo(recipients.toArray(new InternetAddress[0])); // Destinatários
            helper.setSubject(subject); // Assunto
            String finalBody = (body != null ? body : "") +
                    (emailFooter != null ? emailFooter : "");
            helper.setText(finalBody, true);// Corpo (HTML habilitado)

            logger.debug("[EMP-MAIL-007] Email configurado | from='{}' | toCount={} | subject='{}' | htmlEnabled=true",
                    config.getUsername(), recipients.size(), subject); // Log da configuração

            if (attachments != null && !attachments.isEmpty()) {
                for (File file : attachments) {
                    helper.addAttachment(file.getName(), file);
                    logger.debug("[EMP-MAIL-007] Anexo adicionado | nome='{}' | tamanho={} bytes", file.getName(), file.length());
                }
            }

            javaMailSender.send(message); // Envia email
            logger.debug("[EMP-MAIL-007] Email enviado via JavaMailSender"); // Log de envio

            long endTime = System.currentTimeMillis(); // Finaliza contagem
            long duration = endTime - startTime; // Calcula duração

            logger.info("[EMP-MAIL-007] Email enviado com sucesso | to='{}' | subject='{}' | tempo={}ms | anexo={}",
                    to, subject, duration, attachments != null ? attachments.size() : 0); // Log de sucesso

            reset(); // Limpa dados após envio

        } catch (MessagingException e) {
            logger.error("[EMP-MAIL-007] Erro ao enviar email | to='{}' | subject='{}' | erro={}",
                    to, subject, e.getMessage(), e); // Log de erro
            throw new RuntimeException("Erro ao enviar email", e); // Exceção
        } catch (Exception e) {
            logger.error("[EMP-MAIL-007] Erro inesperado no envio de email | erro={}", e.getMessage(), e); // Log de erro
            throw new RuntimeException("Erro inesperado ao enviar email", e); // Exceção
        }
    }

    // [EMP-MAIL-008-1]
    // Monta o footer padrão do e-mail
    public EmailSender buildEmailFooter() {

        logger.debug("[EMP-MAIL-008-1] Construindo footer padrão do e-mail");

        this.emailFooter = """
        <br><br>
        <div style="font-family: Arial, sans-serif; font-size: 13px; line-height: 1.8; color: #333;">
          
          <div style="margin-bottom: 15px;">
            <img src="https://raw.githubusercontent.com/oliveirawilliandev/img/refs/heads/main/logo.png"
                 width="150"
                 style="display: block; border-radius: 4px;">
          </div>

          <div style="margin-bottom: 15px;">
            📧 <a href="mailto:oliveira.willian.dev@gmail.com" style="text-decoration: none; color: #333;">oliveira.willian.dev@gmail.com</a><br>
            🌐 <a href="https://www.erpoliveira.com" target="_blank" style="text-decoration: none; color: #333;">www.erpoliveira.com</a><br>
            📍 Brasil
          </div>

          <div style="padding-top: 5px; border-top: 1px solid #eee; width: 250px;">
            <strong>Conecte-se:</strong><br>
            🐙 <a href="https://github.com/oliveirawilliandev/erp-rest-spring-boot" target="_blank" style="text-decoration: none; color: #0066cc;">GitHub</a><br>
            🔗 <a href="https://www.linkedin.com/in/oliveirawilliandev/" target="_blank" style="text-decoration: none; color: #0066cc;">LinkedIn</a>
          </div>

          <br>

          <span style="font-size: 11px; color: gray; line-height: 1.4; display: block;">
            Este e-mail foi enviado automaticamente pelo sistema ERP Oliveira.<br>
            Por favor, não responda esta mensagem.
          </span>

        </div>
        """;
        return this;
    }
    // [EMP-MAIL-008-2]
    // Monta o footer personalizado do e-mail
    public EmailSender buildEmailFooter(String footer) {

        logger.debug("[EMP-MAIL-008-2] Definindo footer personalizado");

        this.emailFooter = footer;

        return this;
    }

    // [EMP-MAIL-009]
    // Limpa os dados após o envio
    private void reset() {
        logger.debug("[EMP-MAIL-009] Resetando dados do EmailSender"); // Log de reset

        this.to = null; // Limpa destinatário
        this.subject = null; // Limpa assunto
        this.body = null; // Limpa corpo
        this.recipients.clear(); // Limpa lista
        this.attachments.clear(); // Limpa anexo
        this.emailFooter = ""; // Limpa Footer

        logger.debug("[EMP-MAIL-008] Dados resetados com sucesso"); // Log de conclusão
    }


}