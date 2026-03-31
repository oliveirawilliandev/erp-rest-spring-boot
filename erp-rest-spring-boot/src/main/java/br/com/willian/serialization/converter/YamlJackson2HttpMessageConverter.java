package br.com.willian.serialization.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

// Converter HTTP customizado para trabalhar com YAML usando Jackson
// Essa classe permite que a aplicação Spring serialize e deserialize
// objetos Java para YAML e vice-versa em requisições/respostas HTTP
public final class YamlJackson2HttpMessageConverter
        extends AbstractJackson2HttpMessageConverter {

    // Construtor padrão do converter
    // É "package-private" de propósito, evitando uso indevido fora do módulo
    YamlJackson2HttpMessageConverter() {

        // Chama o construtor da classe pai (AbstractJackson2HttpMessageConverter)
        // passando:
        // 1) Um YAMLMapper (Jackson) configurado
        // 2) O MediaType que este converter suporta (application/yaml)
        super(
                new YAMLMapper()
                        // Ignora propriedades nulas durante a serialização
                        // Isso reduz o tamanho do payload YAML e evita campos desnecessários
                        .setSerializationInclusion(JsonInclude.Include.NON_NULL),

                // Define que este converter lida com o tipo MIME "application/yaml"
                MediaType.parseMediaType("application/yaml")
        );
    }
}