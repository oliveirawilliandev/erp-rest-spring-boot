package br.com.oliveirawillian.integrationtests.controllers.withyaml;

import br.com.oliveirawillian.config.TestConfigs;
import br.com.oliveirawillian.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.oliveirawillian.integrationtests.dto.AccountCredentialsDTO;
import br.com.oliveirawillian.integrationtests.dto.TokenDTO;
import br.com.oliveirawillian.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerYamlTest extends AbstractIntegrationTest {


    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;
    private static TokenDTO tokenDTO;
    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();
        tokenDTO = new TokenDTO();


    }

    @Test
    @Order(1)
    void signin() throws JsonProcessingException {
        AccountCredentialsDTO accountCredentialsDTO = new AccountCredentialsDTO("leandro", "admin123");

        tokenDTO = given().config(
                RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
        )


                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(accountCredentialsDTO,objectMapper)
                 .when()
                .post()
                    .then()
                    .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                    .extract()
                        .body()
                .as(TokenDTO.class, objectMapper);



        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());


    }

    @Test
    @Order(2)
    void refreshToken() throws JsonProcessingException {
        tokenDTO = given().config(
                RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
        )


                .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("userName",tokenDTO.getUserName())
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getRefreshToken())
                .when()
                .put("{userName}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(TokenDTO.class, objectMapper);






        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());

    }
}