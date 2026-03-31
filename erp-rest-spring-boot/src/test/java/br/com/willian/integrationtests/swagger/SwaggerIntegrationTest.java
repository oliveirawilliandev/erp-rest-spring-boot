package br.com.willian.integrationtests.swagger;

import br.com.willian.config.TestConfigs;
import br.com.willian.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;

/**
 * Teste de integração responsável por validar a disponibilidade
 * da interface do Swagger UI.
 *
 * Objetivo do teste:
 * - Garantir que a aplicação sobe corretamente
 * - Verificar se a página do Swagger está acessível
 * - Assegurar que a documentação da API está sendo exposta
 *
 * Este teste utiliza:
 * - Servidor em porta definida (DEFINED_PORT)
 * - Banco PostgreSQL via Testcontainers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SwaggerIntegrationTest extends AbstractIntegrationTest { // Base comum de testes com container

    /**
     * Verifica se a página do Swagger UI é carregada com sucesso.
     *
     * Critérios de sucesso:
     * - Endpoint /swagger-ui/index.html retorna HTTP 200
     * - Conteúdo da página contém o texto "Swagger UI"
     *
     * Este teste falhará caso:
     * - A aplicação não suba corretamente
     * - O Swagger esteja desabilitado
     * - A rota do Swagger seja alterada
     */
    @Test
    void shouldDisplaySwaggerUIPage() {

        // Realiza uma requisição GET diretamente na página do Swagger UI
        var content = given()
                .basePath("/swagger-ui/index.html")
                    .port(TestConfigs.SERVER_PORT)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .asString();

        // Valida se o conteúdo retornado corresponde à interface do Swagger
        assertTrue(content.contains("Swagger UI"));
    }
}
