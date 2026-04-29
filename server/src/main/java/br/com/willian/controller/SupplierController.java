package br.com.willian.controller; // Pacote da camada de controle/API

import br.com.willian.controller.docs.SupplierControllerDocs; // Interface de documentação Swagger/OpenAPI
import br.com.willian.dto.v1.SupplierDTO; // DTO de fornecedor
import br.com.willian.service.SupplierService; // Serviço com a lógica de negócio
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

// Define esta classe como um controlador REST com URL base "/api/supplier/v1"
@RestController
// Mapeia as requisições para esta URL base
@RequestMapping("/api/supplier/v1")
// Implementa a interface de documentação para garantir consistência
public class SupplierController implements SupplierControllerDocs {

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

    // Injeta automaticamente a dependência do serviço de fornecedores
    @Autowired
    private SupplierService supplierService;

    //[CTRL-TRACE: SUPP-CTRL-001]: Endpoint responsável por retornar uma lista paginada de recursos
    @GetMapping( // Mapeia requisições GET para este método
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    // Permite ordenação e paginação via parâmetros de requisição
    public ResponseEntity<PagedModel<EntityModel<SupplierDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Parâmetro de página, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação, padrão ascendente
    ) {

        // Registra log da solicitação de listagem com parâmetros
        logger.info("[SUPP-CTRL-001] Listagem solicitada | page={} | size={} | direction={}", page, size, direction);

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
        logger.debug("[SUPP-CTRL-001] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar todos os fornecedores paginados
        var response = supplierService.findAll(pageable);

        logger.debug("[SUPP-CTRL-001] Listagem retornada com sucesso | page={} | size={} | direction={}", page, size, direction);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: SUPP-CTRL-002]: Endpoint responsável por retornar lista paginada filtrada por nome
    @GetMapping( // Mapeia requisições GET para esta URL específica
            value = "/findByName/{name}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<SupplierDTO>>> findByName(
            @PathVariable("name") String name, // Parâmetro do nome vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[SUPP-CTRL-002] Busca por nome solicitada | name={} | page={} | size={}", name, page, size);

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
        logger.debug("[SUPP-CTRL-002] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar fornecedores pelo nome
        var response = supplierService.findByName(name, pageable);

        logger.debug("[SUPP-CTRL-002] Busca por nome retornada com sucesso | name={} | page={} | size={}", name, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: SUPP-CTRL-003]: Endpoint responsável por retornar um recurso específico
    @GetMapping( // Mapeia requisições GET para URL com ID
            value = "/{id}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public SupplierDTO findById(@PathVariable("id") Long id) { // ID do fornecedor vindo da URL

        logger.info("[SUPP-CTRL-003] Busca por ID solicitada | id={}", id);

        // Chama o serviço para buscar fornecedor pelo ID
        var supplier = supplierService.findById(id);
        logger.debug("[SUPP-CTRL-003] Serviço retornou fornecedor | id={} | name={}", supplier.getId(), supplier.getName());

        // Retorna o fornecedor encontrado
        return supplier;
    }

    //[CTRL-TRACE: SUPP-CTRL-004]: Endpoint responsável por retornar fornecedor por documento
    @GetMapping( // Mapeia requisições GET para URL com documento
            value = "/findByDocument/{document}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public SupplierDTO findByDocument(@PathVariable("document") String document) { // Documento do fornecedor vindo da URL

        logger.info("[SUPP-CTRL-004] Busca por documento solicitada | document={}", document);

        // Chama o serviço para buscar fornecedor pelo documento
        var supplier = supplierService.findByDocument(document);
        logger.debug("[SUPP-CTRL-004] Serviço retornou fornecedor | document={}", supplier.getDocument());

        // Retorna o fornecedor encontrado
        return supplier;
    }

    //[CTRL-TRACE: SUPP-CTRL-005]: Endpoint responsável por criar um novo recurso
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
    public ResponseEntity<SupplierDTO> create(@RequestBody SupplierDTO supplierDTO) { // Corpo da requisição convertido para DTO

        logger.info("[SUPP-CTRL-005] Criação solicitada | name={} | email={}", supplierDTO.getName(), supplierDTO.getEmail());

        // Chama o serviço para criar o novo fornecedor
        var created = supplierService.create(supplierDTO);

        logger.debug("[SUPP-CTRL-005] Criação realizada com sucesso | id={} | name={} | email={}",
                created.getId(), created.getName(), created.getEmail());

        // Retorna o fornecedor criado com status 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //[CTRL-TRACE: SUPP-CTRL-006]: Endpoint responsável por remover um recurso
    @DeleteMapping(value = "/{id}") // Mapeia requisições DELETE para URL com ID
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) { // ID do fornecedor a ser removido

        logger.info("[SUPP-CTRL-006] Remoção solicitada | id={}", id);

        // Chama o serviço para remover o fornecedor
        supplierService.delete(id);

        logger.debug("[SUPP-CTRL-006] Remoção realizada com sucesso | id={}", id);

        // Retorna resposta HTTP 204 (Sem conteúdo)
        return ResponseEntity.noContent().build();
    }

    //[CTRL-TRACE: SUPP-CTRL-007]: Endpoint responsável por atualizar um recurso existente
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
    public SupplierDTO update(@RequestBody SupplierDTO supplierDTO) { // DTO com dados atualizados

        logger.info("[SUPP-CTRL-007] Atualização solicitada | id={} | name={} | email={}",
                supplierDTO.getId(), supplierDTO.getName(), supplierDTO.getEmail());

        // Chama o serviço para atualizar o fornecedor
        var updated = supplierService.update(supplierDTO);

        logger.debug("[SUPP-CTRL-007] Atualização realizada com sucesso | id={} | name={} | email={}",
                updated.getId(), updated.getName(), updated.getEmail());

        // Retorna o fornecedor atualizado
        return updated;
    }

    //[CTRL-TRACE: SUPP-CTRL-008]: Endpoint responsável por ativar um recurso
    @PatchMapping( // Mapeia requisições PATCH para ativação
            value = "/activate/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public SupplierDTO activateSupplier(@PathVariable("id") Long id) { // ID do fornecedor a ativar

        logger.info("[SUPP-CTRL-008] Ativação solicitada | id={}", id);

        // Chama o serviço para ativar o fornecedor
        var activated = supplierService.activateSupplier(id);

        logger.debug("[SUPP-CTRL-008] Ativação realizada com sucesso | id={} | active={}",
                activated.getId(), activated.getActive());

        // Retorna o fornecedor ativado
        return activated;
    }

    //[CTRL-TRACE: SUPP-CTRL-009]: Endpoint responsável por desativar um recurso
    @PatchMapping( // Mapeia requisições PATCH para desativação
            value = "/deactivate/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public SupplierDTO deactivateSupplier(@PathVariable("id") Long id) { // ID do fornecedor a desativar

        logger.info("[SUPP-CTRL-009] Desativação solicitada | id={}", id);

        // Chama o serviço para desativar o fornecedor
        var deactivated = supplierService.deactivateSupplier(id);

        logger.debug("[SUPP-CTRL-009] Desativação realizada com sucesso | id={} | active={}",
                deactivated.getId(), deactivated.getActive());

        // Retorna o fornecedor desativado
        return deactivated;
    }
}