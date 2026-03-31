package br.com.willian.exception;

import org.springframework.http.HttpStatus; // Enum de status HTTP
import org.springframework.web.bind.annotation.ResponseStatus; // Define o status HTTP da resposta

@ResponseStatus(HttpStatus.NOT_FOUND) // Retorna HTTP 404 quando o recurso não é encontrado
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message); // Mensagem customizada informando o recurso inexistente
    }
}
