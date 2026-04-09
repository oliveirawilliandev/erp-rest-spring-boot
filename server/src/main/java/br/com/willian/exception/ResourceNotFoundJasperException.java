package br.com.willian.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Retorna HTTP 404 quando o recurso não é encontrado
public class ResourceNotFoundJasperException extends RuntimeException {

    public ResourceNotFoundJasperException(String message) {
        super(message); // Mensagem customizada informando o recurso inexistente
    }
}
