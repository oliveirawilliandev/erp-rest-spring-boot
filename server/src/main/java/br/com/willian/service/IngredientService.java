package br.com.willian.service;

import br.com.willian.controller.IngredientController;
import br.com.willian.dto.v1.IngredientDTO;
import br.com.willian.exception.BadRequestException;
import br.com.willian.exception.RequiredObjectIsNullException;
import br.com.willian.exception.ResourceNotFoundException;
import br.com.willian.mapper.IngredientMapper;
import br.com.willian.model.Ingredient;
import br.com.willian.repository.IngredientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class IngredientService {

    private static Logger logger = LoggerFactory.getLogger(IngredientService.class.getName());

    @Autowired
    private PagedResourcesAssembler<IngredientDTO> assembler;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientMapper ingredientMapper;

    /**
     * [ING-SRV-001] Recupera lista paginada de insumos
     */
    public PagedModel<EntityModel<IngredientDTO>> findAll(Pageable pageable) {
        logger.info("[ING-SRV-001] Finding All Ingredients - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        var ingredients = ingredientRepository.findAll(pageable);

        logger.debug("[ING-SRV-001] Found {} ingredients in page {} of {}",
                ingredients.getNumberOfElements(), ingredients.getNumber() + 1, ingredients.getTotalPages());

        return buildPageModel(pageable, ingredients);
    }

    /**
     * [ING-SRV-002] Recupera insumos paginados por nome
     */
    public PagedModel<EntityModel<IngredientDTO>> findByName(String name, Pageable pageable) {
        logger.info("[ING-SRV-002] Finding Ingredients by name: '{}' - page: {}, size: {}",
                name, pageable.getPageNumber(), pageable.getPageSize());

        var ingredients = ingredientRepository.findByNameContainingIgnoreCase(name, pageable);

        logger.debug("[ING-SRV-002] Found {} results for name '{}'", ingredients.getNumberOfElements(), name);

        return buildPageModel(pageable, ingredients);
    }

    /**
     * [ING-SRV-003] Recupera insumo por ID
     */
    public IngredientDTO findById(Long id) {
        logger.info("[ING-SRV-003] Finding one Ingredient with ID: {}", id);

        var entityLoaded = ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("[ING-SRV-003] Ingredient with ID {} not found", id);
                    return new ResourceNotFoundException("no records found for this ID");
                });

        var dtoLoaded = ingredientMapper.toDTO(entityLoaded);
        logger.debug("[ING-SRV-003] Ingredient found: {}", entityLoaded.getName());

        addHateoasLinks(dtoLoaded);
        return dtoLoaded;
    }

    /**
     * [ING-SRV-004] Cria um novo insumo
     */
    public IngredientDTO create(IngredientDTO ingredientDTO) {
        if (ingredientDTO == null) {
            logger.error("[ING-SRV-004] Attempted to create null ingredient");
            throw new RequiredObjectIsNullException();
        }
        logger.info("[ING-SRV-004] Creating one Ingredient with name: {}", ingredientDTO.getName());

        validateIngredientName(ingredientDTO.getName());
        validateStockAndPrice(ingredientDTO);

        ingredientDTO.setCreatedAt(OffsetDateTime.now());
        ingredientDTO.setUpdatedAt(OffsetDateTime.now());
        ingredientDTO.setActive(true);

        var entity = ingredientMapper.toEntity(ingredientDTO);
        var entityPersisted = ingredientRepository.save(entity);
        var dtoPersisted = ingredientMapper.toDTO(entityPersisted);

        logger.debug("[ING-SRV-004] Ingredient created successfully with ID: {}", dtoPersisted.getId());

        addHateoasLinks(dtoPersisted);
        return dtoPersisted;
    }

    /**
     * [ING-SRV-005] Remove um insumo por ID
     */
    public void delete(Long id) {
        logger.info("[ING-SRV-005] Deleting one Ingredient with ID: {}", id);

        Ingredient entityLoaded = ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("[ING-SRV-005] Ingredient with ID {} not found for deletion", id);
                    return new ResourceNotFoundException("no records found for this ID");
                });

        ingredientRepository.delete(entityLoaded);
        logger.debug("[ING-SRV-005] Ingredient ID {} deleted successfully", id);
    }

    /**
     * [ING-SRV-006] Atualiza um insumo existente
     */
    public IngredientDTO update(IngredientDTO ingredientDTO) {
        if (ingredientDTO == null) {
            logger.error("[ING-SRV-006] Attempted to update null ingredient");
            throw new RequiredObjectIsNullException();
        }

        logger.info("[ING-SRV-006] Updating one Ingredient with ID: {}", ingredientDTO.getId());

        Ingredient entityLoaded = ingredientRepository.findById(ingredientDTO.getId())
                .orElseThrow(() -> {
                    logger.warn("[ING-SRV-006] Ingredient with ID {} not found for update", ingredientDTO.getId());
                    return new ResourceNotFoundException("No records found for this ID");
                });

        logger.debug("[ING-SRV-006] Updating ingredient: {}", entityLoaded.getName());

        // Verifica se o nome foi alterado e se já existe
        if (!entityLoaded.getName().equals(ingredientDTO.getName())) {
            validateIngredientName(ingredientDTO.getName());
        }

        validateStockAndPrice(ingredientDTO);

        // Atualiza campos
        entityLoaded.setName(ingredientDTO.getName());
        entityLoaded.setDescription(ingredientDTO.getDescription());
        entityLoaded.setPurchasePrice(ingredientDTO.getPurchasePrice());
        entityLoaded.setStockQuantity(ingredientDTO.getStockQuantity());
        entityLoaded.setMinimumStock(ingredientDTO.getMinimumStock());
        entityLoaded.setUnitOfMeasure(ingredientDTO.getUnitOfMeasure());
        entityLoaded.setActive(ingredientDTO.getActive());
        entityLoaded.setUpdatedAt(Instant.now());

        //verificação
        if(ingredientDTO.getPhotoUrl() != null && !ingredientDTO.getPhotoUrl().isBlank()) {entityLoaded.setPhotoUrl(ingredientDTO.getPhotoUrl());}
        if(ingredientDTO.getQrCode() != null && !ingredientDTO.getQrCode().isBlank()) {entityLoaded.setQrCode(ingredientDTO.getQrCode());}
        if(ingredientDTO.getBarCode() != null && !ingredientDTO.getBarCode().isBlank()) {entityLoaded.setQrCode(ingredientDTO.getBarCode());}


        // Atualiza fornecedor preferencial se informado
        if (ingredientDTO.getPreferredSupplierId() != null) {
            var supplier = new br.com.willian.model.Supplier();
            supplier.setId(ingredientDTO.getPreferredSupplierId());
            entityLoaded.setPreferredSupplier(supplier);
        }

        Ingredient entityPersisted = ingredientRepository.save(entityLoaded);
        IngredientDTO dtoPersisted = ingredientMapper.toDTO(entityPersisted);

        logger.debug("[ING-SRV-006] Ingredient ID {} updated successfully", ingredientDTO.getId());

        addHateoasLinks(dtoPersisted);
        return dtoPersisted;
    }

    /**
     * [ING-SRV-007] Atualiza a quantidade em estoque
     */
    @Transactional
    public IngredientDTO updateStock(Long id, Integer quantity) {
        logger.info("[ING-SRV-007] Updating stock for Ingredient ID: {}, Quantity: {}", id, quantity);

        if (quantity < 0) {
            logger.error("[ING-SRV-007] Invalid quantity: {} (must be >= 0)", quantity);
            throw new BadRequestException("Stock quantity cannot be negative");
        }

        ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("[ING-SRV-007] Ingredient with ID {} not found for stock update", id);
                    return new ResourceNotFoundException("no records found for this ID");
                });

        ingredientRepository.updateStock(id, quantity);

        var ingredientEntity = ingredientRepository.findById(id).get();
        var ingredientDTO = ingredientMapper.toDTO(ingredientEntity);

        // Verifica se estoque está baixo e loga alerta
        if (ingredientEntity.getStockQuantity() < ingredientEntity.getMinimumStock()) {
            logger.warn("[ING-SRV-007] Low stock alert! Ingredient ID: {}. Stock: {}, Minimum: {}",
                    id, ingredientEntity.getStockQuantity(), ingredientEntity.getMinimumStock());
        }

        logger.debug("[ING-SRV-007] Stock updated for Ingredient ID: {}. New stock: {}",
                id, ingredientEntity.getStockQuantity());

        addHateoasLinks(ingredientDTO);
        return ingredientDTO;
    }

    /**
     * [ING-SRV-008] Ativa um insumo
     */
    @Transactional
    public IngredientDTO activateIngredient(Long id) {
        logger.info("[ING-SRV-008] Activating Ingredient with ID: {}", id);

        ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("[ING-SRV-008] Ingredient with ID {} not found for activation", id);
                    return new ResourceNotFoundException("no records found for this ID");
                });

        ingredientRepository.activateIngredient(id);

        var ingredientEntity = ingredientRepository.findById(id).get();
        var ingredientDTO = ingredientMapper.toDTO(ingredientEntity);

        logger.debug("[ING-SRV-008] Ingredient ID {} activated successfully", id);

        addHateoasLinks(ingredientDTO);
        return ingredientDTO;
    }

    /**
     * [ING-SRV-009] Desativa um insumo
     */
    @Transactional
    public IngredientDTO deactivateIngredient(Long id) {
        logger.info("[ING-SRV-009] Deactivating Ingredient with ID: {}", id);

        ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("[ING-SRV-009] Ingredient with ID {} not found for deactivation", id);
                    return new ResourceNotFoundException("no records found for this ID");
                });

        ingredientRepository.deactivateIngredient(id);

        var ingredientEntity = ingredientRepository.findById(id).get();
        var ingredientDTO = ingredientMapper.toDTO(ingredientEntity);

        logger.debug("[ING-SRV-009] Ingredient ID {} deactivated successfully", id);

        addHateoasLinks(ingredientDTO);
        return ingredientDTO;
    }

    /**
     * [ING-SRV-010] Recupera insumos por status ativo/inativo
     */
    public PagedModel<EntityModel<IngredientDTO>> findByActive(Boolean active, Pageable pageable) {
        logger.info("[ING-SRV-010] Finding Ingredients by active status: {} - page: {}, size: {}",
                active, pageable.getPageNumber(), pageable.getPageSize());

        var ingredients = ingredientRepository.findByActive(active, pageable);

        return buildPageModel(pageable, ingredients);
    }

    /**
     * [ING-SRV-011] Recupera insumos com estoque abaixo do mínimo
     */
    public PagedModel<EntityModel<IngredientDTO>> findLowStock(Pageable pageable) {
        logger.info("[ING-SRV-011] Finding Ingredients with stock below minimum - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        var ingredients = ingredientRepository.findLowStock(pageable);

        logger.warn("[ING-SRV-011] Found {} ingredients with low stock", ingredients.getNumberOfElements());

        return buildPageModel(pageable, ingredients);
    }

    /**
     * [ING-SRV-012] Recupera insumos com estoque abaixo do limite informado
     */
    public PagedModel<EntityModel<IngredientDTO>> findLowStockByThreshold(Integer threshold, Pageable pageable) {
        logger.info("[ING-SRV-012] Finding Ingredients with stock below {} - page: {}, size: {}",
                threshold, pageable.getPageNumber(), pageable.getPageSize());

        var ingredients = ingredientRepository.findByStockQuantityLessThan(threshold, pageable);

        return buildPageModel(pageable, ingredients);
    }

    /**
     * [ING-SRV-013] Recupera insumos por fornecedor preferencial
     */
    public PagedModel<EntityModel<IngredientDTO>> findBySupplier(Long supplierId, Pageable pageable) {
        logger.info("[ING-SRV-013] Finding Ingredients by supplier ID: {} - page: {}, size: {}",
                supplierId, pageable.getPageNumber(), pageable.getPageSize());

        var ingredients = ingredientRepository.findByPreferredSupplierId(supplierId, pageable);

        return buildPageModel(pageable, ingredients);
    }

    /**
     * [ING-SRV-014] Recupera insumos com estoque crítico (muito abaixo do mínimo)
     */
    public PagedModel<EntityModel<IngredientDTO>> findCriticalStock(Pageable pageable) {
        logger.info("[ING-SRV-014] Finding Ingredients with critical stock - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        var ingredients = ingredientRepository.findCriticalStock(pageable);

        if (ingredients.hasContent()) {
            logger.warn("[ING-SRV-014] Found {} ingredients with critical stock", ingredients.getNumberOfElements());
        }

        return buildPageModel(pageable, ingredients);
    }

    /**
     * [ING-SRV-INTERNAL-001] Adiciona links HATEOAS ao DTO
     */
    private void addHateoasLinks(IngredientDTO ingredientDTO) {
        logger.trace("[ING-SRV-INTERNAL-001] Adding HATEOAS links for ingredient ID: {}", ingredientDTO.getId());

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .findById(ingredientDTO.getId())).withSelfRel().withType("GET"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .findAll(1, 12, "asc")).withRel("findAll").withType("GET"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .findByName("", 1, 11, "asc")).withRel("findByName").withType("GET"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .create(ingredientDTO)).withRel("create").withType("POST"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .update(ingredientDTO)).withRel("update").withType("PUT"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .updateStock(ingredientDTO.getId(), null)).withRel("updateStock").withType("PATCH"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .activateIngredient(ingredientDTO.getId())).withRel("activate").withType("PATCH"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .deactivateIngredient(ingredientDTO.getId())).withRel("deactivate").withType("PATCH"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .delete(ingredientDTO.getId())).withRel("delete").withType("DELETE"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .findLowStock(1, 12, "asc")).withRel("lowStock").withType("GET"));

        ingredientDTO.add(linkTo(methodOn(IngredientController.class)
                .findCriticalStock(1, 12, "asc")).withRel("criticalStock").withType("GET"));
    }

    /**
     * [ING-SRV-INTERNAL-002] Constrói modelo paginado com HATEOAS
     */
    private PagedModel<EntityModel<IngredientDTO>> buildPageModel(
            Pageable pageable,
            Page<Ingredient> ingredients) {
        logger.trace("[ING-SRV-INTERNAL-002] Building page model for page {} with {} elements",
                pageable.getPageNumber(), ingredients.getNumberOfElements());

        Page<IngredientDTO> ingredientWithLinks = ingredientMapper.toDTOPage(ingredients);
        ingredientWithLinks.forEach(this::addHateoasLinks);

        Link findAllLink = linkTo(
                methodOn(IngredientController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort())
                        )
        ).withSelfRel();

        return assembler.toModel(ingredientWithLinks, findAllLink);
    }

    /**
     * [ING-SRV-INTERNAL-003] Valida nome do insumo (único)
     */
    private void validateIngredientName(String name) {
        logger.trace("[ING-SRV-INTERNAL-003] Validating ingredient name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            logger.error("[ING-SRV-INTERNAL-003] Ingredient name validation failed: name is null or empty");
            throw new BadRequestException("Ingredient name cannot be null or empty");
        }

        if (ingredientRepository.existsByName(name)) {
            logger.error("[ING-SRV-INTERNAL-003] Ingredient name already exists: {}", name);
            throw new BadRequestException("Ingredient name already registered: " + name);
        }
    }

    /**
     * [ING-SRV-INTERNAL-004] Valida estoque e preço
     */
    private void validateStockAndPrice(IngredientDTO ingredientDTO) {
        logger.trace("[ING-SRV-INTERNAL-004] Validating stock and price for ingredient: {}", ingredientDTO.getName());

        if (ingredientDTO.getPurchasePrice() == null || ingredientDTO.getPurchasePrice() <= 0) {
            logger.error("[ING-SRV-INTERNAL-004] Purchase price validation failed: price must be positive");
            throw new BadRequestException("Purchase price must be greater than zero");
        }

        if (ingredientDTO.getStockQuantity() == null || ingredientDTO.getStockQuantity() < 0) {
            logger.error("[ING-SRV-INTERNAL-004] Stock quantity validation failed: stock cannot be negative");
            throw new BadRequestException("Stock quantity cannot be negative");
        }

        if (ingredientDTO.getMinimumStock() == null || ingredientDTO.getMinimumStock() < 0) {
            logger.error("[ING-SRV-INTERNAL-004] Minimum stock validation failed: minimum stock cannot be negative");
            throw new BadRequestException("Minimum stock cannot be negative");
        }

        if (ingredientDTO.getUnitOfMeasure() == null || ingredientDTO.getUnitOfMeasure().trim().isEmpty()) {
            logger.error("[ING-SRV-INTERNAL-004] Unit of measure validation failed: unit is required");
            throw new BadRequestException("Unit of measure is required");
        }
    }
}