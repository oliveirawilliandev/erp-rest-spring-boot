package br.com.willian.controller.docs; // Pacote da camada de documentação dos controladores

import br.com.willian.dto.v1.SupplierDTO; // DTO de fornecedor
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

// [DOCS-SUPP-001] Tag de documentação para endpoints de fornecedores
@Tag(name = "Supplier", description = "Endpoints for managing Suppliers")
public interface SupplierControllerDocs {

    // [SUPP-DOCS-001] Documentação do endpoint de listagem paginada
    @Operation(
            summary = "Find all suppliers", // Resumo da operação
            description = "Returns a paginated list of suppliers with sorting support", // Descrição detalhada
            tags = {"Supplier"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200", // Sucesso
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = SupplierDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"), // Sem conteúdo
                    @ApiResponse(responseCode = "400", description = "Bad Request"), // Requisição inválida
                    @ApiResponse(responseCode = "401", description = "Unauthorized"), // Não autorizado
                    @ApiResponse(responseCode = "404", description = "Not Found"), // Não encontrado
                    @ApiResponse(responseCode = "500", description = "Internal Server Error") // Erro interno
            }
    )
    ResponseEntity<PagedModel<EntityModel<SupplierDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página (padrão 0)
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página (padrão 12)
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação (asc/desc)
    );

    // [SUPP-DOCS-002] Documentação do endpoint de busca por nome
    @Operation(
            summary = "Find suppliers by name", // Resumo da operação
            description = "Returns a paginated list of suppliers filtered by name", // Descrição detalhada
            tags = {"Supplier"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = SupplierDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<SupplierDTO>>> findByName(
            @PathVariable("name") String name, // Nome do fornecedor
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [SUPP-DOCS-003] Documentação do endpoint de busca por ID
    @Operation(
            summary = "Find supplier by ID", // Resumo da operação
            description = "Returns a specific supplier by its identifier", // Descrição detalhada
            tags = {"Supplier"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    SupplierDTO findById(@PathVariable("id") Long id); // ID do fornecedor

    // [SUPP-DOCS-004] Documentação do endpoint de busca por documento
    @Operation(
            summary = "Find supplier by document", // Resumo da operação
            description = "Returns a specific supplier by its document number", // Descrição detalhada
            tags = {"Supplier"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    SupplierDTO findByDocument(@PathVariable("document") String document); // Documento do fornecedor

    // [SUPP-DOCS-005] Documentação do endpoint de criação
    @Operation(
            summary = "Create supplier", // Resumo da operação
            description = "Creates a new supplier", // Descrição detalhada
            tags = {"Supplier"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "201",
                            description = "Created",
                            content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<SupplierDTO> create(@RequestBody SupplierDTO supplierDTO); // DTO do fornecedor

    // [SUPP-DOCS-06] Documentação do endpoint de remoção
    @Operation(
            summary = "Delete supplier", // Resumo da operação
            description = "Deletes a supplier by ID", // Descrição detalhada
            tags = {"Supplier"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<?> delete(@PathVariable("id") Long id); // ID do fornecedor

    // [SUPP-DOCS-07] Documentação do endpoint de atualização
    @Operation(
            summary = "Update supplier", // Resumo da operação
            description = "Updates an existing supplier", // Descrição detalhada
            tags = {"Supplier"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    SupplierDTO update(@RequestBody SupplierDTO supplierDTO); // DTO do fornecedor com dados atualizados

    // [SUPP-DOCS-08] Documentação do endpoint de ativação
    @Operation(
            summary = "Activate supplier", // Resumo da operação
            description = "Activates a supplier by ID", // Descrição detalhada
            tags = {"Supplier"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    SupplierDTO activateSupplier(@PathVariable("id") Long id); // ID do fornecedor

    // [SUPP-DOCS-09] Documentação do endpoint de desativação
    @Operation(
            summary = "Deactivate supplier", // Resumo da operação
            description = "Deactivates a supplier by ID", // Descrição detalhada
            tags = {"Supplier"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    SupplierDTO deactivateSupplier(@PathVariable("id") Long id); // ID do fornecedor
}