package br.com.willian.exception;

import org.springframework.http.HttpStatus; // Enum de status HTTP
import org.springframework.web.bind.annotation.ResponseStatus; // Define status HTTP padrão da exceção

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Retorna HTTP 500 quando a exceção é lançada
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message); // Permite informar mensagem customizada de erro
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause); // Permite encadear a exceção original
    }
}
