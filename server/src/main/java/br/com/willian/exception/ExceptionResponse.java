package br.com.willian.exception;

import java.util.Date; // Representa data/hora da ocorrência do erro

public record ExceptionResponse(

        Date timestamp,  // Data e hora em que a exceção ocorreu
        String message,  // Mensagem descritiva do erro
        String details   // Detalhes adicionais (ex: URI da requisição)

) {
    // Record usado como resposta padrão para erros da API
}
