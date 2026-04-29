package br.com.willian.mapper;

import br.com.willian.dto.v1.SupplierDTO;
import br.com.willian.model.Supplier;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Mapper responsável por converter objetos da entidade {@link Supplier} para {@link SupplierDTO} e vice-versa.
 * Utiliza MapStruct para automação das conversões entre objetos e fornece métodos auxiliares para conversão
 * de listas e páginas, além de tratamento de datas entre {@link Instant} e {@link OffsetDateTime}.
 * Este Mapper é configurado como componente Spring, podendo ser injetado em serviços ou controladores.
 */
@Mapper(componentModel = "spring") // Configura o MapStruct para gerar implementação como componente Spring
public interface SupplierMapper {

    // Logger para rastreamento (não pode ser estático em interface, mas o MapStruct gerará a implementação)
    Logger logger = LoggerFactory.getLogger(SupplierMapper.class); // Logger para rastreamento

    /**
     * [Supp-MAPPER-001]
     * Converte uma instância de {@link Supplier} para {@link SupplierDTO}.
     * @param supplier a entidade a ser convertida
     * @return o DTO correspondente ou {@code null} se a entidade for {@code null}
     */
    SupplierDTO toDTO(Supplier supplier); // Método MapStruct para conversão entidade → DTO

    /**
     * [Supp-MAPPER-002]
     * Converte uma instância de {@link SupplierDTO} para {@link Supplier}.
     * @param supplierDTO o DTO a ser convertido
     * @return a entidade correspondente ou {@code null} se o DTO for {@code null}
     */
    Supplier toEntity(SupplierDTO supplierDTO); // Método MapStruct para conversão DTO → entidade

    /**
     * [Supp-MAPPER-003]
     * Converte uma lista de entidades {@link Supplier} para uma lista de {@link SupplierDTO}.
     * @param supplierList lista de entidades
     * @return lista de DTOs correspondente ou lista vazia se a entrada for {@code null}
     */
    List<SupplierDTO> toDTOList(List<Supplier> supplierList); // Método MapStruct para conversão lista de entidades → lista de DTOs

    /**
     * [Supp-MAPPER-004]
     * Converte uma lista de {@link SupplierDTO} para uma lista de entidades {@link Supplier}.
     * @param supplierDTOList lista de DTOs
     * @return lista de entidades correspondente ou lista vazia se a entrada for {@code null}
     */
    List<Supplier> toEntityList(List<SupplierDTO> supplierDTOList); // Método MapStruct para conversão lista de DTOs → lista de entidades

    /**
     * [Supp-MAPPER-005]
     * Converte um {@link Instant} para {@link OffsetDateTime} no fuso horário UTC.
     *
     * @param instant a data/hora em {@link Instant}
     * @return {@link OffsetDateTime} correspondente ou {@code null} se o instant for {@code null}
     */
    default OffsetDateTime map(Instant instant) { // Método auxiliar para conversão Instant → OffsetDateTime
        if (instant == null) {
            logger.debug("[Supp-MAPPER-005] Instant nulo recebido para conversão"); // Log de debug
            return null; // Retorna nulo
        }

        logger.debug("[Supp-MAPPER-005] Convertendo Instant para OffsetDateTime | instant={}", instant); // Log da conversão
        OffsetDateTime result = instant.atZone(ZoneId.of("UTC")).toOffsetDateTime(); // Converte para UTC
        logger.debug("[Supp-MAPPER-005] Conversão concluída | result={}", result); // Log do resultado

        return result; // Retorna OffsetDateTime
    }

    /**
     * [Supp-MAPPER-006]
     * Converte um {@link OffsetDateTime} para {@link Instant}.
     * @param offsetDateTime a data/hora em {@link OffsetDateTime}
     * @return {@link Instant} correspondente ou {@code null} se o OffsetDateTime for {@code null}
     */
    default Instant map(OffsetDateTime offsetDateTime) { // Método auxiliar para conversão OffsetDateTime → Instant
        if (offsetDateTime == null) {
            logger.debug("[Supp-MAPPER-006] OffsetDateTime nulo recebido para conversão"); // Log de debug
            return null; // Retorna nulo
        }

        logger.debug("[Supp-MAPPER-006] Convertendo OffsetDateTime para Instant | offsetDateTime={}", offsetDateTime); // Log da conversão
        Instant result = offsetDateTime.toInstant(); // Converte para Instant
        logger.debug("[Supp-MAPPER-006] Conversão concluída | result={}", result); // Log do resultado

        return result; // Retorna Instant
    }

    /**
     * [Supp-MAPPER-007]
     * Converte uma página de entidades {@link Supplier} para uma página de {@link SupplierDTO}.
     * Este método preserva informações de paginação (como número da página, tamanho e total de elementos)
     * e converte o conteúdo da página utilizando {@link #toDTOList(List)}.
     * @param suppliersPage página de entidades
     * @return página de DTOs correspondente ou página vazia se a entrada for {@code null}
     */
    default Page<SupplierDTO> toDTOPage(Page<Supplier> suppliersPage) { // Método auxiliar para conversão de página
        logger.debug("[Supp-MAPPER-007] Convertendo Page<Supplier> para Page<SupplierDTO>"); // Log de início

        if (suppliersPage == null) {
            logger.warn("[Supp-MAPPER-007] Página de entidades nula, retornando página vazia"); // Log de aviso
            return Page.empty(); // Retorna página vazia
        }

        logger.debug("[Supp-MAPPER-007] Página recebida | pageNumber={} | pageSize={} | totalElements={} | contentSize={}",
                suppliersPage.getNumber(), suppliersPage.getSize(),
                suppliersPage.getTotalElements(), suppliersPage.getContent().size()); // Log dos dados da página

        List<SupplierDTO> dtoList = toDTOList(suppliersPage.getContent()); // Converte conteúdo da página
        logger.debug("[Supp-MAPPER-007] Conteúdo convertido | dtoListSize={}", dtoList.size()); // Log do tamanho da lista convertida

        Page<SupplierDTO> result = new PageImpl<>(
                dtoList, // Lista de DTOs
                suppliersPage.getPageable(), // Informações de paginação originais
                suppliersPage.getTotalElements() // Total de elementos
        );

        logger.debug("[Supp-MAPPER-007] Conversão concluída | pageNumber={} | pageSize={} | totalElements={}",
                result.getNumber(), result.getSize(), result.getTotalElements()); // Log de conclusão

        return result; // Retorna página de DTOs
    }

    /**
     * [Supp-MAPPER-008]
     * Converte uma página de {@link SupplierDTO} para uma página de entidades {@link Supplier}.
     * Este método preserva informações de paginação e converte o conteúdo da página utilizando {@link #toEntityList(List)}.
     * @param dtoPage página de DTOs
     * @return página de entidades correspondente ou página vazia se a entrada for {@code null}
     */
    default Page<Supplier> toEntityPage(Page<SupplierDTO> dtoPage) { // Método auxiliar para conversão de página DTO → entidade
        logger.debug("[Supp-MAPPER-008] Convertendo Page<SupplierDTO> para Page<Supplier>"); // Log de início

        if (dtoPage == null) {
            logger.warn("[Supp-MAPPER-008] Página de DTOs nula, retornando página vazia"); // Log de aviso
            return Page.empty(); // Retorna página vazia
        }

        logger.debug("[Supp-MAPPER-008] Página recebida | pageNumber={} | pageSize={} | totalElements={} | contentSize={}",
                dtoPage.getNumber(), dtoPage.getSize(),
                dtoPage.getTotalElements(), dtoPage.getContent().size()); // Log dos dados da página

        List<Supplier> entityList = toEntityList(dtoPage.getContent()); // Converte conteúdo da página
        logger.debug("[Supp-MAPPER-008] Conteúdo convertido | entityListSize={}", entityList.size()); // Log do tamanho da lista convertida

        Page<Supplier> result = new PageImpl<>(
                entityList, // Lista de entidades
                dtoPage.getPageable(), // Informações de paginação originais
                dtoPage.getTotalElements() // Total de elementos
        );

        logger.debug("[Supp-MAPPER-008] Conversão concluída | pageNumber={} | pageSize={} | totalElements={}",
                result.getNumber(), result.getSize(), result.getTotalElements()); // Log de conclusão

        return result; // Retorna página de entidades
    }
}
