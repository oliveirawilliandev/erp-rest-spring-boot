package br.com.willian.exception;

import org.springframework.http.HttpStatus; // Enum de status HTTP
import org.springframework.web.bind.annotation.ResponseStatus; // Define status HTTP padrão da exceção

@ResponseStatus(HttpStatus.BAD_REQUEST) // Retorna HTTP 400 quando a exceção é lançada
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message); // Permite informar mensagem customizada de erro
    }

    public BadRequestException() {
        super("Unsupported file extension! "); // Mensagem padrão quando nenhuma é informada
    }
}
