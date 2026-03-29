package br.com.oliveirawillian.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
@Bean
    OpenAPI customOpenAPI(){
        return new OpenAPI().info(new Info().title("Rest API's RESTfull 0 with Java, Spring Boot, Kubernetes and Docker - Foo Bar")
                .version("v1").description("Rest API's RESTfull 0 with Java, Spring Boot, Kubernetes and Docker")
                .termsOfService("WillianFerreiradeOliveira.com").license(new License().name("Apache 2.0").url("WillianFerreiradeOliveira.com")));
    };
}
