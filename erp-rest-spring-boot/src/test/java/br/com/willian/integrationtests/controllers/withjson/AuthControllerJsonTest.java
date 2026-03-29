package br.com.oliveirawillian.integrationtests.controllers.withjson;

import br.com.oliveirawillian.config.TestConfigs;
import br.com.oliveirawillian.integrationtests.dto.AccountCredentialsDTO;
import br.com.oliveirawillian.integrationtests.dto.TokenDTO;
import br.com.oliveirawillian.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerJsonTest extends AbstractIntegrationTest {



    private static TokenDTO tokenDTO;
    @BeforeAll
    static void setUp() {

        tokenDTO = new TokenDTO();


    }

    @Test
    @Order(1)
    void signin() {
        AccountCredentialsDTO accountCredentialsDTO = new AccountCredentialsDTO("leandro", "admin123");
        tokenDTO = given()
                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(accountCredentialsDTO)
                 .when()
                .post()
                    .then()
                    .statusCode(200)
                        .extract()
                        .body()
                        .as(TokenDTO.class);

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());


    }

    @Test
    @Order(2)
    void refreshToken() {
        tokenDTO = given()
                .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("userName",tokenDTO.getUserName())
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getRefreshToken())
                .when()
                .put("{userName}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class);

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());

    }
}