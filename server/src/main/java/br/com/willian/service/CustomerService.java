package br.com.willian.service; // Pacote da camada de serviço

import br.com.willian.controller.CustomerController; // Controller para links HATEOAS
import br.com.willian.dto.v1.CustomerDTO; // DTO de cliente
import br.com.willian.exception.BadRequestException; // Exceção para requisição inválida
import br.com.willian.exception.RequiredObjectIsNullException; // Exceção para objeto nulo
import br.com.willian.exception.ResourceNotFoundException; // Exceção para recurso não encontrado
import br.com.willian.file.exporter.factory.FileExporterFactory; // Factory de exportadores
import br.com.willian.file.importer.factory.FileImporterFactory; // Factory de importadores
import br.com.willian.mapper.CustomerMapper; // Mapper para conversão Entity/DTO
import br.com.willian.model.Customer; // Entidade Customer
import br.com.willian.repository.CustomerRepository; // Repository para acesso ao banco
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
public class CustomerService {

    // Logger para rastreamento
    private static Logger logger = LoggerFactory.getLogger(CustomerService.class.getName());

    @Autowired // Injeta montador de recursos paginados
    private PagedResourcesAssembler<CustomerDTO> assembler;

    @Autowired // Injeta repository de clientes
    private CustomerRepository customerRepository;

    @Autowired // Injeta mapper de clientes
    private CustomerMapper customerMapper;

    @Autowired // Injeta factory de importadores
    private FileImporterFactory importer;

    @Autowired // Injeta factory de exportadores
    private FileExporterFactory exporter;

    // [CUST-SRV-001] Recupera lista paginada de clientes
    public PagedModel<EntityModel<CustomerDTO>> findAll(Pageable pageable) {
        logger.info("[CUST-SRV-001] Finding All Customers - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize()); // Log da requisição

        var customers = customerRepository.findAll(pageable); // Busca paginada

        logger.debug("[CUST-SRV-001] Found {} customers in page {} of {}",
                customers.getNumberOfElements(), customers.getNumber() + 1, customers.getTotalPages()); // Log do resultado

        return buildPageModel(pageable, customers); // Retorna modelo paginado com HATEOAS
    }

    // [CUST-SRV-002] Recupera clientes paginados por nome
    public PagedModel<EntityModel<CustomerDTO>> findByName(String name, Pageable pageable) {
        logger.info("[CUST-SRV-002] Finding Customers by name: '{}' - page: {}, size: {}",
                name, pageable.getPageNumber(), pageable.getPageSize()); // Log da busca

        var customers = customerRepository.findByNameContainingIgnoreCase(name, pageable); // Busca por nome

        logger.debug("[CUST-SRV-002] Found {} results for name '{}'", customers.getNumberOfElements(), name); // Log do resultado

        return buildPageModel(pageable, customers); // Retorna modelo paginado
    }

    // [CUST-SRV-003] Recupera cliente por ID
    public CustomerDTO findById(Long id) {
        logger.info("[CUST-SRV-003] Finding one Customer with ID: {}", id); // Log da busca

        var entityLoaded = customerRepository.findById(id) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[CUST-SRV-003] Customer with ID {} not found", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        var dtoLoaded = customerMapper.toDTO(entityLoaded); // Converte para DTO
        logger.debug("[CUST-SRV-003] Customer found: {}", entityLoaded.getName()); // Log do cliente encontrado

        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [CUST-SRV-004] Recupera cliente por email
    public CustomerDTO findByEmail(String email) {
        logger.info("[CUST-SRV-004] Finding Customer by email: {}", email); // Log da busca

        var entityLoaded = customerRepository.findByEmail(email) // Busca por email
                .orElseThrow(() -> {
                    logger.warn("[CUST-SRV-004] Customer with email {} not found", email); // Log de aviso
                    return new ResourceNotFoundException("no records found for this email"); // Exceção
                });

        var dtoLoaded = customerMapper.toDTO(entityLoaded); // Converte para DTO
        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [CUST-SRV-005] Recupera cliente por documento
    public CustomerDTO findByDocument(String document) {
        logger.info("[CUST-SRV-005] Finding Customer by document: {}", document); // Log da busca

        var entityLoaded = customerRepository.findByDocument(document) // Busca por documento
                .orElseThrow(() -> {
                    logger.warn("[CUST-SRV-005] Customer with document {} not found", document); // Log de aviso
                    return new ResourceNotFoundException("no records found for this document"); // Exceção
                });

        var dtoLoaded = customerMapper.toDTO(entityLoaded); // Converte para DTO
        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [CUST-SRV-006] Cria um novo cliente
    public CustomerDTO create(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            logger.error("[CUST-SRV-006] Attempted to create null customer"); // Log de erro
            throw new RequiredObjectIsNullException(); // Exceção
        }
        logger.info("[CUST-SRV-006] Creating one Customer with email: {}", customerDTO.getEmail()); // Log da criação

        validateDocument(customerDTO.getDocument()); // Valida documento único

        customerDTO.setCreatedAt(OffsetDateTime.now()); // Data de criação
        customerDTO.setUpdatedAt(OffsetDateTime.now()); // Data de atualização

        var entity = customerMapper.toEntity(customerDTO); // Converte para entidade
        var entityPersisted = customerRepository.save(entity); // Persiste
        var dtoPersisted = customerMapper.toDTO(entityPersisted); // Converte para DTO

        logger.debug("[CUST-SRV-006] Customer created successfully with ID: {}", dtoPersisted.getId()); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO criado
    }

    // [CUST-SRV-007] Remove um cliente por ID
    public void delete(Long id) {
        logger.info("[CUST-SRV-007] Deleting one Customer with ID: {}", id); // Log da deleção

        Customer entityLoaded = customerRepository.findById(id) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[CUST-SRV-007] Customer with ID {} not found for deletion", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        customerRepository.delete(entityLoaded); // Deleta
        logger.debug("[CUST-SRV-007] Customer ID {} deleted successfully", id); // Log de sucesso
    }

    // [CUST-SRV-008] Atualiza um cliente existente
    public CustomerDTO update(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            logger.error("[CUST-SRV-008] Attempted to update null customer"); // Log de erro
            throw new RequiredObjectIsNullException(); // Exceção
        }

        logger.info("[CUST-SRV-008] Updating one Customer with ID: {}", customerDTO.getId()); // Log da atualização

        Customer entityLoaded = customerRepository.findById(customerDTO.getId()) // Busca por ID
                .orElseThrow(() -> {
                    logger.warn("[CUST-SRV-008] Customer with ID {} not found for update", customerDTO.getId()); // Log de aviso
                    return new ResourceNotFoundException("No records found for this ID"); // Exceção
                });

        logger.debug("[CUST-SRV-008] Updating customer: {}", entityLoaded.getName()); // Log do cliente sendo atualizado

        // Atualiza campos
        entityLoaded.setName(customerDTO.getName());
        entityLoaded.setEmail(customerDTO.getEmail());
        entityLoaded.setPhone(customerDTO.getPhone());
        entityLoaded.setDocument(customerDTO.getDocument());
        entityLoaded.setZipCode(customerDTO.getZipCode());
        entityLoaded.setStreet(customerDTO.getStreet());
        entityLoaded.setStreetNumber(customerDTO.getStreetNumber());
        entityLoaded.setAddressComplement(customerDTO.getAddressComplement());
        entityLoaded.setNeighborhood(customerDTO.getNeighborhood());
        entityLoaded.setCity(customerDTO.getCity());
        entityLoaded.setState(customerDTO.getState());
        entityLoaded.setActive(customerDTO.getActive());



        entityLoaded.setUpdatedAt(Instant.now()); // Atualiza timestamp

        //Verificação
        if(customerDTO.getPhotoUrl() != null && !customerDTO.getPhotoUrl().isBlank()) {entityLoaded.setPhotoUrl(customerDTO.getPhotoUrl());}
        if(customerDTO.getQrCode() != null && !customerDTO.getQrCode().isBlank()) {entityLoaded.setQrCode(customerDTO.getQrCode());}
        if(customerDTO.getBarCode() != null && !customerDTO.getBarCode().isBlank()) {entityLoaded.setQrCode(customerDTO.getBarCode());}


        Customer entityPersisted = customerRepository.save(entityLoaded); // Salva
        CustomerDTO dtoPersisted = customerMapper.toDTO(entityPersisted); // Converte para DTO

        logger.debug("[CUST-SRV-008] Customer ID {} updated successfully", customerDTO.getId()); // Log de sucesso

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO atualizado
    }

    // [CUST-SRV-009] Ativa um cliente (status = true)
    @Transactional // Método executado dentro de transação
    public CustomerDTO activateCustomer(Long id) {
        logger.info("[CUST-SRV-009] Activating Customer with ID: {}", id); // Log da ativação

        customerRepository.findById(id) // Verifica existência
                .orElseThrow(() -> {
                    logger.warn("[CUST-SRV-009] Customer with ID {} not found for activation", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        customerRepository.activateCustomer(id); // Ativa cliente

        var customerEntity = customerRepository.findById(id).get(); // Busca atualizado
        var customerDTO = customerMapper.toDTO(customerEntity); // Converte para DTO

        logger.debug("[CUST-SRV-009] Customer ID {} activated successfully", id); // Log de sucesso

        addHateoasLinks(customerDTO); // Adiciona links HATEOAS
        return customerDTO; // Retorna DTO
    }

    // [CUST-SRV-010] Desativa um cliente (status = false)
    @Transactional // Método executado dentro de transação
    public CustomerDTO deactivateCustomer(Long id) {
        logger.info("[CUST-SRV-010] Deactivating Customer with ID: {}", id); // Log da desativação

        customerRepository.findById(id) // Verifica existência
                .orElseThrow(() -> {
                    logger.warn("[CUST-SRV-010] Customer with ID {} not found for deactivation", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        customerRepository.deactivateCustomer(id); // Desativa cliente

        var customerEntity = customerRepository.findById(id).get(); // Busca atualizado
        var customerDTO = customerMapper.toDTO(customerEntity); // Converte para DTO

        logger.debug("[CUST-SRV-010] Customer ID {} deactivated successfully", id); // Log de sucesso

        addHateoasLinks(customerDTO); // Adiciona links HATEOAS
        return customerDTO; // Retorna DTO
    }

    // [CUST-SRV-INTERNAL-001] Adiciona links HATEOAS ao DTO
    private void addHateoasLinks(CustomerDTO customerDTO) {
        logger.trace("[CUST-SRV-INTERNAL-001] Adding HATEOAS links for customer ID: {}", customerDTO.getId());

        customerDTO.add(linkTo(methodOn(CustomerController.class)
                .findById(customerDTO.getId())).withSelfRel().withType("GET"));

        customerDTO.add(linkTo(methodOn(CustomerController.class)
                .findAll(1, 12, "asc")).withRel("findAll").withType("GET"));

        customerDTO.add(linkTo(methodOn(CustomerController.class)
                .findByName("", 1, 11, "asc")).withRel("findByName").withType("GET"));

        customerDTO.add(linkTo(methodOn(CustomerController.class)
                .create(customerDTO)).withRel("create").withType("POST"));

        customerDTO.add(linkTo(methodOn(CustomerController.class)
                .update(customerDTO)).withRel("update").withType("PUT"));

        customerDTO.add(linkTo(methodOn(CustomerController.class)
                .activateCustomer(customerDTO.getId())).withRel("activate").withType("PATCH"));

        customerDTO.add(linkTo(methodOn(CustomerController.class)
                .deactivateCustomer(customerDTO.getId())).withRel("deactivate").withType("PATCH"));

        customerDTO.add(linkTo(methodOn(CustomerController.class)
                .delete(customerDTO.getId())).withRel("delete").withType("DELETE"));

    }

    // [CUST-SRV-INTERNAL-002] Constrói modelo paginado com HATEOAS
    private PagedModel<EntityModel<CustomerDTO>> buildPageModel(
            Pageable pageable,
            Page<Customer> customers) {
        logger.trace("[CUST-SRV-INTERNAL-002] Building page model for page {} with {} elements",
                pageable.getPageNumber(), customers.getNumberOfElements());

        Page<CustomerDTO> customerWithLinks = customerMapper.toDTOPage(customers); // Converte página para DTO
        customerWithLinks.forEach(this::addHateoasLinks); // Adiciona links a cada item

        Link findAllLink = linkTo( // Cria link para a própria consulta
                methodOn(CustomerController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort())
                        )
        ).withSelfRel();

        return assembler.toModel(customerWithLinks, findAllLink); // Retorna modelo paginado
    }

    // [CUST-SRV-INTERNAL-003] Valida documento único
    private void validateDocument(String document) {
        logger.trace("[CUST-SRV-INTERNAL-003] Validating document: {}", document);

        if (document == null || document.trim().isEmpty()) {
            logger.error("[CUST-SRV-INTERNAL-003] Document validation failed: document is null or empty");
            throw new BadRequestException("Email inválido");
        }

        if (customerRepository.existsByDocument(document)) {
            logger.error("[CUST-SRV-INTERNAL-003] Document already exists: {}", document);
            throw new BadRequestException("Document already registered: " + document);
        }
    }


}