package br.com.willian.integrationtests.controllers.withxml;

import br.com.oliveirawillian.config.TestConfigs;
import br.com.oliveirawillian.integrationtests.dto.AccountCredentialsDTO;
import br.com.oliveirawillian.integrationtests.dto.TokenDTO;
import br.com.oliveirawillian.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerXmlTest extends AbstractIntegrationTest {


    private static RequestSpecification specification;
    private static XmlMapper objectMapper;
    private static TokenDTO tokenDTO;
    @BeforeAll
    static void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        tokenDTO = new TokenDTO();


    }

    @Test
    @Order(1)
    void signin() throws JsonProcessingException {
        AccountCredentialsDTO accountCredentialsDTO = new AccountCredentialsDTO("leandro", "admin123");
        var content = given()
                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(accountCredentialsDTO)
                 .when()
                .post()
                    .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                    .extract()
                        .body()
                .asString();

        TokenDTO createdTokenDTO = objectMapper.readValue(content, TokenDTO.class);
        tokenDTO = createdTokenDTO;

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());


    }

    @Test
    @Order(2)
    void refreshToken() throws JsonProcessingException {
        var content = given()
                .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParam("userName",tokenDTO.getUserName())
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getRefreshToken())
                .when()
                .put("{userName}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        TokenDTO createdTokenDTO = objectMapper.readValue(content, TokenDTO.class);
        tokenDTO = createdTokenDTO;

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());


        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());

    }
}