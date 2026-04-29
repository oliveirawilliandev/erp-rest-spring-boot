package br.com.willian.controller.docs; // Pacote da camada de documentação dos controladores

import br.com.willian.dto.v1.ProductDTO; // DTO de produto
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

// [DOCS-PROD-001] Tag de documentação para endpoints de produtos
@Tag(name = "Product", description = "Endpoints for managing Products")
public interface ProductControllerDocs {

    // [PROD-DOCS-001] Documentação do endpoint de listagem paginada
    @Operation(
            summary = "Find all products", // Resumo da operação
            description = "Returns a paginated list of products with sorting support", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200", // Sucesso
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"), // Sem conteúdo
                    @ApiResponse(responseCode = "400", description = "Bad Request"), // Requisição inválida
                    @ApiResponse(responseCode = "401", description = "Unauthorized"), // Não autorizado
                    @ApiResponse(responseCode = "404", description = "Not Found"), // Não encontrado
                    @ApiResponse(responseCode = "500", description = "Internal Server Error") // Erro interno
            }
    )
    ResponseEntity<PagedModel<EntityModel<ProductDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página (padrão 0)
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página (padrão 12)
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação (asc/desc)
    );

    // [PROD-DOCS-002] Documentação do endpoint de busca por nome
    @Operation(
            summary = "Find products by name", // Resumo da operação
            description = "Returns a paginated list of products filtered by name", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<ProductDTO>>> findByName(
            @PathVariable("name") String name, // Nome do produto
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [PROD-DOCS-003] Documentação do endpoint de busca por ID
    @Operation(
            summary = "Find product by ID", // Resumo da operação
            description = "Returns a specific product by its identifier", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = ProductDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ProductDTO findById(@PathVariable("id") Long id); // ID do produto

    // [PROD-DOCS-004] Documentação do endpoint de criação
    @Operation(
            summary = "Create product", // Resumo da operação
            description = "Creates a new product", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "201",
                            description = "Created",
                            content = @Content(schema = @Schema(implementation = ProductDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<ProductDTO> create(@RequestBody ProductDTO productDTO); // DTO do produto

    // [PROD-DOCS-005] Documentação do endpoint de remoção
    @Operation(
            summary = "Delete product", // Resumo da operação
            description = "Deletes a product by ID", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<?> delete(@PathVariable("id") Long id); // ID do produto

    // [PROD-DOCS-06] Documentação do endpoint de atualização
    @Operation(
            summary = "Update product", // Resumo da operação
            description = "Updates an existing product", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = ProductDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ProductDTO update(@RequestBody ProductDTO productDTO); // DTO do produto com dados atualizados

    // [PROD-DOCS-07] Documentação do endpoint de atualização de estoque
    @Operation(
            summary = "Update product stock", // Resumo da operação
            description = "Updates the stock quantity of a product", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = ProductDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ProductDTO updateStock(
            @PathVariable("id") Long id, // ID do produto
            @RequestParam("quantity") Integer quantity // Quantidade a adicionar (positivo) ou remover (negativo)
    );

    // [PROD-DOCS-08] Documentação do endpoint de ativação
    @Operation(
            summary = "Activate product", // Resumo da operação
            description = "Activates a product by ID", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = ProductDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ProductDTO activateProduct(@PathVariable("id") Long id); // ID do produto

    // [PROD-DOCS-09] Documentação do endpoint de desativação
    @Operation(
            summary = "Deactivate product", // Resumo da operação
            description = "Deactivates a product by ID", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = ProductDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ProductDTO deactivateProduct(@PathVariable("id") Long id); // ID do produto

    // [PROD-DOCS-010] Documentação do endpoint de busca por status ativo/inativo
    @Operation(
            summary = "Find products by active status", // Resumo da operação
            description = "Returns a paginated list of products filtered by active status", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<ProductDTO>>> findByActive(
            @PathVariable("active") Boolean active, // Status ativo/inativo
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );

    // [PROD-DOCS-011] Documentação do endpoint de busca por estoque baixo
    @Operation(
            summary = "Find products with low stock", // Resumo da operação
            description = "Returns a paginated list of products with stock below the specified threshold", // Descrição detalhada
            tags = {"Product"}, // Tag de agrupamento
            responses = { // Possíveis respostas da API
                    @ApiResponse(responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                            )),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<PagedModel<EntityModel<ProductDTO>>> findLowStock(
            @PathVariable("threshold") Integer threshold, // Limite mínimo de estoque (ex: 10)
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    );
}