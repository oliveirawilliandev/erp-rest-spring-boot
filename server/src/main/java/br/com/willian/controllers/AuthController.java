package br.com.willian.controllers; // Pacote da camada de controle/API

import br.com.willian.controllers.docs.AuthControllerDocs; // Interface de documentação
import br.com.willian.dto.v1.security.AccountCredentialAdminDTO;
import br.com.willian.dto.v1.security.AccountCredentialsDTO; // DTO para credenciais
import br.com.willian.services.AuthService; // Serviço de autenticação
import io.swagger.v3.oas.annotations.tags.Tag; // Tag do Swagger
import org.apache.commons.lang3.StringUtils; // Utilitários para string
import org.slf4j.Logger; // Interface de logging
import org.slf4j.LoggerFactory; // Factory para logger
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.http.HttpStatus; // Códigos HTTP
import org.springframework.http.MediaType; // Tipos de mídia
import org.springframework.http.ResponseEntity; // Resposta HTTP
import org.springframework.web.bind.annotation.*; // Anotações REST

@Tag(name = "Authentication") // Tag para documentação Swagger
@RestController // Controller REST
@RequestMapping("/auth") // URL base
public class AuthController implements AuthControllerDocs {

    // Logger para rastreamento
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired // Injeção de dependência
    private AuthService authService; // Serviço de autenticação

    // [AUTH-CTRL-001] Endpoint de login
    @PostMapping("/signin")
    @Override
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO accountCredentialsDTO) throws Exception {

        logger.debug("[AUTH-CTRL-001] Requisição de login | username={}",
                accountCredentialsDTO != null ? accountCredentialsDTO.getUserName() : "null");

        // Valida credenciais
        if (credentialsIsInvalid(accountCredentialsDTO)) {
            logger.warn("[AUTH-CTRL-001] Credenciais inválidas | username={}",
                    accountCredentialsDTO != null ? accountCredentialsDTO.getUserName() : "null");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        // Chama serviço de autenticação
        var token = authService.signIn(accountCredentialsDTO);

        if (token == null) {
            logger.warn("[AUTH-CTRL-001] Token não gerado | username={}", accountCredentialsDTO.getUserName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        logger.debug("[AUTH-CTRL-001] Login bem-sucedido | username={}", accountCredentialsDTO.getUserName());
        return token;
    }

    // [AUTH-CTRL-002] Endpoint de refresh token
    @PutMapping("/refresh/{userName}")
    @Override
    public ResponseEntity<?> refreshToken(
            @PathVariable("userName") String userName,
            @RequestHeader("Authorization") String refreshToken) throws Exception {

        logger.debug("[AUTH-CTRL-002] Requisição de refresh token | username={}", userName);

        // Valida parâmetros
        if (parametersAreInvalid(userName, refreshToken)) {
            logger.warn("[AUTH-CTRL-002] Parâmetros inválidos | username={}", userName);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        // Chama serviço de refresh
        var token = authService.refreshToken(userName, refreshToken);

        if (token == null) {
            logger.warn("[AUTH-CTRL-002] Token não gerado | username={}", userName);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        logger.debug("[AUTH-CTRL-002] Refresh token bem-sucedido | username={}", userName);
        return token;
    }

    // [AUTH-CTRL-003] Valida parâmetros de refresh
    private boolean parametersAreInvalid(String userName, String refreshToken) {
        boolean invalid = StringUtils.isBlank(userName) || StringUtils.isBlank(refreshToken);
        if (invalid) logger.debug("[AUTH-CTRL-003] Parâmetros inválidos detectados");
        return invalid;
    }

    // [AUTH-CTRL-004] Valida credenciais de login
    private static boolean credentialsIsInvalid(AccountCredentialsDTO accountCredentialsDTO) {
        boolean invalid = accountCredentialsDTO == null
                || StringUtils.isBlank(accountCredentialsDTO.getPassword())
                || StringUtils.isBlank(accountCredentialsDTO.getUserName());
        if (invalid) logger.debug("[AUTH-CTRL-004] Credenciais inválidas detectadas");
        return invalid;
    }

    // [AUTH-CTRL-005] Endpoint de criação de usuário
    @PostMapping(value = "/createUser",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<AccountCredentialsDTO> create(@RequestBody AccountCredentialsDTO credentialsDTO) {

        logger.debug("[AUTH-CTRL-005] Requisição de criação de usuário | username={}",
                credentialsDTO != null ? credentialsDTO.getUserName() : "null");

        AccountCredentialsDTO result = authService.create(credentialsDTO);

        logger.debug("[AUTH-CTRL-005] Usuário criado com sucesso | username={}",
                result != null ? result.getUserName() : "null");

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // [AUTH-CTRL-005] Endpoint de criação de usuario administrador
    @PostMapping(value = "/createAdmin",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Override
    public ResponseEntity<AccountCredentialAdminDTO> createAdmin(@RequestBody AccountCredentialAdminDTO credentialAdminDTO) {

        logger.info("[AUTH-CTRL-005] Requisição de criação de usuário | username={} | roles={}",
                credentialAdminDTO != null ? credentialAdminDTO.getUserName() : "null",
                credentialAdminDTO != null ? credentialAdminDTO.getRoles() : "null");

        AccountCredentialAdminDTO result = authService.createAdmin(credentialAdminDTO);

        logger.debug("[AUTH-CTRL-005] Usuário criado com sucesso | username={}",
                result != null ? result.getUserName() : "null");

        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }
}