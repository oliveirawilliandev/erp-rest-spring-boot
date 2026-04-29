package br.com.willian.service; // Pacote da camada de serviço

import br.com.willian.controller.ProductController; // Controller para links HATEOAS
import br.com.willian.dto.v1.ProductDTO; // DTO de produto
import br.com.willian.exception.BadRequestException; // Exceção para requisição inválida
import br.com.willian.exception.RequiredObjectIsNullException; // Exceção para objeto nulo
import br.com.willian.exception.ResourceNotFoundException; // Exceção para recurso não encontrado
import br.com.willian.file.exporter.factory.FileExporterFactory; // Factory de exportadores
import br.com.willian.file.importer.factory.FileImporterFactory; // Factory de importadores
import br.com.willian.mapper.ProductMapper; // Mapper para conversão Product/ProductDTO
import br.com.willian.model.Product; // Entidade Product
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

import java.time.Instant; // Timestamp UTC
import java.time.OffsetDateTime; // Data/hora com offset

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo; // Método estático para links
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn; // Método estático para controllers

@Service // Define a classe como um serviço Spring
public class ProductService {

    // Logger para rastreamento
    private static Logger logger = LoggerFactory.getLogger(ProductService.class.getName());

    @Autowired // Injeta montador de recursos paginados
    private PagedResourcesAssembler<ProductDTO> assembler;

    @Autowired // Injeta repository de produtos
    private ProductRepository productRepository;

    @Autowired // Injeta mapper de produtos
    private ProductMapper productMapper;

    @Autowired // Injeta factory de importadores
    private FileImporterFactory importer;

    @Autowired // Injeta factory de exportadores
    private FileExporterFactory exporter;

    // [PROD-SRV-001] Recupera lista paginada de produtos
    public PagedModel<EntityModel<ProductDTO>> findAll(Pageable pageable) {
        logger.info("[PROD-SRV-001] Finding All Products - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize()); // Log da requisição

        var products = productRepository.findAll(pageable); // Busca paginada

        logger.debug("[PROD-SRV-001] Found {} products in page {} of {}",
                products.getNumberOfElements(), products.getNumber() + 1, products.getTotalPages()); // Log do resultado

        return buildPageModel(pageable, products); // Retorna modelo paginado com HATEOAS
    }

    // [PROD-SRV-002] Recupera produtos paginados por nome
    public PagedModel<EntityModel<ProductDTO>> findByName(String name, Pageable pageable) {
        logger.info("[PROD-SRV-002] Finding Products by name: '{}' - page: {}, size: {}",
                name, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var products = productRepository.findByNameContainingIgnoreCase(name, pageable); // Busca por nome

        logger.debug("[PROD-SRV-002] Found {} results for name '{}'", products.getNumberOfElements(), name); // Log do resultado

        return buildPageModel(pageable, products); // Retorna modelo paginado
    }

    // [PROD-SRV-003] Recupera produto por ID
    public ProductDTO findById(Long id) {
        logger.info("[PROD-SRV-003] Finding one Product with ID: {}", id); // Log da busca

        var entityLoaded = productRepository.findById(id) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[PROD-SRV-003] Product with ID {} not found", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        var dtoLoaded = productMapper.toDTO(entityLoaded); // Converte para DTO
        logger.debug("[PROD-SRV-003] Product found: {}", entityLoaded.getName()); // Log do produto encontrado

        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [PROD-SRV-004] Cria um novo produto
    public ProductDTO create(ProductDTO productDTO) {
        if (productDTO == null) {
            logger.error("[PROD-SRV-004] Attempted to create null product"); // Log de erro
            throw new RequiredObjectIsNullException(); // Exceção
        }
        logger.info("[PROD-SRV-004] Creating one Product with name: {}", productDTO.getName()); // Log da criação

        validateProductName(productDTO.getName()); // Valida nome único
        validatePrice(productDTO.getPrice(), productDTO.getStartingPrice()); // Valida preços

        productDTO.setCreatedAt(OffsetDateTime.now()); // Data de criação
        productDTO.setUpdatedAt(OffsetDateTime.now()); // Data de atualização

        var entity = productMapper.toEntity(productDTO); // Converte para entidade
        var entityPersisted = productRepository.save(entity); // Persiste
        var dtoPersisted = productMapper.toDTO(entityPersisted); // Converte para DTO

        logger.debug("[PROD-SRV-004] Product created successfully with ID: {}", dtoPersisted.getId()); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO criado
    }

    // [PROD-SRV-005] Remove um produto por ID
    public void delete(Long id) {
        logger.info("[PROD-SRV-005] Deleting one Product with ID: {}", id); // Log da deleção

        Product entityLoaded = productRepository.findById(id) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[PROD-SRV-005] Product with ID {} not found for deletion", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        productRepository.delete(entityLoaded); // Deleta
        logger.debug("[PROD-SRV-005] Product ID {} deleted successfully", id); // Log de sucesso
    }

    // [PROD-SRV-006] Atualiza um produto existente
    public ProductDTO update(ProductDTO productDTO) {
        if (productDTO == null) {
            logger.error("[PROD-SRV-006] Attempted to update null product"); // Log de erro
            throw new RequiredObjectIsNullException(); // Exceção
        }

        logger.info("[PROD-SRV-006] Updating one Product with ID: {}", productDTO.getId()); // Log da atualização

        Product entityLoaded = productRepository.findById(productDTO.getId()) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[PROD-SRV-006] Product with ID {} not found for update", productDTO.getId()); // Log de aviso
                    return new ResourceNotFoundException("No records found for this ID"); // Exceção
                });

        logger.debug("[PROD-SRV-006] Updating product: {}", entityLoaded.getName()); // Log do produto sendo atualizado

        // Atualiza campos
        entityLoaded.setName(productDTO.getName());
        entityLoaded.setDescription(productDTO.getDescription());
        entityLoaded.setPrice(productDTO.getPrice());
        entityLoaded.setStartingPrice(productDTO.getStartingPrice());
        entityLoaded.setStockQuantity(productDTO.getStockQuantity());
        entityLoaded.setActive(productDTO.getActive());
        entityLoaded.setUpdatedAt(Instant.now()); // Atualiza timestamp

        //verificação
        if(productDTO.getPhotoUrl() != null && !productDTO.getPhotoUrl().isBlank()) {entityLoaded.setPhotoUrl(productDTO.getPhotoUrl());}
        if(productDTO.getQrCode() != null && !productDTO.getQrCode().isBlank()) {entityLoaded.setQrCode(productDTO.getQrCode());}
        if(productDTO.getBarCode() != null && !productDTO.getBarCode().isBlank()) {entityLoaded.setQrCode(productDTO.getBarCode());}

        Product entityPersisted = productRepository.save(entityLoaded); // Salva
        ProductDTO dtoPersisted = productMapper.toDTO(entityPersisted); // Converte para DTO

        logger.debug("[PROD-SRV-006] Product ID {} updated successfully", productDTO.getId()); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO atualizado
    }

    // [PROD-SRV-007] Atualiza a quantidade em estoque
    @Transactional // Método executado dentro de transação
    public ProductDTO updateStock(Long id, Integer quantity) {
        logger.info("[PROD-SRV-007] Updating stock for Product ID: {}, Quantity: {}", id, quantity); // Log da atualização

        productRepository.findById(id) // Verifica existência
                .orElseThrow(() -> {
                    logger.warn("[PROD-SRV-007] Product with ID {} not found for stock update", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        productRepository.updateStock(id, quantity); // Atualiza estoque

        var productEntity = productRepository.findById(id).get(); // Busca atualizado
        var productDTO = productMapper.toDTO(productEntity); // Converte para DTO

        logger.debug("[PROD-SRV-007] Stock updated for Product ID: {}. New stock: {}",
                id, productEntity.getStockQuantity()); // Log de sucesso

        addHateoasLinks(productDTO); // Adiciona links HATEOAS
        return productDTO; // Retorna DTO
    }

    // [PROD-SRV-008] Ativa um produto (status = true)
    @Transactional // Método executado dentro de transação
    public ProductDTO activateProduct(Long id) {
        logger.info("[PROD-SRV-008] Activating Product with ID: {}", id); // Log da ativação

        productRepository.findById(id) // Verifica existência
                .orElseThrow(() -> {
                    logger.warn("[PROD-SRV-008] Product with ID {} not found for activation", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        productRepository.activateProduct(id); // Ativa produto

        var productEntity = productRepository.findById(id).get(); // Busca atualizado
        var productDTO = productMapper.toDTO(productEntity); // Converte para DTO

        logger.debug("[PROD-SRV-008] Product ID {} activated successfully", id); // Log de sucesso

        addHateoasLinks(productDTO); // Adiciona links HATEOAS
        return productDTO; // Retorna DTO
    }

    // [PROD-SRV-009] Desativa um produto (status = false)
    @Transactional // Método executado dentro de transação
    public ProductDTO deactivateProduct(Long id) {
        logger.info("[PROD-SRV-009] Deactivating Product with ID: {}", id); // Log da desativação

        productRepository.findById(id) // Verifica existência
                .orElseThrow(() -> {
                    logger.warn("[PROD-SRV-009] Product with ID {} not found for deactivation", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        productRepository.deactivateProduct(id); // Desativa produto

        var productEntity = productRepository.findById(id).get(); // Busca atualizado
        var productDTO = productMapper.toDTO(productEntity); // Converte para DTO

        logger.debug("[PROD-SRV-009] Product ID {} deactivated successfully", id); // Log de sucesso

        addHateoasLinks(productDTO); // Adiciona links HATEOAS
        return productDTO; // Retorna DTO
    }

    // [PROD-SRV-010] Recupera produtos por status ativo/inativo
    public PagedModel<EntityModel<ProductDTO>> findByActive(Boolean active, Pageable pageable) {
        logger.info("[PROD-SRV-010] Finding Products by active status: {} - page: {}, size: {}",
                active, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var products = productRepository.findByActive(active, pageable); // Busca por status

        return buildPageModel(pageable, products); // Retorna modelo paginado
    }

    // [PROD-SRV-011] Recupera produtos com estoque abaixo do limite
    public PagedModel<EntityModel<ProductDTO>> findLowStock(Integer threshold, Pageable pageable) {
        logger.info("[PROD-SRV-011] Finding Products with stock below {} - page: {}, size: {}",
                threshold, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var products = productRepository.findByStockQuantityLessThan(threshold, pageable); // Busca por estoque baixo

        return buildPageModel(pageable, products); // Retorna modelo paginado
    }

    // [PROD-SRV-INTERNAL-001] Adiciona links HATEOAS ao DTO
    private void addHateoasLinks(ProductDTO productDTO) {
        logger.trace("[PROD-SRV-INTERNAL-001] Adding HATEOAS links for product ID: {}", productDTO.getId());

        productDTO.add(linkTo(methodOn(ProductController.class)
                .findById(productDTO.getId())).withSelfRel().withType("GET"));

        productDTO.add(linkTo(methodOn(ProductController.class)
                .findAll(1, 12, "asc")).withRel("findAll").withType("GET"));

        productDTO.add(linkTo(methodOn(ProductController.class)
                .findByName("", 1, 11, "asc")).withRel("findByName").withType("GET"));

        productDTO.add(linkTo(methodOn(ProductController.class)
                .create(productDTO)).withRel("create").withType("POST"));

        productDTO.add(linkTo(methodOn(ProductController.class)
                .update(productDTO)).withRel("update").withType("PUT"));

        productDTO.add(linkTo(methodOn(ProductController.class)
                .updateStock(productDTO.getId(), null)).withRel("updateStock").withType("PATCH"));

        productDTO.add(linkTo(methodOn(ProductController.class)
                .activateProduct(productDTO.getId())).withRel("activate").withType("PATCH"));

        productDTO.add(linkTo(methodOn(ProductController.class)
                .deactivateProduct(productDTO.getId())).withRel("deactivate").withType("PATCH"));

        productDTO.add(linkTo(methodOn(ProductController.class)
                .delete(productDTO.getId())).withRel("delete").withType("DELETE"));
    }

    // [PROD-SRV-INTERNAL-002] Constrói modelo paginado com HATEOAS
    private PagedModel<EntityModel<ProductDTO>> buildPageModel(
            Pageable pageable,
            Page<Product> products) {
        logger.trace("[PROD-SRV-INTERNAL-002] Building page model for page {} with {} elements",
                pageable.getPageNumber(), products.getNumberOfElements());

        Page<ProductDTO> productWithLinks = productMapper.toDTOPage(products); // Converte página para DTO
        productWithLinks.forEach(this::addHateoasLinks); // Adiciona links a cada item

        Link findAllLink = linkTo( // Cria link para a própria consulta
                methodOn(ProductController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort())
                        )
        ).withSelfRel();

        return assembler.toModel(productWithLinks, findAllLink); // Retorna modelo paginado
    }

    // [PROD-SRV-INTERNAL-003] Valida nome do produto (único)
    private void validateProductName(String name) {
        logger.trace("[PROD-SRV-INTERNAL-003] Validating product name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            logger.error("[PROD-SRV-INTERNAL-003] Product name validation failed: name is null or empty");
            throw new BadRequestException("Product name cannot be null or empty");
        }

        if (productRepository.existsByName(name)) {
            logger.error("[PROD-SRV-INTERNAL-003] Product name already exists: {}", name);
            throw new BadRequestException("Product name already registered: " + name);
        }
    }

    // [PROD-SRV-INTERNAL-004] Valida preços (price > 0, startingPrice >= 0, price >= startingPrice)
    private void validatePrice(Double price, Double startingPrice) {
        logger.trace("[PROD-SRV-INTERNAL-004] Validating price: {} / starting price: {}", price, startingPrice);

        if (price == null || price <= 0) {
            logger.error("[PROD-SRV-INTERNAL-004] Price validation failed: price must be positive");
            throw new BadRequestException("Price must be greater than zero");
        }

        if (startingPrice == null || startingPrice < 0) {
            logger.error("[PROD-SRV-INTERNAL-004] Starting price validation failed: starting price cannot be negative");
            throw new BadRequestException("Starting price cannot be negative");
        }

        if (price < startingPrice) {
            logger.error("[PROD-SRV-INTERNAL-004] Price validation failed: price {} is less than starting price {}", price, startingPrice);
            throw new BadRequestException("Price cannot be less than starting price");
        }
    }
}