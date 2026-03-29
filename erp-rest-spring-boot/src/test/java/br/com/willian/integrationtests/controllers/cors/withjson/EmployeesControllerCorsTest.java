package br.com.willian.integrationtests.controllers.withjson;

import br.com.willian.config.TestConfigs;
import br.com.willian.integrationtests.dto.EmployeesDTO;
import br.com.willian.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração do controller de Employees
 * com foco na validação de CORS (Cross-Origin Resource Sharing).
 *
 * Cenários cobertos:
 * - Requisições com ORIGIN permitida
 * - Requisições com ORIGIN bloqueada
 *
 * Os testes utilizam:
 * - Testcontainers (AbstractIntegrationTest)
 * - RestAssured para chamadas HTTP
 * - Jackson para serialização/deserialização JSON
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeesControllerCorsTest extends AbstractIntegrationTest {

    //Configuração base das requisições HTTP do RestAssured
    private static RequestSpecification specification;

    // ObjectMapper para conversão JSON <-> Objeto
    private static ObjectMapper objectMapper;

    // DTO usado nos testes de criação e consulta
    private static EmployeesDTO employeesDTO;

    private static String cpf;

    // Inicialização global antes da execução dos testes
    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // Evita erro caso a API retorne campos não mapeados no DTO

        employeesDTO = new EmployeesDTO();
    }

    // ======================================================
    // CREATE - ORIGIN PERMITIDA
    // ======================================================

    /**
     * Testa a criação de um Employee com ORIGIN permitida.
     * Espera:
     * - Status 200
     * - Retorno do objeto criado corretamente
     */
    @Test
    @Order(1)
    void createWithAllowedOrigin() throws JsonProcessingException {

        employeesDTO = mockEmployee(); // Preenche o DTO com dados válidos

        // Configuração da requisição
        specification = new RequestSpecBuilder() // cria config da requisição
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_OLIVEIRAWILLIANDEV)  // header Origin (CORS)
                .setBasePath("/api/employee/v1")  // path base da API
                .setPort(TestConfigs.SERVER_PORT)  // porta da aplicação
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))  // log da requisição
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))  // log da resposta
                .build();  // finaliza config

        // Execução da requisição POST
        var content = given(specification)  // inicia requisição
                .contentType(MediaType.APPLICATION_JSON_VALUE)  // informa JSON no body
                .body(employeesDTO)  // dados enviados
                .when()  // executa requisição
                .post()  // metodo POST
                .then()  // inicia validações
                .statusCode(200)  // espera sucesso
                .extract()  // extrai resposta
                .body()  // acessa body
                .asString();  // converte em String

        // Converte resposta para DTO
        EmployeesDTO created = objectMapper.readValue(content, EmployeesDTO.class);
        employeesDTO = created;

        // Validações
        assertNotNull(created.getId());
        assertTrue(created.getId() > 0);
        assertEquals("Willian", created.getFirstName());
        assertEquals("Oliveira", created.getLastName());
        assertEquals(cpf, created.getCpf());
        assertEquals("Blumenau", created.getCity());
        assertEquals("SC", created.getState());
        assertEquals("Developer", created.getJobTitle());
        assertTrue(created.getActive());
    }

    // ======================================================
    // CREATE - ORIGIN BLOQUEADA
    // ======================================================

    /**
     * Testa a criação de um Employee com ORIGIN não permitida.
     * Espera:
     * - Status 403
     * - Mensagem "Invalid CORS request"
     */
    @Test
    @Order(2)
    void createWithWrongOrigin() {

        specification = new RequestSpecBuilder() // cria config da requisição
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)  // origem bloqueada
                .setBasePath("/api/employee/v1")  // path da API
                .setPort(TestConfigs.SERVER_PORT)  // porta da aplicação
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))  // log da requisição
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))  // log da resposta
                .build();  // finaliza config

        var content = given(specification)  // inicia requisição
                .contentType(MediaType.APPLICATION_JSON_VALUE)  // body em JSON
                .body(employeesDTO)  // dados enviados
                .when()  // executa
                .post()  // POST
                .then()  // validações
                .statusCode(403)  // acesso negado
                .extract()  // extrai resposta
                .body()  // acessa body
                .asString();  // converte em String

        assertEquals("Invalid CORS request", content); // valida mensagem de erro
    }

    // ======================================================
    // FIND BY ID - ORIGIN PERMITIDA
    // ======================================================

    /**
     * Testa a busca por ID com ORIGIN permitida.
     * Espera:
     * - Status 200
     * - Retorno correto do Employee
     */
    @Test
    @Order(3)
    void findByIdWithAllowedOrigin() throws JsonProcessingException {

        specification = new RequestSpecBuilder()  // cria config da requisição
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)  // origem permitida
                .setBasePath("/api/employee/v1")  // path da API
                .setPort(TestConfigs.SERVER_PORT)  // porta da aplicação
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))  // log da requisição
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))  // log da resposta
                .build();  // finaliza config

        var content = given(specification)  // inicia requisição
                .contentType(MediaType.APPLICATION_JSON_VALUE)  // aceita JSON
                .pathParam("id", employeesDTO.getId())  // ID do employee
                .when()  // executa
                .get("{id}")  // GET por ID
                .then()  // validações
                .statusCode(200)  // sucesso
                .extract()  // extrai resposta
                .body()  // acessa body
                .asString();  // converte em String

        EmployeesDTO found = objectMapper.readValue(content, EmployeesDTO.class);

        // Validações
        assertEquals(employeesDTO.getId(), found.getId());
        assertEquals("Willian", found.getFirstName());
        assertEquals("Oliveira", found.getLastName());
        assertEquals("Blumenau", found.getCity());
        assertEquals("SC", found.getState());
        assertTrue(found.getActive());
    }

    // ======================================================
    // FIND BY ID - ORIGIN BLOQUEADA
    // ======================================================

    /**
     * Testa a busca por ID com ORIGIN não permitida.
     * Espera:
     * - Status 403
     * - Mensagem "Invalid CORS request"
     */
    @Test
    @Order(4)
    void findByIdWithWrongOrigin() {

        specification = new RequestSpecBuilder()  // cria config da requisição
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)  // origem bloqueada
                .setBasePath("/api/employee/v1")  // path da API
                .setPort(TestConfigs.SERVER_PORT)  // porta da aplicação
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))  // log da requisição
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))  // log da resposta
                .build();  // finaliza config


        var content = given(specification)  // inicia requisição
                .contentType(MediaType.APPLICATION_JSON_VALUE)  // aceita JSON
                .pathParam("id", employeesDTO.getId())  // ID do employee
                .when()  // executa
                .get("{id}")  // GET por ID
                .then()  // validações
                .statusCode(403)  // acesso negado
                .extract()  // extrai resposta
                .body()  // acessa body
                .asString();  // converte em String

        assertEquals("Invalid CORS request", content); // valida mensagem de erro
    }

    // ======================================================
    // MOCK DE EMPLOYEE
    // ======================================================

    /**
     * Cria um mock completo de EmployeeDTO
     * utilizado nos testes de criação.
     */
    private EmployeesDTO mockEmployee() {
        Random random = new Random();
        cpf = String.format("%011d", Math.abs(random.nextLong() % 100000000000L));
        String email = cpf.substring(0, 6);

        EmployeesDTO dto = new EmployeesDTO();
        dto.setFirstName("Willian");
        dto.setLastName("Oliveira");

        dto.setCpf(cpf);
        dto.setEmail("willian"+email+"@teste.com");
        dto.setPhone("4730000000");
        dto.setMobilePhone("47999999999");

        dto.setZipCode("89000-000");
        dto.setStreet("Rua Central");
        dto.setStreetNumber("123");
        dto.setNeighborhood("Centro");
        dto.setCity("Blumenau");
        dto.setState("SC");
        dto.setJobTitle("Developer");
        dto.setDepartment("TI");
        dto.setActive(true);

        dto.setBirthDate(new Date(631152000000L)); // 01/01/1990
        dto.setHireDate(new Date());
        dto.setTerminationDate(null);
        dto.setCreatedAt(new Date());
        dto.setUpdatedAt(new Date());
        return  dto;
    }
}
