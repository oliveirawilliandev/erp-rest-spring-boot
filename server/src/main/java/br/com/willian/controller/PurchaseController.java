package br.com.willian.controller; // Pacote da camada de controle/API

import br.com.willian.controller.docs.PurchaseControllerDocs; // Interface de documentação Swagger/OpenAPI
import br.com.willian.dto.v1.PurchaseDTO; // DTO de compra
import br.com.willian.model.enums.PurchaseStatus; // Enum de status da compra
import br.com.willian.service.PurchaseService; // Serviço com a lógica de negócio
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

// Define esta classe como um controlador REST com URL base "/api/purchase/v1"
@RestController
// Mapeia as requisições para esta URL base
@RequestMapping("/api/purchase/v1")
// Implementa a interface de documentação para garantir consistência
public class PurchaseController implements PurchaseControllerDocs {

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    // Injeta automaticamente a dependência do serviço de compras
    @Autowired
    private PurchaseService purchaseService;

    //[CTRL-TRACE: PUR-CTRL-001]: Endpoint responsável por retornar uma lista paginada de recursos
    @GetMapping( // Mapeia requisições GET para este método
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    // Permite ordenação e paginação via parâmetros de requisição
    public ResponseEntity<PagedModel<EntityModel<PurchaseDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Parâmetro de página, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação, padrão ascendente
    ) {

        // Registra log da solicitação de listagem com parâmetros
        logger.info("[PUR-CTRL-001] Listagem solicitada | page={} | size={} | direction={}", page, size, direction);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por ID
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "id")
        );
        logger.debug("[PUR-CTRL-001] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar todas as compras paginadas
        var response = purchaseService.findAll(pageable);

        logger.debug("[PUR-CTRL-001] Listagem retornada com sucesso | page={} | size={} | direction={}", page, size, direction);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: PUR-CTRL-002]: Endpoint responsável por retornar um recurso específico
    @GetMapping( // Mapeia requisições GET para URL com ID
            value = "/{id}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public PurchaseDTO findById(@PathVariable("id") Long id) { // ID da compra vindo da URL

        logger.info("[PUR-CTRL-002] Busca por ID solicitada | id={}", id);

        // Chama o serviço para buscar compra pelo ID
        var purchase = purchaseService.findById(id);
        logger.debug("[PUR-CTRL-002] Serviço retornou compra | id={}", purchase.getId());

        // Retorna a compra encontrada
        return purchase;
    }

    //[CTRL-TRACE: PUR-CTRL-003]: Endpoint responsável por retornar compras filtradas por ID do fornecedor
    @GetMapping( // Mapeia requisições GET para URL com supplierId
            value = "/findBySupplierId/{supplierId}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<PurchaseDTO>>> findBySupplierId(
            @PathVariable("supplierId") Long supplierId, // ID do fornecedor vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[PUR-CTRL-003] Busca por supplierId solicitada | supplierId={} | page={} | size={}", supplierId, page, size);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por ID
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "id")
        );
        logger.debug("[PUR-CTRL-003] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar compras pelo ID do fornecedor
        var response = purchaseService.findBySupplierId(supplierId, pageable);

        logger.debug("[PUR-CTRL-003] Busca por supplierId retornada com sucesso | supplierId={} | page={} | size={}", supplierId, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: PUR-CTRL-004]: Endpoint responsável por retornar compras filtradas por ID do funcionário
    @GetMapping( // Mapeia requisições GET para URL com employeeId
            value = "/findByEmployeeId/{employeeId}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<PurchaseDTO>>> findByEmployeeId(
            @PathVariable("employeeId") Long employeeId, // ID do funcionário vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[PUR-CTRL-004] Busca por employeeId solicitada | employeeId={} | page={} | size={}", employeeId, page, size);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por ID
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "id")
        );
        logger.debug("[PUR-CTRL-004] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar compras pelo ID do funcionário
        var response = purchaseService.findByEmployeeId(employeeId, pageable);

        logger.debug("[PUR-CTRL-004] Busca por employeeId retornada com sucesso | employeeId={} | page={} | size={}", employeeId, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: PUR-CTRL-005]: Endpoint responsável por retornar compras filtradas por status
    @GetMapping( // Mapeia requisições GET para URL com status
            value = "/findByStatus/{status}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<PurchaseDTO>>> findByStatus(
            @PathVariable("status") PurchaseStatus status, // Status da compra vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[PUR-CTRL-005] Busca por status solicitada | status={} | page={} | size={}", status, page, size);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por ID
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "id")
        );
        logger.debug("[PUR-CTRL-005] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar compras pelo status
        var response = purchaseService.findByStatus(status, pageable);

        logger.debug("[PUR-CTRL-005] Busca por status retornada com sucesso | status={} | page={} | size={}", status, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: PUR-CTRL-006]: Endpoint responsável por criar um novo recurso
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
    public ResponseEntity<PurchaseDTO> create(@RequestBody PurchaseDTO purchaseDTO) { // Corpo da requisição convertido para DTO

        logger.info("[PUR-CTRL-006] Criação solicitada | supplierId={} | employeeId={}", purchaseDTO.getSupplierId(), purchaseDTO.getEmployeeId());

        // Chama o serviço para criar a nova compra
        var created = purchaseService.create(purchaseDTO);

        logger.debug("[PUR-CTRL-006] Criação realizada com sucesso | id={} | supplierId={} | employeeId={}",
                created.getId(), created.getSupplierId(), created.getEmployeeId());

        // Retorna a compra criada com status 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //[CTRL-TRACE: PUR-CTRL-007]: Endpoint responsável por atualizar o status de um recurso
    @PatchMapping( // Mapeia requisições PATCH para atualização de status
            value = "/updateStatus/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public PurchaseDTO updateStatus(
            @PathVariable("id") Long id, // ID da compra vindo da URL
            @RequestParam("status") PurchaseStatus status) { // Novo status da compra

        logger.info("[PUR-CTRL-007] Atualização de status solicitada | id={} | status={}", id, status);

        // Chama o serviço para atualizar o status da compra
        var updated = purchaseService.updateStatus(id, status);

        logger.debug("[PUR-CTRL-007] Status atualizado com sucesso | id={} | status={}", updated.getId(), updated.getStatus());

        // Retorna a compra atualizada
        return updated;
    }

    //[CTRL-TRACE: PUR-CTRL-008]: Endpoint responsável por remover um recurso
    @DeleteMapping(value = "/{id}") // Mapeia requisições DELETE para URL com ID
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) { // ID da compra a ser removida

        logger.info("[PUR-CTRL-008] Remoção solicitada | id={}", id);

        // Chama o serviço para remover a compra
        purchaseService.delete(id);

        logger.debug("[PUR-CTRL-008] Remoção realizada com sucesso | id={}", id);

        // Retorna resposta HTTP 204 (Sem conteúdo)
        return ResponseEntity.noContent().build();
    }
}