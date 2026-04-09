package br.com.willian.exception;

import org.springframework.http.HttpStatus; // Enum de status HTTP
import org.springframework.web.bind.annotation.ResponseStatus; // Define status HTTP da exceção

@ResponseStatus(HttpStatus.NOT_FOUND) // Retorna HTTP 404 quando a exceção é lançada
public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String message) {
        super(message); // Permite informar mensagem customizada
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause); // Permite encadear a exceção original
    }
}
