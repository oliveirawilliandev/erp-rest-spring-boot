package br.com.willian.config;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.yaml.MappingJackson2YamlHttpMessageConverter;

@Configuration
public class YamlConfig {

    @Bean
    public MappingJackson2YamlHttpMessageConverter yamlHttpMessageConverter() {
        YAMLMapper mapper = new YAMLMapper();

        // Registrar módulo para suporte a datas Java 8
        mapper.registerModule(new JavaTimeModule());

        // Configurar para não escrever datas como timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Configurar para escrever datas como strings ISO
        mapper.findAndRegisterModules();

        return new MappingJackson2YamlHttpMessageConverter(mapper);
    }
}
