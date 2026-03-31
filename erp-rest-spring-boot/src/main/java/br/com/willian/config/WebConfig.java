package br.com.willian.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Classe de configuração global do Spring MVC.
 *
 * Responsável por:
 * 1) Definir negociação de conteúdo (JSON, XML, YAML)
 * 2) Configurar CORS para permitir chamadas externas (front-end, APIs, etc.)
 *
 * Qualquer mudança aqui afeta TODA a aplicação.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Define os domínios permitidos para CORS.
     *
     * O valor vem do application.properties ou application.yml:
     * cors.originPatterns=http://localhost:3000,http://meusite.com
     *
     * Caso não exista a propriedade, usa "default".
     */
    @Value("${cors.originPatterns:default}")
    private String corsOriginPatterns = "";

    /**
     * Configuração de negociação de conteúdo (Content Negotiation).
     *
     * Define como a API decide se retorna JSON, XML ou YAML.
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        /*
         * OPÇÃO 1 (comentada):
         * Permite escolher o formato via query param:
         * Ex: ?mediaType=xml
         *
         * Não recomendado para APIs REST modernas.
         */
        // configurer.favorParameter(true)
        //     .parameterName("mediaType")
        //     .ignoreAcceptHeader(true)
        //     .useRegisteredExtensionsOnly(false)
        //     .defaultContentType(MediaType.APPLICATION_JSON)
        //     .mediaType("json", MediaType.APPLICATION_JSON)
        //     .mediaType("xml", MediaType.APPLICATION_XML);

        /*
         * OPÇÃO 2 (ativa):
         * Negociação via HEADER Accept (padrão REST)
         *
         * Ex:
         * Accept: application/json
         * Accept: application/xml
         * Accept: application/x-yaml
         */
        configurer
                .favorParameter(false)  // Não permite negociação por parâmetro de URL
                .ignoreAcceptHeader(false) // Respeita o header Accept enviado pelo cliente
                .useRegisteredExtensionsOnly(false) // Permite extensões não registradas explicitamente
                .defaultContentType(MediaType.APPLICATION_JSON) // Caso o cliente não envie Accept, retorna JSON por padrão

                // Tipos de mídia suportados pela API
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("yaml", MediaType.APPLICATION_YAML);
    }

    /**
     * Configuração global de CORS (Cross-Origin Resource Sharing).
     *
     * Necessário para permitir que front-ends externos
     * (React, Angular, etc.) acessem a API.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        /*
         * Converte a string de origens permitidas
         * separadas por vírgula em array.
         */
        var allowedOrigins = corsOriginPatterns.split(",");

        registry.addMapping("/**") // Aplica CORS para todos os endpoints
                .allowedOrigins(allowedOrigins) // Domínios permitidos
                .allowedMethods("*") // Permite TODOS os métodos HTTP (GET, POST, PUT, DELETE, etc.)

                /*
                 * Permite envio de cookies, tokens e credenciais.
                 * IMPORTANTE: allowedOrigins NÃO pode ser "*" quando isso está true.
                 */
                .allowCredentials(true);
    }
}
