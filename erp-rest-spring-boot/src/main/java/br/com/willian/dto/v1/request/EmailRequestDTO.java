package br.com.willian.dto.v1.request;

import java.io.Serializable;
import java.util.Objects; // Utilitário para equals e hashCode

public class EmailRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;


    private String to;       // Endereço de e-mail do destinatário
    private String subject;  // Assunto do e-mail
    private String body;     // Corpo/conteúdo do e-mail

    public EmailRequestDTO() {
        // Construtor padrão necessário para frameworks de serialização (Jackson)
    }

    public String getTo() {
        return to; // Retorna destinatário do e-mail
    }

    public void setTo(String to) {
        this.to = to; // Define destinatário do e-mail
    }

    public String getSubject() {
        return subject; // Retorna assunto do e-mail
    }

    public void setSubject(String subject) {
        this.subject = subject; // Define assunto do e-mail
    }

    public String getBody() {
        return body; // Retorna corpo do e-mail
    }

    public void setBody(String body) {
        this.body = body; // Define conteúdo do e-mail
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EmailRequestDTO that)) return false; // Verifica tipo
        return Objects.equals(to, that.to) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(body, that.body); // Compara campos relevantes
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                to,
                subject,
                body
        ); // Gera hash consistente com equals
    }
}
