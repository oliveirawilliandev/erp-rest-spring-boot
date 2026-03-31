package br.com.willian.security.jwt; // Pacote da camada de segurança JWT

import br.com.willian.dto.v1.security.TokenDTO; // DTO para tokens JWT
import com.auth0.jwt.JWT; // Biblioteca JWT
import com.auth0.jwt.JWTVerifier; // Verificador de tokens JWT
import com.auth0.jwt.algorithms.Algorithm; // Algoritmo de assinatura JWT
import com.auth0.jwt.interfaces.DecodedJWT; // Token JWT decodificado
import jakarta.annotation.PostConstruct; // Executa após construção do bean
import jakarta.servlet.http.HttpServletRequest; // Requisição HTTP
import org.apache.commons.lang3.StringUtils; // Utilitários para string
import org.slf4j.Logger; // Interface de logging
import org.slf4j.LoggerFactory; // Factory para logger
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.beans.factory.annotation.Value; // Injeta valores de propriedades
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Token de autenticação
import org.springframework.security.core.Authentication; // Interface de autenticação
import org.springframework.security.core.userdetails.UserDetails; // Detalhes do usuário
import org.springframework.security.core.userdetails.UserDetailsService; // Serviço de usuário
import org.springframework.stereotype.Service; // Marca como serviço
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // Builder para URIs

import java.util.Base64; // Codificação Base64
import java.util.Date; // Manipulação de datas
import java.util.List; // Interface List

@Service // Define a classe como um serviço Spring
public class JwtTokenProvider {



    // Logger para rastreamento
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${security.jwt.token.secret-key: secret}") // Injeta secret-key das propriedades
    private String secretKey = "secret"; // Chave secreta para assinatura (valor padrão)

    @Value("${security.jwt.token.lenght: 3600000}") // Injeta validade das propriedades
    private long validityInMilliseconds = 3600000; // Validade do token em ms (1h padrão)

    @Autowired // Injeção de dependência
    private UserDetailsService userDetailsService; // Serviço para carregar detalhes do usuário

    Algorithm algorithm = null; // Algoritmo de assinatura JWT

    // [JWT-PROVIDER-001] Inicializa o componente após construção
    @PostConstruct
    protected void init() {
        logger.debug("[JWT-PROVIDER-001] Inicializando JwtTokenProvider");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes()); // Codifica secretKey em Base64
        algorithm = Algorithm.HMAC256(secretKey); // Cria algoritmo HMAC256 com a chave
        logger.debug("[JWT-PROVIDER-001] Algoritmo JWT inicializado");
    }

    // [JWT-PROVIDER-002] Cria token de acesso
    public TokenDTO createAccessToken(String username, List<String> roles) {
        logger.debug("[JWT-PROVIDER-002] Criando access token | username={}", username);

        Date now = new Date(); // Data atual
        Date validity = new Date(now.getTime() + validityInMilliseconds); // Data de expiração

        String accessToken = getAccessToken(username, roles, now, validity); // Gera access token
        String refreshToken = getRefreshToken(username, roles, now); // Gera refresh token

        logger.debug("[JWT-PROVIDER-002] Tokens criados | username={}", username);
        return new TokenDTO(username, true, now, validity, accessToken, refreshToken);
    }

    // [JWT-PROVIDER-003] Renova access token usando refresh token
    public TokenDTO refreshAccessToken(String refreshToken) {
        logger.debug("[JWT-PROVIDER-003] Renovando access token");

        String token = "";
        if (refreshTokenContainsBearer(refreshToken)) {
            token = refreshToken.substring("Bearer ".length()); // Remove prefixo Bearer
        }

        JWTVerifier verifier = JWT.require(algorithm).build(); // Cria verificador
        DecodedJWT decodedJWT = verifier.verify(token); // Verifica e decodifica token

        String username = decodedJWT.getSubject(); // Extrai username
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class); // Extrai roles

        logger.info("[JWT-PROVIDER-003] Token renovado | username={}", username);
        return createAccessToken(username, roles); // Cria novo access token
    }

    // [JWT-PROVIDER-004] Gera access token
    private String getAccessToken(String username, List<String> roles, Date now, Date validity) {
        logger.debug("[JWT-PROVIDER-004] Gerando access token | username={}", username);

        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(); // URL base ex : "http://localhost:8080/"

        return JWT.create()
                .withClaim("roles", roles) // Adiciona roles como claims
                .withIssuedAt(now) // Data de emissão
                .withExpiresAt(validity) // Data de expiração
                .withSubject(username) // Subject (username)
                .withIssuer(issuerUrl) // Issuer (URL da aplicação)
                .sign(algorithm); // Assina com algoritmo
    }

    // [JWT-PROVIDER-005] Gera refresh token (validade 3x maior)
    private String getRefreshToken(String username, List<String> roles, Date now) {
        logger.debug("[JWT-PROVIDER-005] Gerando refresh token | username={}", username);

        Date refreshTokenValidity = new Date(now.getTime() + validityInMilliseconds * 3); // 3x a validade normal

        return JWT.create()
                .withClaim("roles", roles) // Adiciona roles
                .withIssuedAt(now) // Data de emissão
                .withExpiresAt(refreshTokenValidity) // Data de expiração estendida
                .withSubject(username) // Subject
                .sign(algorithm); // Assina
    }

    // [JWT-PROVIDER-006] Obtém autenticação a partir do token
    public Authentication getAuthentication(String token) {
        logger.debug("[JWT-PROVIDER-006] Obtendo autenticação do token");

        DecodedJWT decodedJWT = decodedToken(token); // Decodifica token
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject()); // Carrega usuário

        logger.debug("[JWT-PROVIDER-006] Autenticação criada | username={}", decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()); // Retorna autenticação
    }

    // [JWT-PROVIDER-007] Decodifica token JWT
    private DecodedJWT decodedToken(String token) {
        logger.trace("[JWT-PROVIDER-007] Decodificando token");

        Algorithm alg = Algorithm.HMAC256(secretKey.getBytes()); // Algoritmo com secretKey
        JWTVerifier verifier = JWT.require(alg).build(); // Cria verificador
        DecodedJWT decodedJWT = verifier.verify(token); // Verifica e decodifica

        logger.trace("[JWT-PROVIDER-007] Token decodificado");
        return decodedJWT;
    }

    // [JWT-PROVIDER-008] Extrai token do header Authorization
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Obtém header

        if (refreshTokenContainsBearer(bearerToken)) {
            String token = bearerToken.substring("Bearer ".length()); // Remove prefixo
            logger.debug("[JWT-PROVIDER-008] Token extraído do header");
            return token;
        }
    /*  Nenhum token JWT presente na requisição.
        Isso é esperado em endpoints públicos como /auth/signin (login),
        onde o cliente ainda não possui um token.
        Também cobre casos onde o header Authorization não existe
        ou não está no formato "Bearer <token>".
        A requisição seguirá sem autenticação no contexto de segurança.*/
        return null;
    }

    // [JWT-PROVIDER-009] Verifica se token contém prefixo Bearer
    private static boolean refreshTokenContainsBearer(String refreshToken) {
        return StringUtils.isNotBlank(refreshToken) && refreshToken.startsWith("Bearer ");
    }

    // [JWT-PROVIDER-010] Valida token JWT
    public boolean validateToken(String token) {
        logger.debug("[JWT-PROVIDER-010] Validando token");

        try {
            DecodedJWT decodedJWT = decodedToken(token); // Decodifica

            if (decodedJWT.getExpiresAt().before(new Date())) { // Verifica se expirou "Verica se a data de agora é antes do expirado"
                logger.warn("[JWT-PROVIDER-010] Token expirado");
                return false;
            }

            logger.debug("[JWT-PROVIDER-010] Token válido");
            return true;

        } catch (Exception e) {
            logger.error("[JWT-PROVIDER-010] Token inválido: {}", e.getMessage());

            return false;
        }
    }
}