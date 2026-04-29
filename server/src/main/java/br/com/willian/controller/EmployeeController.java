package br.com.willian.controller;

import br.com.willian.controller.docs.EmployeeControllerDocs; // Interface de documentação Swagger/OpenAPI
import br.com.willian.dto.v1.CustomerDTO;
import br.com.willian.dto.v1.EmployeeDTO;
import br.com.willian.file.exporter.MediaTypes; // Constantes para tipos de mídia de exportação
import br.com.willian.service.EmployeeService; // Serviço com a lógica de negócio
import jakarta.servlet.http.HttpServletRequest; // Interface para acessar dados da requisição HTTP
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.core.io.Resource; // Representação de recurso para download
import org.springframework.data.domain.PageRequest; // Implementação de Pageable para paginação
import org.springframework.data.domain.Pageable; // Interface para paginação
import org.springframework.data.domain.Sort; // Configuração de ordenação
import org.springframework.hateoas.EntityModel; // Wrapper HATEOAS para entidades
import org.springframework.hateoas.PagedModel; // Modelo HATEOAS para páginas
import org.springframework.http.HttpHeaders; // Constantes para cabeçalhos HTTP
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // Constantes para tipos de mídia
import org.springframework.http.ResponseEntity; // Entidade de resposta HTTP
import org.springframework.web.bind.annotation.*; // Anotações Spring para REST controllers
import org.springframework.web.multipart.MultipartFile; // Representação de arquivo upload
import java.util.List; // Interface List
import java.util.Map; // Interface Map

// Define esta classe como um controlador REST com URL base "/api/employee/v1"
@RestController
// Mapeia as requisições para esta URL base
@RequestMapping("/api/employee/v1")
// Implementa a interface de documentação para garantir consistência
public class EmployeeController implements EmployeeControllerDocs {

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger =
            LoggerFactory.getLogger(EmployeeController.class);

    // Injeta automaticamente a dependência do serviço de funcionários
    @Autowired
    private EmployeeService employeeService;

    //[CTRL-TRACE: EMP-CTRL-001]: Endpoint responsável por retornar uma lista paginada de recursos
    @GetMapping( // Mapeia requisições GET para este método
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    // Permite ordenação e paginação via parâmetros de requisição
    public ResponseEntity<PagedModel<EntityModel<EmployeeDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Parâmetro de página, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação, padrão ascendente
    ) {

        // Registra log da solicitação de listagem com parâmetros
        logger.info("[EMP-CTRL-001] Listagem solicitada | page={} | size={} | direction={}", page, size, direction);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por nome
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "firstName")
        );
        logger.debug("[EMP-CTRL-001] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar todos os funcionários paginados
        var response = employeeService.findAll(pageable);

        logger.debug("[EMP-CTRL-001] Listagem retornada com sucesso | page={} | size={} | direction={}", page, size, direction);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: EMP-CTRL-002]: Endpoint responsável por retornar lista paginada filtrada por nome
    @GetMapping( // Mapeia requisições GET para esta URL específica
            value = "/findEmployeeByName/{name}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<EmployeeDTO>>> findByName(
            @PathVariable("name") String name, // Parâmetro do nome vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[EMP-CTRL-002] Busca por nome solicitada | name={} | page={} | size={}", name, page, size);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por nome
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName", "lastName"));
        logger.debug("[EMP-CTRL-002] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar funcionários pelo nome
        var response = employeeService.findByName(name, pageable);

        logger.debug("[EMP-CTRL-002] Busca por nome retornada com sucesso | name={} | page={} | size={}", name, page, size);

        // Retorna resposta HTTP 200 com o conteúdo

        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: EMP-CTRL-003]: Endpoint responsável por retornar um recurso específico
    @GetMapping( // Mapeia requisições GET para URL com ID
            value = "/{id}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public EmployeeDTO findById(@PathVariable("id") Long id) { // ID do funcionário vindo da URL

        logger.info("[EMP-CTRL-003] Busca por ID solicitada | id={}", id);

        // Chama o serviço para buscar funcionário pelo ID
        var employee = employeeService.findById(id);
        logger.debug("[EMP-CTRL-003] Serviço retornou funcionário | id={} | nome={} {}",
                id, employee.getFirstName(), employee.getLastName());



        // Retorna o funcionário encontrado
        return employee;
    }

    //[CTRL-TRACE: EMP-CTRL-004]: Endpoint responsável por criar um novo recurso
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
    public ResponseEntity<EmployeeDTO> create(@RequestBody EmployeeDTO employees) { // Corpo da requisição convertido para DTO

        logger.info("[EMP-CTRL-004] Criação solicitada | firstName={} | lastName={} | email={}",
                employees.getFirstName(), employees.getLastName(), employees.getEmail());

        // Chama o serviço para criar o novo funcionário
        var created = employeeService.create(employees);

        logger.debug("[EMP-CTRL-004] Criação realizada com sucesso | id={} | firstName={} | lastName={} | email={}",
                created.getId(), created.getFirstName(), created.getLastName(), created.getEmail());

        // Retorna o funcionário criado
        return ResponseEntity.status(HttpStatus.CREATED).body(created) ;

    }

    //[CTRL-TRACE: EMP-CTRL-005]: Endpoint responsável por remover um recurso
    @DeleteMapping(value = "/{id}") // Mapeia requisições DELETE para URL com ID
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) { // ID do funcionário a ser removido

        logger.info("[EMP-CTRL-005] Remoção solicitada | id={}", id);

        // Chama o serviço para remover o funcionário
        employeeService.delete(id);

        logger.debug("[EMP-CTRL-005] Remoção realizada com sucesso | id={}", id);

        // Retorna resposta HTTP 204 (Sem conteúdo)
        return ResponseEntity.noContent().build();
    }

    //[CTRL-TRACE: EMP-CTRL-006]: Endpoint responsável por atualizar um recurso existente
    @PutMapping( // Mapeia requisições PUT para atualização
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
    public EmployeeDTO update(@RequestBody EmployeeDTO employees) { // DTO com dados atualizados

        logger.info("[EMP-CTRL-006] Atualização solicitada | id={} | firstName={} | lastName={} | email={}",
                employees.getId(), employees.getFirstName(), employees.getLastName(), employees.getEmail());

        // Chama o serviço para atualizar o funcionário
        var updated = employeeService.update(employees);

        logger.debug("[EMP-CTRL-006] Atualização realizada com sucesso | id={} | firstName={} | lastName={} | email={}",
                updated.getId(), updated.getFirstName(), updated.getLastName(), updated.getEmail());

        // Retorna o funcionário atualizado
        return updated;
    }

    //[CTRL-TRACE: EMP-CTRL-007]: Endpoint responsável por desativar um recurso
    @PatchMapping( // Mapeia requisições PATCH para desativação
            value = "/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public EmployeeDTO disableEmployee(@PathVariable("id") Long id) { // ID do funcionário a desativar

        logger.info("[EMP-CTRL-007] Desativação solicitada | id={}", id);

        // Chama o serviço para desativar o funcionário
        var disabled = employeeService.disableEmployee(id);

        logger.debug("[EMP-CTRL-007] Desativação realizada com sucesso | id={} | active={}",
                disabled.getId(), disabled.getActive());

        // Retorna o funcionário desativado
        return disabled;
    }



    // [CTRL-TRACE: EMP-CTRL-008]
    // Exporta uma lista paginada de registros
    @GetMapping(value = "/exportPage", produces = { // Mapeia GET para exportação de página
            MediaTypes.APPLICATION_CSV_VALUE,     // text/csv - formato CSV
            MediaTypes.APPLICATION_XLSX_VALUE,    // application/vnd.openxmlformats-officedocument.spreadsheetml.sheet - formato Excel
            MediaTypes.APPLICATION_PDF_VALUE      // application/pdf - formato PDF
    })
    @Override
    public   ResponseEntity<Resource> exportPage(
            @RequestParam(value =  "page", defaultValue = "0") Integer page, // Número da página
            @RequestParam(value =  "size", defaultValue = "12") Integer size , // Tamanho da página
            @RequestParam(value =  "direction", defaultValue = "asc") String direction, // Direção da ordenação
            HttpServletRequest request // Objeto para acessar detalhes da requisição
    ) {

        logger.info("[EMP-CTRL-008] Exportação de página solicitada | page={} | size={} | direction={}", page, size, direction);

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC; // Define a direção da ordenação
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName")); // Cria objeto Pageable para paginação
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT); // Obtém o cabeçalho Accept da requisição
        logger.debug("[EMP-CTRL-008] Accept header recebido: {} | Pageable: page={}, size={}, sort={}",
                acceptHeader, page, size, sortDirection);

        Resource file = employeeService.exportPage(pageable, acceptHeader);  // Chama o serviço para exportar a página no formato solicitado
        logger.debug("[EMP-CTRL-008] Serviço retornou arquivo para exportação");

        // Mapa que relaciona tipos de mídia com extensões de arquivo
        Map<String, String> extensionMap = Map.of(MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx",
                MediaTypes.APPLICATION_CSV_VALUE, ".csv",
                MediaTypes.APPLICATION_PDF_VALUE, ".pdf");

        var fileExtension = extensionMap.getOrDefault(acceptHeader,""); // Obtém a extensão baseada no Accept header
        var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream"; // Define o content type ou usa padrão
        var filename = "Employee+exported" + fileExtension; // Cria o nome do arquivo com extensão apropriada

        logger.debug("[EMP-CTRL-008] Exportação concluída | filename={} | contentType={} | page={}", filename, contentType, page);

        // Retorna o arquivo como anexo para download
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType  + "; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+ filename + "\"")
                .body(file);
    }

    // [CTRL-TRACE: EMP-CTRL-009]
    // Exportar dados de funcionários como PDF
    @GetMapping(value = "/export/{id}", // Mapeia GET para exportar um funcionário específico
            produces = {
                    MediaTypes.APPLICATION_PDF_VALUE // Apenas formato PDF
            })
    @Override
    public ResponseEntity<Resource> export(@PathVariable("id") Long id, HttpServletRequest request) {

        logger.info("[EMP-CTRL-009] Exportação de funcionário solicitada | id={}", id);

        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);  // Obtém o cabeçalho Accept da requisição
        logger.debug("[EMP-CTRL-009] Accept header recebido: {} | id={}", acceptHeader, id);

        Resource file = employeeService.exportEmployee(id, acceptHeader); // Chama o serviço para exportar o funcionário específico
        logger.debug("[EMP-CTRL-009] Serviço retornou arquivo para exportação | id={}", id);

        logger.debug("[EMP-CTRL-009] Exportação de funcionário concluída | id={} | filename=employee.pdf", id);

        // Retorna o arquivo PDF como anexo
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(acceptHeader))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=employee.pdf")
                .body(file);
    }

    // [CTRL-TRACE: EMP-CTRL-010]
    // Realiza importação em massa de registros
    @PostMapping( value = "/massCreation", // Mapeia POST para criação em massa
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public List<EmployeeDTO> massCreation(@RequestParam("file") MultipartFile file) { // Arquivo enviado no formulário

        logger.info("[EMP-CTRL-010] Importação em massa solicitada | fileName={} | size={} bytes | contentType={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        // Validação adicional de log para arquivo vazio
        if (file.isEmpty()) {
            logger.warn("[EMP-CTRL-010] Arquivo vazio recebido para importação | fileName={}", file.getOriginalFilename());
        }

        // Chama o serviço para processar a criação em massa
        var listCreatedInMass = employeeService.massCreation(file);

        logger.debug("[EMP-CTRL-010] Serviço processou importação | totalRegistros={}", listCreatedInMass.size());
        logger.debug("[EMP-CTRL-010] Importação em massa concluída com sucesso | fileName={} | totalRegistros={}",
                file.getOriginalFilename(), listCreatedInMass.size());

        // Retorna lista de funcionários criados
        return listCreatedInMass;
    }

    //[CTRL-TRACE: EMP-CTRL-011] - Busca por email
    @GetMapping(
            value = "/findByEmail/{email}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public EmployeeDTO findByEmail(@PathVariable("email") String email) {
        logger.info("[EMP-CTRL-011] Busca por email solicitada | email={}", email);
        var employee = employeeService.findByEmail(email);
        logger.debug("[EMP-CTRL-011] Serviço retornou employee | email={}", employee.getEmail());
        return employee;
    }

    //[CTRL-TRACE: EMP-CTRL-012] - Busca por documento (CPF)
    @GetMapping(
            value = "/findByDocument/{document}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public EmployeeDTO findByDocument(@PathVariable("document") String document) {
        logger.info("[EMP-CTRL-012] Busca por documento solicitada | document={}", document);
        var employee = employeeService.findByDocument(document);
        logger.debug("[EMP-CTRL-012] Serviço retornou employee | document={}", employee.getCpf());
        return employee;
    }

    //[CTRL-TRACE: EMP-CTRL-013] - Ativa funcionário
    @PatchMapping(
            value = "/activate/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public EmployeeDTO activateEmployee(@PathVariable("id") Long id) {
        logger.info("[EMP-CTRL-013] Ativação solicitada | id={}", id);
        var activated = employeeService.activateEmployee(id);
        logger.debug("[EMP-CTRL-013] Ativação realizada com sucesso | id={} | active={}",
                activated.getId(), activated.getActive());
        return activated;
    }

    //[CTRL-TRACE: EMP-CTRL-014] - Busca funcionários ativos
    @GetMapping(
            value = "/active",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<EmployeeDTO>>> findActiveEmployees(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[EMP-CTRL-014] Busca de funcionários ativos solicitada | page={} | size={} | direction={}", page, size, direction);

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName", "lastName"));

        var response = employeeService.findByActiveTrue(pageable);

        logger.debug("[EMP-CTRL-014] Busca de ativos retornada com sucesso");
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: EMP-CTRL-015] - Busca funcionários inativos
    @GetMapping(
            value = "/inactive",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<EmployeeDTO>>> findInactiveEmployees(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[EMP-CTRL-015] Busca de funcionários inativos solicitada | page={} | size={} | direction={}", page, size, direction);

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName", "lastName"));

        var response = employeeService.findByActiveFalse(pageable);

        logger.debug("[EMP-CTRL-015] Busca de inativos retornada com sucesso");
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: EMP-CTRL-016] - Busca funcionários por departamento
    @GetMapping(
            value = "/department/{department}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<EmployeeDTO>>> findEmployeesByDepartment(
            @PathVariable("department") String department,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[EMP-CTRL-016] Busca por departamento solicitada | department={} | page={} | size={}", department, page, size);

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName", "lastName"));

        var response = employeeService.findByDepartmentIgnoreCase(department, pageable);

        logger.debug("[EMP-CTRL-016] Busca por departamento retornada com sucesso | department={}", department);
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: EMP-CTRL-017] - Busca funcionários por cargo
    @GetMapping(
            value = "/jobTitle/{jobTitle}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<EmployeeDTO>>> findEmployeesByJobTitle(
            @PathVariable("jobTitle") String jobTitle,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[EMP-CTRL-017] Busca por cargo solicitada | jobTitle={} | page={} | size={}", jobTitle, page, size);

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName", "lastName"));

        var response = employeeService.findByJobTitleIgnoreCase(jobTitle, pageable);

        logger.debug("[EMP-CTRL-017] Busca por cargo retornada com sucesso | jobTitle={}", jobTitle);
        return ResponseEntity.ok(response);
    }
}