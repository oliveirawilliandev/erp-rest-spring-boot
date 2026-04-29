package br.com.willian.controller.docs; // Pacote da camada de documentação dos controladores

import br.com.willian.dto.v1.PurchaseDTO; // DTO de compra
import br.com.willian.model.enums.PurchaseStatus; // Enum de status da compra
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

// [DOCS-PUR-001] Tag de documentação para endpoints de compras
@Tag(name = "Purchase", description = "Endpoints for managing Purchases")
public interface PurchaseControllerDocs {

    // [PUR-DOCS-001] Documentação do endpoint de listagem paginada
    @Operation(
            summary = "Find all purchases", // Resumo da operação
            description = "Returns a paginated list of purchases with sorting support", // Descrição detalhada
            tags = {"Purchase"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200", // Sucesso
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PurchaseDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"), // Sem conteúdo
                    @ApiResponse(responseCode = "400", description = "Bad Request"), // Requisição inválida
                    @ApiResponse(responseCode = "401", description = "Unauthorized"), // Não autorizado
                    @ApiResponse(responseCode = "404", description = "Not Found"), // Não encontrado
                    @ApiResponse(responseCode = "500", description = "Internal Server Error") // Erro interno
            }
    )
    ResponseEntity<PagedModel<EntityModel<PurchaseDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página (padrão 0)
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página (padrão 12)
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação (asc/desc)
    );

    // [PUR-DOCS-002] Documentação do endpoint de busca por ID
    @Operation(
            summary = "Find purchase by ID", // Resumo da operação
            description = "Returns a specific purchase by its identifier", // Descrição detalhada
            tags = {"Purchase"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = PurchaseDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    PurchaseDTO findById(@PathVariable("id") Long id); // ID da compra

    // [PUR-DOCS-003] Documentação do endpoint de busca por ID do fornecedor
    @Operation(
            summary = "Find purchases by supplier ID", // Resumo da operação
            description = "Returns a paginated list of purchases filtered by supplier ID", // Descrição detalhada
            tags = {"Purchase"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PurchaseDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<PurchaseDTO>>> findBySupplierId(
            @PathVariable("supplierId") Long supplierId, // ID do fornecedor
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [PUR-DOCS-004] Documentação do endpoint de busca por ID do funcionário
    @Operation(
            summary = "Find purchases by employee ID", // Resumo da operação
            description = "Returns a paginated list of purchases filtered by employee ID", // Descrição detalhada
            tags = {"Purchase"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PurchaseDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<PurchaseDTO>>> findByEmployeeId(
            @PathVariable("employeeId") Long employeeId, // ID do funcionário
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [PUR-DOCS-005] Documentação do endpoint de busca por status
    @Operation(
            summary = "Find purchases by status", // Resumo da operação
            description = "Returns a paginated list of purchases filtered by status", // Descrição detalhada
            tags = {"Purchase"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PurchaseDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<PurchaseDTO>>> findByStatus(
            @PathVariable("status") PurchaseStatus status, // Status da compra
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [PUR-DOCS-06] Documentação do endpoint de criação
    @Operation(
            summary = "Create purchase", // Resumo da operação
            description = "Creates a new purchase", // Descrição detalhada
            tags = {"Purchase"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "201",
                            description = "Created",
                            content = @Content(schema = @Schema(implementation = PurchaseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PurchaseDTO> create(@RequestBody PurchaseDTO purchaseDTO); // DTO da compra

    // [PUR-DOCS-07] Documentação do endpoint de atualização de status
    @Operation(
            summary = "Update purchase status", // Resumo da operação
            description = "Updates the status of an existing purchase", // Descrição detalhada
            tags = {"Purchase"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = PurchaseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    PurchaseDTO updateStatus(
            @PathVariable("id") Long id, // ID da compra
            @RequestParam("status") PurchaseStatus status // Novo status
    );

    // [PUR-DOCS-08] Documentação do endpoint de remoção
    @Operation(
            summary = "Delete purchase", // Resumo da operação
            description = "Deletes a purchase by ID", // Descrição detalhada
            tags = {"Purchase"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<?> delete(@PathVariable("id") Long id); // ID da compra
}