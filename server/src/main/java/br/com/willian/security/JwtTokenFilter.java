package br.com.willian.security; // Pacote da camada de segurança JWT

import jakarta.servlet.FilterChain; // Cadeia de filtros
import jakarta.servlet.ServletException; // Exceção de servlet
import jakarta.servlet.ServletRequest; // Requisição genérica
import jakarta.servlet.ServletResponse; // Resposta genérica
import jakarta.servlet.http.HttpServletRequest; // Requisição HTTP
import org.apache.commons.lang3.StringUtils; // Utilitários para string
import org.slf4j.Logger; // Interface de logging
import org.slf4j.LoggerFactory; // Factory para logger
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.security.core.Authentication; // Interface de autenticação
import org.springframework.security.core.context.SecurityContextHolder; // Contexto de segurança
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean; // Filtro genérico Spring

import java.io.IOException; // Exceção de I/O
@Service
public class JwtTokenFilter extends GenericFilterBean { // Filtro para processar tokens JWT

    // Logger para rastreamento
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired // Injeção de dependência
    private JwtTokenProvider jwtTokenProvider; // Provider para operações com tokens


    // [JWT-FILTER-001] Construtor com injeção
    public JwtTokenFilter(JwtTokenProvider tokenProvider) {
        logger.debug("[JWT-FILTER-001] Inicializando JwtTokenFilter");
        this.jwtTokenProvider = tokenProvider; // Atribui provider
    }

    // [JWT-FILTER-002] Filtro principal executado em cada requisição
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
            throws IOException, ServletException {

        logger.debug("[JWT-FILTER-002] Processando requisição");

        // Extrai token do header Authorization
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if (StringUtils.isNotBlank(token)) { // Se token existe
            logger.debug("[JWT-FILTER-002] Token encontrado, validando");

            if (jwtTokenProvider.validateToken(token)) { // Valida token
                logger.debug("[JWT-FILTER-002] Token válido, obtendo autenticação");

                Authentication authentication = jwtTokenProvider.getAuthentication(token); // Obtém autenticação

                if (authentication != null) {
                    // Define autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("[JWT-FILTER-002] Autenticação estabelecida | user={}",
                            authentication.getName());
                }
            } else {
                logger.debug("[JWT-FILTER-002] Token inválido ou expirado");
            }
        } else {
            logger.debug("[JWT-FILTER-002] Nenhum token encontrado na requisição");
        }

        // Continua a cadeia de filtros
        filter.doFilter(request, response);
        logger.trace("[JWT-FILTER-002] Requisição prosseguida na cadeia de filtros");
    }
}