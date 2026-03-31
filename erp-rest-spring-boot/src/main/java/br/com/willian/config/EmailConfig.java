package br.com.willian.config;

import org.springframework.boot.context.properties.ConfigurationProperties; // Faz bind das propriedades do application.yml/properties
import org.springframework.context.annotation.Configuration; // Registra a classe como Bean do Spring

@Configuration // Indica que esta classe contém configurações da aplicação
@ConfigurationProperties(prefix = "spring.mail") // Mapeia propriedades com prefixo spring.mail
public class EmailConfig {

    private String host;        // Endereço do servidor SMTP (ex: smtp.gmail.com)
    private int port = 587;     // Porta do servidor SMTP (587 padrão para TLS)
    private String username;    // Usuário do serviço de e-mail
    private String password;    // Senha do serviço de e-mail
    private String from;        // Endereço padrão de envio (remetente)
    private Boolean ssl;        // Indica se SSL está habilitado

    public EmailConfig() {
        // Construtor padrão exigido pelo Spring para bind das propriedades
    }

    public String getHost() {
        return host; // Retorna host SMTP configurado
    }

    public void setHost(String host) {
        this.host = host; // Define host SMTP
    }

    public int getPort() {
        return port; // Retorna porta SMTP
    }

    public void setPort(int port) {
        this.port = port; // Define porta SMTP
    }

    public String getUsername() {
        return username; // Retorna usuário do e-mail
    }

    public void setUsername(String username) {
        this.username = username; // Define usuário do e-mail
    }

    public String getPassword() {
        return password; // Retorna senha do e-mail
    }

    public void setPassword(String password) {
        this.password = password; // Define senha do e-mail
    }

    public String getFrom() {
        return from; // Retorna remetente padrão
    }

    public void setFrom(String from) {
        this.from = from; // Define remetente padrão
    }

    public Boolean getSsl() {
        return ssl; // Retorna se SSL está habilitado
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl; // Define se SSL deve ser utilizado
    }
}
