package br.com.willian.exception.handler;

import br.com.willian.exception.*; // Exceções customizadas da aplicação
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus; // Enum de status HTTP
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity; // Wrapper da resposta HTTP
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice; // Intercepta exceções globalmente
import org.springframework.web.bind.annotation.ExceptionHandler; // Mapeia exceções específicas
import org.springframework.web.bind.annotation.RestController; // Permite retorno JSON
import org.springframework.web.context.request.WebRequest; // Detalhes da requisição
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler; // Handler base do Spring

import java.util.Date; // Data/hora da exceção

@ControllerAdvice // Aplica tratamento global de exceções nos controllers
@RestController // Retorna respostas em formato JSON
public class CustomEntityReponseHandler extends ResponseEntityExceptionHandler {

    // ======================================================
    // TRATAMENTO GLOBAL (EXCEÇÕES NÃO TRATADAS)
    // ======================================================


    // [EXC-HANDLER-001] Trata erros de JSON mal formatado ou valores inválidos
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, // Exceção de mensagem não legível
            HttpHeaders headers, // Cabeçalhos HTTP
            HttpStatusCode status, // Status HTTP original
            WebRequest request) { // Requisição web

        // Mensagem padrão para erros de JSON
        String message = "JSON inválido";

        // Obtém a causa raiz da exceção
        Throwable cause = ex.getCause();

        // Verifica se a causa é erro de formato inválido (ex: string em campo numérico)
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {

            Object value = ife.getValue(); // Valor inválido que foi fornecido

            // Se o erro for em um campo do tipo Enum (como Role)
            if (ife.getTargetType().isEnum()) {
                // Mensagem específica para enum inválido
                message = "Enum inválido: " + value; // Ex: "Role inválida: ADMIN_ERROR"
            }
        }

        // Cria resposta de erro padronizada
        ExceptionResponse response = new ExceptionResponse(
                new Date(), // Timestamp do erro
                message, // Mensagem descritiva
                request.getDescription(false) // Descrição da requisição
        );

        // Retorna resposta com status 400 (BAD REQUEST)
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class) // Captura qualquer exceção não tratada
    public final ResponseEntity<ExceptionResponse> handleALLExceptions(
            Exception ex,                          // Exceção lançada
            WebRequest request                     // Dados da requisição
    ) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),                        // Timestamp do erro
                ex.getMessage(),                   // Mensagem da exceção
                request.getDescription(false)      // Descrição da requisição
        );

        return new ResponseEntity<>(
                response,                          // Corpo da resposta
                HttpStatus.INTERNAL_SERVER_ERROR   // HTTP 500
        );
    }

    // ======================================================
    // EXCEÇÕES TRATADAS ESPECIFICAMENTE
    // ======================================================

    @ExceptionHandler(ResourceNotFoundException.class) // Recurso não encontrado
    public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(
            Exception ex,                          // Exceção lançada
            WebRequest request                     // Dados da requisição
    ) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),                        // Timestamp
                ex.getMessage(),                   // Mensagem da exceção
                request.getDescription(false)      // URI requisitada
        );

        return new ResponseEntity<>(
                response,                          // Corpo da resposta
                HttpStatus.NOT_FOUND               // HTTP 404
        );
    }

    @ExceptionHandler(ResourceNotFoundJasperException.class) // Recurso não encontrado
    public final ResponseEntity<ExceptionResponse> handleNotFoundJasperExceptions(
            Exception ex,                          // Exceção lançada
            WebRequest request                     // Dados da requisição
    ) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),                        // Timestamp
                ex.getMessage(),                   // Mensagem da exceção
                request.getDescription(false)      // URI requisitada
        );

        return  ResponseEntity.status(HttpStatus.NOT_FOUND) // HTTP 404
                .header("Content-Type", "application/json") //Cliente aceita/Suporta Json
                .body(response); // Corpo da resposta
    }

    @ExceptionHandler(RequiredObjectIsNullException.class) // Objeto obrigatório nulo
    public final ResponseEntity<ExceptionResponse> handleRequiredObjectExceptions(
            Exception ex,                          // Exceção lançada
            WebRequest request                     // Dados da requisição
    ) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),                        // Timestamp
                ex.getMessage(),                   // Mensagem do erro
                request.getDescription(false)      // Detalhes da requisição
        );

        return new ResponseEntity<>(
                response,                          // Corpo da resposta
                HttpStatus.BAD_REQUEST             // HTTP 400
        );
    }

    @ExceptionHandler(InvalidGenderException.class) // Objeto obrigatório nulo
    public final ResponseEntity<ExceptionResponse> handleInvalidGenderExceptions(
            Exception ex,                          // Exceção lançada
            WebRequest request                     // Dados da requisição
    ) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),                        // Timestamp
                ex.getMessage(),                   // Mensagem do erro
                request.getDescription(false)      // Detalhes da requisição
        );

        return new ResponseEntity<>(
                response,                          // Corpo da resposta
                HttpStatus.BAD_REQUEST             // HTTP 400
        );
    }

    @ExceptionHandler(BadRequestException.class) // Erro de requisição inválida
    public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(
            Exception ex,                          // Exceção lançada
            WebRequest request                     // Dados da requisição
    ) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),                        // Timestamp
                ex.getMessage(),                   // Mensagem da exceção
                request.getDescription(false)      // Detalhes da requisição
        );

        return new ResponseEntity<>(
                response,                          // Corpo da resposta
                HttpStatus.BAD_REQUEST             // HTTP 400
        );
    }

    @ExceptionHandler(FileNotFoundException.class) // Arquivo não encontrado
    public final ResponseEntity<ExceptionResponse> handleFileNotFoundExceptions(
            Exception ex,                          // Exceção lançada
            WebRequest request                     // Dados da requisição
    ) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),                        // Timestamp
                ex.getMessage(),                   // Mensagem do erro
                request.getDescription(false)      // Detalhes da requisição
        );

        return new ResponseEntity<>(
                response,                          // Corpo da resposta
                HttpStatus.NOT_FOUND               // HTTP 404
        );
    }

    @ExceptionHandler(FileStorageException.class) // Erro ao salvar arquivo
    public final ResponseEntity<ExceptionResponse> handleFileStorageExceptions(
            Exception ex,                          // Exceção lançada
            WebRequest request                     // Dados da requisição
    ) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),                        // Timestamp
                ex.getMessage(),                   // Mensagem do erro
                request.getDescription(false)      // Detalhes da requisição
        );

        return new ResponseEntity<>(
                response,                          // Corpo da resposta
                HttpStatus.INTERNAL_SERVER_ERROR   // HTTP 500
        );
    }
    @ExceptionHandler(InvalidJwtAuthenticationException.class) // Token JWT inválido
    public final ResponseEntity<ExceptionResponse> handleInvalidJwtAuthenticationExceptions(
            Exception ex,                          // Exceção lançada
            WebRequest request                     // Dados da requisição
    ) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),                        // Timestamp
                ex.getMessage(),                   // Mensagem da exceção
                request.getDescription(false)      // Detalhes da requisição
        );

        return new ResponseEntity<>(
                response,                          // Corpo da resposta
                HttpStatus.FORBIDDEN               // HTTP 403
        );
    }

}
