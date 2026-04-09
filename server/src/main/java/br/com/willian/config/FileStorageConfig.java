package br.com.willian.config;

import org.springframework.boot.context.properties.ConfigurationProperties; // Mapeia propriedades do application.yml/properties
import org.springframework.context.annotation.Configuration; // Registra a classe como Bean de configuração

@Configuration // Indica que esta classe faz parte das configurações da aplicação
@ConfigurationProperties(prefix = "file") // Propriedades com prefixo "file"
public class FileStorageConfig {

    private String uploadDir; // Diretório base onde os arquivos serão armazenados

    public FileStorageConfig() {
        // Construtor padrão necessário para o bind automático das propriedades
    }

    public String getUploadDir() {
        return uploadDir; // Retorna o diretório configurado para upload de arquivos
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir; // Define o diretório onde os arquivos serão salvos
    }
}
