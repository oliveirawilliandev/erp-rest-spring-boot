package br.com.willian.service; // Pacote da camada de serviço

import br.com.willian.controller.PurchaseController; // Controller para links HATEOAS
import br.com.willian.dto.v1.PurchaseDTO; // DTO de compra
import br.com.willian.dto.v1.PurchaseItemDTO; // DTO de item da compra
import br.com.willian.exception.RequiredObjectIsNullException; // Exceção para objeto nulo
import br.com.willian.exception.ResourceNotFoundException; // Exceção para recurso não encontrado
import br.com.willian.file.exporter.factory.FileExporterFactory; // Factory de exportadores
import br.com.willian.file.importer.factory.FileImporterFactory; // Factory de importadores
import br.com.willian.mapper.IngredientMapper;
import br.com.willian.mapper.PurchaseItemMapper;
import br.com.willian.mapper.PurchaseMapper; // Mapper para conversão Purchase/PurchaseDTO
import br.com.willian.model.Ingredient;
import br.com.willian.model.Ingredient; // Entidade Ingredient
import br.com.willian.model.Purchase; // Entidade Purchase
import br.com.willian.model.PurchaseItem; // Entidade PurchaseItem
import br.com.willian.model.enums.PurchaseStatus; // Enum de status da compra
import br.com.willian.repository.IngredientRepository;
import br.com.willian.repository.PurchaseItemRepository; // Repository de itens da compra
import br.com.willian.repository.PurchaseRepository; // Repository de compras
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo; // Método estático para links
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn; // Método estático para controllers

@Service // Define a classe como um serviço Spring
public class PurchaseService {

    // Logger para rastreamento
    private static Logger logger = LoggerFactory.getLogger(PurchaseService.class.getName());

    @Autowired // Injeta montador de recursos paginados
    private PagedResourcesAssembler<PurchaseDTO> assembler;

    @Autowired // Injeta repository de compras
    private PurchaseRepository purchaseRepository;

    @Autowired // Injeta repository de itens da compra
    private PurchaseItemRepository purchaseItemRepository;

    @Autowired // Injeta repository de produtos
    private IngredientRepository ingredientRepository;

    @Autowired // Injeta mapper de compras
    private PurchaseMapper purchaseMapper;

    @Autowired // Injeta mapper de produtos
    private IngredientMapper ingredientMapper;
    @Autowired // Injeta mapper de produtos
    private PurchaseItemMapper purchaseItemMapper;

    @Autowired // Injeta factory de importadores
    private FileImporterFactory importer;

    @Autowired // Injeta factory de exportadores
    private FileExporterFactory exporter;

    // [PUR-SRV-001] Recupera lista paginada de compras
    public PagedModel<EntityModel<PurchaseDTO>> findAll(Pageable pageable) {
        logger.info("[PUR-SRV-001] Finding All Purchases - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize()); // Log da requisição

        var purchases = purchaseRepository.findAll(pageable); // Busca paginada

        logger.debug("[PUR-SRV-001] Found {} purchases in page {} of {}",
                purchases.getNumberOfElements(), purchases.getNumber() + 1, purchases.getTotalPages()); // Log do resultado

        return buildPageModel(pageable, purchases); // Retorna modelo paginado com HATEOAS
    }

    // [PUR-SRV-002] Recupera compra por ID com seus itens
    public PurchaseDTO findById(Long id) {
        logger.info("[PUR-SRV-002] Finding one Purchase with ID: {}", id); // Log da busca

        var entityLoaded = purchaseRepository.findById(id) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[PUR-SRV-002] Purchase with ID {} not found", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        var dtoLoaded = purchaseMapper.toDTO(entityLoaded); // Converte para DTO

        // Carrega itens da compra
        var items = purchaseItemRepository.findByPurchaseId(id);
        dtoLoaded.setItems(items.stream().map(item -> {
            var dto = purchaseItemMapper.toDTO(item); // Converte item para DTO

            var ingredientId = item.getIngredientId();
            if (ingredientId != null) {
                ingredientRepository.findById(ingredientId) // Busca produto
                        .map(ingredientMapper::toDTO) // Converte para DTO
                        .ifPresent(dto::setIngredient); // Adiciona ao item
            }

            return dto;
        }).toList());

        logger.debug("[PUR-SRV-002] Purchase found: ID {}", entityLoaded.getId()); // Log da compra encontrada

        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [PUR-SRV-003] Recupera compras por ID do fornecedor (paginado)
    public PagedModel<EntityModel<PurchaseDTO>> findBySupplierId(Long supplierId, Pageable pageable) {
        logger.info("[PUR-SRV-003] Finding Purchases by supplier ID: {} - page: {}, size: {}",
                supplierId, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var purchases = purchaseRepository.findBySupplierId(supplierId, pageable); // Busca por fornecedor

        logger.debug("[PUR-SRV-003] Found {} purchases for supplier {}", purchases.getNumberOfElements(), supplierId); // Log do resultado

        return buildPageModel(pageable, purchases); // Retorna modelo paginado
    }

    // [PUR-SRV-004] Recupera compras por ID do funcionário (paginado)
    public PagedModel<EntityModel<PurchaseDTO>> findByEmployeeId(Long employeeId, Pageable pageable) {
        logger.info("[PUR-SRV-004] Finding Purchases by employee ID: {} - page: {}, size: {}",
                employeeId, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var purchases = purchaseRepository.findByEmployeeId(employeeId, pageable); // Busca por funcionário

        logger.debug("[PUR-SRV-004] Found {} purchases for employee {}", purchases.getNumberOfElements(), employeeId); // Log do resultado

        return buildPageModel(pageable, purchases); // Retorna modelo paginado
    }

    // [PUR-SRV-005] Recupera compras por status (paginado)
    public PagedModel<EntityModel<PurchaseDTO>> findByStatus(PurchaseStatus status, Pageable pageable) {
        logger.info("[PUR-SRV-005] Finding Purchases by status: {} - page: {}, size: {}",
                status, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var purchases = purchaseRepository.findByStatus(status, pageable); // Busca por status

        logger.debug("[PUR-SRV-005] Found {} purchases with status {}", purchases.getNumberOfElements(), status); // Log do resultado

        return buildPageModel(pageable, purchases); // Retorna modelo paginado
    }

    // [PUR-SRV-006] Cria uma nova compra e atualiza estoque
    @Transactional // Método executado dentro de transação
    public PurchaseDTO create(PurchaseDTO purchaseDTO) {
        if (purchaseDTO == null) {
            logger.error("[PUR-SRV-006] Attempted to create null purchase"); // Log de erro
            throw new RequiredObjectIsNullException(); // Exceção
        }
        logger.info("[PUR-SRV-006] Creating one Purchase for supplier ID: {}", purchaseDTO.getSupplierId()); // Log da criação

        // Cria entidade Purchase
        Purchase purchase = new Purchase();
        purchase.setSupplierId(purchaseDTO.getSupplierId());
        purchase.setEmployeeId(purchaseDTO.getEmployeeId());
        purchase.setStatus(PurchaseStatus.PENDING); // Status inicial
        purchase.setPurchaseDate(Instant.now()); // Data da compra

        Double totalAmount = 0.0; // Acumulador do valor total

        // Calcula valor total da compra
        for (PurchaseItemDTO itemDTO : purchaseDTO.getItems()) {
            Ingredient ingredient = ingredientRepository.findById(itemDTO.getIngredientId()) // Busca produto
                    .orElseThrow(() -> {
                        logger.error("[PUR-SRV-006] Ingredient not found with ID: {}", itemDTO.getIngredientId()); // Log de erro
                        return new ResourceNotFoundException("Ingredient not found with id: " + itemDTO.getIngredientId()); // Exceção
                    });

            totalAmount += itemDTO.getQuantity() * itemDTO.getUnitPrice(); // Acumula valor
        }

        purchase.setTotalAmount(totalAmount); // Define valor total
        purchase = purchaseRepository.save(purchase); // Salva compra

        // Salva itens e atualiza estoque
        for (PurchaseItemDTO itemDTO : purchaseDTO.getItems()) {
            PurchaseItem item = new PurchaseItem();
            item.setPurchaseId(purchase.getId()); // Associa à compra
            item.setIngredientId(itemDTO.getIngredientId());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice()); // Preço congelado
            purchaseItemRepository.save(item); // Salva item

            // Atualiza estoque do produto (adiciona quantidade comprada)
            Ingredient ingredient = ingredientRepository.findById(itemDTO.getIngredientId()).get(); // Busca produto
            ingredient.setStockQuantity(ingredient.getStockQuantity() + itemDTO.getQuantity()); // Incrementa estoque
            ingredientRepository.save(ingredient); // Salva produto
        }

        var dtoPersisted = purchaseMapper.toDTO(purchase); // Converte para DTO
        var items = purchaseItemRepository.findByPurchaseId(purchase.getId()); // Busca itens salvos
        dtoPersisted.setItems(purchaseItemMapper.toDTOList(items)); // Adiciona list itens ao DTO


        logger.debug("[PUR-SRV-006] Purchase created successfully with ID: {}", dtoPersisted.getId()); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO criado
    }

    // [PUR-SRV-007] Atualiza o status de uma compra
    @Transactional // Método executado dentro de transação
    public PurchaseDTO updateStatus(Long id, PurchaseStatus status) {
        logger.info("[PUR-SRV-007] Updating status for Purchase ID: {} to {}", id, status); // Log da atualização

        Purchase purchase = purchaseRepository.findById(id) // Busca compra
                .orElseThrow(() -> {
                    logger.warn("[PUR-SRV-007] Purchase with ID {} not found for status update", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        purchase.setStatus(status); // Atualiza status

        Purchase entityPersisted = purchaseRepository.save(purchase); // Salva alterações
        var dtoPersisted = purchaseMapper.toDTO(entityPersisted); // Converte para DTO
        var items = purchaseItemRepository.findByPurchaseId(id); // Busca itens
        dtoPersisted.setItems(purchaseItemMapper.toDTOList(items)); // Adiciona List itens ao DTO

        logger.debug("[PUR-SRV-007] Purchase ID {} status updated to {}", id, status); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO atualizado
    }

    // [PUR-SRV-008] Remove uma compra e seus itens
    public void delete(Long id) {
        logger.info("[PUR-SRV-008] Deleting one Purchase with ID: {}", id); // Log da deleção

        Purchase entityLoaded = purchaseRepository.findById(id) // Busca compra
                .orElseThrow(() -> {
                    logger.warn("[PUR-SRV-008] Purchase with ID {} not found for deletion", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        purchaseItemRepository.deleteByPurchaseId(id); // Remove itens primeiro (FK)
        purchaseRepository.delete(entityLoaded); // Remove compra

        logger.debug("[PUR-SRV-008] Purchase ID {} deleted successfully", id); // Log de sucesso
    }

    // [PUR-SRV-INTERNAL-001] Adiciona links HATEOAS ao DTO
    private void addHateoasLinks(PurchaseDTO purchaseDTO) {
        logger.trace("[PUR-SRV-INTERNAL-001] Adding HATEOAS links for purchase ID: {}", purchaseDTO.getId());

        purchaseDTO.add(linkTo(methodOn(PurchaseController.class)
                .findById(purchaseDTO.getId())).withSelfRel().withType("GET"));

        purchaseDTO.add(linkTo(methodOn(PurchaseController.class)
                .findAll(1, 12, "asc")).withRel("findAll").withType("GET"));

        purchaseDTO.add(linkTo(methodOn(PurchaseController.class)
                .create(purchaseDTO)).withRel("create").withType("POST"));

        purchaseDTO.add(linkTo(methodOn(PurchaseController.class)
                .updateStatus(purchaseDTO.getId(), purchaseDTO.getStatus())).withRel("updateStatus").withType("PATCH"));

        purchaseDTO.add(linkTo(methodOn(PurchaseController.class)
                .delete(purchaseDTO.getId())).withRel("delete").withType("DELETE"));
    }

    // [PUR-SRV-INTERNAL-002] Constrói modelo paginado com HATEOAS
    private PagedModel<EntityModel<PurchaseDTO>> buildPageModel(
            Pageable pageable,
            Page<Purchase> purchases) {
        logger.trace("[PUR-SRV-INTERNAL-002] Building page model for page {} with {} elements",
                pageable.getPageNumber(), purchases.getNumberOfElements());

        Page<PurchaseDTO> purchaseWithLinks = purchaseMapper.toDTOPage(purchases); // Converte página para DTO
        purchaseWithLinks.forEach(this::addHateoasLinks); // Adiciona links a cada item

        Link findAllLink = linkTo( // Cria link para a própria consulta
                methodOn(PurchaseController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort())
                        )
        ).withSelfRel();

        return assembler.toModel(purchaseWithLinks, findAllLink); // Retorna modelo paginado
    }

}