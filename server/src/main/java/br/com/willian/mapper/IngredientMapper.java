package br.com.willian.mapper;

import br.com.willian.dto.v1.IngredientDTO;
import br.com.willian.model.Ingredient;
import br.com.willian.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    Logger logger = LoggerFactory.getLogger(IngredientMapper.class);

    /**
     * [ING-MAPPER-001] Converte Ingredient → IngredientDTO
     */
    @Mapping(source = "preferredSupplier.id", target = "preferredSupplierId")
    @Mapping(source = "preferredSupplier.name", target = "preferredSupplierName")
    IngredientDTO toDTO(Ingredient ingredient);

    /**
     * [ING-MAPPER-002] Converte IngredientDTO → Ingredient
     */
    @Mapping(target = "preferredSupplier", source = "preferredSupplierId", qualifiedByName = "mapSupplierIdToSupplier")
    Ingredient toEntity(IngredientDTO ingredientDTO);

    /**
     * [ING-MAPPER-003] Converte lista de Ingredient → lista de IngredientDTO
     */
    List<IngredientDTO> toDTOList(List<Ingredient> ingredientList);

    /**
     * [ING-MAPPER-004] Converte lista de IngredientDTO → lista de Ingredient
     */
    List<Ingredient> toEntityList(List<IngredientDTO> ingredientDTOList);

    /**
     * [ING-MAPPER-005] Converte Instant → OffsetDateTime (UTC)
     */
    default OffsetDateTime map(Instant instant) {
        if (instant == null) {
            logger.debug("[ING-MAPPER-005] Instant nulo recebido para conversão");
            return null;
        }
        logger.debug("[ING-MAPPER-005] Convertendo Instant para OffsetDateTime | instant={}", instant);
        OffsetDateTime result = instant.atZone(ZoneId.of("UTC")).toOffsetDateTime();
        logger.debug("[ING-MAPPER-005] Conversão concluída | result={}", result);
        return result;
    }

    /**
     * [ING-MAPPER-006] Converte OffsetDateTime → Instant
     */
    default Instant map(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            logger.debug("[ING-MAPPER-006] OffsetDateTime nulo recebido para conversão");
            return null;
        }
        logger.debug("[ING-MAPPER-006] Convertendo OffsetDateTime para Instant | offsetDateTime={}", offsetDateTime);
        Instant result = offsetDateTime.toInstant();
        logger.debug("[ING-MAPPER-006] Conversão concluída | result={}", result);
        return result;
    }

    /**
     * [ING-MAPPER-007] Converte página de Ingredient → página de IngredientDTO
     */
    default Page<IngredientDTO> toDTOPage(Page<Ingredient> ingredientsPage) {
        logger.debug("[ING-MAPPER-007] Convertendo Page<Ingredient> para Page<IngredientDTO>");

        if (ingredientsPage == null) {
            logger.warn("[ING-MAPPER-007] Página de entidades nula, retornando página vazia");
            return Page.empty();
        }

        logger.debug("[ING-MAPPER-007] Página recebida | pageNumber={} | pageSize={} | totalElements={} | contentSize={}",
                ingredientsPage.getNumber(), ingredientsPage.getSize(),
                ingredientsPage.getTotalElements(), ingredientsPage.getContent().size());

        List<IngredientDTO> dtoList = toDTOList(ingredientsPage.getContent());
        logger.debug("[ING-MAPPER-007] Conteúdo convertido | dtoListSize={}", dtoList.size());

        Page<IngredientDTO> result = new PageImpl<>(
                dtoList,
                ingredientsPage.getPageable(),
                ingredientsPage.getTotalElements()
        );

        logger.debug("[ING-MAPPER-007] Conversão concluída | pageNumber={} | pageSize={} | totalElements={}",
                result.getNumber(), result.getSize(), result.getTotalElements());

        return result;
    }

    /**
     * [ING-MAPPER-008] Converte página de IngredientDTO → página de Ingredient
     */
    default Page<Ingredient> toEntityPage(Page<IngredientDTO> dtoPage) {
        logger.debug("[ING-MAPPER-008] Convertendo Page<IngredientDTO> para Page<Ingredient>");

        if (dtoPage == null) {
            logger.warn("[ING-MAPPER-008] Página de DTOs nula, retornando página vazia");
            return Page.empty();
        }

        logger.debug("[ING-MAPPER-008] Página recebida | pageNumber={} | pageSize={} | totalElements={} | contentSize={}",
                dtoPage.getNumber(), dtoPage.getSize(),
                dtoPage.getTotalElements(), dtoPage.getContent().size());

        List<Ingredient> entityList = toEntityList(dtoPage.getContent());
        logger.debug("[ING-MAPPER-008] Conteúdo convertido | entityListSize={}", entityList.size());

        Page<Ingredient> result = new PageImpl<>(
                entityList,
                dtoPage.getPageable(),
                dtoPage.getTotalElements()
        );

        logger.debug("[ING-MAPPER-008] Conversão concluída | pageNumber={} | pageSize={} | totalElements={}",
                result.getNumber(), result.getSize(), result.getTotalElements());

        return result;
    }

    /**
     * [ING-MAPPER-009] Mapeia ID do fornecedor para entidade Supplier
     */
    @Named("mapSupplierIdToSupplier")
    default Supplier mapSupplierIdToSupplier(Long supplierId) {
        if (supplierId == null) {
            return null;
        }
        Supplier supplier = new Supplier();
        supplier.setId(supplierId);
        return supplier;
    }
}