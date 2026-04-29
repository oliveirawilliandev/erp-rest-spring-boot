package br.com.willian.service; // Pacote da camada de serviço

import br.com.willian.controller.OrderController; // Controller para links HATEOAS
import br.com.willian.dto.v1.OrderDTO; // DTO de pedido
import br.com.willian.dto.v1.OrderItemDTO; // DTO de item do pedido
import br.com.willian.exception.BadRequestException; // Exceção para requisição inválida
import br.com.willian.exception.RequiredObjectIsNullException; // Exceção para objeto nulo
import br.com.willian.exception.ResourceNotFoundException; // Exceção para recurso não encontrado
import br.com.willian.file.exporter.factory.FileExporterFactory; // Factory de exportadores
import br.com.willian.file.importer.factory.FileImporterFactory; // Factory de importadores
import br.com.willian.mapper.OrderMapper; // Mapper para conversão Order/OrderDTO
import br.com.willian.mapper.ProductMapper; // Mapper para conversão Product/ProductDTO
import br.com.willian.model.Order; // Entidade Order
import br.com.willian.model.OrderItem; // Entidade OrderItem
import br.com.willian.model.Product; // Entidade Product
import br.com.willian.model.enums.OrderStatus; // Enum de status do pedido
import br.com.willian.repository.OrderItemRepository; // Repository de itens do pedido
import br.com.willian.repository.OrderRepository; // Repository de pedidos
import br.com.willian.repository.ProductRepository; // Repository de produtos
import org.slf4j.Logger; // Interface de logging
import org.slf4j.LoggerFactory; // Factory para logger
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.data.domain.Page; // Página de resultados
import org.springframework.data.domain.Pageable; // Configuração de paginação
import org.springframework.data.web.PagedResourcesAssembler; // Montador de recursos paginados
import org.springframework.hateoas.EntityModel; // Wrapper HATEOAS para entidades
import org.springframework.hateoas.Link; // Link HATEOAS
import org.springframework.hateoas.PagedModel; // Modelo HATEOAS paginado
import org.springframework.stereotype.Service; // Marca como serviço
import org.springframework.transaction.annotation.Transactional; // Controle transacional

import java.math.BigDecimal; // Precisão para valores monetários
import java.time.Instant; // Timestamp UTC
import java.util.List; // Interface List

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo; // Método estático para links
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn; // Método estático para controllers

@Service // Define a classe como um serviço Spring
public class OrderService {

    // Logger para rastreamento
    private static Logger logger = LoggerFactory.getLogger(OrderService.class.getName());

    @Autowired // Injeta montador de recursos paginados
    private PagedResourcesAssembler<OrderDTO> assembler;

    @Autowired // Injeta repository de pedidos
    private OrderRepository orderRepository;

    @Autowired // Injeta repository de itens do pedido
    private OrderItemRepository orderItemRepository;

    @Autowired // Injeta repository de produtos
    private ProductRepository productRepository;

    @Autowired // Injeta mapper de pedidos
    private OrderMapper orderMapper;

    @Autowired // Injeta mapper de produtos
    private ProductMapper productMapper;

    @Autowired // Injeta factory de importadores
    private FileImporterFactory importer;

    @Autowired // Injeta factory de exportadores
    private FileExporterFactory exporter;

    // [ORD-SRV-001] Recupera lista paginada de pedidos
    public PagedModel<EntityModel<OrderDTO>> findAll(Pageable pageable) {
        logger.info("[ORD-SRV-001] Finding All Orders - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize()); // Log da requisição

        var orders = orderRepository.findAll(pageable); // Busca paginada

        logger.debug("[ORD-SRV-001] Found {} orders in page {} of {}",
                orders.getNumberOfElements(), orders.getNumber() + 1, orders.getTotalPages()); // Log do resultado

        return buildPageModel(pageable, orders); // Retorna modelo paginado com HATEOAS
    }

    // [ORD-SRV-002] Recupera pedido por ID com seus itens
    public OrderDTO findById(Long id) {
        logger.info("[ORD-SRV-002] Finding one Order with ID: {}", id); // Log da busca

        var entityLoaded = orderRepository.findById(id) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[ORD-SRV-002] Order with ID {} not found", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        var dtoLoaded = orderMapper.toDTO(entityLoaded); // Converte para DTO

        // Carrega itens do pedido
        var items = orderItemRepository.findByOrderId(id);
        dtoLoaded.setItems(items.stream().map(item -> {
            var dto = convertItemToDTO(item); // Converte item para DTO

            var productId = item.getProductId();
            if (productId != null) {
                productRepository.findById(productId) // Busca produto
                        .map(productMapper::toDTO) // Converte para DTO
                        .ifPresent(dto::setProduct); // Adiciona ao item
            }
            return dto;
        }).toList());

        logger.debug("[ORD-SRV-002] Order found: ID {}", entityLoaded.getId()); // Log do pedido encontrado

        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [ORD-SRV-003] Recupera pedidos por ID do cliente (paginado)
    public PagedModel<EntityModel<OrderDTO>> findByCustomerId(Long customerId, Pageable pageable) {
        logger.info("[ORD-SRV-003] Finding Orders by customer ID: {} - page: {}, size: {}",
                customerId, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var orders = orderRepository.findByCustomerId(customerId, pageable); // Busca por cliente

        logger.debug("[ORD-SRV-003] Found {} orders for customer {}", orders.getNumberOfElements(), customerId); // Log do resultado

        return buildPageModel(pageable, orders); // Retorna modelo paginado
    }

    // [ORD-SRV-004] Recupera pedidos por ID do funcionário (paginado)
    public PagedModel<EntityModel<OrderDTO>> findByEmployeeId(Long employeeId, Pageable pageable) {
        logger.info("[ORD-SRV-004] Finding Orders by employee ID: {} - page: {}, size: {}",
                employeeId, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var orders = orderRepository.findByEmployeeId(employeeId, pageable); // Busca por funcionário

        logger.debug("[ORD-SRV-004] Found {} orders for employee {}", orders.getNumberOfElements(), employeeId); // Log do resultado

        return buildPageModel(pageable, orders); // Retorna modelo paginado
    }

    // [ORD-SRV-005] Recupera pedidos por status (paginado)
    public PagedModel<EntityModel<OrderDTO>> findByStatus(OrderStatus status, Pageable pageable) {
        logger.info("[ORD-SRV-005] Finding Orders by status: {} - page: {}, size: {}",
                status, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var orders = orderRepository.findByStatus(status, pageable); // Busca por status

        logger.debug("[ORD-SRV-005] Found {} orders with status {}", orders.getNumberOfElements(), status); // Log do resultado

        return buildPageModel(pageable, orders); // Retorna modelo paginado
    }

    // [ORD-SRV-006] Cria um novo pedido com seus itens
    @Transactional // Método executado dentro de transação
    public OrderDTO create(OrderDTO orderDTO) {
        if (orderDTO == null) {
            logger.error("[ORD-SRV-006] Attempted to create null order"); // Log de erro
            throw new RequiredObjectIsNullException(); // Exceção
        }
        logger.info("[ORD-SRV-006] Creating one Order for customer ID: {}", orderDTO.getCustomerId()); // Log da criação

        // Cria entidade Order
        Order order = new Order();
        order.setEmployeeId(orderDTO.getEmployeeId());
        order.setCustomerId(orderDTO.getCustomerId());
        order.setStatus(OrderStatus.PENDING); // Status inicial
        order.setCreatedAt(Instant.now()); // Data de criação
        order.setUpdatedAt(Instant.now()); // Data de atualização

        BigDecimal totalAmount = BigDecimal.ZERO; // Acumulador do valor total

        // Processa cada item do pedido
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId()) // Busca produto
                    .orElseThrow(() -> {
                        logger.error("[ORD-SRV-006] Product not found with ID: {}", itemDTO.getProductId()); // Log de erro
                        return new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()); // Exceção
                    });

            // Valida estoque
            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                logger.error("[ORD-SRV-006] Insufficient stock for product: {}", product.getName()); // Log de erro
                throw new BadRequestException("Insufficient stock for product: " + product.getName()); // Exceção
            }

            // Atualiza estoque
            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);

            // Acumula valor total
            totalAmount = totalAmount.add(BigDecimal.valueOf(itemDTO.getQuantity() * product.getPrice()));
        }

        order.setTotalAmount(totalAmount); // Define valor total
        order = orderRepository.save(order); // Salva pedido

        // Salva os itens do pedido
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId()).get(); // Busca produto
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId()); // Associa ao pedido
            item.setProductId(itemDTO.getProductId());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(product.getPrice()); // Preço congelado no momento da compra
            orderItemRepository.save(item); // Salva item
        }

        var dtoPersisted = orderMapper.toDTO(order); // Converte para DTO
        var items = orderItemRepository.findByOrderId(order.getId()); // Busca itens salvos
        dtoPersisted.setItems(items.stream().map(this::convertItemToDTO).toList()); // Adiciona itens ao DTO

        logger.debug("[ORD-SRV-006] Order created successfully with ID: {}", dtoPersisted.getId()); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO criado
    }

    // [ORD-SRV-007] Atualiza o status de um pedido
    @Transactional // Método executado dentro de transação
    public OrderDTO updateStatus(Long id, OrderStatus status) {
        logger.info("[ORD-SRV-007] Updating status for Order ID: {} to {}", id, status); // Log da atualização

        Order order = orderRepository.findById(id) // Busca pedido
                .orElseThrow(() -> {
                    logger.warn("[ORD-SRV-007] Order with ID {} not found for status update", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        order.setStatus(status); // Atualiza status
        order.setUpdatedAt(Instant.now()); // Atualiza timestamp

        Order entityPersisted = orderRepository.save(order); // Salva alterações
        var dtoPersisted = orderMapper.toDTO(entityPersisted); // Converte para DTO
        var items = orderItemRepository.findByOrderId(id); // Busca itens
        dtoPersisted.setItems(items.stream().map(this::convertItemToDTO).toList()); // Adiciona itens ao DTO

        logger.debug("[ORD-SRV-007] Order ID {} status updated to {}", id, status); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO atualizado
    }

    // [ORD-SRV-008] Cancela um pedido e restaura o estoque
    @Transactional // Método executado dentro de transação
    public OrderDTO cancel(Long id) {
        logger.info("[ORD-SRV-008] Cancelling Order with ID: {}", id); // Log do cancelamento

        Order order = orderRepository.findById(id) // Busca pedido
                .orElseThrow(() -> {
                    logger.warn("[ORD-SRV-008] Order with ID {} not found for cancellation", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        // Valida se pedido já foi entregue
        if (order.getStatus() == OrderStatus.DELIVERED) {
            logger.error("[ORD-SRV-008] Cannot cancel order {} - already delivered", id); // Log de erro
            throw new IllegalStateException("Cannot cancel an order that has already been delivered"); // Exceção
        }

        order.setStatus(OrderStatus.CANCELLED); // Define status como cancelado
        order.setUpdatedAt(Instant.now()); // Atualiza timestamp

        // Restaura estoque dos produtos
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity()); // Restaura quantidade
            productRepository.save(product); // Salva produto
        }

        Order entityPersisted = orderRepository.save(order); // Salva pedido
        var dtoPersisted = orderMapper.toDTO(entityPersisted); // Converte para DTO
        dtoPersisted.setItems(items.stream().map(this::convertItemToDTO).toList()); // Adiciona itens ao DTO

        logger.debug("[ORD-SRV-008] Order ID {} cancelled successfully", id); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO cancelado
    }

    // [ORD-SRV-009] Remove um pedido e seus itens
    public void delete(Long id) {
        logger.info("[ORD-SRV-009] Deleting one Order with ID: {}", id); // Log da deleção

        Order entityLoaded = orderRepository.findById(id) // Busca pedido
                .orElseThrow(() -> {
                    logger.warn("[ORD-SRV-009] Order with ID {} not found for deletion", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        orderItemRepository.deleteByOrderId(id); // Remove itens primeiro (FK)
        orderRepository.delete(entityLoaded); // Remove pedido

        logger.debug("[ORD-SRV-009] Order ID {} deleted successfully", id); // Log de sucesso
    }

    // [ORD-SRV-INTERNAL-001] Adiciona links HATEOAS ao DTO
    private void addHateoasLinks(OrderDTO orderDTO) {
        logger.trace("[ORD-SRV-INTERNAL-001] Adding HATEOAS links for order ID: {}", orderDTO.getId());

        orderDTO.add(linkTo(methodOn(OrderController.class)
                .findById(orderDTO.getId())).withSelfRel().withType("GET"));

        orderDTO.add(linkTo(methodOn(OrderController.class)
                .findAll(1, 12, "asc")).withRel("findAll").withType("GET"));

        orderDTO.add(linkTo(methodOn(OrderController.class)
                .create(orderDTO)).withRel("create").withType("POST"));

        orderDTO.add(linkTo(methodOn(OrderController.class)
                .updateStatus(orderDTO.getId(), orderDTO.getStatus())).withRel("updateStatus").withType("PATCH"));

        orderDTO.add(linkTo(methodOn(OrderController.class)
                .cancel(orderDTO.getId())).withRel("cancel").withType("POST"));

        orderDTO.add(linkTo(methodOn(OrderController.class)
                .delete(orderDTO.getId())).withRel("delete").withType("DELETE"));
    }

    // [ORD-SRV-INTERNAL-002] Constrói modelo paginado com HATEOAS
    private PagedModel<EntityModel<OrderDTO>> buildPageModel(
            Pageable pageable,
            Page<Order> orders) {
        logger.trace("[ORD-SRV-INTERNAL-002] Building page model for page {} with {} elements",
                pageable.getPageNumber(), orders.getNumberOfElements());

        Page<OrderDTO> orderWithLinks = orderMapper.toDTOPage(orders); // Converte página para DTO
        orderWithLinks.forEach(this::addHateoasLinks); // Adiciona links a cada item

        Link findAllLink = linkTo( // Cria link para a própria consulta
                methodOn(OrderController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort())
                        )
        ).withSelfRel();

        return assembler.toModel(orderWithLinks, findAllLink); // Retorna modelo paginado
    }

    // [ORD-SRV-INTERNAL-003] Converte entidade OrderItem para DTO
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId()); // ID do item
        dto.setOrderId(item.getOrderId()); // ID do pedido
        dto.setProductId(item.getProductId()); // ID do produto
        dto.setQuantity(item.getQuantity()); // Quantidade
        dto.setUnitPrice(item.getUnitPrice()); // Preço unitário
        return dto; // Retorna DTO
    }
}