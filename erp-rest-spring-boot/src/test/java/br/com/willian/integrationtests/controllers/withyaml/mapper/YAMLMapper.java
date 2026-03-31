package br.com.willian.integrationtests.controllers.withyaml.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;

/*
 * Mapper customizado para permitir o uso de YAML nos testes com RestAssured.
 *
 * Motivo da existência desta classe:
 * ---------------------------------
 * O RestAssured NÃO possui suporte nativo completo para serialização e
 * desserialização de YAML.
 *
 * Por isso, implementamos a interface io.restassured.mapper.ObjectMapper
 * para controlar manualmente:
 *
 *  - Como objetos Java são convertidos em YAML (serialize)
 *  - Como YAML é convertido de volta para objetos Java (deserialize)
 *
 * Esta abordagem segue exatamente o padrão utilizado no curso,
 * garantindo compatibilidade total com YAML.
 */
public class YAMLMapper implements ObjectMapper {

    // ObjectMapper do Jackson configurado especificamente para YAML
    // Ele é responsável por converter YAML <-> Objetos Java
    private com.fasterxml.jackson.databind.ObjectMapper mapper;

    // TypeFactory usada para resolver tipos genéricos durante a desserialização
    // Necessária para que o Jackson saiba exatamente qual tipo criar
    protected TypeFactory typeFactory;

    /*
     * Construtor padrão.
     *
     * Aqui configuramos:
     *  - YAMLFactory → habilita suporte ao formato YAML
     *  - FAIL_ON_UNKNOWN_PROPERTIES → desativado para evitar erros
     *    quando a API retorna campos não mapeados nos DTOs
     */
    public YAMLMapper() {
        mapper = new com.fasterxml.jackson.databind.ObjectMapper(new YAMLFactory());

        // Evita falha caso o YAML contenha propriedades extras
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Registra módulo para suporte a Java 8 Time (LocalDate, OffsetDateTime, etc.)
        // Sem esta configuração, as datas seriam serializadas como objetos vazios ou arrays numéricos
        mapper.registerModule(new JavaTimeModule()); // Suporte a LocalDate, OffsetDateTime
        // Garante que datas sejam escritas no formato ISO-8601 (ex: 1990-01-01) em vez de timestamps numéricos
        // Essencial para que o backend receba as datas no formato esperado e evite erros de constraint
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Formato ISO (1990-01-01)

        // Inicializa a fábrica de tipos padrão do Jackson
        typeFactory = TypeFactory.defaultInstance();
    }

    /*
     * Metodo chamado automaticamente pelo RestAssured
     * para converter o BODY da resposta (YAML) em um objeto Java.
     *
     * Fluxo:
     *  1. Obtém o conteúdo da resposta como String (YAML puro)
     *  2. Obtém o tipo de destino esperado (DTO, Wrapper, etc.)
     *  3. Usa o Jackson para converter YAML -> Objeto Java
     */
    @Override
    public Object deserialize(ObjectMapperDeserializationContext context) {

        // Converte o corpo da resposta para String (YAML)
        var content = context.getDataToDeserialize().asString();

        // Tipo esperado para desserialização (ex: EmployeesDTO.class)
        Class type = (Class) context.getType();

        try {
            // Converte o YAML para o tipo Java informado
            return mapper.readValue(content, typeFactory.constructType(type));
        } catch (JsonProcessingException e) {
            // Erro lançado caso o YAML esteja malformado ou incompatível com o DTO
            throw new IllegalArgumentException("erro deserializing YML content", e);
        }
    }

    /*
     * Método chamado automaticamente pelo RestAssured
     * para converter um objeto Java em YAML antes de enviá-lo no request body.
     *
     * Observação importante:
     * ----------------------
     * Para YAML, o RestAssured só consegue enviar corretamente
     * quando o corpo é uma String.
     *
     * Por isso este método SEMPRE retorna uma String YAML.
     */
    @Override
    public Object serialize(ObjectMapperSerializationContext context) {
        try {
            // Converte o objeto Java em uma String no formato YAML
            return mapper.writeValueAsString(context.getObjectToSerialize());
        } catch (JsonProcessingException e) {
            // Erro lançado caso o objeto não possa ser convertido para YAML
            throw new IllegalArgumentException("erro serializing YML content", e);
        }
    }
}
