package br.com.willian.config; // Pacote de configuração

import br.com.willian.security.jwt.JwtTokenFilter; // Filtro JWT
import br.com.willian.security.jwt.JwtTokenProvider; // Provedor JWT
import org.slf4j.Logger; // Interface de logging
import org.slf4j.LoggerFactory; // Factory para logger
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.context.annotation.Bean; // Define bean Spring
import org.springframework.context.annotation.Configuration; // Classe de configuração
import org.springframework.security.authentication.AuthenticationManager; // Gerenciador de autenticação
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Configuração de autenticação
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Configuração HTTP Security
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Habilita segurança web
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Desabilitar configurações padrão
import org.springframework.security.config.http.SessionCreationPolicy; // Política de sessão
import org.springframework.security.crypto.password.DelegatingPasswordEncoder; // Delegador de codificadores
import org.springframework.security.crypto.password.PasswordEncoder; // Codificador de senhas
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder; // Codificador PBKDF2
import org.springframework.security.web.SecurityFilterChain; // Cadeia de filtros
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Filtro de autenticação padrão

import java.util.HashMap; // Implementação de Map
import java.util.Map; // Interface Map

@EnableWebSecurity // Habilita segurança Spring
@Configuration // Classe de configuração Spring
public class SecurityConfig {

    // Logger para rastreamento
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired // Injeção de dependência
    private JwtTokenProvider jwtTokenProvider; // Provedor para operações com tokens


    // [SEC-CONFIG-001] Construtor com injeção
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        logger.debug("[SEC-CONFIG-001] Inicializando SecurityConfig");
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // [SEC-CONFIG-002] Configura codificador de senhas (PBKDF2)
    @Bean
    PasswordEncoder passwordEncoder() {
        logger.debug("[SEC-CONFIG-002] Configurando PasswordEncoder (PBKDF2)");

        // Cria encoder PBKDF2 com parâmetros de segurança
        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", // Salt
                8, // Iterações
                185000, // Tamanho do hash
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256); // Algoritmo

        Map<String, PasswordEncoder> encoders = new HashMap<>(); // Mapa de encoders
        encoders.put("pbkdf2", pbkdf2Encoder); // Registra encoder

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders); // Delegador
        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder); // Encoder padrão

        logger.debug("[SEC-CONFIG-002] PasswordEncoder configurado: PBKDF2WithHmacSHA256");

        //Gera um token baseado em um valor
        //String valorToken = "123456";
        //logger.info("Token Criptografado Valor " + valorToken+ " : "+passwordEncoder.encode(valorToken));
        return passwordEncoder;
    }

    // [SEC-CONFIG-003] Configura AuthenticationManager
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        logger.debug("[SEC-CONFIG-003] Configurando AuthenticationManager");
        return configuration.getAuthenticationManager(); // Obtém gerenciador padrão
    }

    // [SEC-CONFIG-004] Configura cadeia de filtros de segurança
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.debug("[SEC-CONFIG-004] Configurando SecurityFilterChain");

        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider); // Cria filtro JWT

        //@formatter:off

        logger.info("[SEC-CONFIG-004A] ALERTA GRAVE DE SEGURANÇA \"/auth/createUser\" esta com Acesso liberado ");
        return http
                .httpBasic(AbstractHttpConfigurer::disable) // Desabilita autenticação básica
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF (stateless)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // Adiciona filtro JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Sessão stateless "Nao guarda o estado da sessão"
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers( // URLs públicas (sem autenticação)
                                "/auth/signin", // Login
                                "/auth/refresh/**", // Refresh token
                                "/auth/createUser", // url para criação de user commum "COMMON_USER"
                                "/swagger-ui/**", // Swagger UI
                                "/v3/api-docs/**" // OpenAPI docs
                        ).permitAll()
                        .requestMatchers("/api/**").hasRole("COMMON_USER") // URLs da API requerem autenticação
                        .requestMatchers("/auth/createAdmin").hasRole("CREATE_USERS") // URL createUser da API requerem autenticação para nao deixa qualquer usuario criar conta.
                        .requestMatchers("/users").denyAll() // Bloqueia acesso a /users


                )

                .cors(cors -> {}) // Habilita CORS (configuração externa)
                .build();
        //@formatter:on
    }
}