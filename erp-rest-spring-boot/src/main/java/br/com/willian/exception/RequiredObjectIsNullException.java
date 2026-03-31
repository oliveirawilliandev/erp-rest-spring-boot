package br.com.willian.exception;

import org.springframework.http.HttpStatus; // Enum de status HTTP
import org.springframework.web.bind.annotation.ResponseStatus; // Define o status HTTP da resposta

@ResponseStatus(HttpStatus.BAD_REQUEST) // Retorna HTTP 400 quando a exceção é lançada
public class RequiredObjectIsNullException extends RuntimeException {

    public RequiredObjectIsNullException(String message) {
        super(message); // Permite informar mensagem customizada de erro
    }

    public RequiredObjectIsNullException() {
        super("It is not allowed to persist a null object"); // Mensagem padrão para objeto nulo
    }
}
