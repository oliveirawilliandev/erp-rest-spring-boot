package br.com.willian.service; // Pacote da camada de serviço

import br.com.willian.controller.SupplierController; // Controller para links HATEOAS
import br.com.willian.dto.v1.SupplierDTO; // DTO de fornecedor
import br.com.willian.exception.*; // Exceções do sistema
import br.com.willian.file.exporter.factory.FileExporterFactory; // Factory de exportadores
import br.com.willian.file.importer.factory.FileImporterFactory; // Factory de importadores
import br.com.willian.mapper.SupplierMapper; // Mapper para conversão Supplier/SupplierDTO
import br.com.willian.model.Supplier; // Entidade Supplier
import br.com.willian.repository.SupplierRepository; // Repository de fornecedores
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
public class SupplierService {

    // Logger para rastreamento
    private static Logger logger = LoggerFactory.getLogger(SupplierService.class.getName());

    @Autowired // Injeta montador de recursos paginados
    private PagedResourcesAssembler<SupplierDTO> assembler;

    @Autowired // Injeta repository de fornecedores
    private SupplierRepository supplierRepository;

    @Autowired // Injeta mapper de fornecedores
    private SupplierMapper supplierMapper;

    @Autowired // Injeta factory de importadores
    private FileImporterFactory importer;

    @Autowired // Injeta factory de exportadores
    private FileExporterFactory exporter;

    // [SUPP-SRV-001] Recupera lista paginada de fornecedores
    public PagedModel<EntityModel<SupplierDTO>> findAll(Pageable pageable) {
        logger.info("[SUPP-SRV-001] Finding All Suppliers - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize()); // Log da requisição

        var suppliers = supplierRepository.findAll(pageable); // Busca paginada

        logger.debug("[SUPP-SRV-001] Found {} suppliers in page {} of {}",
                suppliers.getNumberOfElements(), suppliers.getNumber() + 1, suppliers.getTotalPages()); // Log do resultado

        return buildPageModel(pageable, suppliers); // Retorna modelo paginado com HATEOAS
    }

    // [SUPP-SRV-002] Recupera fornecedores paginados por nome
    public PagedModel<EntityModel<SupplierDTO>> findByName(String name, Pageable pageable) {
        logger.info("[SUPP-SRV-002] Finding Suppliers by name: '{}' - page: {}, size: {}",
                name, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var suppliers = supplierRepository.findByNameContainingIgnoreCase(name, pageable); // Busca por nome

        logger.debug("[SUPP-SRV-002] Found {} results for name '{}'", suppliers.getNumberOfElements(), name); // Log do resultado

        return buildPageModel(pageable, suppliers); // Retorna modelo paginado
    }

    // [SUPP-SRV-003] Recupera fornecedor por ID
    public SupplierDTO findById(Long id) {
        logger.info("[SUPP-SRV-003] Finding one Supplier with ID: {}", id); // Log da busca

        var entityLoaded = supplierRepository.findById(id) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[SUPP-SRV-003] Supplier with ID {} not found", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        var dtoLoaded = supplierMapper.toDTO(entityLoaded); // Converte para DTO
        logger.debug("[SUPP-SRV-003] Supplier found: {}", entityLoaded.getName()); // Log do fornecedor encontrado

        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [SUPP-SRV-004] Recupera fornecedor por documento (CNPJ/CPF)
    public SupplierDTO findByDocument(String document) {
        logger.info("[SUPP-SRV-004] Finding Supplier by document: {}", document); // Log da busca

        var entityLoaded = supplierRepository.findByDocument(document) // Busca por documento
                .orElseThrow(() -> {
                    logger.warn("[SUPP-SRV-004] Supplier with document {} not found", document); // Log de aviso
                    return new ResourceNotFoundException("no records found for this document"); // Exceção
                });

        var dtoLoaded = supplierMapper.toDTO(entityLoaded); // Converte para DTO
        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [SUPP-SRV-005] Cria um novo fornecedor
    public SupplierDTO create(SupplierDTO supplierDTO) {
        if (supplierDTO == null) {
            logger.error("[SUPP-SRV-005] Attempted to create null supplier"); // Log de erro
            throw new RequiredObjectIsNullException(); // Exceção
        }
        logger.info("[SUPP-SRV-005] Creating one Supplier with name: {}", supplierDTO.getName()); // Log da criação

        validateDocument(supplierDTO.getDocument()); // Valida documento único
        validateEmail(supplierDTO.getEmail()); // Valida email único (se informado)

        supplierDTO.setCreatedAt(OffsetDateTime.now()); // Data de criação
        supplierDTO.setUpdatedAt(OffsetDateTime.now()); // Data de atualização

        var entity = supplierMapper.toEntity(supplierDTO); // Converte para entidade
        var entityPersisted = supplierRepository.save(entity); // Persiste
        var dtoPersisted = supplierMapper.toDTO(entityPersisted); // Converte para DTO

        logger.debug("[SUPP-SRV-005] Supplier created successfully with ID: {}", dtoPersisted.getId()); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO criado
    }

    // [SUPP-SRV-006] Remove um fornecedor por ID
    public void delete(Long id) {
        logger.info("[SUPP-SRV-006] Deleting one Supplier with ID: {}", id); // Log da deleção

        Supplier entityLoaded = supplierRepository.findById(id) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[SUPP-SRV-006] Supplier with ID {} not found for deletion", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        supplierRepository.delete(entityLoaded); // Deleta
        logger.debug("[SUPP-SRV-006] Supplier ID {} deleted successfully", id); // Log de sucesso
    }

    // [SUPP-SRV-007] Atualiza um fornecedor existente
    public SupplierDTO update(SupplierDTO supplierDTO) {
        if (supplierDTO == null) {
            logger.error("[SUPP-SRV-007] Attempted to update null supplier"); // Log de erro
            throw new RequiredObjectIsNullException(); // Exceção
        }

        logger.info("[SUPP-SRV-007] Updating one Supplier with ID: {}", supplierDTO.getId()); // Log da atualização

        Supplier entityLoaded = supplierRepository.findById(supplierDTO.getId()) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[SUPP-SRV-007] Supplier with ID {} not found for update", supplierDTO.getId()); // Log de aviso
                    return new ResourceNotFoundException("No records found for this ID"); // Exceção
                });

        logger.debug("[SUPP-SRV-007] Updating supplier: {}", entityLoaded.getName()); // Log do fornecedor sendo atualizado

        // Atualiza campos
        entityLoaded.setName(supplierDTO.getName());
        entityLoaded.setDocument(supplierDTO.getDocument());
        entityLoaded.setEmail(supplierDTO.getEmail());
        entityLoaded.setPhone(supplierDTO.getPhone());
        entityLoaded.setZipCode(supplierDTO.getZipCode());
        entityLoaded.setStreet(supplierDTO.getStreet());
        entityLoaded.setStreetNumber(supplierDTO.getStreetNumber());
        entityLoaded.setAddressComplement(supplierDTO.getAddressComplement());
        entityLoaded.setNeighborhood(supplierDTO.getNeighborhood());
        entityLoaded.setCity(supplierDTO.getCity());
        entityLoaded.setState(supplierDTO.getState());
        entityLoaded.setActive(supplierDTO.getActive());
        entityLoaded.setUpdatedAt(Instant.now()); // Atualiza timestamp

        //verificação
        if(supplierDTO.getPhotoUrl() != null && !supplierDTO.getPhotoUrl().isBlank()) {entityLoaded.setPhotoUrl(supplierDTO.getPhotoUrl());}
        if(supplierDTO.getQrCode() != null && !supplierDTO.getQrCode().isBlank()) {entityLoaded.setQrCode(supplierDTO.getQrCode());}
        if(supplierDTO.getBarCode() != null && !supplierDTO.getBarCode().isBlank()) {entityLoaded.setQrCode(supplierDTO.getBarCode());}

        Supplier entityPersisted = supplierRepository.save(entityLoaded); // Salva
        SupplierDTO dtoPersisted = supplierMapper.toDTO(entityPersisted); // Converte para DTO

        logger.debug("[SUPP-SRV-007] Supplier ID {} updated successfully", supplierDTO.getId()); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO atualizado
    }

    // [SUPP-SRV-008] Ativa um fornecedor (status = true)
    @Transactional // Método executado dentro de transação
    public SupplierDTO activateSupplier(Long id) {
        logger.info("[SUPP-SRV-008] Activating Supplier with ID: {}", id); // Log da ativação

        supplierRepository.findById(id) // Verifica existência
                .orElseThrow(() -> {
                    logger.warn("[SUPP-SRV-008] Supplier with ID {} not found for activation", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        supplierRepository.activateSupplier(id); // Ativa fornecedor

        var supplierEntity = supplierRepository.findById(id).get(); // Busca atualizado
        var supplierDTO = supplierMapper.toDTO(supplierEntity); // Converte para DTO

        logger.debug("[SUPP-SRV-008] Supplier ID {} activated successfully", id); // Log de sucesso

        addHateoasLinks(supplierDTO); // Adiciona links HATEOAS
        return supplierDTO; // Retorna DTO
    }

    // [SUPP-SRV-009] Desativa um fornecedor (status = false)
    @Transactional // Método executado dentro de transação
    public SupplierDTO deactivateSupplier(Long id) {
        logger.info("[SUPP-SRV-009] Deactivating Supplier with ID: {}", id); // Log da desativação

        supplierRepository.findById(id) // Verifica existência
                .orElseThrow(() -> {
                    logger.warn("[SUPP-SRV-009] Supplier with ID {} not found for deactivation", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        supplierRepository.deactivateSupplier(id); // Desativa fornecedor

        var supplierEntity = supplierRepository.findById(id).get(); // Busca atualizado
        var supplierDTO = supplierMapper.toDTO(supplierEntity); // Converte para DTO

        logger.debug("[SUPP-SRV-009] Supplier ID {} deactivated successfully", id); // Log de sucesso

        addHateoasLinks(supplierDTO); // Adiciona links HATEOAS
        return supplierDTO; // Retorna DTO
    }

    // [SUPP-SRV-INTERNAL-001] Adiciona links HATEOAS ao DTO
    private void addHateoasLinks(SupplierDTO supplierDTO) {
        logger.trace("[SUPP-SRV-INTERNAL-001] Adding HATEOAS links for supplier ID: {}", supplierDTO.getId());

        supplierDTO.add(linkTo(methodOn(SupplierController.class)
                .findById(supplierDTO.getId())).withSelfRel().withType("GET"));

        supplierDTO.add(linkTo(methodOn(SupplierController.class)
                .findAll(1, 12, "asc")).withRel("findAll").withType("GET"));

        supplierDTO.add(linkTo(methodOn(SupplierController.class)
                .findByName("", 1, 11, "asc")).withRel("findByName").withType("GET"));

        supplierDTO.add(linkTo(methodOn(SupplierController.class)
                .create(supplierDTO)).withRel("create").withType("POST"));

        supplierDTO.add(linkTo(methodOn(SupplierController.class)
                .update(supplierDTO)).withRel("update").withType("PUT"));

        supplierDTO.add(linkTo(methodOn(SupplierController.class)
                .activateSupplier(supplierDTO.getId())).withRel("activate").withType("PATCH"));

        supplierDTO.add(linkTo(methodOn(SupplierController.class)
                .deactivateSupplier(supplierDTO.getId())).withRel("deactivate").withType("PATCH"));

        supplierDTO.add(linkTo(methodOn(SupplierController.class)
                .delete(supplierDTO.getId())).withRel("delete").withType("DELETE"));
    }

    // [SUPP-SRV-INTERNAL-002] Constrói modelo paginado com HATEOAS
    private PagedModel<EntityModel<SupplierDTO>> buildPageModel(
            Pageable pageable,
            Page<Supplier> suppliers) {
        logger.trace("[SUPP-SRV-INTERNAL-002] Building page model for page {} with {} elements",
                pageable.getPageNumber(), suppliers.getNumberOfElements());

        Page<SupplierDTO> supplierWithLinks = supplierMapper.toDTOPage(suppliers); // Converte página para DTO
        supplierWithLinks.forEach(this::addHateoasLinks); // Adiciona links a cada item

        Link findAllLink = linkTo( // Cria link para a própria consulta
                methodOn(SupplierController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort())
                        )
        ).withSelfRel();

        return assembler.toModel(supplierWithLinks, findAllLink); // Retorna modelo paginado
    }

    // [SUPP-SRV-INTERNAL-003] Valida documento único (CNPJ/CPF)
    private void validateDocument(String document) {
        logger.trace("[SUPP-SRV-INTERNAL-003] Validating document: {}", document);

        if (document == null || document.trim().isEmpty()) {
            logger.error("[SUPP-SRV-INTERNAL-003] Document validation failed: document is null or empty");
            throw new BadRequestException("Document cannot be null or empty");
        }

        if (supplierRepository.existsByDocument(document)) {
            logger.error("[SUPP-SRV-INTERNAL-003] Document already exists: {}", document);
            throw new BadRequestException("Document already registered: " + document);
        }
    }

    // [SUPP-SRV-INTERNAL-004] Valida email único (opcional)
    private void validateEmail(String email) {
        logger.trace("[SUPP-SRV-INTERNAL-004] Validating email: {}", email);

        if (email != null && !email.trim().isEmpty()) {
            if (supplierRepository.existsByEmail(email)) {
                logger.error("[SUPP-SRV-INTERNAL-004] Email already exists: {}", email);
                throw new BadRequestException("Email already registered: " + email);
            }
        }
    }
}