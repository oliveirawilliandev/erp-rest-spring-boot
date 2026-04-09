package br.com.willian.services; // Pacote da camada de serviço

import br.com.willian.repository.UserRepository; // Repository para acesso aos dados de usuário
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.security.core.userdetails.UserDetails; // Interface do Spring Security
import org.springframework.security.core.userdetails.UserDetailsService; // Interface de serviço de usuário
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exceção para usuário não encontrado
import org.springframework.stereotype.Service; // Marca a classe como Service

@Service // Define a classe como um serviço Spring
public class UserService implements UserDetailsService { // Implementa a interface do Spring Security

    // Logger para rastreamento
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired // Injeção de dependência
    UserRepository userRepository; // Repository para acesso aos dados de usuário

    // [USER-SRV-001] Construtor com injeção
    public UserService(UserRepository userRepository) {
        logger.debug("[USER-SRV-001] Inicializando UserService");
        this.userRepository = userRepository;
    }

    // [USER-SRV-002] Carrega usuário pelo username (método obrigatório do Spring Security)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        logger.debug("[USER-SRV-002] Buscando usuário | username={}", username);

        // Validação básica
        if (username == null || username.trim().isEmpty()) {
            logger.error("[USER-SRV-002] Username inválido");
            throw new UsernameNotFoundException("Username não pode ser vazio");
        }

        // Busca no banco
        var user = userRepository.findbyUsername(username);

        if (user != null) {
            logger.debug("[USER-SRV-002] Usuário encontrado | username={}", username);
            return user;
        } else {
            logger.debug("[USER-SRV-002] Usuário não encontrado | username={}", username);
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }
    }
}