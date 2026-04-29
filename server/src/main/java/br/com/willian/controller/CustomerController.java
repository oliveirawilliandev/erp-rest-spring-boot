package br.com.willian.controller; // Pacote da camada de controle/API

import br.com.willian.controller.docs.CustomerControllerDocs; // Interface de documentação Swagger/OpenAPI
import br.com.willian.dto.v1.CustomerDTO; // DTO de cliente
import br.com.willian.service.CustomerService; // Serviço com a lógica de negócio
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

// Define esta classe como um controlador REST com URL base "/api/customer/v1"
@RestController
// Mapeia as requisições para esta URL base
@RequestMapping("/api/customer/v1")
// Implementa a interface de documentação para garantir consistência
public class CustomerController implements CustomerControllerDocs {

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    // Injeta automaticamente a dependência do serviço de clientes
    @Autowired
    private CustomerService customerService;

    //[CTRL-TRACE: CUST-CTRL-001]: Endpoint responsável por retornar uma lista paginada de recursos
    @GetMapping( // Mapeia requisições GET para este método
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    // Permite ordenação e paginação via parâmetros de requisição
    public ResponseEntity<PagedModel<EntityModel<CustomerDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Parâmetro de página, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Tamanho da página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação, padrão ascendente
    ) {

        // Registra log da solicitação de listagem com parâmetros
        logger.info("[CUST-CTRL-001] Listagem solicitada | page={} | size={} | direction={}", page, size, direction);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por nome
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "name")
        );
        logger.debug("[CUST-CTRL-001] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar todos os clientes paginados
        var response = customerService.findAll(pageable);

        logger.debug("[CUST-CTRL-001] Listagem retornada com sucesso | page={} | size={} | direction={}", page, size, direction);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: CUST-CTRL-002]: Endpoint responsável por retornar lista paginada filtrada por nome
    @GetMapping( // Mapeia requisições GET para esta URL específica
            value = "/findByName/{name}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<CustomerDTO>>> findByName(
            @PathVariable("name") String name, // Parâmetro do nome vindo da URL
            @RequestParam(value = "page", defaultValue = "0") Integer page, // Página atual, padrão 0
            @RequestParam(value = "size", defaultValue = "12") Integer size, // Itens por página, padrão 12
            @RequestParam(value = "direction", defaultValue = "asc") String direction // Direção da ordenação
    ) {

        logger.info("[CUST-CTRL-002] Busca por nome solicitada | name={} | page={} | size={}", name, page, size);

        // Define a direção da ordenação baseada no parâmetro
        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC // Se 'desc', usa descendente
                : Sort.Direction.ASC; // Caso contrário, ascendente

        // Cria objeto Pageable com paginação e ordenação por nome
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "name")
        );
        logger.debug("[CUST-CTRL-002] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        // Chama o serviço para buscar clientes pelo nome
        var response = customerService.findByName(name, pageable);

        logger.debug("[CUST-CTRL-002] Busca por nome retornada com sucesso | name={} | page={} | size={}", name, page, size);

        // Retorna resposta HTTP 200 com o conteúdo
        return ResponseEntity.ok(response);
    }

    //[CTRL-TRACE: CUST-CTRL-003]: Endpoint responsável por retornar um recurso específico
    @GetMapping( // Mapeia requisições GET para URL com ID
            value = "/{id}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public CustomerDTO findById(@PathVariable("id") Long id) { // ID do cliente vindo da URL

        logger.info("[CUST-CTRL-003] Busca por ID solicitada | id={}", id);

        // Chama o serviço para buscar cliente pelo ID
        var customer = customerService.findById(id);
        logger.debug("[CUST-CTRL-003] Serviço retornou cliente | id={} | name={}", customer.getId(), customer.getName());

        // Retorna o cliente encontrado
        return customer;
    }

    //[CTRL-TRACE: CUST-CTRL-004]: Endpoint responsável por retornar cliente por email
    @GetMapping( // Mapeia requisições GET para URL com email
            value = "/findByEmail/{email}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public CustomerDTO findByEmail(@PathVariable("email") String email) { // Email do cliente vindo da URL

        logger.info("[CUST-CTRL-004] Busca por email solicitada | email={}", email);

        // Chama o serviço para buscar cliente pelo email
        var customer = customerService.findByEmail(email);
        logger.debug("[CUST-CTRL-004] Serviço retornou cliente | email={}", customer.getEmail());

        // Retorna o cliente encontrado
        return customer;
    }

    //[CTRL-TRACE: CUST-CTRL-005]: Endpoint responsável por retornar cliente por documento
    @GetMapping( // Mapeia requisições GET para URL com documento
            value = "/findByDocument/{document}",
            produces = { // Define os formatos de resposta suportados
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public CustomerDTO findByDocument(@PathVariable("document") String document) { // Documento do cliente vindo da URL

        logger.info("[CUST-CTRL-005] Busca por documento solicitada | document={}", document);

        // Chama o serviço para buscar cliente pelo documento
        var customer = customerService.findByDocument(document);
        logger.debug("[CUST-CTRL-005] Serviço retornou cliente | document={}", customer.getDocument());

        // Retorna o cliente encontrado
        return customer;
    }

    //[CTRL-TRACE: CUST-CTRL-006]: Endpoint responsável por criar um novo recurso
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
    public ResponseEntity<CustomerDTO> create(@RequestBody CustomerDTO customerDTO) { // Corpo da requisição convertido para DTO

        logger.info("[CUST-CTRL-006] Criação solicitada | name={} | email={}", customerDTO.getName(), customerDTO.getEmail());

        // Chama o serviço para criar o novo cliente
        var created = customerService.create(customerDTO);

        logger.debug("[CUST-CTRL-006] Criação realizada com sucesso | id={} | name={} | email={}",
                created.getId(), created.getName(), created.getEmail());

        // Retorna o cliente criado com status 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //[CTRL-TRACE: CUST-CTRL-007]: Endpoint responsável por remover um recurso
    @DeleteMapping(value = "/{id}") // Mapeia requisições DELETE para URL com ID
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) { // ID do cliente a ser removido

        logger.info("[CUST-CTRL-007] Remoção solicitada | id={}", id);

        // Chama o serviço para remover o cliente
        customerService.delete(id);

        logger.debug("[CUST-CTRL-007] Remoção realizada com sucesso | id={}", id);

        // Retorna resposta HTTP 204 (Sem conteúdo)
        return ResponseEntity.noContent().build();
    }

    //[CTRL-TRACE: CUST-CTRL-008]: Endpoint responsável por atualizar um recurso existente
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
    public CustomerDTO update(@RequestBody CustomerDTO customerDTO) { // DTO com dados atualizados

        logger.info("[CUST-CTRL-008] Atualização solicitada | id={} | name={} | email={}",
                customerDTO.getId(), customerDTO.getName(), customerDTO.getEmail());

        // Chama o serviço para atualizar o cliente
        var updated = customerService.update(customerDTO);

        logger.debug("[CUST-CTRL-008] Atualização realizada com sucesso | id={} | name={} | email={}",
                updated.getId(), updated.getName(), updated.getEmail());

        // Retorna o cliente atualizado
        return updated;
    }

    //[CTRL-TRACE: CUST-CTRL-009]: Endpoint responsável por ativar um recurso
    @PatchMapping( // Mapeia requisições PATCH para ativação
            value = "/activate/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public CustomerDTO activateCustomer(@PathVariable("id") Long id) { // ID do cliente a ativar

        logger.info("[CUST-CTRL-009] Ativação solicitada | id={}", id);

        // Chama o serviço para ativar o cliente
        var activated = customerService.activateCustomer(id);

        logger.debug("[CUST-CTRL-009] Ativação realizada com sucesso | id={} | active={}",
                activated.getId(), activated.getActive());

        // Retorna o cliente ativado
        return activated;
    }

    //[CTRL-TRACE: CUST-CTRL-010]: Endpoint responsável por desativar um recurso
    @PatchMapping( // Mapeia requisições PATCH para desativação
            value = "/deactivate/{id}",
            produces = { // Define os formatos de resposta
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public CustomerDTO deactivateCustomer(@PathVariable("id") Long id) { // ID do cliente a desativar

        logger.info("[CUST-CTRL-010] Desativação solicitada | id={}", id);

        // Chama o serviço para desativar o cliente
        var deactivated = customerService.deactivateCustomer(id);

        logger.debug("[CUST-CTRL-010] Desativação realizada com sucesso | id={} | active={}",
                deactivated.getId(), deactivated.getActive());

        // Retorna o cliente desativado
        return deactivated;
    }
}