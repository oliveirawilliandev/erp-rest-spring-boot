package br.com.willian.service; // Pacote da camada de serviço

import br.com.willian.dto.v1.security.UserProfileDTO;
import br.com.willian.exception.ResourceNotFoundException;
import br.com.willian.repository.UserRepository; // Repository para acesso aos dados de usuário
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Interface do Spring Security
import org.springframework.security.core.userdetails.UserDetailsService; // Interface de serviço de usuário
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exceção para usuário não encontrado
import org.springframework.stereotype.Service; // Marca a classe como Service
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void updateUserPhoto(Long id, String photoUrl) {
        logger.info("[USER-SRV-003] Atualizando foto do usuário | userId={} | photoUrl={}", id, photoUrl);

        var user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("[USER-SRV-003] Usuário não encontrado | userId={}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        user.setPhotoUrl(photoUrl);
        userRepository.save(user);

        logger.info("[USER-SRV-003] Foto atualizada com sucesso | userId={}", id);
    }

    // No AuthService.java
    public UserProfileDTO getCurrentUserProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        logger.debug("[USER-SRV-005] Buscando perfil | username={}", username);

        var user = userRepository.findbyUsername(username);

        if (user == null) {
            logger.warn("[USER-SRV-005] Usuário não encontrado | username={}", username);
            return null;
        }

        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setUserName(user.getUsername());
        profile.setFullName(user.getFullName());
        profile.setEmail(user.getEmail());
        profile.setPhotoUrl(user.getPhotoUrl());

        return profile;
    }
}