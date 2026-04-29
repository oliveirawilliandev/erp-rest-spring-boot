package br.com.willian.controller;

import br.com.willian.controller.docs.IngredientControllerDocs;
import br.com.willian.dto.v1.IngredientDTO;
import br.com.willian.service.IngredientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingredient/v1")
public class IngredientController implements IngredientControllerDocs {

    private static final Logger logger = LoggerFactory.getLogger(IngredientController.class);

    @Autowired
    private IngredientService ingredientService;

    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<IngredientDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[ING-CTRL-001] Listagem solicitada | page={} | size={} | direction={}", page, size, direction);

        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        logger.debug("[ING-CTRL-001] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        var response = ingredientService.findAll(pageable);

        logger.debug("[ING-CTRL-001] Listagem retornada com sucesso | page={} | size={} | direction={}", page, size, direction);

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/findByName/{name}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<IngredientDTO>>> findByName(
            @PathVariable("name") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[ING-CTRL-002] Busca por nome solicitada | name={} | page={} | size={}", name, page, size);

        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        logger.debug("[ING-CTRL-002] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        var response = ingredientService.findByName(name, pageable);

        logger.debug("[ING-CTRL-002] Busca por nome retornada com sucesso | name={} | page={} | size={}", name, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public IngredientDTO findById(@PathVariable("id") Long id) {
        logger.info("[ING-CTRL-003] Busca por ID solicitada | id={}", id);

        var ingredient = ingredientService.findById(id);
        logger.debug("[ING-CTRL-003] Serviço retornou ingrediente | id={} | name={}", ingredient.getId(), ingredient.getName());

        return ingredient;
    }

    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<IngredientDTO> create(@RequestBody IngredientDTO ingredientDTO) {
        logger.info("[ING-CTRL-004] Criação solicitada | name={} | purchasePrice={}",
                ingredientDTO.getName(), ingredientDTO.getPurchasePrice());

        var created = ingredientService.create(ingredientDTO);

        logger.debug("[ING-CTRL-004] Criação realizada com sucesso | id={} | name={} | purchasePrice={}",
                created.getId(), created.getName(), created.getPurchasePrice());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        logger.info("[ING-CTRL-005] Remoção solicitada | id={}", id);

        ingredientService.delete(id);

        logger.debug("[ING-CTRL-005] Remoção realizada com sucesso | id={}", id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public IngredientDTO update(@RequestBody IngredientDTO ingredientDTO) {
        logger.info("[ING-CTRL-006] Atualização solicitada | id={} | name={} | purchasePrice={}",
                ingredientDTO.getId(), ingredientDTO.getName(), ingredientDTO.getPurchasePrice());

        var updated = ingredientService.update(ingredientDTO);

        logger.debug("[ING-CTRL-006] Atualização realizada com sucesso | id={} | name={} | purchasePrice={}",
                updated.getId(), updated.getName(), updated.getPurchasePrice());

        return updated;
    }

    @PatchMapping(
            value = "/updateStock/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public IngredientDTO updateStock(
            @PathVariable("id") Long id,
            @RequestParam("quantity") Integer quantity) {
        logger.info("[ING-CTRL-007] Atualização de estoque solicitada | id={} | quantity={}", id, quantity);

        var updated = ingredientService.updateStock(id, quantity);

        logger.debug("[ING-CTRL-007] Estoque atualizado com sucesso | id={} | stockQuantity={}",
                updated.getId(), updated.getStockQuantity());

        return updated;
    }

    @PatchMapping(
            value = "/activate/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public IngredientDTO activateIngredient(@PathVariable("id") Long id) {
        logger.info("[ING-CTRL-008] Ativação solicitada | id={}", id);

        var activated = ingredientService.activateIngredient(id);

        logger.debug("[ING-CTRL-008] Ativação realizada com sucesso | id={} | active={}",
                activated.getId(), activated.getActive());

        return activated;
    }

    @PatchMapping(
            value = "/deactivate/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public IngredientDTO deactivateIngredient(@PathVariable("id") Long id) {
        logger.info("[ING-CTRL-009] Desativação solicitada | id={}", id);

        var deactivated = ingredientService.deactivateIngredient(id);

        logger.debug("[ING-CTRL-009] Desativação realizada com sucesso | id={} | active={}",
                deactivated.getId(), deactivated.getActive());

        return deactivated;
    }

    @GetMapping(
            value = "/findByActive/{active}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<IngredientDTO>>> findByActive(
            @PathVariable("active") Boolean active,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[ING-CTRL-010] Busca por status ativo solicitada | active={} | page={} | size={}", active, page, size);

        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        logger.debug("[ING-CTRL-010] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        var response = ingredientService.findByActive(active, pageable);

        logger.debug("[ING-CTRL-010] Busca por status ativo retornada com sucesso | active={} | page={} | size={}", active, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/findLowStock",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<IngredientDTO>>> findLowStock(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[ING-CTRL-011] Busca por estoque baixo solicitada | page={} | size={}", page, size);

        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "stockQuantity"));
        logger.debug("[ING-CTRL-011] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        var response = ingredientService.findLowStock(pageable);

        logger.debug("[ING-CTRL-011] Busca por estoque baixo retornada com sucesso | page={} | size={}", page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/findLowStockByThreshold/{threshold}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<IngredientDTO>>> findLowStockByThreshold(
            @PathVariable("threshold") Integer threshold,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[ING-CTRL-012] Busca por estoque abaixo do limite solicitada | threshold={} | page={} | size={}", threshold, page, size);

        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "stockQuantity"));
        logger.debug("[ING-CTRL-012] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        var response = ingredientService.findLowStockByThreshold(threshold, pageable);

        logger.debug("[ING-CTRL-012] Busca por estoque abaixo do limite retornada com sucesso | threshold={} | page={} | size={}", threshold, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/findBySupplier/{supplierId}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<IngredientDTO>>> findBySupplier(
            @PathVariable("supplierId") Long supplierId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[ING-CTRL-013] Busca por fornecedor solicitada | supplierId={} | page={} | size={}", supplierId, page, size);

        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        logger.debug("[ING-CTRL-013] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        var response = ingredientService.findBySupplier(supplierId, pageable);

        logger.debug("[ING-CTRL-013] Busca por fornecedor retornada com sucesso | supplierId={} | page={} | size={}", supplierId, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/findCriticalStock",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PagedModel<EntityModel<IngredientDTO>>> findCriticalStock(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        logger.info("[ING-CTRL-014] Busca por estoque crítico solicitada | page={} | size={}", page, size);

        var sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "stockQuantity"));
        logger.debug("[ING-CTRL-014] Pageable criado: page={}, size={}, sort={}", page, size, sortDirection);

        var response = ingredientService.findCriticalStock(pageable);

        logger.debug("[ING-CTRL-014] Busca por estoque crítico retornada com sucesso | page={} | size={}", page, size);

        return ResponseEntity.ok(response);
    }
}