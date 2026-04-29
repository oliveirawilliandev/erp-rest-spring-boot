package br.com.willian.controller.docs; // Pacote da camada de documentação dos controladores

import br.com.willian.dto.v1.OrderDTO; // DTO de pedido
import br.com.willian.model.enums.OrderStatus; // Enum de status do pedido
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

// [DOCS-ORD-001] Tag de documentação para endpoints de pedidos
@Tag(name = "Order", description = "Endpoints for managing Orders")
public interface OrderControllerDocs {

    // [ORD-DOCS-001] Documentação do endpoint de listagem paginada
    @Operation(
            summary = "Find all orders", // Resumo da operação
            description = "Returns a paginated list of orders with sorting support", // Descrição detalhada
            tags = {"Order"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200", // Sucesso
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"), // Sem conteúdo
                    @ApiResponse(responseCode = "400", description = "Bad Request"), // Requisição inválida
                    @ApiResponse(responseCode = "401", description = "Unauthorized"), // Não autorizado
                    @ApiResponse(responseCode = "404", description = "Not Found"), // Não encontrado
                    @ApiResponse(responseCode = "500", description = "Internal Server Error") // Erro interno
            }
    )
    ResponseEntity<PagedModel<EntityModel<OrderDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página (padrão 0)
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página (padrão 12)
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação (asc/desc)
    );

    // [ORD-DOCS-002] Documentação do endpoint de busca por ID
    @Operation(
            summary = "Find order by ID", // Resumo da operação
            description = "Returns a specific order by its identifier", // Descrição detalhada
            tags = {"Order"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    OrderDTO findById(@PathVariable("id") Long id); // ID do pedido

    // [ORD-DOCS-003] Documentação do endpoint de busca por ID do cliente
    @Operation(
            summary = "Find orders by customer ID", // Resumo da operação
            description = "Returns a paginated list of orders filtered by customer ID", // Descrição detalhada
            tags = {"Order"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<OrderDTO>>> findByCustomerId(
            @PathVariable("customerId") Long customerId, // ID do cliente
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [ORD-DOCS-004] Documentação do endpoint de busca por ID do funcionário
    @Operation(
            summary = "Find orders by employee ID", // Resumo da operação
            description = "Returns a paginated list of orders filtered by employee ID", // Descrição detalhada
            tags = {"Order"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<OrderDTO>>> findByEmployeeId(
            @PathVariable("employeeId") Long employeeId, // ID do funcionário
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [ORD-DOCS-005] Documentação do endpoint de busca por status
    @Operation(
            summary = "Find orders by status", // Resumo da operação
            description = "Returns a paginated list of orders filtered by status", // Descrição detalhada
            tags = {"Order"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<OrderDTO>>> findByStatus(
            @PathVariable("status") OrderStatus status, // Status do pedido
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [ORD-DOCS-06] Documentação do endpoint de criação
    @Operation(
            summary = "Create order", // Resumo da operação
            description = "Creates a new order", // Descrição detalhada
            tags = {"Order"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "201",
                            description = "Created",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<OrderDTO> create(@RequestBody OrderDTO orderDTO); // DTO do pedido

    // [ORD-DOCS-07] Documentação do endpoint de atualização de status
    @Operation(
            summary = "Update order status", // Resumo da operação
            description = "Updates the status of an existing order", // Descrição detalhada
            tags = {"Order"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    OrderDTO updateStatus(
            @PathVariable("id") Long id, // ID do pedido
            @RequestParam("status") OrderStatus status // Novo status
    );

    // [ORD-DOCS-08] Documentação do endpoint de cancelamento
    @Operation(
            summary = "Cancel order", // Resumo da operação
            description = "Cancels an existing order and restores stock", // Descrição detalhada
            tags = {"Order"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    OrderDTO cancel(@PathVariable("id") Long id); // ID do pedido

    // [ORD-DOCS-09] Documentação do endpoint de remoção
    @Operation(
            summary = "Delete order", // Resumo da operação
            description = "Deletes an order by ID", // Descrição detalhada
            tags = {"Order"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<?> delete(@PathVariable("id") Long id); // ID do pedido
}