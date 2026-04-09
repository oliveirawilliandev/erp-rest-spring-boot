package br.com.willian.mapper; // Pacote da camada de mapper

import br.com.willian.dto.v1.EmployeesDTO; // DTO para transferência de dados
import br.com.willian.model.Employees; // Entidade JPA
import org.mapstruct.Mapper; // Anotação do MapStruct
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.data.domain.Page; // Interface de página do Spring
import org.springframework.data.domain.PageImpl; // Implementação de página

import java.time.Instant; // Representação de timestamp
import java.time.OffsetDateTime; // Data/hora com offset
import java.time.ZoneId; // Zona de fuso horário
import java.util.List; // Interface List

/**
 * Mapper responsável por converter objetos da entidade {@link Employees} para {@link EmployeesDTO} e vice-versa.
 * Utiliza MapStruct para automação das conversões entre objetos e fornece métodos auxiliares para conversão
 * de listas e páginas, além de tratamento de datas entre {@link Instant} e {@link OffsetDateTime}.
 * Este Mapper é configurado como componente Spring, podendo ser injetado em serviços ou controladores.
 */
@Mapper(componentModel = "spring") // Configura o MapStruct para gerar implementação como componente Spring
public interface EmployeesMapper {

    // Logger para rastreamento (não pode ser estático em interface, mas o MapStruct gerará a implementação)
    Logger logger = LoggerFactory.getLogger(EmployeesMapper.class); // Logger para rastreamento

    /**
     * [EMP-MAPPER-001]
     * Converte uma instância de {@link Employees} para {@link EmployeesDTO}.
     * @param employees a entidade a ser convertida
     * @return o DTO correspondente ou {@code null} se a entidade for {@code null}
     */
    EmployeesDTO toDTO(Employees employees); // Método MapStruct para conversão entidade → DTO

    /**
     * [EMP-MAPPER-002]
     * Converte uma instância de {@link EmployeesDTO} para {@link Employees}.
     * @param employeesDTO o DTO a ser convertido
     * @return a entidade correspondente ou {@code null} se o DTO for {@code null}
     */
    Employees toEntity(EmployeesDTO employeesDTO); // Método MapStruct para conversão DTO → entidade

    /**
     * [EMP-MAPPER-003]
     * Converte uma lista de entidades {@link Employees} para uma lista de {@link EmployeesDTO}.
     * @param employeesList lista de entidades
     * @return lista de DTOs correspondente ou lista vazia se a entrada for {@code null}
     */
    List<EmployeesDTO> toDTOList(List<Employees> employeesList); // Método MapStruct para conversão lista de entidades → lista de DTOs

    /**
     * [EMP-MAPPER-004]
     * Converte uma lista de {@link EmployeesDTO} para uma lista de entidades {@link Employees}.
     * @param employeesDTOList lista de DTOs
     * @return lista de entidades correspondente ou lista vazia se a entrada for {@code null}
     */
    List<Employees> toEntityList(List<EmployeesDTO> employeesDTOList); // Método MapStruct para conversão lista de DTOs → lista de entidades

    /**
     * [EMP-MAPPER-005]
     * Converte um {@link Instant} para {@link OffsetDateTime} no fuso horário UTC.
     *
     * @param instant a data/hora em {@link Instant}
     * @return {@link OffsetDateTime} correspondente ou {@code null} se o instant for {@code null}
     */
    default OffsetDateTime map(Instant instant) { // Método auxiliar para conversão Instant → OffsetDateTime
        if (instant == null) {
            logger.debug("[EMP-MAPPER-005] Instant nulo recebido para conversão"); // Log de debug
            return null; // Retorna nulo
        }

        logger.debug("[EMP-MAPPER-005] Convertendo Instant para OffsetDateTime | instant={}", instant); // Log da conversão
        OffsetDateTime result = instant.atZone(ZoneId.of("UTC")).toOffsetDateTime(); // Converte para UTC
        logger.debug("[EMP-MAPPER-005] Conversão concluída | result={}", result); // Log do resultado

        return result; // Retorna OffsetDateTime
    }

    /**
     * [EMP-MAPPER-006]
     * Converte um {@link OffsetDateTime} para {@link Instant}.
     * @param offsetDateTime a data/hora em {@link OffsetDateTime}
     * @return {@link Instant} correspondente ou {@code null} se o OffsetDateTime for {@code null}
     */
    default Instant map(OffsetDateTime offsetDateTime) { // Método auxiliar para conversão OffsetDateTime → Instant
        if (offsetDateTime == null) {
            logger.debug("[EMP-MAPPER-006] OffsetDateTime nulo recebido para conversão"); // Log de debug
            return null; // Retorna nulo
        }

        logger.debug("[EMP-MAPPER-006] Convertendo OffsetDateTime para Instant | offsetDateTime={}", offsetDateTime); // Log da conversão
        Instant result = offsetDateTime.toInstant(); // Converte para Instant
        logger.debug("[EMP-MAPPER-006] Conversão concluída | result={}", result); // Log do resultado

        return result; // Retorna Instant
    }

    /**
     * [EMP-MAPPER-007]
     * Converte uma página de entidades {@link Employees} para uma página de {@link EmployeesDTO}.
     * Este método preserva informações de paginação (como número da página, tamanho e total de elementos)
     * e converte o conteúdo da página utilizando {@link #toDTOList(List)}.
     * @param employeesPage página de entidades
     * @return página de DTOs correspondente ou página vazia se a entrada for {@code null}
     */
    default Page<EmployeesDTO> toDTOPage(Page<Employees> employeesPage) { // Método auxiliar para conversão de página
        logger.debug("[EMP-MAPPER-007] Convertendo Page<Employees> para Page<EmployeesDTO>"); // Log de início

        if (employeesPage == null) {
            logger.warn("[EMP-MAPPER-007] Página de entidades nula, retornando página vazia"); // Log de aviso
            return Page.empty(); // Retorna página vazia
        }

        logger.debug("[EMP-MAPPER-007] Página recebida | pageNumber={} | pageSize={} | totalElements={} | contentSize={}",
                employeesPage.getNumber(), employeesPage.getSize(),
                employeesPage.getTotalElements(), employeesPage.getContent().size()); // Log dos dados da página

        List<EmployeesDTO> dtoList = toDTOList(employeesPage.getContent()); // Converte conteúdo da página
        logger.debug("[EMP-MAPPER-007] Conteúdo convertido | dtoListSize={}", dtoList.size()); // Log do tamanho da lista convertida

        Page<EmployeesDTO> result = new PageImpl<>(
                dtoList, // Lista de DTOs
                employeesPage.getPageable(), // Informações de paginação originais
                employeesPage.getTotalElements() // Total de elementos
        );

        logger.debug("[EMP-MAPPER-007] Conversão concluída | pageNumber={} | pageSize={} | totalElements={}",
                result.getNumber(), result.getSize(), result.getTotalElements()); // Log de conclusão

        return result; // Retorna página de DTOs
    }

    /**
     * [EMP-MAPPER-008]
     * Converte uma página de {@link EmployeesDTO} para uma página de entidades {@link Employees}.
     * Este método preserva informações de paginação e converte o conteúdo da página utilizando {@link #toEntityList(List)}.
     * @param dtoPage página de DTOs
     * @return página de entidades correspondente ou página vazia se a entrada for {@code null}
     */
    default Page<Employees> toEntityPage(Page<EmployeesDTO> dtoPage) { // Método auxiliar para conversão de página DTO → entidade
        logger.debug("[EMP-MAPPER-008] Convertendo Page<EmployeesDTO> para Page<Employees>"); // Log de início

        if (dtoPage == null) {
            logger.warn("[EMP-MAPPER-008] Página de DTOs nula, retornando página vazia"); // Log de aviso
            return Page.empty(); // Retorna página vazia
        }

        logger.debug("[EMP-MAPPER-008] Página recebida | pageNumber={} | pageSize={} | totalElements={} | contentSize={}",
                dtoPage.getNumber(), dtoPage.getSize(),
                dtoPage.getTotalElements(), dtoPage.getContent().size()); // Log dos dados da página

        List<Employees> entityList = toEntityList(dtoPage.getContent()); // Converte conteúdo da página
        logger.debug("[EMP-MAPPER-008] Conteúdo convertido | entityListSize={}", entityList.size()); // Log do tamanho da lista convertida

        Page<Employees> result = new PageImpl<>(
                entityList, // Lista de entidades
                dtoPage.getPageable(), // Informações de paginação originais
                dtoPage.getTotalElements() // Total de elementos
        );

        logger.debug("[EMP-MAPPER-008] Conversão concluída | pageNumber={} | pageSize={} | totalElements={}",
                result.getNumber(), result.getSize(), result.getTotalElements()); // Log de conclusão

        return result; // Retorna página de entidades
    }
}