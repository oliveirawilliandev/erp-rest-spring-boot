package br.com.willian.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração global do OpenAPI / Swagger.
 *
 * Responsável por definir as informações exibidas na documentação da API,
 * como título, versão, descrição, termos de uso e licença.
 *
 * Essa configuração é utilizada por ferramentas como:
 * - Swagger UI
 * - Springdoc OpenAPI
 *
 * Qualquer alteração aqui reflete diretamente na documentação pública da API.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Bean responsável por customizar o OpenAPI.
     *
     * Define os metadados principais da API, que aparecem no Swagger UI:
     * - Título da API
     * - Versão
     * - Descrição
     * - Termos de serviço
     * - Licença
     *
     * @return instância configurada de {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("ERP Rest API's RESTfull with Java, Spring Boot")  // Nome exibido no topo do Swagger UI
                        .version("v1")   // Versão atual da API
                        .description("ERP Rest API's RESTfull with Java, Spring Boot") // Descrição geral do propósito da API
                        .termsOfService("https://oliveirawilliandev.com.br")   // URL ou texto referente aos termos de serviço
                        // Informações de licenciamento da API
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://oliveirawilliandev.com.br")
                        )
                );
    }
}
