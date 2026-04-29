package br.com.willian.controller; // Pacote da camada de controle/API

import br.com.willian.controller.docs.ProductControllerDocs; // Interface de documentação Swagger/OpenAPI
import br.com.willian.dto.v1.ProductDTO; // DTO de produto
import br.com.willian.service.ProductService; // Serviço com a lógica de negócio
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.data.domain.PageRequest; // Implementação de Pageable para paginação
import org.springframework.data.domain.Pageable; // Interface para paginação
import org.springframework.data.domain.Sort; // Configuração de ordenação
import org.springframework.hateoas.EntityModel; // Wrapper HATEOAS para entidades
import org.springframework.hateoas.PagedModel; // Modelo HATEOAS para páginas
import org.springframework.http.HttpStatus; // Códigos de status HTTP
import org.springframework.http.MediaType; // Constantes para tipos de mídia
import org.springframework.http.ResponseEntity; // Entidade de resposta HTTP
import org.springframework.web.bind.annotation.*; // Anotações Spring para REST controllers

// Define esta classe como um controlador REST com URL base "/api/product/v1"
@RestController
// Mapeia as requisições para esta URL base
@RequestMapping("/api/product/v1")
// Implementa a interface de documentação para garantir consistência
public class ProductController implements ProductControllerDocs {

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    // Injeta automaticamente a dependência do serviço de produtos
    @Autowired
    private ProductService productService;

    //[CTRL-TRACE: PROD-CTRL-001]: Endpoint responsável por retornar uma lista paginada de recursos
    @GetMapping( // Mapeia requisições GET para este método
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    // Permite ordenação e paginação via parâmetros de requisição
    public ResponseEntity<PagedModel<EntityModel<ProductDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Parâmetro de página, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação, padrão ascendente
    ) {

        // Registra log da solicitação de listagem com parâmetros
        logger.info("[PROD-CTRL-001] Listagem solicitada | page={} | size={} | direction={}", page, size, direction);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por nome
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "name")
        );
        logger.debug("[PROD-CTRL-001] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar todos os produtos paginados
        var response = productService.findAll(pageable);

        logger.debug("[PROD-CTRL-001] Listagem retornada com sucesso | page={} | size={} | direction={}", page, size, direction);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: PROD-CTRL-002]: Endpoint responsável por retornar lista paginada filtrada por nome
    @GetMapping( // Mapeia requisições GET para esta URL específica
            value = "/findByName/{name}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<ProductDTO>>> findByName(
            @PathVariable("name") String name, // Parâmetro do nome vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[PROD-CTRL-002] Busca por nome solicitada | name={} | page={} | size={}", name, page, size);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por nome
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "name")
        );
        logger.debug("[PROD-CTRL-002] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar produtos pelo nome
        var response = productService.findByName(name, pageable);

        logger.debug("[PROD-CTRL-002] Busca por nome retornada com sucesso | name={} | page={} | size={}", name, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: PROD-CTRL-003]: Endpoint responsável por retornar um recurso específico
    @GetMapping( // Mapeia requisições GET para URL com ID
            value = "/{id}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ProductDTO findById(@PathVariable("id") Long id) { // ID do produto vindo da URL

        logger.info("[PROD-CTRL-003] Busca por ID solicitada | id={}", id);

        // Chama o serviço para buscar produto pelo ID
        var product = productService.findById(id);
        logger.debug("[PROD-CTRL-003] Serviço retornou produto | id={} | name={}", product.getId(), product.getName());

        // Retorna o produto encontrado
        return product;
    }

    //[CTRL-TRACE: PROD-CTRL-004]: Endpoint responsável por criar um novo recurso
    @PostMapping( // Mapeia requisições POST para criar novo recurso
            consumes = { // Define os formatos aceitos no corpo da requisição
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO productDTO) { // Corpo da requisição convertido para DTO

        logger.info("[PROD-CTRL-004] Criação solicitada | name={} | price={}", productDTO.getName(), productDTO.getPrice());

        // Chama o serviço para criar o novo produto
        var created = productService.create(productDTO);

        logger.debug("[PROD-CTRL-004] Criação realizada com sucesso | id={} | name={} | price={}",
                created.getId(), created.getName(), created.getPrice());

        // Retorna o produto criado com status 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //[CTRL-TRACE: PROD-CTRL-005]: Endpoint responsável por remover um recurso
    @DeleteMapping(value = "/{id}") // Mapeia requisições DELETE para URL com ID
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) { // ID do produto a ser removido

        logger.info("[PROD-CTRL-005] Remoção solicitada | id={}", id);

        // Chama o serviço para remover o produto
        productService.delete(id);

        logger.debug("[PROD-CTRL-005] Remoção realizada com sucesso | id={}", id);

        // Retorna resposta HTTP 204 (Sem conteúdo)
        return ResponseEntity.noContent().build();
    }

    //[CTRL-TRACE: PROD-CTRL-006]: Endpoint responsável por atualizar um recurso existente
    @PutMapping( // Mapeia requisições PUT para atualização
            consumes = { // Define os formatos aceitos no corpo da requisição
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ProductDTO update(@RequestBody ProductDTO productDTO) { // DTO com dados atualizados

        logger.info("[PROD-CTRL-006] Atualização solicitada | id={} | name={} | price={}",
                productDTO.getId(), productDTO.getName(), productDTO.getPrice());

        // Chama o serviço para atualizar o produto
        var updated = productService.update(productDTO);

        logger.debug("[PROD-CTRL-006] Atualização realizada com sucesso | id={} | name={} | price={}",
                updated.getId(), updated.getName(), updated.getPrice());

        // Retorna o produto atualizado
        return updated;
    }

    //[CTRL-TRACE: PROD-CTRL-007]: Endpoint responsável por atualizar o estoque de um produto
    @PatchMapping( // Mapeia requisições PATCH para atualização de estoque
            value = "/updateStock/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ProductDTO updateStock(
            @PathVariable("id") Long id, // ID do produto vindo da URL
            @RequestParam("quantity") Integer quantity) { // Quantidade a adicionar/remover do estoque

        logger.info("[PROD-CTRL-007] Atualização de estoque solicitada | id={} | quantity={}", id, quantity);

        // Chama o serviço para atualizar o estoque do produto
        var updated = productService.updateStock(id, quantity);

        logger.debug("[PROD-CTRL-007] Estoque atualizado com sucesso | id={} | stockQuantity={}",
                updated.getId(), updated.getStockQuantity());

        // Retorna o produto com estoque atualizado
        return updated;
    }

    //[CTRL-TRACE: PROD-CTRL-008]: Endpoint responsável por ativar um produto
    @PatchMapping( // Mapeia requisições PATCH para ativação
            value = "/activate/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ProductDTO activateProduct(@PathVariable("id") Long id) { // ID do produto a ativar

        logger.info("[PROD-CTRL-008] Ativação solicitada | id={}", id);

        // Chama o serviço para ativar o produto
        var activated = productService.activateProduct(id);

        logger.debug("[PROD-CTRL-008] Ativação realizada com sucesso | id={} | active={}",
                activated.getId(), activated.getActive());

        // Retorna o produto ativado
        return activated;
    }

    //[CTRL-TRACE: PROD-CTRL-009]: Endpoint responsável por desativar um produto
    @PatchMapping( // Mapeia requisições PATCH para desativação
            value = "/deactivate/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ProductDTO deactivateProduct(@PathVariable("id") Long id) { // ID do produto a desativar

        logger.info("[PROD-CTRL-009] Desativação solicitada | id={}", id);

        // Chama o serviço para desativar o produto
        var deactivated = productService.deactivateProduct(id);

        logger.debug("[PROD-CTRL-009] Desativação realizada com sucesso | id={} | active={}",
                deactivated.getId(), deactivated.getActive());

        // Retorna o produto desativado
        return deactivated;
    }

    //[CTRL-TRACE: PROD-CTRL-010]: Endpoint responsável por retornar produtos filtrados por status ativo/inativo
    @GetMapping( // Mapeia requisições GET para URL com active status
            value = "/findByActive/{active}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<ProductDTO>>> findByActive(
            @PathVariable("active") Boolean active, // Status ativo/inativo vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[PROD-CTRL-010] Busca por status ativo solicitada | active={} | page={} | size={}", active, page, size);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por nome
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "name")
        );
        logger.debug("[PROD-CTRL-010] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar produtos por status ativo/inativo
        var response = productService.findByActive(active, pageable);

        logger.debug("[PROD-CTRL-010] Busca por status ativo retornada com sucesso | active={} | page={} | size={}", active, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: PROD-CTRL-011]: Endpoint responsável por retornar produtos com estoque abaixo do limite
    @GetMapping( // Mapeia requisições GET para URL com threshold
            value = "/findLowStock/{threshold}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<ProductDTO>>> findLowStock(
            @PathVariable("threshold") Integer threshold, // Limite mínimo de estoque
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[PROD-CTRL-011] Busca por estoque baixo solicitada | threshold={} | page={} | size={}", threshold, page, size);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por quantidade em estoque
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "stockQuantity")
        );
        logger.debug("[PROD-CTRL-011] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar produtos com estoque abaixo do limite
        var response = productService.findLowStock(threshold, pageable);

        logger.debug("[PROD-CTRL-011] Busca por estoque baixo retornada com sucesso | threshold={} | page={} | size={}", threshold, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }
}