package br.com.willian.integrationtests.controllers.withxml;

import br.com.willian.config.TestConfigs;
import br.com.willian.integrationtests.dto.AccountCredentialsDTO;
import br.com.willian.integrationtests.dto.EmployeesDTO;
import br.com.willian.integrationtests.dto.TokenDTO;
import br.com.willian.integrationtests.dto.wrappers.xml.employees.PageModelEmployeesXML;
import br.com.willian.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;
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
 * - Jackson para serialização/deserialização XML
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeesControllerXmlTest extends AbstractIntegrationTest { // Base comum de testes com container

    //Configuração base das requisições HTTP do RestAssured
    private static RequestSpecification specification;

    // ObjectMapper para conversão XML <-> Objeto
    private static ObjectMapper objectMapper;

    // DTO usado nos testes de criação e consulta
    private static EmployeesDTO employeesDTO;

    //variavel de geração de CPF aleatorio
    private static String cpf;

    // Mapper do Jackson usado para converter XML em objeto Java e objeto Java em XML.
    private static XmlMapper xmlMapper;

    private static TokenDTO tokenDTO;

    // Inicialização global antes da execução dos testes
    @BeforeAll
    static void setUp() {
        objectMapper = new XmlMapper();
        // Ignora campos não mapeados no DTO
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // Evita erro caso a API retorne campos não mapeados no DTO
        // Suporte a Java 8 Time (LocalDate, OffsetDateTime)
        objectMapper.registerModule(new JavaTimeModule());
        // Formato ISO para datas
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        employeesDTO = new EmployeesDTO();
        tokenDTO = new TokenDTO();
    }

    @Test
    @Order(0)
    void signin() throws JsonProcessingException {
        AccountCredentialsDTO accountCredentialsDTO = new AccountCredentialsDTO("leandro", "admin123");
        var content =
                given()
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

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN,TestConfigs.ORIGIN_ERUDIO)
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION,"Bearer " + tokenDTO.getAccessToken())

                .setBasePath("/api/books/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();


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
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION,"Bearer " + tokenDTO.getAccessToken())
                .setBasePath("/api/employee/v1")  // path base da API
                .setPort(TestConfigs.SERVER_PORT)  // porta da aplicação
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))  // log da requisição
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))  // log da resposta
                .build();  // finaliza config

        // Serializa "manualmente" o objeto para XML usando o mapper configurado com JAXB e JavaTimeModule
        // Garante que as datas (LocalDate, OffsetDateTime) sejam formatadas corretamente (ex: 1990-01-01)
        // Ignora o serializador padrão do RestAssured que estava gerando campos de data vazios (<birthDate/>)
        String xmlContent = objectMapper.writeValueAsString(employeesDTO);

        // Execução da requisição POST
        System.out.println(objectMapper.writeValueAsString(employeesDTO));
        var content = given(specification)  // inicia requisição
                .contentType(MediaType.APPLICATION_XML_VALUE)  // informa XML no body
                .accept(MediaType.APPLICATION_XML_VALUE)  // aceita XML
                .body(xmlContent)  // dados enviados
                .when()  // executa requisição
                .post()  // metodo POST
                .then()  // inicia validações
                .statusCode(201)  // espera sucesso
                .contentType(MediaType.APPLICATION_XML_VALUE)
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
    // UPDATE - ORIGIN PERMITIDA
    // ======================================================

    /**
     * Testa a UPDATE de um Employee com ORIGIN permitida.
     * Espera:
     * - Status 200
     * - Retorno do objeto criado corretamente
     */
    @Test
    @Order(2)
    void updateWithAllowedOrigin() throws JsonProcessingException {
        employeesDTO.setLastName("Ferreira");
        employeesDTO.setJobTitle("Progamador Developer");
        // Serializa "manualmente" o objeto para XML usando o mapper configurado com JAXB e JavaTimeModule
        // Garante que as datas (LocalDate, OffsetDateTime) sejam formatadas corretamente (ex: 1990-01-01)
        // Ignora o serializador padrão do RestAssured que estava gerando campos de data vazios (<birthDate/>)
        String xmlContent = objectMapper.writeValueAsString(employeesDTO);


        // Execução da requisição POST
        var content = given(specification)  // inicia requisição
                .contentType(MediaType.APPLICATION_XML_VALUE)  // informa XML no body
                .accept(MediaType.APPLICATION_XML_VALUE)  // aceita XML
                .body(xmlContent)  // dados enviados
                .when()  // executa requisição
                .put()  // metodo PUT
                .then()  // inicia validações
                .statusCode(200)  // espera sucesso
                .contentType(MediaType.APPLICATION_XML_VALUE)
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
        assertEquals("Ferreira", created.getLastName());
        assertEquals(cpf, created.getCpf());
        assertEquals("Blumenau", created.getCity());
        assertEquals("SC", created.getState());
        assertEquals("Progamador Developer", created.getJobTitle());
        assertTrue(created.getActive());
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



        var content = given(specification)  // inicia requisição
                .contentType(MediaType.APPLICATION_XML_VALUE)  // aceita XML
                .accept(MediaType.APPLICATION_XML_VALUE)  // aceita XML
                .pathParam("id", employeesDTO.getId())  // ID do employee
                .when()  // executa
                .get("{id}")  // GET por ID
                .then()  // validações
                .statusCode(200)  // sucesso
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()  // extrai resposta
                .body()  // acessa body
                .asString();  // converte em String

        EmployeesDTO found = objectMapper.readValue(content, EmployeesDTO.class);

        // Validações
        assertEquals(employeesDTO.getId(), found.getId());
        assertEquals("Willian", found.getFirstName());
        assertEquals("Ferreira", found.getLastName());
        assertEquals("Blumenau", found.getCity());
        assertEquals("SC", found.getState());
        assertEquals("Progamador Developer", found.getJobTitle());
        assertTrue(found.getActive());
    }

    // ======================================================
    // DISABLE EMPLOYEE - ORIGIN PERMITIDA
    // ======================================================

    /**
     * Desativa logicamente um registro com ORIGIN permitida.
     * Espera:
     * - Status 200
     * - Retorno correto do Employee
     */
    @Test
    @Order(4)
    void disableWithAllowedOrigin() throws JsonProcessingException {



        var content = given(specification)  // inicia requisição
                .contentType(MediaType.APPLICATION_XML_VALUE)  // aceita XML
                .accept(MediaType.APPLICATION_XML_VALUE)  // aceita XML
                .pathParam("id", employeesDTO.getId())  // ID do employee
                .when()  // executa
                .patch("{id}")  // PATCH por ID
                .then()  // validações
                .statusCode(200)  // sucesso
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()  // extrai resposta
                .body()  // acessa body
                .asString();  // converte em String

        EmployeesDTO found = objectMapper.readValue(content, EmployeesDTO.class);

        // Validações
        assertEquals(employeesDTO.getId(), found.getId());
        assertEquals("Willian", found.getFirstName());
        assertEquals("Ferreira", found.getLastName());
        assertEquals("Blumenau", found.getCity());
        assertEquals("SC", found.getState());
        assertEquals("Progamador Developer", found.getJobTitle());
        assertFalse(found.getActive());
    }


    // ======================================================
    // DELETA EMPLOYEE - ORIGIN PERMITIDA
    // ======================================================

    /**
     * Remove um registro existente pelo identificador com ORIGIN permitida.
     * Espera:
     * - Status 200
     * - Retorno correto do Employee
     */
    @Test
    @Order(5)
    void deleteWithAllowedOrigin() throws JsonProcessingException {



        var content = given(specification)  // inicia requisição
                .pathParam("id", employeesDTO.getId())  // ID do employee
                .when()  // executa
                .delete("{id}")  // DELETE por ID
                .then()  // validações
                .statusCode(204); // sucesso
    }

    // ======================================================
    // FIND ALL - ORIGIN PERMITIDA
    // ======================================================

    /**
     * Teste de integração para o endpoint de listagem paginada de funcionários (GET /api/employee/v1)
     * com uma origem (Origin header) permitida pela política CORS.
     *
     * Este teste verifica que:
     *
     *   A API aceita requisições da origem configurada
     *    Retorna status HTTP 200 (OK)
     *    Retorna o conteúdo no formato XML correto  
     *    A paginação funciona conforme os parâmetros fornecidos  
     *    Os dados retornados possuem estrutura válida  
     *   
     *
     *   Cenário testado:  Listagem de funcionários com paginação (página 0, tamanho 12,
     * ordenação ascendente) a partir de uma origem autorizada.
     *
     *   Dependências:  Espera que o banco de dados tenha dados populados para validação.
     *
     *   Ordem de execução:  6 - Executado após testes de CRUD individuais e antes de
     * testes com origens não permitidas.
     *
     * see EmployeesControllerXMLTest#setup() Configuração inicial do teste
     * see EmployeesControllerXMLTest#specification Configuração do RestAssured
     * see WrapperEmployeesDTO Wrapper para resposta paginada
     * see EmployeesDTO DTO do funcionário
     *
     * throws JsonProcessingException Se houver erro na desserialização do XML
     */
    @Test
    @Order(6)
    void findAllWithAllowedOrigin() throws JsonProcessingException {
        // ======================================================
        // 1. EXECUÇÃO DA REQUISIÇÃO HTTP
        // ======================================================

        // Realiza requisição GET para o endpoint de listagem de funcionários
        // com parâmetros de paginação e ordenação

        var content = given(specification)  // Usa configuração compartilhada do RestAssured
                .accept(MediaType.APPLICATION_XML_VALUE)  // aceita XML  // aceita XML
                .queryParam("page", 0)                // Número da página (paginação começa em 0)
                .queryParam("size", 12)                // Quantidade máxima de registros retornados por página
                .queryParam("direction", "asc")       // Direção da ordenação (ascendente)
                .when()  // executa
                .get()// Endpoint GET
                .then()  // validações
                .statusCode(200)  // sucesso
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()  // extrai resposta
                .body()  // acessa body
                .asString();  // converte em String

        // ======================================================
        // 2. PROCESSAMENTO DA RESPOSTA XML
        // ======================================================

        // Desserializa o XML da resposta para o objeto wrapper que contém
        // a estrutura paginada com metadados HATEOAS
        PageModelEmployeesXML wrapper = objectMapper.readValue(content, PageModelEmployeesXML.class);

        // Extrai a lista de funcionários do wrapper
        // O wrapper contém um objeto "_embedded" com a propriedade "employeesDTOList"

        List<EmployeesDTO> employeesDTOList = wrapper.getContent();


        // Validação de tamanho da página
        assertEquals(12, employeesDTOList.size(),
                "A página deve conter exatamente 12 registros conforme solicitado");

        // Validação de que a lista não está vazia
        assertFalse(employeesDTOList.isEmpty(),
                "A lista de funcionários não deve estar vazia");


        // Obtém o primeiro funcionário da lista paginada
        // A ordenação padrão é por firstName ascendente

        EmployeesDTO employeeOne = employeesDTOList.get(0);

        // ======================================================
        // 3. VALIDAÇÕES DOS DADOS RETORNADOS
        // ======================================================

        // Validações básicas do objeto funcionário

        assertNotNull(employeeOne.getId());
        assertTrue(employeeOne.getId() > 0);

        // Validações de estrutura de dados
        assertEquals(4, employeeOne.getId());
        assertEquals("Ana", employeeOne.getFirstName());
        assertEquals("Pereira", employeeOne.getLastName());
        assertEquals("São Paulo", employeeOne.getCity());
        assertEquals("SP", employeeOne.getState());
        assertEquals("Assistente", employeeOne.getJobTitle());
        assertTrue(employeeOne.getActive()); // "O funcionário deve estar ativo


        // [EMPLOYEETWO]
        // Obtém o Segundo funcionário da lista paginada
        // A ordenação padrão é por firstName ascendente

        EmployeesDTO employeeTwo = employeesDTOList.get(1);

        // ======================================================
        // 3. VALIDAÇÕES DOS DADOS RETORNADOS
        // ======================================================

        // Validações básicas do objeto funcionário

        assertNotNull(employeeTwo.getId());
        assertTrue(employeeTwo.getId() > 0);

        // Validações de estrutura de dados
        assertEquals(6, employeeTwo.getId());
        assertEquals("Beatriz", employeeTwo.getFirstName());
        assertEquals("Costa", employeeTwo.getLastName());
        assertEquals("São Paulo", employeeTwo.getCity());
        assertEquals("SP", employeeTwo.getState());
        assertEquals("Analista", employeeTwo.getJobTitle());
        assertTrue(employeeTwo.getActive()); // "O funcionário deve estar ativo



        // [EMPLOYEE FOUR]
        // Obtém o primeiro funcionário da lista paginada
        // A ordenação padrão é por firstName ascendente

        EmployeesDTO employeeFour = employeesDTOList.get(3);

        // ======================================================
        // 3. VALIDAÇÕES DOS DADOS RETORNADOS
        // ======================================================

        // Validações básicas do objeto funcionário

        assertNotNull(employeeFour.getId());
        assertTrue(employeeFour.getId() > 0);

        // Validações de estrutura de dados
        assertEquals(3, employeeFour.getId());
        assertEquals("Carlos", employeeFour.getFirstName());
        assertEquals("Souza", employeeFour.getLastName());
        assertEquals("São Paulo", employeeFour.getCity());
        assertEquals("SP", employeeFour.getState());
        assertEquals("Coordenador", employeeFour.getJobTitle());
        assertTrue(employeeFour.getActive()); // "O funcionário deve estar ativo



    }


    // ======================================================
    // FIND By Name- ORIGIN PERMITIDA
    // ======================================================

    /**
     * Teste de integração para o endpoint de listagem paginada de funcionários (GET /api/employee/v1)
     * com uma origem (Origin header) permitida pela política CORS.
     *
     * Este teste verifica que:
     *
     *   A API aceita requisições da origem configurada
     *    Retorna status HTTP 200 (OK)
     *    Retorna o conteúdo no formato XML correto
     *    A paginação funciona conforme os parâmetros fornecidos
     *    Os dados retornados possuem estrutura válida
     *
     *
     *   Cenário testado:  Listagem de funcionários com paginação (página 0, tamanho 12,
     * ordenação ascendente e informação de um nome pelo pathParam firstName) a partir de uma origem autorizada.
     *
     *   Dependências:  Espera que o banco de dados tenha dados populados para validação.
     *
     *   Ordem de execução:  6 - Executado após testes de CRUD individuais e antes de
     * testes com origens não permitidas.
     *
     * see EmployeesControllerXMLTest#setup() Configuração inicial do teste
     * see EmployeesControllerXMLTest#specification Configuração do RestAssured
     * see WrapperEmployeesDTO Wrapper para resposta paginada
     * see EmployeesDTO DTO do funcionário
     *
     * throws JsonProcessingException Se houver erro na desserialização do XML
     */
    @Test
    @Order(7)
    void findByNameWithAllowedOrigin() throws JsonProcessingException {
        // ======================================================
        // 1. EXECUÇÃO DA REQUISIÇÃO HTTP
        // ======================================================

        // Realiza requisição GET para o endpoint de listagem de funcionários
        // com parâmetros de paginação e ordenação

        var content = given(specification)  // Usa configuração compartilhada do RestAssured
                .accept(MediaType.APPLICATION_XML_VALUE)  // aceita XML  // aceita XML
                .pathParam("firstName", "a")          // Path Param usado para filtrar funcionários pelo primeiro nome
                .queryParam("page", 0)                // Número da página (paginação começa em 0)
                .queryParam("size", 4)                // Quantidade máxima de registros retornados por página
                .queryParam("direction", "asc")       // Direção da ordenação (ascendente)
                .when()  // executa
                .get("findEmployeeByName/{firstName}")// Endpoint GET com injeção do path param "firstName"
                .then()  // validações
                .statusCode(200)  // sucesso
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()  // extrai resposta
                .body()  // acessa body
                .asString();  // converte em String

        // ======================================================
        // 2. PROCESSAMENTO DA RESPOSTA XML
        // ======================================================

        // Desserializa o XML da resposta para o objeto wrapper que contém
        // a estrutura paginada com metadados HATEOAS
        PageModelEmployeesXML wrapper = objectMapper.readValue(content, PageModelEmployeesXML.class);

        // Extrai a lista de funcionários do wrapper
        // O wrapper contém um objeto "_embedded" com a propriedade "employeesDTOList"

        List<EmployeesDTO> employeesDTOList = wrapper.getContent();


        // Validação de tamanho da página
        assertEquals(4, employeesDTOList.size(),
                "A página deve conter exatamente 12 registros conforme solicitado");

        // Validação de que a lista não está vazia
        assertFalse(employeesDTOList.isEmpty(),
                "A lista de funcionários não deve estar vazia");


        // Obtém o primeiro funcionário da lista paginada
        // A ordenação padrão é por firstName ascendente

        EmployeesDTO employeeOne = employeesDTOList.get(0);

        // ======================================================
        // 3. VALIDAÇÕES DOS DADOS RETORNADOS
        // ======================================================

        // Validações básicas do objeto funcionário

        assertNotNull(employeeOne.getId());
        assertTrue(employeeOne.getId() > 0);

        // Validações de estrutura de dados
        assertEquals(4, employeeOne.getId());
        assertEquals("Ana", employeeOne.getFirstName());
        assertEquals("Pereira", employeeOne.getLastName());
        assertEquals("São Paulo", employeeOne.getCity());
        assertEquals("SP", employeeOne.getState());
        assertEquals("Assistente", employeeOne.getJobTitle());
        assertTrue(employeeOne.getActive()); // "O funcionário deve estar ativo


        // [EMPLOYEETWO]
        // Obtém o Segundo funcionário da lista paginada
        // A ordenação padrão é por firstName ascendente

        EmployeesDTO employeeTwo = employeesDTOList.get(1);

        // ======================================================
        // 3. VALIDAÇÕES DOS DADOS RETORNADOS
        // ======================================================

        // Validações básicas do objeto funcionário

        assertNotNull(employeeTwo.getId());
        assertTrue(employeeTwo.getId() > 0);

        // Validações de estrutura de dados
        assertEquals(6, employeeTwo.getId());
        assertEquals("Beatriz", employeeTwo.getFirstName());
        assertEquals("Costa", employeeTwo.getLastName());
        assertEquals("São Paulo", employeeTwo.getCity());
        assertEquals("SP", employeeTwo.getState());
        assertEquals("Analista", employeeTwo.getJobTitle());
        assertTrue(employeeTwo.getActive()); // "O funcionário deve estar ativo



        // [EMPLOYEE FOUR]
        // Obtém o primeiro funcionário da lista paginada
        // A ordenação padrão é por firstName ascendente

        EmployeesDTO employeeFour = employeesDTOList.get(3);

        // ======================================================
        // 3. VALIDAÇÕES DOS DADOS RETORNADOS
        // ======================================================

        // Validações básicas do objeto funcionário

        assertNotNull(employeeFour.getId());
        assertTrue(employeeFour.getId() > 0);

        // Validações de estrutura de dados
        assertEquals(3, employeeFour.getId());
        assertEquals("Carlos", employeeFour.getFirstName());
        assertEquals("Souza", employeeFour.getLastName());
        assertEquals("São Paulo", employeeFour.getCity());
        assertEquals("SP", employeeFour.getState());
        assertEquals("Coordenador", employeeFour.getJobTitle());
        assertTrue(employeeFour.getActive()); // "O funcionário deve estar ativo



    }





    // ======================================================
    // MOCK DE EMPLOYEE
    // ======================================================

    /**
     * Cria um mock completo de EmployeeDTO
     * utilizado nos testes de criação.
     */

    private EmployeesDTO mockEmployee() {
        //Geração aleatoria de 11 numeros
        Random random = new Random();
        cpf = String.format("%011d", Math.abs(random.nextLong() % 100000000000L));
        // Geração Aleatoria de 6 numero usando o principio do CPF
        String email = cpf.substring(0, 6);

        EmployeesDTO dto = new EmployeesDTO();
        dto.setFirstName("Willian");
        dto.setLastName("Oliveira");

        dto.setCpf(cpf);
        dto.setEmail("willian"+email+"@teste.com");
        dto.setGender("MALE");
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

        dto.setBirthDate(LocalDate.of(1990,1,1)); // 01/01/1990
        dto.setHireDate(LocalDate.now());
        dto.setTerminationDate(null);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        return  dto;
    }

}
