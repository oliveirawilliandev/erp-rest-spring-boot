package br.com.willian.integrationtests.testcontainers;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Classe base para testes de integração.
 *
 * Responsável por:
 * - Subir um container PostgreSQL usando Testcontainers
 * - Injetar dinamicamente as propriedades de conexão no Spring Context
 *
 * Deve ser estendida por todos os testes de integração que dependem de banco.
 */
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)

public class AbstractIntegrationTest {

    /**
     * Initializer customizado do Spring.
     *
     * É executado ANTES do contexto da aplicação ser inicializado,
     * permitindo sobrescrever propriedades como datasource.
     */
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        /**
         * Container PostgreSQL estático.
         *
         * - Sobe apenas uma vez para toda a suíte de testes
         * - Usa imagem específica para evitar variações entre versões
         */
        static PostgreSQLContainer<?> postgresql =
                new PostgreSQLContainer<>("postgres:13.22");

        /**
         * Inicializa e garante que todos os containers sejam iniciados
         * antes da aplicação tentar acessar o banco.
         *
         * deepStart permite inicialização correta mesmo com múltiplos containers.
         */
        private static void startContainers() {
            Startables.deepStart(Stream.of(postgresql)).join();
        }

        /**
         * Método chamado automaticamente pelo Spring Test Context.
         *
         * Aqui:
         * - Inicia o container
         * - Injeta as propriedades de conexão no Environment
         */
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            // Garante que o PostgreSQL esteja rodando
            startContainers();

            // Obtém o environment configurável do Spring
            ConfigurableEnvironment environment = applicationContext.getEnvironment();

            // Cria um PropertySource com as configurações vindas do container
            MapPropertySource testcontainers =
                    new MapPropertySource(
                            "testcontainers",
                            (Map) createConnectionConfiguration()
                    );

            // Adiciona como primeira fonte para sobrescrever application.yml/properties
            environment.getPropertySources().addFirst(testcontainers);
        }

        /**
         * Cria o mapa de propriedades de conexão do datasource.
         *
         * Essas propriedades substituem automaticamente:
         * - spring.datasource.url
         * - spring.datasource.username
         * - spring.datasource.password
         *
         * Evita dependência de banco local ou configurações externas.
         */
        private Map<String, String> createConnectionConfiguration() {
            return Map.of(
                    "spring.datasource.url", postgresql.getJdbcUrl(),
                    "spring.datasource.username", postgresql.getUsername(),
                    "spring.datasource.password", postgresql.getPassword()
            );
        }
    }
}
