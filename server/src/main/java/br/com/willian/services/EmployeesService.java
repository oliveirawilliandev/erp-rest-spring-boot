package br.com.willian.services; // Pacote da camada de serviço

import br.com.willian.controllers.EmployeesController; // Controller para referências HATEOAS
import br.com.willian.dto.v1.EmployeesDTO; // DTO para transferência de dados
import br.com.willian.exception.*; // Exceções personalizadas do sistema
import br.com.willian.file.exporter.contract.EmployeesExporter; // Contrato para exportadores
import br.com.willian.file.exporter.factory.FileExporterFactory; // Factory para selecionar exportador
import br.com.willian.file.importer.contract.FileImporter; // Contrato para importadores
import br.com.willian.file.importer.factory.FileImporterFactory; // Factory para selecionar importador
import br.com.willian.mapper.EmployeesMapper; // Mapper para conversão Entity/DTO
import br.com.willian.model.Employees; // Entidade JPA
import br.com.willian.model.enums.Gender; // Enum de gêneros válidos
import br.com.willian.repository.EmployeesRepository; // Repository para acesso ao banco
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
import java.util.concurrent.atomic.AtomicLong; // Contador atômico
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
public class EmployeesService { // Serviço para operações com funcionários


    private static Logger logger = LoggerFactory.getLogger(EmployeesService.class.getName()); // Logger para rastreamento

    @Autowired // Injeta dependência do montador de recursos paginados
    private PagedResourcesAssembler<EmployeesDTO> assembler; // Montador de modelos paginados HATEOAS

    @Autowired // Injeta dependência do repository
    private EmployeesRepository employeesRepository; // Repository para acesso aos dados

    @Autowired // Injeta dependência do mapper
    private EmployeesMapper employeesMapper; // Mapper para conversão entre entidade e DTO

    @Autowired // Injeta dependência da factory de importadores
    private FileImporterFactory importer; // Factory para criar importadores baseado no arquivo

    @Autowired // Injeta dependência da factory de exportadores
    FileExporterFactory exporter; // Factory para criar exportadores baseado no header Accept

    // [SERVICE-TRACE: EMP-SRV-001]
    // Recupera uma lista paginada de registros
    public PagedModel<EntityModel<EmployeesDTO>> findAll(Pageable pageable) { // Método para buscar todos com paginação
        logger.info("[EMP-SRV-001] Finding All Employees - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize()); // Log com parâmetros de paginação
        var employee = employeesRepository.findAll(pageable); // Busca todos funcionários paginados no banco
        logger.debug("[EMP-SRV-001] Found {} employees in page {} of {}", employee.getNumberOfElements(), employee.getNumber() + 1, employee.getTotalPages()); // Log detalhado dos resultados
        return buildPageModel(pageable, employee); // Constrói modelo paginado com HATEOAS e retorna
    }

    // [SERVICE-TRACE: EMP-SRV-002]
    // Recupera registros paginados com base em um critério de busca
    public PagedModel<EntityModel<EmployeesDTO>> findByName(String firstName, Pageable pageable) { // Busca por nome paginada
        logger.info("[EMP-SRV-002] Finding People by name: '{}' - page: {}, size: {}", firstName, pageable.getPageNumber(), pageable.getPageSize()); // Log com nome e paginação
        var employee = employeesRepository.findEmployeesByName(firstName, pageable); // Executa query de busca no repository
        logger.debug("[EMP-SRV-002] Found {} results for name '{}'", employee.getNumberOfElements(), firstName); // Log da quantidade de resultados
        return buildPageModel(pageable, employee); // Constrói e retorna modelo paginado com HATEOAS
    }

    // [SERVICE-TRACE: EMP-SRV-003]
    // Recupera um único registro pelo identificador
    public EmployeesDTO findById(Long id) {
        logger.info("[EMP-SRV-003] Finding one Employees with ID: {}", id); // Log com ID buscado

        var entityLoaded = employeesRepository.findById(id) // Busca entidade pelo ID no banco
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-003] Employee with ID {} not found", id); // Log de warning quando não encontra
                    return new ResourceNotFoundException("no records found for this ID"); // Lança exceção se não encontrar
                });

        var dtoLoaded = employeesMapper.toDTO(entityLoaded); // Converte entidade para DTO
        logger.debug("[EMP-SRV-003] Employee found: {} {}", entityLoaded.getFirstName(), entityLoaded.getLastName()); // Log do nome do funcionário encontrado

        addHateoasLinks(dtoLoaded); // Adiciona links HATEOAS ao DTO
        return dtoLoaded; // Retorna DTO com links
    }

    // [SERVICE-TRACE: EMP-SRV-004]
    // Cria e persiste um novo registro
    public EmployeesDTO create(EmployeesDTO employeesDTO) {
        if (employeesDTO == null) {
            logger.error("[EMP-SRV-004] Attempted to create null employee"); // Log de erro para DTO nulo
            throw new RequiredObjectIsNullException(); // Valida se DTO não é nulo
        }
        logger.info("[EMP-SRV-004] Creating one Employees with email: {}", employeesDTO.getEmail()); // Log com email do funcionário

        // Validação simples do enum
        employeesDTO.setGender(employeesDTO.getGender().toUpperCase()); // Converte gênero para maiúsculo (tratamento temporário)
        employeesDTO.setState(employeesDTO.getState().toUpperCase()); // Converte estado para maiúsculo (tratamento temporário)
        validateGender(employeesDTO.getGender()); // Valida se gênero existe no enum

        employeesDTO.setCreatedAt(OffsetDateTime.now()); // Define timestamp de criação
        var entity = employeesMapper.toEntity(employeesDTO); // Converte DTO para entidade
        var entityPersisted = employeesRepository.save(entity); // Persiste entidade no banco
        var dtoEmployeesPersisted = employeesMapper.toDTO(entityPersisted); // Converte entidade persistida para DTO
        logger.debug("[EMP-SRV-004] Employee created successfully with ID: {}", dtoEmployeesPersisted.getId()); // Log de sucesso com ID gerado

        addHateoasLinks(dtoEmployeesPersisted); // Adiciona links HATEOAS ao DTO
        return dtoEmployeesPersisted; // Retorna DTO criado
    }

    // [SERVICE-TRACE: EMP-SRV-005]
    // Remove um registro existente pelo identificador
    public void delete(Long id) {
        logger.info("[EMP-SRV-005] Deleting one Employees with ID: {}", id); // Log com ID para deleção

        Employees entityLoaded = employeesRepository.findById(id) // Busca entidade pelo ID
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-005] Employee with ID {} not found for deletion", id); // Log de warning quando não encontra
                    return new ResourceNotFoundException("no records found for this ID"); // Lança exceção se não encontrar
                });

        employeesRepository.delete(entityLoaded); // Deleta entidade do banco
        logger.debug("[EMP-SRV-005] Employee ID {} deleted successfully", id); // Log de sucesso na deleção
    }

    // [SERVICE-TRACE: EMP-SRV-006]
    // Atualiza um registro existente
    public EmployeesDTO update(EmployeesDTO employeesDTO) {

        if (employeesDTO == null) { // Verifica se DTO não é nulo
            logger.error("[EMP-SRV-006] Attempted to update null employee"); // Log de erro para DTO nulo
            throw new RequiredObjectIsNullException(); // Lança exceção se for nulo
        }

        logger.info("[EMP-SRV-006] Updating one Employees with ID: {}", employeesDTO.getId()); // Log com ID para atualização

        Employees entityLoaded = employeesRepository.findById(employeesDTO.getId()) // Busca entidade pelo ID do DTO
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-006] Employee with ID {} not found for update", employeesDTO.getId()); // Log de warning quando não encontra
                    return new ResourceNotFoundException("No records found for this ID"); // Lança exceção se não encontrar
                });

        logger.debug("[EMP-SRV-006] Updating employee: {} {}", entityLoaded.getFirstName(), entityLoaded.getLastName()); // Log do nome antes da atualização

        // Dados pessoais
        entityLoaded.setFirstName(employeesDTO.getFirstName()); // Atualiza primeiro nome
        entityLoaded.setLastName(employeesDTO.getLastName()); // Atualiza último nome
        entityLoaded.setCpf(employeesDTO.getCpf()); // Atualiza CPF
        entityLoaded.setEmail(employeesDTO.getEmail()); // Atualiza email
        entityLoaded.setPhone(employeesDTO.getPhone()); // Atualiza telefone fixo
        entityLoaded.setMobilePhone(employeesDTO.getMobilePhone()); // Atualiza telefone celular
        entityLoaded.setBirthDate(employeesDTO.getBirthDate()); // Atualiza data de nascimento

        // Endereço
        entityLoaded.setZipCode(employeesDTO.getZipCode()); // Atualiza CEP
        entityLoaded.setStreet(employeesDTO.getStreet()); // Atualiza logradouro
        entityLoaded.setStreetNumber(employeesDTO.getStreetNumber()); // Atualiza número
        entityLoaded.setAddressComplement(employeesDTO.getAddressComplement()); // Atualiza complemento
        entityLoaded.setNeighborhood(employeesDTO.getNeighborhood()); // Atualiza bairro
        entityLoaded.setCity(employeesDTO.getCity()); // Atualiza cidade
        entityLoaded.setState(employeesDTO.getState()); // Atualiza estado (UF)

        // Dados profissionais
        entityLoaded.setJobTitle(employeesDTO.getJobTitle()); // Atualiza cargo
        entityLoaded.setDepartment(employeesDTO.getDepartment()); // Atualiza departamento
        entityLoaded.setHireDate(employeesDTO.getHireDate()); // Atualiza data de contratação
        entityLoaded.setTerminationDate(employeesDTO.getTerminationDate()); // Atualiza data de desligamento
        entityLoaded.setActive(employeesDTO.getActive()); // Atualiza status ativo/inativo

        // Auditoria
        entityLoaded.setUpdatedAt(Instant.now()); // Define timestamp de atualização

        // Códigos e mídia
        entityLoaded.setBarCode(employeesDTO.getBarCode()); // Atualiza código de barras
        entityLoaded.setQrCode(employeesDTO.getQrCode()); // Atualiza QR code
        entityLoaded.setPhotoUrl(employeesDTO.getPhotoUrl()); // Atualiza URL da foto

        Employees entityPersisted = employeesRepository.save(entityLoaded); // Salva entidade atualizada
        EmployeesDTO dtoPersisted = employeesMapper.toDTO(entityPersisted); // Converte para DTO
        logger.debug("[EMP-SRV-006] Employee ID {} updated successfully", employeesDTO.getId()); // Log de sucesso na atualização

        addHateoasLinks(dtoPersisted); // Adiciona links HATEOAS
        return dtoPersisted; // Retorna DTO atualizado
    }

    // [SERVICE-TRACE: EMP-SRV-007]
    // Desativa logicamente um registro
    @Transactional // Método executado dentro de transação
    public EmployeesDTO disableEmployee(Long id) {
        logger.info("[EMP-SRV-007] Disabling one Employees with ID: {}", id); // Log com ID para desativação

        employeesRepository.findById(id) // Busca entidade pelo ID
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-007] Employee with ID {} not found for disable", id); // Log de warning quando não encontra
                    return new ResourceNotFoundException("no records found for this ID"); // Lança exceção se não encontrar
                });

        employeesRepository.disableEmployee(id); // Executa query de desativação lógica (ativa = false)

        var employeesEntity = employeesRepository.findById(id).get(); // Recupera entidade atualizada
        var employeesDTO = employeesMapper.toDTO(employeesEntity); // Converte para DTO
        logger.debug("[EMP-SRV-007] Employee ID {} disabled successfully. Active status: {}", id, employeesEntity.getActive()); // Log de sucesso com status

        addHateoasLinks(employeesDTO); // Adiciona links HATEOAS
        return employeesDTO; // Retorna DTO desativado
    }

    // [SERVICE-TRACE: EMP-SRV-008]
    // Exporta uma lista paginada de registros
    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("[EMP-SRV-008] Exporting a Employees page! Format: {}, Page: {}", acceptHeader, pageable.getPageNumber()); // Log com formato e página

        var employees = employeesRepository.findAll(pageable) // Busca página de funcionários
                .map(employee -> employeesMapper.toDTO(employee)) // Converte cada entidade para DTO
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
        logger.info("[EMP-SRV-009] Exporting data of one Employees with ID: {}, Format: {}", id, acceptHeader); // Log com ID e formato

        var entityLoaded = employeesRepository.findById(id) // Busca entidade pelo ID
                .orElseThrow(() -> {
                    logger.warn("[EMP-SRV-009] Employee with ID {} not found for export", id); // Log de warning quando não encontra
                    return new ResourceNotFoundJasperException("no records found for this ID"); // Lança exceção se não encontrar
                });

        var dtoLoaded = employeesMapper.toDTO(entityLoaded); // Converte entidade para DTO
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
    public List<EmployeesDTO> massCreation(MultipartFile file) {
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

            List<Employees> entities = importer.importFile(inputStream) // Importa dados do arquivo
                    .stream() // Converte para stream
                    .map(dto -> {
                        logger.debug("[EMP-SRV-010] Importing employee: {}", dto.getEmail()); // Log de cada importação individual
                        return employeesRepository.save(employeesMapper.toEntity(dto)); // Para cada DTO, converte para entidade e salva
                    })
                    .toList(); // Coleta resultados em lista
            logger.info("[EMP-SRV-010] Successfully imported {} employees", entities.size()); // Log da quantidade importada

            return entities.stream() // Stream das entidades persistidas
                    .map(entity -> { // Para cada entidade
                        var dto = employeesMapper.toDTO(entity); // Converte para DTO
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

    // [[EMP-SRV-INTERNAL-001: ADD HATEOAS]
    // Método privado para adicionar links HATEOAS ao DTO
    private void addHateoasLinks(EmployeesDTO employeesDTO) {
        logger.trace("[EMP-SRV-INTERNAL-001] Adding HATEOAS links for employee ID: {}", employeesDTO.getId()); // Log trace para fluxo interno

        employeesDTO.add(linkTo(methodOn(EmployeesController.class) // Adiciona link self
                .findById(employeesDTO.getId())).withSelfRel().withType("GET")); // Link para buscar por ID (GET)

        employeesDTO.add(linkTo(methodOn(EmployeesController.class) // Adiciona link para listar todos
                .findAll(1, 12, "asc")).withRel("findAll").withType("GET")); // Link para listar todos (GET)

        employeesDTO.add(linkTo(methodOn(EmployeesController.class) // Adiciona link para buscar por nome
                .findByName("", 1, 11, "asc")).withRel("findByName").withType("GET")); // Link para busca por nome (GET)

        employeesDTO.add(linkTo(methodOn(EmployeesController.class) // Adiciona link para criar
                .create(employeesDTO)).withRel("create").withType("POST")); // Link para criar novo (POST)

        employeesDTO.add(linkTo(methodOn(EmployeesController.class)) // Adiciona link para importação
                .slash("massCreation").withRel("massCreation").withType("POST")); // Link para importação em massa (POST)

        employeesDTO.add(linkTo(methodOn(EmployeesController.class) // Adiciona link para atualizar
                .update(employeesDTO)).withRel("update").withType("PUT")); // Link para atualizar (PUT)

        employeesDTO.add(linkTo(methodOn(EmployeesController.class) // Adiciona link para desativar
                .disableEmployee(employeesDTO.getId())).withRel("disableEmployee").withType("PATCH")); // Link para desativação (PATCH)

        employeesDTO.add(linkTo(methodOn(EmployeesController.class) // Adiciona link para deletar
                .delete(employeesDTO.getId())).withRel("delete").withType("DELETE")); // Link para deletar (DELETE)

        employeesDTO.add(linkTo(methodOn(EmployeesController.class) // Adiciona link para exportar página
                .exportPage(1,12,"asc", null)).withRel("exportPage").withType("GET").withTitle("Export Employees")); // Link para exportação (GET)

    }

    // [EMP-SRV-INTERNAL-002: PagedModel - HATEOAS]
    // Método privado para Constrói um modelo paginado com suporte a HATEOAS
    private PagedModel<EntityModel<EmployeesDTO>> buildPageModel(
            Pageable pageable, // Configuração de paginação
            Page<Employees> employee) { // Página de entidades
        logger.trace("[EMP-SRV-INTERNAL-002] Building page model for page {} with {} elements", pageable.getPageNumber(), employee.getNumberOfElements()); // Log trace da construção

        Page<EmployeesDTO> employeeWithLinks = employeesMapper.toDTOPage(employee); // Converte página de entidades para DTOs
        employeeWithLinks.forEach(e -> addHateoasLinks(e)); // Para cada DTO, adiciona links HATEOAS

        Link findAllLink = WebMvcLinkBuilder.linkTo( // Cria link para a própria consulta
                WebMvcLinkBuilder.methodOn(EmployeesController.class) // Referência ao controller
                        .findAll( // Método findAll
                                pageable.getPageNumber(), // Número da página atual
                                pageable.getPageSize(), // Tamanho da página
                                String.valueOf(pageable.getSort()) // String de ordenação
                        )
        ).withSelfRel(); // Define como link self

        PagedModel<EntityModel<EmployeesDTO>> result = assembler.toModel(employeeWithLinks, findAllLink); // Monta modelo paginado com links
        return result; // Retorna modelo paginado
    }

    // [EMP-SRV-INTERNAL-003 Gender]
    // // Método privado para validar gênero se o gênero é válido do enum Gender
    // throws InvalidGenderException se o gênero for nulo ou inválido
    private void validateGender(String gender) {
        logger.trace("[EMP-SRV-INTERNAL-003] Validating gender: {}", gender); // Log do gênero a validar
        try { // Bloco try-catch para validação
            if (gender == null) {
                logger.error("[EMP-SRV-INTERNAL-003] Gender validation failed: gender is null"); // Log de erro para nulo
                throw new InvalidGenderException("Gênero não pode ser nulo"); // Valida se não é nulo
            }
            Gender.valueOf(gender.toUpperCase()); // Tenta converter para enum (lança IllegalArgumentException se inválido)
        } catch (Exception e) { // Captura qualquer exceção (null ou inválido)
            logger.error("[EMP-SRV-INTERNAL-003] Invalid gender attempt: {}", gender); // Log de erro
            String allowedValues = Arrays.stream(Gender.values()) // Stream dos valores do enum
                    .map(Enum::name) // Extrai nomes
                    .collect(Collectors.joining(", ")); // Junta com vírgula
            throw new InvalidGenderException( // Lança exceção personalizada
                    String.format("Gênero '%s' inválido. Valores permitidos: %s", // Mensagem formatada
                            gender, allowedValues) // Parâmetros: gênero informado e valores permitidos
            );
        }
    }
}