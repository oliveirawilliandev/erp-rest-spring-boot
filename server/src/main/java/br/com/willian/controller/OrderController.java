package br.com.willian.controller; // Pacote da camada de controle/API

import br.com.willian.controller.docs.OrderControllerDocs; // Interface de documentação Swagger/OpenAPI
import br.com.willian.dto.v1.OrderDTO; // DTO de pedido
import br.com.willian.model.enums.OrderStatus; // Enum de status do pedido
import br.com.willian.service.OrderService; // Serviço com a lógica de negócio
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

// Define esta classe como um controlador REST com URL base "/api/order/v1"
@RestController
// Mapeia as requisições para esta URL base
@RequestMapping("/api/order/v1")
// Implementa a interface de documentação para garantir consistência
public class OrderController implements OrderControllerDocs {

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    // Injeta automaticamente a dependência do serviço de pedidos
    @Autowired
    private OrderService orderService;

    //[CTRL-TRACE: ORD-CTRL-001]: Endpoint responsável por retornar uma lista paginada de recursos
    @GetMapping( // Mapeia requisições GET para este método
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    // Permite ordenação e paginação via parâmetros de requisição
    public ResponseEntity<PagedModel<EntityModel<OrderDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Parâmetro de página, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação, padrão ascendente
    ) {

        // Registra log da solicitação de listagem com parâmetros
        logger.info("[ORD-CTRL-001] Listagem solicitada | page={} | size={} | direction={}", page, size, direction);

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
        logger.debug("[ORD-CTRL-001] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar todos os pedidos paginados
        var response = orderService.findAll(pageable);

        logger.debug("[ORD-CTRL-001] Listagem retornada com sucesso | page={} | size={} | direction={}", page, size, direction);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: ORD-CTRL-002]: Endpoint responsável por retornar um recurso específico
    @GetMapping( // Mapeia requisições GET para URL com ID
            value = "/{id}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public OrderDTO findById(@PathVariable("id") Long id) { // ID do pedido vindo da URL

        logger.info("[ORD-CTRL-002] Busca por ID solicitada | id={}", id);

        // Chama o serviço para buscar pedido pelo ID
        var order = orderService.findById(id);
        logger.debug("[ORD-CTRL-002] Serviço retornou pedido | id={}", order.getId());

        // Retorna o pedido encontrado
        return order;
    }

    //[CTRL-TRACE: ORD-CTRL-003]: Endpoint responsável por retornar pedidos filtrados por ID do cliente
    @GetMapping( // Mapeia requisições GET para URL com customerId
            value = "/findByCustomerId/{customerId}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<OrderDTO>>> findByCustomerId(
            @PathVariable("customerId") Long customerId, // ID do cliente vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[ORD-CTRL-003] Busca por customerId solicitada | customerId={} | page={} | size={}", customerId, page, size);

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
        logger.debug("[ORD-CTRL-003] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar pedidos pelo ID do cliente
        var response = orderService.findByCustomerId(customerId, pageable);

        logger.debug("[ORD-CTRL-003] Busca por customerId retornada com sucesso | customerId={} | page={} | size={}", customerId, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: ORD-CTRL-004]: Endpoint responsável por retornar pedidos filtrados por ID do funcionário
    @GetMapping( // Mapeia requisições GET para URL com employeeId
            value = "/findByEmployeeId/{employeeId}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<OrderDTO>>> findByEmployeeId(
            @PathVariable("employeeId") Long employeeId, // ID do funcionário vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[ORD-CTRL-004] Busca por employeeId solicitada | employeeId={} | page={} | size={}", employeeId, page, size);

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
        logger.debug("[ORD-CTRL-004] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar pedidos pelo ID do funcionário
        var response = orderService.findByEmployeeId(employeeId, pageable);

        logger.debug("[ORD-CTRL-004] Busca por employeeId retornada com sucesso | employeeId={} | page={} | size={}", employeeId, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: ORD-CTRL-005]: Endpoint responsável por retornar pedidos filtrados por status
    @GetMapping( // Mapeia requisições GET para URL com status
            value = "/findByStatus/{status}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<OrderDTO>>> findByStatus(
            @PathVariable("status") OrderStatus status, // Status do pedido vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[ORD-CTRL-005] Busca por status solicitada | status={} | page={} | size={}", status, page, size);

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
        logger.debug("[ORD-CTRL-005] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar pedidos pelo status
        var response = orderService.findByStatus(status, pageable);

        logger.debug("[ORD-CTRL-005] Busca por status retornada com sucesso | status={} | page={} | size={}", status, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: ORD-CTRL-006]: Endpoint responsável por criar um novo recurso
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
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO orderDTO) { // Corpo da requisição convertido para DTO

        logger.info("[ORD-CTRL-006] Criação solicitada | customerId={} | employeeId={}", orderDTO.getCustomerId(), orderDTO.getEmployeeId());

        // Chama o serviço para criar o novo pedido
        var created = orderService.create(orderDTO);

        logger.debug("[ORD-CTRL-006] Criação realizada com sucesso | id={} | customerId={} | employeeId={}",
                created.getId(), created.getCustomerId(), created.getEmployeeId());

        // Retorna o pedido criado com status 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //[CTRL-TRACE: ORD-CTRL-007]: Endpoint responsável por atualizar o status de um recurso
    @PatchMapping( // Mapeia requisições PATCH para atualização de status
            value = "/updateStatus/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public OrderDTO updateStatus(
            @PathVariable("id") Long id, // ID do pedido vindo da URL
            @RequestParam("status") OrderStatus status) { // Novo status do pedido

        logger.info("[ORD-CTRL-007] Atualização de status solicitada | id={} | status={}", id, status);

        // Chama o serviço para atualizar o status do pedido
        var updated = orderService.updateStatus(id, status);

        logger.debug("[ORD-CTRL-007] Status atualizado com sucesso | id={} | status={}", updated.getId(), updated.getStatus());

        // Retorna o pedido atualizado
        return updated;
    }

    //[CTRL-TRACE: ORD-CTRL-008]: Endpoint responsável por cancelar um recurso
    @PostMapping( // Mapeia requisições POST para cancelamento
            value = "/cancel/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public OrderDTO cancel(@PathVariable("id") Long id) { // ID do pedido a ser cancelado

        logger.info("[ORD-CTRL-008] Cancelamento solicitada | id={}", id);

        // Chama o serviço para cancelar o pedido
        var cancelled = orderService.cancel(id);

        logger.debug("[ORD-CTRL-008] Cancelamento realizado com sucesso | id={} | status={}", cancelled.getId(), cancelled.getStatus());

        // Retorna o pedido cancelado
        return cancelled;
    }

    //[CTRL-TRACE: ORD-CTRL-009]: Endpoint responsável por remover um recurso
    @DeleteMapping(value = "/{id}") // Mapeia requisições DELETE para URL com ID
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) { // ID do pedido a ser removido

        logger.info("[ORD-CTRL-009] Remoção solicitada | id={}", id);

        // Chama o serviço para remover o pedido
        orderService.delete(id);

        logger.debug("[ORD-CTRL-009] Remoção realizada com sucesso | id={}", id);

        // Retorna resposta HTTP 204 (Sem conteúdo)
        return ResponseEntity.noContent().build();
    }
}