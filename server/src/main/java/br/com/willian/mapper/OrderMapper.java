package br.com.willian.mapper;

import br.com.willian.dto.v1.OrderDTO;
import br.com.willian.model.Order;
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
 * Mapper responsável por converter objetos da entidade {@link Order} para {@link OrderDTO} e vice-versa.
 * Utiliza MapStruct para automação das conversões entre objetos e fornece métodos auxiliares para conversão
 * de listas e páginas, além de tratamento de datas entre {@link Instant} e {@link OffsetDateTime}.
 * Este Mapper é configurado como componente Spring, podendo ser injetado em serviços ou controladores.
 */
@Mapper(componentModel = "spring") // Configura o MapStruct para gerar implementação como componente Spring
public interface OrderMapper {

    // Logger para rastreamento (não pode ser estático em interface, mas o MapStruct gerará a implementação)
    Logger logger = LoggerFactory.getLogger(OrderMapper.class); // Logger para rastreamento

    /**
     * [ORD-MAPPER-001]
     * Converte uma instância de {@link Order} para {@link OrderDTO}.
     * @param order a entidade a ser convertida
     * @return o DTO correspondente ou {@code null} se a entidade for {@code null}
     */
    OrderDTO toDTO(Order order); // Método MapStruct para conversão entidade → DTO

    /**
     * [ORD-MAPPER-002]
     * Converte uma instância de {@link OrderDTO} para {@link Order}.
     * @param orderDTO o DTO a ser convertido
     * @return a entidade correspondente ou {@code null} se o DTO for {@code null}
     */
    Order toEntity(OrderDTO orderDTO); // Método MapStruct para conversão DTO → entidade

    /**
     * [ORD-MAPPER-003]
     * Converte uma lista de entidades {@link Order} para uma lista de {@link OrderDTO}.
     * @param orderList lista de entidades
     * @return lista de DTOs correspondente ou lista vazia se a entrada for {@code null}
     */
    List<OrderDTO> toDTOList(List<Order> orderList); // Método MapStruct para conversão lista de entidades → lista de DTOs

    /**
     * [ORD-MAPPER-004]
     * Converte uma lista de {@link OrderDTO} para uma lista de entidades {@link Order}.
     * @param orderDTOList lista de DTOs
     * @return lista de entidades correspondente ou lista vazia se a entrada for {@code null}
     */
    List<Order> toEntityList(List<OrderDTO> orderDTOList); // Método MapStruct para conversão lista de DTOs → lista de entidades

    /**
     * [ORD-MAPPER-005]
     * Converte um {@link Instant} para {@link OffsetDateTime} no fuso horário UTC.
     *
     * @param instant a data/hora em {@link Instant}
     * @return {@link OffsetDateTime} correspondente ou {@code null} se o instant for {@code null}
     */
    default OffsetDateTime map(Instant instant) { // Método auxiliar para conversão Instant → OffsetDateTime
        if (instant == null) {
            logger.debug("[ORD-MAPPER-005] Instant nulo recebido para conversão"); // Log de debug
            return null; // Retorna nulo
        }

        logger.debug("[ORD-MAPPER-005] Convertendo Instant para OffsetDateTime | instant={}", instant); // Log da conversão
        OffsetDateTime result = instant.atZone(ZoneId.of("UTC")).toOffsetDateTime(); // Converte para UTC
        logger.debug("[ORD-MAPPER-005] Conversão concluída | result={}", result); // Log do resultado

        return result; // Retorna OffsetDateTime
    }

    /**
     * [ORD-MAPPER-006]
     * Converte um {@link OffsetDateTime} para {@link Instant}.
     * @param offsetDateTime a data/hora em {@link OffsetDateTime}
     * @return {@link Instant} correspondente ou {@code null} se o OffsetDateTime for {@code null}
     */
    default Instant map(OffsetDateTime offsetDateTime) { // Método auxiliar para conversão OffsetDateTime → Instant
        if (offsetDateTime == null) {
            logger.debug("[ORD-MAPPER-006] OffsetDateTime nulo recebido para conversão"); // Log de debug
            return null; // Retorna nulo
        }

        logger.debug("[ORD-MAPPER-006] Convertendo OffsetDateTime para Instant | offsetDateTime={}", offsetDateTime); // Log da conversão
        Instant result = offsetDateTime.toInstant(); // Converte para Instant
        logger.debug("[ORD-MAPPER-006] Conversão concluída | result={}", result); // Log do resultado

        return result; // Retorna Instant
    }

    /**
     * [ORD-MAPPER-007]
     * Converte uma página de entidades {@link Order} para uma página de {@link OrderDTO}.
     * Este método preserva informações de paginação (como número da página, tamanho e total de elementos)
     * e converte o conteúdo da página utilizando {@link #toDTOList(List)}.
     * @param ordersPage página de entidades
     * @return página de DTOs correspondente ou página vazia se a entrada for {@code null}
     */
    default Page<OrderDTO> toDTOPage(Page<Order> ordersPage) { // Método auxiliar para conversão de página
        logger.debug("[ORD-MAPPER-007] Convertendo Page<Order> para Page<OrderDTO>"); // Log de início

        if (ordersPage == null) {
            logger.warn("[ORD-MAPPER-007] Página de entidades nula, retornando página vazia"); // Log de aviso
            return Page.empty(); // Retorna página vazia
        }

        logger.debug("[ORD-MAPPER-007] Página recebida | pageNumber={} | pageSize={} | totalElements={} | contentSize={}",
                ordersPage.getNumber(), ordersPage.getSize(),
                ordersPage.getTotalElements(), ordersPage.getContent().size()); // Log dos dados da página

        List<OrderDTO> dtoList = toDTOList(ordersPage.getContent()); // Converte conteúdo da página
        logger.debug("[ORD-MAPPER-007] Conteúdo convertido | dtoListSize={}", dtoList.size()); // Log do tamanho da lista convertida

        Page<OrderDTO> result = new PageImpl<>(
                dtoList, // Lista de DTOs
                ordersPage.getPageable(), // Informações de paginação originais
                ordersPage.getTotalElements() // Total de elementos
        );

        logger.debug("[ORD-MAPPER-007] Conversão concluída | pageNumber={} | pageSize={} | totalElements={}",
                result.getNumber(), result.getSize(), result.getTotalElements()); // Log de conclusão

        return result; // Retorna página de DTOs
    }

    /**
     * [ORD-MAPPER-008]
     * Converte uma página de {@link OrderDTO} para uma página de entidades {@link Order}.
     * Este método preserva informações de paginação e converte o conteúdo da página utilizando {@link #toEntityList(List)}.
     * @param dtoPage página de DTOs
     * @return página de entidades correspondente ou página vazia se a entrada for {@code null}
     */
    default Page<Order> toEntityPage(Page<OrderDTO> dtoPage) { // Método auxiliar para conversão de página DTO → entidade
        logger.debug("[ORD-MAPPER-008] Convertendo Page<OrderDTO> para Page<Order>"); // Log de início

        if (dtoPage == null) {
            logger.warn("[ORD-MAPPER-008] Página de DTOs nula, retornando página vazia"); // Log de aviso
            return Page.empty(); // Retorna página vazia
        }

        logger.debug("[ORD-MAPPER-008] Página recebida | pageNumber={} | pageSize={} | totalElements={} | contentSize={}",
                dtoPage.getNumber(), dtoPage.getSize(),
                dtoPage.getTotalElements(), dtoPage.getContent().size()); // Log dos dados da página

        List<Order> entityList = toEntityList(dtoPage.getContent()); // Converte conteúdo da página
        logger.debug("[ORD-MAPPER-008] Conteúdo convertido | entityListSize={}", entityList.size()); // Log do tamanho da lista convertida

        Page<Order> result = new PageImpl<>(
                entityList, // Lista de entidades
                dtoPage.getPageable(), // Informações de paginação originais
                dtoPage.getTotalElements() // Total de elementos
        );

        logger.debug("[ORD-MAPPER-008] Conversão concluída | pageNumber={} | pageSize={} | totalElements={}",
                result.getNumber(), result.getSize(), result.getTotalElements()); // Log de conclusão

        return result; // Retorna página de entidades
    }
}
