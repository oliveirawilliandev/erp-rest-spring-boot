package br.com.willian.controller.docs; // Pacote da camada de documentação dos controladores

import br.com.willian.dto.v1.CustomerDTO; // DTO de cliente
import io.swagger.v3.oas.annotations.Operation; // Anotação para documentar operações
import io.swagger.v3.oas.annotations.media.ArraySchema; // Anotação para schemas de array
import io.swagger.v3.oas.annotations.media.Content; // Anotação para conteúdo de resposta
import io.swagger.v3.oas.annotations.media.Schema; // Anotação para schema de dados
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Anotação para respostas da API
import io.swagger.v3.oas.annotations.tags.Tag; // Anotação para tags de agrupamento
import org.springframework.hateoas.EntityModel; // Wrapper HATEOAS para entidades
import org.springframework.hateoas.PagedModel; // Modelo HATEOAS para páginas
import org.springframework.http.MediaType; // Constantes para tipos de mídia
import org.springframework.http.ResponseEntity; // Entidade de resposta HTTP
import org.springframework.web.bind.annotation.PathVariable; // Parâmetro da URL
import org.springframework.web.bind.annotation.RequestBody; // Corpo da requisição
import org.springframework.web.bind.annotation.RequestParam; // Parâmetro de consulta

// [DOCS-CUST-001] Tag de documentação para endpoints de clientes
@Tag(name = "Customer", description = "Endpoints for managing Customers")
public interface CustomerControllerDocs {

    // [CUST-DOCS-001] Documentação do endpoint de listagem paginada
    @Operation(
            summary = "Find all customers", // Resumo da operação
            description = "Returns a paginated list of customers with sorting support", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200", // Sucesso
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CustomerDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"), // Sem conteúdo
                    @ApiResponse(responseCode = "400", description = "Bad Request"), // Requisição inválida
                    @ApiResponse(responseCode = "401", description = "Unauthorized"), // Não autorizado
                    @ApiResponse(responseCode = "404", description = "Not Found"), // Não encontrado
                    @ApiResponse(responseCode = "500", description = "Internal Server Error") // Erro interno
            }
    )
    ResponseEntity<PagedModel<EntityModel<CustomerDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página (padrão 0)
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página (padrão 12)
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação (asc/desc)
    );

    // [CUST-DOCS-002] Documentação do endpoint de busca por nome
    @Operation(
            summary = "Find customers by name", // Resumo da operação
            description = "Returns a paginated list of customers filtered by name", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CustomerDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<CustomerDTO>>> findByName(
            @PathVariable("name") String name, // Nome do cliente
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [CUST-DOCS-003] Documentação do endpoint de busca por ID
    @Operation(
            summary = "Find customer by ID", // Resumo da operação
            description = "Returns a specific customer by its identifier", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    CustomerDTO findById(@PathVariable("id") Long id); // ID do cliente

    // [CUST-DOCS-004] Documentação do endpoint de busca por email
    @Operation(
            summary = "Find customer by email", // Resumo da operação
            description = "Returns a specific customer by its email address", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    CustomerDTO findByEmail(@PathVariable("email") String email); // Email do cliente

    // [CUST-DOCS-005] Documentação do endpoint de busca por documento
    @Operation(
            summary = "Find customer by document", // Resumo da operação
            description = "Returns a specific customer by its document number", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    CustomerDTO findByDocument(@PathVariable("document") String document); // Documento do cliente

    // [CUST-DOCS-06] Documentação do endpoint de criação
    @Operation(
            summary = "Create customer", // Resumo da operação
            description = "Creates a new customer", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "201",
                            description = "Created",
                            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<CustomerDTO> create(@RequestBody CustomerDTO customerDTO); // DTO do cliente

    // [CUST-DOCS-07] Documentação do endpoint de remoção
    @Operation(
            summary = "Delete customer", // Resumo da operação
            description = "Deletes a customer by ID", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<?> delete(@PathVariable("id") Long id); // ID do cliente

    // [CUST-DOCS-08] Documentação do endpoint de atualização
    @Operation(
            summary = "Update customer", // Resumo da operação
            description = "Updates an existing customer", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    CustomerDTO update(@RequestBody CustomerDTO customerDTO); // DTO do cliente com dados atualizados

    // [CUST-DOCS-09] Documentação do endpoint de ativação
    @Operation(
            summary = "Activate customer", // Resumo da operação
            description = "Activates a customer by ID", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    CustomerDTO activateCustomer(@PathVariable("id") Long id); // ID do cliente

    // [CUST-DOCS-010] Documentação do endpoint de desativação
    @Operation(
            summary = "Deactivate customer", // Resumo da operação
            description = "Deactivates a customer by ID", // Descrição detalhada
            tags = {"Customer"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    CustomerDTO deactivateCustomer(@PathVariable("id") Long id); // ID do cliente
}