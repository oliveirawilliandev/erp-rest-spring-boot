package br.com.willian.service; // Pacote da camada de serviço

import br.com.willian.controller.EmployeeController; // Controller para referências HATEOAS
import br.com.willian.dto.v1.EmployeeDTO;
import br.com.willian.exception.*; // Exceções personalizadas do sistema
import br.com.willian.file.exporter.contract.EmployeesExporter; // Contrato para exportadores
import br.com.willian.file.exporter.factory.FileExporterFactory; // Factory para selecionar exportador
import br.com.willian.file.importer.contract.FileImporter; // Contrato para importadores
import br.com.willian.file.importer.factory.FileImporterFactory; // Factory para selecionar importador
import br.com.willian.mapper.EmployeeMapper;
import br.com.willian.model.Employee;
import br.com.willian.model.enums.GenderType; // Enum de gêneros válidos
import br.com.willian.repository.EmployeeRepository; // Repository para acesso ao banco
import org.slf4j.Logger; // Interface de logging
import org.slf4j.LoggerFactory; // Factory para logger
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.core.io.Resource; // Recurso para download
import org.springframework.data.domain.Page; // Página de resultados
import org.springframework.data.domain.Pageable; // Configuração de paginação
import org.springframework.data.web.PagedResourcesAssembler; // Montador de recursos paginados
import org.springframework.stereotype.Service; // Annotation de serviço Spring

import java.io.InputStream; // Stream para leitura de arquivos
import java.time.*; // Classes de data e hora
import java.util.Arrays; // Utilitário para arrays
import java.util.List; // Interface List
import java.util.Optional; // Container opcional
import java.util.stream.Collectors; // Utilitário para streams

import org.springframework.hateoas.EntityModel; // Wrapper HATEOAS para entidades
import org.springframework.hateoas.Link; // Link HATEOAS
import org.springframework.hateoas.PagedModel; // Modelo HATEOAS paginado
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder; // Builder de links
import org.springframework.transaction.annotation.Transactional; // Controle transacional
import org.springframework.web.multipart.MultipartFile; // Arquivo multipart upload

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo; // Método estático para links
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn; // Método estático para controllers

@Service // Define a classe como um serviço Spring
public class EmployeeService { // Serviço para operações com funcionários


    private static Logger logger = LoggerFactory.getLogger(EmployeeService.class.getName()); // Logger para rastreamento

    @Autowired // Injeta dependência do montador de recursos paginados
    private PagedResourcesAssembler<EmployeeDTO> assembler; // Montador de modelos paginados HATEOAS

    @Autowired // Injeta dependência do repository
    private EmployeeRepository employeesRepository; // Repository para acesso aos dados

    @Autowired // Injeta dependência do mapper
    private EmployeeMapper employeeMapper; // Mapper para conversão entre entidade e DTO

    @Autowired // Injeta dependência da factory de importadores
    private FileImporterFactory importer; // Factory para criar importadores baseado no arquivo

    @Autowired // Injeta dependência da factory de exportadores
    FileExporterFactory exporter; // Factory para criar exportadores baseado no header Accept

    // [SERVICE-TRACE: EMP-SRV-001]
    // Recupera uma lista paginada de registros
    public PagedModel<EntityModel<EmployeeDTO>> findAll(Pageable pageable) { // Método para buscar todos com paginação
        logger.info("[EMP-SRV-001] Finding All Employee - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize()); // Log com parâmetros de paginação
        var employee = employeesRepository.findAll(pageable); // Busca todos funcionários paginados no banco
        logger.debug("[EMP-SRV-001] Found {} employees in page {} of {}", employee.getNumberOfElements(), employee.getNumber() + 1, employee.getTotalPages()); // Log detalhado dos resultados
        return buildPageModel(pageable, employee); // Constrói modelo paginado com HATEOAS e retorna
    }

    // [SERVICE-TRACE: EMP-SRV-002]
    // Recupera registros paginados com base em um critério de busca
    public PagedModel<EntityModel<EmployeeDTO>> findByName(String name, Pageable pageable) { // Busca por nome paginada
        logger.info("[EMP-SRV-002] Finding People by name: '{}' - page: {}, size: {}", name, pageable.getPageNumber(), pageable.getPageSize()); // Log com nome e paginação
        var employee = employeesRepository.findEmployeesByName(name, pageable); // Executa query de busca no repository
        logger.debug("[EMP-SRV-002] Found {} results for name '{}'", employee.getNumberOfElements(), name); // Log da quantidade de resultados
        return buildPageModel(pageable, employee); // Constrói e retorna modelo paginado com HATEOAS
    }

    // [SERVICE-TRACE: EMP-SRV-003]
    // Recupera um único registro pelo identificador
    public EmployeeDTO findById(Long id) {
        logger.info("[EMP-SRV-003] Finding one Employee with ID: {}", id); // Log com ID buscado

        var entityLoaded = employeesRepository.findById(id) // Busca entidade pelo ID no banco
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-003] Employee with ID {} not found", id); // Log de warning quando não encontra
                    return new ResourceNotFoundException("no records found for this ID"); // Lança exceção se não encontrar
                });

        var dtoLoaded = employeeMapper.toDTO(entityLoaded); // Converte entidade para DTO
        logger.debug("[EMP-SRV-003] Employee found: {} {}", entityLoaded.getFirstName(), entityLoaded.getLastName()); // Log do nome do funcionário encontrado

        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS ao DTO
        return dtoLoaded; // Retorna DTO com links
    }

    // [SERVICE-TRACE: EMP-SRV-004]
    // Cria e persiste um novo registro
    public EmployeeDTO create(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            logger.error("[EMP-SRV-004] Attempted to create null employee"); // Log de erro para DTO nulo
            throw new RequiredObjectIsNullException(); // Valida se DTO não é nulo
        }
        logger.info("[EMP-SRV-004] Creating one Employee with email: {}", employeeDTO.getEmail()); // Log com email do funcionário
        validateCpf(employeeDTO.getCpf()); // Valida documento único
        validateEmail(employeeDTO.getEmail()); // Valida email único


        // Validação simples do enum
        employeeDTO.setGender(employeeDTO.getGender().toUpperCase()); // Converte gênero para maiúsculo (tratamento temporário)
        employeeDTO.setState(employeeDTO.getState().toUpperCase()); // Converte estado para maiúsculo (tratamento temporário)
        validateGender(employeeDTO.getGender()); // Valida se gênero existe no enum

        employeeDTO.setCreatedAt(OffsetDateTime.now()); // Define timestamp de criação
        employeeDTO.setUpdatedAt(OffsetDateTime.now()); // Data de atualização

        var entity = employeeMapper.toEntity(employeeDTO); // Converte DTO para entidade
        var entityPersisted = employeesRepository.save(entity); // Persiste entidade no banco
        var dtoEmployeesPersisted = employeeMapper.toDTO(entityPersisted); // Converte entidade persistida para DTO
        logger.debug("[EMP-SRV-004] Employee created successfully with ID: {}", dtoEmployeesPersisted.getId()); // Log de sucesso com ID gerado

        addHateoasLinks(dtoEmployeesPersisted); // Adiciona links HATEOAS ao DTO
        return dtoEmployeesPersisted; // Retorna DTO criado
    }

    // [SERVICE-TRACE: EMP-SRV-005]
    // Remove um registro existente pelo identificador
    public void delete(Long id) {
        logger.info("[EMP-SRV-005] Deleting one Employee with ID: {}", id); // Log com ID para deleção

        Employee entityLoaded = employeesRepository.findById(id) // Busca entidade pelo ID
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-005] Employee with ID {} not found for deletion", id); // Log de warning quando não encontra
                    return new ResourceNotFoundException("no records found for this ID"); // Lança exceção se não encontrar
                });

        employeesRepository.delete(entityLoaded); // Deleta entidade do banco
        logger.debug("[EMP-SRV-005] Employee ID {} deleted successfully", id); // Log de sucesso na deleção
    }

    // [SERVICE-TRACE: EMP-SRV-006]
    // Atualiza um registro existente
    public EmployeeDTO update(EmployeeDTO employeeDTO) {

        if (employeeDTO == null) { // Verifica se DTO não é nulo
            logger.error("[EMP-SRV-006] Attempted to update null employee"); // Log de erro para DTO nulo
            throw new RequiredObjectIsNullException(); // Lança exceção se for nulo
        }

        logger.info("[EMP-SRV-006] Updating one Employee with ID: {}", employeeDTO.getId()); // Log com ID para atualização

        Employee entityLoaded = employeesRepository.findById(employeeDTO.getId()) // Busca entidade pelo ID do DTO
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-006] Employee with ID {} not found for update", employeeDTO.getId()); // Log de warning quando não encontra
                    return new ResourceNotFoundException("No records found for this ID"); // Lança exceção se não encontrar
                });

        logger.debug("[EMP-SRV-006] Updating employee: {} {}", entityLoaded.getFirstName(), entityLoaded.getLastName()); // Log do nome antes da atualização

        if (entityLoaded.getCpf() != employeeDTO.getCpf()) {
            validateCpf(employeeDTO.getCpf()); // Valida documento único
        }

        if (entityLoaded.getEmail() != employeeDTO.getEmail()) {
            validateEmail(employeeDTO.getEmail()); // Valida email único
        }

        // Dados pessoais
        entityLoaded.setFirstName(employeeDTO.getFirstName()); // Atualiza primeiro nome
        entityLoaded.setLastName(employeeDTO.getLastName()); // Atualiza último nome
        entityLoaded.setCpf(employeeDTO.getCpf()); // Atualiza CPF
        entityLoaded.setEmail(employeeDTO.getEmail()); // Atualiza email
        entityLoaded.setPhone(employeeDTO.getPhone()); // Atualiza telefone fixo
        entityLoaded.setMobilePhone(employeeDTO.getMobilePhone()); // Atualiza telefone celular
        entityLoaded.setBirthDate(employeeDTO.getBirthDate()); // Atualiza data de nascimento

        // Endereço
        entityLoaded.setZipCode(employeeDTO.getZipCode()); // Atualiza CEP
        entityLoaded.setStreet(employeeDTO.getStreet()); // Atualiza logradouro
        entityLoaded.setStreetNumber(employeeDTO.getStreetNumber()); // Atualiza número
        entityLoaded.setAddressComplement(employeeDTO.getAddressComplement()); // Atualiza complemento
        entityLoaded.setNeighborhood(employeeDTO.getNeighborhood()); // Atualiza bairro
        entityLoaded.setCity(employeeDTO.getCity()); // Atualiza cidade
        entityLoaded.setState(employeeDTO.getState()); // Atualiza estado (UF)

        // Dados profissionais
        entityLoaded.setJobTitle(employeeDTO.getJobTitle()); // Atualiza cargo
        entityLoaded.setDepartment(employeeDTO.getDepartment()); // Atualiza departamento
        entityLoaded.setHireDate(employeeDTO.getHireDate()); // Atualiza data de contratação
        entityLoaded.setTerminationDate(employeeDTO.getTerminationDate()); // Atualiza data de desligamento
        entityLoaded.setActive(employeeDTO.getActive()); // Atualiza status ativo/inativo

        // Auditoria
        entityLoaded.setUpdatedAt(Instant.now()); // Define timestamp de atualização

        // Códigos e mídia- Verificação
        if(employeeDTO.getPhotoUrl() != null && !employeeDTO.getPhotoUrl().isBlank()) {entityLoaded.setPhotoUrl(employeeDTO.getPhotoUrl());}
        if(employeeDTO.getQrCode() != null && !employeeDTO.getQrCode().isBlank()) {entityLoaded.setQrCode(employeeDTO.getQrCode());}
        if(employeeDTO.getBarCode() != null && !employeeDTO.getBarCode().isBlank()) {entityLoaded.setQrCode(employeeDTO.getBarCode());}

        Employee entityPersisted = employeesRepository.save(entityLoaded); // Salva entidade atualizada
        EmployeeDTO dtoPersisted = employeeMapper.toDTO(entityPersisted); // Converte para DTO
        logger.debug("[EMP-SRV-006] Employee ID {} updated successfully", employeeDTO.getId()); // Log de sucesso na atualização

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO atualizado
    }

    // [SERVICE-TRACE: EMP-SRV-007]
    // Desativa logicamente um registro
    @Transactional // Método executado dentro de transação
    public EmployeeDTO disableEmployee(Long id) {
        logger.info("[EMP-SRV-007] Disabling one Employee with ID: {}", id); // Log com ID para desativação

        employeesRepository.findById(id) // Busca entidade pelo ID
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-007] Employee with ID {} not found for disable", id); // Log de warning quando não encontra
                    return new ResourceNotFoundException("no records found for this ID"); // Lança exceção se não encontrar
                });

        employeesRepository.disableEmployee(id); // Executa query de desativação lógica (ativa = false)

        var employeesEntity = employeesRepository.findById(id).get(); // Recupera entidade atualizada
        var employeesDTO = employeeMapper.toDTO(employeesEntity); // Converte para DTO
        logger.debug("[EMP-SRV-007] Employee ID {} disabled successfully. Active status: {}", id, employeesEntity.getActive()); // Log de sucesso com status

        addHateoasLinks(employeesDTO); // Adiciona links HATEOAS
        return employeesDTO; // Retorna DTO desativado
    }

    // [SERVICE-TRACE: EMP-SRV-008]
    // Exporta uma lista paginada de registros
    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("[EMP-SRV-008] Exporting a Employee page! Format: {}, Page: {}", acceptHeader, pageable.getPageNumber()); // Log com formato e página

        var employees = employeesRepository.findAll(pageable) // Busca página de funcionários
                .map(employee -> employeeMapper.toDTO(employee)) // Converte cada entidade para DTO
                .getContent(); // Obtém apenas o conteúdo da página
        logger.debug("[EMP-SRV-008] Exporting {} employees", employees.size()); // Log da quantidade exportada

        try { // Bloco try-catch para tratamento de exceções de exportação
            EmployeesExporter exporter = this.exporter.getExporter(acceptHeader); // Obtém exportador adequado baseado no Accept header
            logger.debug("[EMP-SRV-008] Using exporter: {}", exporter.getClass().getSimpleName()); // Log do exportador usado
            return exporter.exportEmployees(employees); // Executa exportação e retorna recurso
        } catch (Exception e) { // Captura qualquer exceção durante exportação
            logger.error("[EMP-SRV-008] Error during file export: {}", e.getMessage(), e); // Log de erro detalhado
            throw new RuntimeException("Error during file export", e); // Lança runtime exception com causa
        }
    }

    // [SERVICE-TRACE: EMP-SRV-009]
    // Exporta os dados de um registro
    public Resource exportEmployee(Long id, String acceptHeader) {
        logger.info("[EMP-SRV-009] Exporting data of one Employee with ID: {}, Format: {}", id, acceptHeader); // Log com ID e formato

        var entityLoaded = employeesRepository.findById(id) // Busca entidade pelo ID
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-009] Employee with ID {} not found for export", id); // Log de warning quando não encontra
                    return new ResourceNotFoundJasperException("no records found for this ID"); // Lança exceção se não encontrar
                });

        var dtoLoaded = employeeMapper.toDTO(entityLoaded); // Converte entidade para DTO
        logger.debug("[EMP-SRV-009] Exporting employee: {} {}", entityLoaded.getFirstName(), entityLoaded.getLastName()); // Log do nome do funcionário
        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS

        try { // Bloco try-catch para tratamento de exceções de exportação
            EmployeesExporter exporter = this.exporter.getExporter(acceptHeader); // Obtém exportador adequado baseado no Accept header
            logger.debug("[EMP-SRV-009] Using exporter: {}", exporter.getClass().getSimpleName()); // Log do exportador usado
            return exporter.exportEmployee(dtoLoaded); // Executa exportação e retorna recurso
        } catch (Exception e) { // Captura qualquer exceção durante exportação
            logger.error("[EMP-SRV-009] Error during file export for ID {}: {}", id, e.getMessage(), e); // Log de erro detalhado com ID
            throw new RuntimeException("Error during file export", e); // Lança runtime exception com causa
        }
    }

    // [SERVICE-TRACE: EMP-SRV-010]
    // Realiza importação em massa de registros
    public List<EmployeeDTO> massCreation(MultipartFile file) {
        logger.info("[EMP-SRV-010] Importing Employee from file! Filename: {}", file.getOriginalFilename()); // Log com nome do arquivo

        if (file.isEmpty()) {
            logger.error("[EMP-SRV-010] Import attempted with empty file"); // Log de erro para arquivo vazio
            throw new BadRequestException("Please set a valid File!"); // Valida se arquivo não está vazio
        }

        try (InputStream inputStream = file.getInputStream()) { // Try-with-resources para garantir fechamento do stream

            String fileName = Optional.ofNullable(file.getOriginalFilename()) // Obtém nome original do arquivo
                    .orElseThrow(() -> {
                        logger.error("[EMP-SRV-010] Import attempted with null filename"); // Log de erro para nome nulo
                        return new BadRequestException("File name cannot be null"); // Lança exceção se nome for nulo
                    });

            FileImporter importer = this.importer.getImporter(fileName); // Obtém importador adequado baseado na extensão
            logger.debug("[EMP-SRV-010] Using importer: {}", importer.getClass().getSimpleName()); // Log do importador usado

            List<Employee> entities = importer.importFile(inputStream) // Importa dados do arquivo
                    .stream() // Converte para stream
                    .map(dto -> {
                        logger.debug("[EMP-SRV-010] Importing employee: {}", dto.getEmail()); // Log de cada importação individual
                        return employeesRepository.save(employeeMapper.toEntity(dto)); // Para cada DTO, converte para entidade e salva
                    })
                    .toList(); // Coleta resultados em lista
            logger.info("[EMP-SRV-010] Successfully imported {} employees", entities.size()); // Log da quantidade importada

            return entities.stream() // Stream das entidades persistidas
                    .map(entity -> { // Para cada entidade
                        var dto = employeeMapper.toDTO(entity); // Converte para DTO
                        addHateoasLinks(dto); // Adiciona links HATEOAS
                        return dto; // Retorna DTO
                    }).toList(); // Coleta em lista

        } catch(InvalidGenderException e){ // Captura exceção específica de gênero inválido
            logger.error("[EMP-SRV-010] Invalid gender in import file: {}", e.getMessage()); // Log de erro específico
            throw new InvalidGenderException(e.getMessage()); // Relança exceção de gênero inválido
        }
        catch (Exception e) { // Captura qualquer outra exceção
            logger.error("[EMP-SRV-010] Error processing file: {}", e.getMessage(), e); // Log de erro detalhado
            throw new FileStorageException("Error processing file!"); // Lança exceção de armazenamento
        }
    }


    // [EMP-SRV-011] Recupera employee por email
    public EmployeeDTO findByEmail(String email) {
        logger.info("[EMP-SRV-011] Finding employee by email: {}", email); // Log da busca

        var entityLoaded = employeesRepository.findByEmail(email) // Busca por email
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-011] employee with email {} not found", email); // Log de aviso
                    return new ResourceNotFoundException("no records found for this email"); // Exceção
                });

        var dtoLoaded = employeeMapper.toDTO(entityLoaded); // Converte para DTO
        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [EMP-SRV-012] Recupera cliente por documento
    public EmployeeDTO findByDocument(String cpf) {
        logger.info("[EMP-SRV-012] Finding employee by cpf: {}", cpf); // Log da busca

        var entityLoaded = employeesRepository.findByCpf(cpf) // Busca por documento
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-012] employee with cpf {} not found", cpf); // Log de aviso
                    return new ResourceNotFoundException("no records found for this cpf"); // Exceção
                });
        var dtoLoaded = employeeMapper.toDTO(entityLoaded); // Converte para DTO
        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS
        return dtoLoaded; // Retorna DTO
    }

    // [EMP-SRV-013] Ativa um Employee (status = true)
    public EmployeeDTO activateEmployee(Long id) {
        logger.info("[EMP-SRV-013] Activating Employee with ID: {}", id); // Log da ativação

        employeesRepository.findById(id) // Verifica existência
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-013] Employee with ID {} not found for activation", id); // Log de aviso
                    return new ResourceNotFoundException("no records found for this ID"); // Exceção
                });

        employeesRepository.activateEmployee(id); // Ativa Employee

        var employeeEntity = employeesRepository.findById(id).get(); // Busca atualizado
        var employeeDTO = employeeMapper.toDTO(employeeEntity); // Converte para DTO

        logger.debug("[EMP-SRV-013] Customer ID {} activated successfully", id); // Log de sucesso

        addHateoasLinks(employeeDTO); // Adiciona links HATEOAS
        return employeeDTO; // Retorna DTO
    }

    // [SERVICE-TRACE: EMP-SRV-014]
    // Recupera registros paginados com base em um critério de busca de People Active
    public PagedModel<EntityModel<EmployeeDTO>> findByActiveTrue(Pageable pageable) { // Busca por nome paginada
        logger.info("[EMP-SRV-014] Finding People active:  - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize()); // Log com nome e paginação
        var employee = employeesRepository.findByActiveTrue(pageable); // Executa query de busca no repository
        logger.debug("[EMP-SRV-014] Found {} results for active ", employee.getNumberOfElements()); // Log da quantidade de resultados
        return buildPageModel(pageable, employee); // Constrói e retorna modelo paginado com HATEOAS
    }

    // [SERVICE-TRACE: EMP-SRV-015]
    // Recupera registros paginados com base em um critério de busca de People disable
    public PagedModel<EntityModel<EmployeeDTO>> findByActiveFalse(Pageable pageable) { // Busca por nome paginada
        logger.info("[EMP-SRV-015] Finding People disable:  - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize()); // Log com nome e paginação
        var employee = employeesRepository.findByActiveFalse(pageable); // Executa query de busca no repository
        logger.debug("[EMP-SRV-015] Found {} results for disable ", employee.getNumberOfElements()); // Log da quantidade de resultados
        return buildPageModel(pageable, employee); // Constrói e retorna modelo paginado com HATEOAS
    }

    // [SERVICE-TRACE: EMP-SRV-016]
    // Recupera registros paginados com base em um critério de busca de People deparamento
    public PagedModel<EntityModel<EmployeeDTO>> findByDepartmentIgnoreCase(String department,Pageable pageable) { // Busca por nome paginada
        logger.info("[EMP-SRV-016] Finding People department: {} - page: {}, size: {}",department, pageable.getPageNumber(), pageable.getPageSize()); // Log com nome e paginação
        var employee = employeesRepository.findByDepartmentIgnoreCase(department,pageable); // Executa query de busca no repository
        logger.debug("[EMP-SRV-016] Found {} results for department {} ", employee.getNumberOfElements(), department); // Log da quantidade de resultados
        return buildPageModel(pageable, employee); // Constrói e retorna modelo paginado com HATEOAS
    }

    // [SERVICE-TRACE: EMP-SRV-017]
    // Recupera registros paginados com base em um critério de busca de People jobTitle
    public PagedModel<EntityModel<EmployeeDTO>> findByJobTitleIgnoreCase(String jobTitle,Pageable pageable) { // Busca por nome paginada
        logger.info("[EMP-SRV-017] Finding People department: {} - page: {}, size: {}",jobTitle, pageable.getPageNumber(), pageable.getPageSize()); // Log com nome e paginação
        var employee = employeesRepository.findByJobTitleIgnoreCase(jobTitle,pageable); // Executa query de busca no repository
        logger.debug("[EMP-SRV-017] Found {} results for department {} ", employee.getNumberOfElements(), jobTitle); // Log da quantidade de resultados
        return buildPageModel(pageable, employee); // Constrói e retorna modelo paginado com HATEOAS
    }


    // [[EMP-SRV-INTERNAL-001: ADD HATEOAS]
    // Método privado para adicionar links HATEOAS ao DTO
    private void addHateoasLinks(EmployeeDTO employeeDTO) {
        logger.trace("[EMP-SRV-INTERNAL-001] Adding HATEOAS links for employee ID: {}", employeeDTO.getId()); // Log trace para fluxo interno

        employeeDTO.add(linkTo(methodOn(EmployeeController.class) // Adiciona link self
                .findById(employeeDTO.getId())).withSelfRel().withType("GET")); // Link para buscar por ID (GET)

        employeeDTO.add(linkTo(methodOn(EmployeeController.class) // Adiciona link para listar todos
                .findAll(1, 12, "asc")).withRel("findAll").withType("GET")); // Link para listar todos (GET)

        employeeDTO.add(linkTo(methodOn(EmployeeController.class) // Adiciona link para buscar por nome
                .findByName("", 1, 11, "asc")).withRel("findByName").withType("GET")); // Link para busca por nome (GET)

        employeeDTO.add(linkTo(methodOn(EmployeeController.class) // Adiciona link para criar
                .create(employeeDTO)).withRel("create").withType("POST")); // Link para criar novo (POST)

        employeeDTO.add(linkTo(methodOn(EmployeeController.class)) // Adiciona link para importação
                .slash("massCreation").withRel("massCreation").withType("POST")); // Link para importação em massa (POST)

        employeeDTO.add(linkTo(methodOn(EmployeeController.class) // Adiciona link para atualizar
                .update(employeeDTO)).withRel("update").withType("PUT")); // Link para atualizar (PUT)

        employeeDTO.add(linkTo(methodOn(EmployeeController.class) // Adiciona link para desativar
                .disableEmployee(employeeDTO.getId())).withRel("disableEmployee").withType("PATCH")); // Link para desativação (PATCH)

        employeeDTO.add(linkTo(methodOn(EmployeeController.class) // Adiciona link para deletar
                .delete(employeeDTO.getId())).withRel("delete").withType("DELETE")); // Link para deletar (DELETE)

        employeeDTO.add(linkTo(methodOn(EmployeeController.class) // Adiciona link para exportar página
                .exportPage(1,12,"asc", null)).withRel("exportPage").withType("GET").withTitle("Export Employee")); // Link para exportação (GET)

    }

    // [EMP-SRV-INTERNAL-002: PagedModel - HATEOAS]
    // Método privado para Constrói um modelo paginado com suporte a HATEOAS
    private PagedModel<EntityModel<EmployeeDTO>> buildPageModel(
            Pageable pageable, // Configuração de paginação
            Page<Employee> employee) { // Página de entidades
        logger.trace("[EMP-SRV-INTERNAL-002] Building page model for page {} with {} elements", pageable.getPageNumber(), employee.getNumberOfElements()); // Log trace da construção

        Page<EmployeeDTO> employeeWithLinks = employeeMapper.toDTOPage(employee); // Converte página de entidades para DTOs
        employeeWithLinks.forEach(e -> addHateoasLinks(e)); // Para cada DTO, adiciona links HATEOAS

        Link findAllLink = WebMvcLinkBuilder.linkTo( // Cria link para a própria consulta
                WebMvcLinkBuilder.methodOn(EmployeeController.class) // Referência ao controller
                        .findAll( // Método findAll
                                pageable.getPageNumber(), // Número da página atual
                                pageable.getPageSize(), // Tamanho da página
                                String.valueOf(pageable.getSort()) // String de ordenação
                        )
        ).withSelfRel(); // Define como link self

        PagedModel<EntityModel<EmployeeDTO>> result = assembler.toModel(employeeWithLinks, findAllLink); // Monta modelo paginado com links
        return result; // Retorna modelo paginado
    }

    // [EMP-SRV-INTERNAL-003 GenderType]
    // // Método privado para validar gênero se o gênero é válido do enum GenderType
    // throws InvalidGenderException se o gênero for nulo ou inválido
    private void validateGender(String gender) {
        logger.trace("[EMP-SRV-INTERNAL-003] Validating gender: {}", gender); // Log do gênero a validar
        try { // Bloco try-catch para validação
            if (gender == null) {
                logger.error("[EMP-SRV-INTERNAL-003] GenderType validation failed: gender is null"); // Log de erro para nulo
                throw new InvalidGenderException("Gênero não pode ser nulo"); // Valida se não é nulo
            }
            GenderType.valueOf(gender.toUpperCase()); // Tenta converter para enum (lança IllegalArgumentException se inválido)
        } catch (Exception e) { // Captura qualquer exceção (null ou inválido)
            logger.error("[EMP-SRV-INTERNAL-003] Invalid gender attempt: {}", gender); // Log de erro
            String allowedValues = Arrays.stream(GenderType.values()) // Stream dos valores do enum
                    .map(Enum::name) // Extrai nomes
                    .collect(Collectors.joining(", ")); // Junta com vírgula
            throw new InvalidGenderException( // Lança exceção personalizada
                    String.format("Gênero '%s' inválido. Valores permitidos: %s", // Mensagem formatada
                            gender, allowedValues) // Parâmetros: gênero informado e valores permitidos
            );
        }
    }

    // [EMP-SRV-INTERNAL-004] Valida cpf único
    private void validateCpf(String cpf) {
        logger.trace("[EMP-SRV-INTERNAL-004] Validating CPF: {}", cpf);

        if (cpf == null || cpf.trim().isEmpty()) {
            logger.error("[EMP-SRV-INTERNAL-004] CPF validation failed: CPF is null or empty");
            throw new BadRequestException("CPF inválido");
        }

        if (employeesRepository.existsByCpf(cpf)) {
            logger.error("[EMP-SRV-INTERNAL-004] CPF already exists: {}", cpf);
            throw new BadRequestException("CPF already registered: " + cpf);
        }
    }

    // [EMP-SRV-INTERNAL-005] Valida email único
    private void validateEmail(String email) {
        logger.trace("[EMP-SRV-INTERNAL-005] Validating email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.error("[EMP-SRV-INTERNAL-005] Email validation failed: email is null or empty");
            throw new BadRequestException("Email cannot be null or empty");
        }

        if (employeesRepository.existsByEmail(email)) {
            logger.error("[EMP-SRV-INTERNAL-005] Email already exists: {}", email);
            throw new BadRequestException("Email already registered: " + email);
        }
    }

}