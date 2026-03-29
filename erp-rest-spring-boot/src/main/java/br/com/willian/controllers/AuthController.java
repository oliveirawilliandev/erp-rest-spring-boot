package br.com.oliveirawillian.controllers;

import br.com.oliveirawillian.controllers.docs.AuthControllerDocs;
import br.com.oliveirawillian.data.dto.v1.PersonDTO;
import br.com.oliveirawillian.data.dto.v1.security.AccountCredentialsDTO;
import br.com.oliveirawillian.data.dto.v1.security.TokenDTO;
import br.com.oliveirawillian.services.AuthService;
import br.com.oliveirawillian.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint! ")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {
    @Autowired
    private AuthService authService;
    @PostMapping("/signin")
    @Override
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO accountCredentialsDTO) throws Exception {
        if(credentialsIsInvalid(accountCredentialsDTO)) {
           return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }
        var token = authService.signIn(accountCredentialsDTO);
        if(token == null) {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

        }
        return token;
    }


    @PutMapping("/refresh/{userName}")
    @Override
    public ResponseEntity<?> refreshToken(@PathVariable("userName") String userName, @RequestHeader("Authorization") String refreshToken) throws Exception {
        if(parametersAreInvalid(userName, refreshToken)) {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }
        var token = authService.refreshToken(userName,refreshToken);
        if(token == null) {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

        }
        return token;

    }

    private boolean parametersAreInvalid(String userName, String refreshToken) {
        return  StringUtils.isBlank(userName) || StringUtils.isBlank(refreshToken);
    }

    private static boolean credentialsIsInvalid(AccountCredentialsDTO accountCredentialsDTO) {
        return accountCredentialsDTO == null
                || StringUtils.isBlank(accountCredentialsDTO.getPassword())
                || StringUtils.isBlank(accountCredentialsDTO.getUserName());
    }

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
    public AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentialsDTO) {
        return authService.create(credentialsDTO);
    }


}
