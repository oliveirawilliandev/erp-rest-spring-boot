package br.com.willian.services; // Pacote da camada de serviço

import br.com.willian.dto.v1.security.AccountCredentialAdminDTO;

import br.com.willian.dto.v1.security.AccountCredentialsDTO; // DTO para credenciais
import br.com.willian.dto.v1.security.TokenDTO; // DTO para tokens
import br.com.willian.dto.v1.security.enums.RoleEnum;
import br.com.willian.exception.RequiredObjectIsNullException; // Exceção para objeto nulo
import br.com.willian.mapper.AccountCredentialAdminMapper;
import br.com.willian.mapper.AccountCredentialsMapper; // Mapper para conversão
import br.com.willian.model.User; // Entidade User
import br.com.willian.repository.PermissionRepository;
import br.com.willian.repository.UserRepository; // Repository de usuário
import br.com.willian.security.jwt.JwtTokenProvider; // Provedor JWT
import org.slf4j.Logger; // Interface de logging
import org.slf4j.LoggerFactory; // Factory para logger
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.http.ResponseEntity; // Resposta HTTP
import org.springframework.security.authentication.AuthenticationManager; // Gerenciador de autenticação
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Token de autenticação
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exceção usuário não encontrado
import org.springframework.security.crypto.password.DelegatingPasswordEncoder; // Delegador de codificadores
import org.springframework.security.crypto.password.PasswordEncoder; // Codificador de senhas
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder; // Codificador PBKDF2
import org.springframework.stereotype.Service; // Marca como serviço

import java.util.HashMap; // Implementação de Map
import java.util.List;
import java.util.Map; // Interface Map
import java.util.Set;
import java.util.stream.Collectors;

@Service // Define a classe como um serviço Spring
public class AuthService {

    @Autowired // Injeção de dependência
    private AuthenticationManager authenticationManager; // Gerenciador de autenticação

    @Autowired // Injeção de dependência
    private JwtTokenProvider jwtTokenProvider; // Provedor JWT

    @Autowired // Injeção de dependência
    private UserRepository userRepository; // Repository de usuário

    @Autowired // Injeção de dependência
    private PermissionRepository permissionRepository; // Repository de PermissionUser

    @Autowired // Injeção de dependência
    private AccountCredentialsMapper accountCredentialsMapper; // Mapper para conversão

    @Autowired // Injeção de dependência
    private AccountCredentialAdminMapper accountCredentialAdminMapper; // Mapper para conversão

    Logger logger = LoggerFactory.getLogger(AuthService.class); // Logger para rastreamento

    // [AUTH-SRV-001] Autenticação de usuário (login)
    public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO accountCredentialsDTO) throws Exception {

        logger.debug("[AUTH-SRV-001] Tentativa de login | username={}", accountCredentialsDTO.getUserName());

        // Autentica com Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        accountCredentialsDTO.getUserName(),
                        accountCredentialsDTO.getPassword()
                )
        );

        // Busca usuário no banco
        var user = userRepository.findbyUsername(accountCredentialsDTO.getUserName());

        if (user == null) {
            logger.error("[AUTH-SRV-001] Usuário não encontrado | username={}", accountCredentialsDTO.getUserName());
            throw new UsernameNotFoundException("Username " + accountCredentialsDTO.getUserName() + " not found!");
        }

        // Gera token JWT
        var token = jwtTokenProvider.createAccessToken(accountCredentialsDTO.getUserName(), user.getRoles());

        logger.debug("[AUTH-SRV-001] Login bem-sucedido | username={}", accountCredentialsDTO.getUserName());
        return ResponseEntity.ok(token);
    }

    // [AUTH-SRV-002] Renovação de token (refresh)
    public ResponseEntity<TokenDTO> refreshToken(String userName, String refreshToken) {

        logger.info("[AUTH-SRV-002] Tentativa de refresh token | username={}", userName);

        var user = userRepository.findbyUsername(userName);

        if (user == null) {
            logger.error("[AUTH-SRV-002] Usuário não encontrado | username={}", userName);
            throw new UsernameNotFoundException("Username " + userName + " not found!");
        }

        TokenDTO token = jwtTokenProvider.refreshAccessToken(refreshToken);

        logger.info("[AUTH-SRV-002] Refresh token realizado | username={}", userName);
        return ResponseEntity.ok(token);
    }

    // [AUTH-SRV-003] Gera senha hasheada com PBKDF2
    private String generateHashedPassword(String password) {
        logger.debug("[AUTH-SRV-003] Gerando hash de senha");

        // Configura encoder PBKDF2
        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("",
                8,
                185000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", pbkdf2Encoder);

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);

        String hashed = passwordEncoder.encode(password);
        logger.debug("[AUTH-SRV-003] Hash gerado com sucesso");

        return hashed;
    }

    // [AUTH-SRV-004] Criação de novo usuário
    public AccountCredentialsDTO create(AccountCredentialsDTO accountCredentialsDTO) {

        if (accountCredentialsDTO.getUserName() == null || accountCredentialsDTO.getPassword() == null || accountCredentialsDTO.getFullName() == null ) {
            logger.error("[AUTH-SRV-004] DTO nulo recebido");
            throw new RequiredObjectIsNullException();
        }else if(accountCredentialsDTO.getFullName().trim().isEmpty()){
            logger.error("[AUTH-SRV-004] DTO FullName vazio recebido");
            throw new IllegalArgumentException("FullName não pode ser vazio");
        }

        logger.debug("[AUTH-SRV-004] Criando novo usuário | username={}", accountCredentialsDTO.getUserName());

        // Cria nova entidade User
        var entity = new User();
        entity.setFullName(accountCredentialsDTO.getFullName());
        entity.setUserName(accountCredentialsDTO.getUserName());
        entity.setPassword(generateHashedPassword(accountCredentialsDTO.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);


        // Busca permissão ROLE_COMMON_USER no banco ou lança exceção se não existir
        var roleUser = permissionRepository
                .findByDescription(RoleEnum.ROLE_COMMON_USER.name()) // Busca pelo nome da role
                .orElseThrow(() -> new RuntimeException("Permissão não encontrada")); // Exceção se não encontrar

        // Define a lista Set de permissões do COMMON usuário  (apenas com a Permissão ROLE_COMMON_USER)
        entity.setPermissions(Set.of(roleUser)); // Atribui permissão ao usuário

        // Salva e retorna DTO
        AccountCredentialsDTO result = accountCredentialsMapper.toDTO(userRepository.save(entity));

        logger.debug("[AUTH-SRV-004] Usuário criado com sucesso | username={}", result.getUserName());

        return result;
    }
    // [AUTH-SRV-005] Criação de novo usuário administrador
    public AccountCredentialAdminDTO createAdmin(AccountCredentialAdminDTO accountCredentialAdminDTO) {

        if (accountCredentialAdminDTO.getUserName() == null || accountCredentialAdminDTO.getPassword() == null || accountCredentialAdminDTO.getFullName() == null || accountCredentialAdminDTO.getRoles() == null || accountCredentialAdminDTO.getRoles().isEmpty() ) {
            logger.error("[AUTH-SRV-004] DTO nulo recebido");
            throw new RequiredObjectIsNullException();
        }else if(accountCredentialAdminDTO.getFullName().trim().isEmpty()){
            logger.error("[AUTH-SRV-004] DTO FullName vazio recebido");
            throw new IllegalArgumentException("FullName não pode ser vazio");
        }

        logger.debug("[AUTH-SRV-004] Criando novo Administrador | username={}", accountCredentialAdminDTO.getUserName());

        // Cria nova entidade User
        var entity = new User();
        entity.setFullName(accountCredentialAdminDTO.getFullName());
        entity.setUserName(accountCredentialAdminDTO.getUserName());
        entity.setPassword(generateHashedPassword(accountCredentialAdminDTO.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);

        // Converte a lista SET  de enums RoleEnum para lista de nomes (String)
        var roleNames = accountCredentialAdminDTO.getRoles().stream() // Stream das roles do DTO
                .map(Enum::name) // Converte cada enum para seu nome (ex: ROLE_ADMIN)
                .collect(Collectors.toSet()); // Coleta em Set

        // Adiciona a lista SET de permissões o COMMON usuário
        roleNames.add(RoleEnum.ROLE_COMMON_USER.name());

        // Busca no banco todas as permissões cujas descrições estão na lista de nomes
        var roles = permissionRepository.findByDescriptionIn(roleNames); // Consulta por lista de descrições

        // Valida se pelo menos uma permissão foi encontrada
        if (roles.isEmpty()) {
            // Lança exceção se nenhuma permissão válida foi informada
            throw new RuntimeException("Nenhuma permissão válida informada");
        }

        // Atribui todas as permissões encontradas à entidade User
        entity.setPermissions(roles); // Define a lista de permissões do usuário

        // Salva e retorna DTO
        AccountCredentialAdminDTO result = accountCredentialAdminMapper.toDTO(userRepository.save(entity));

        logger.debug("[AUTH-SRV-004] Administrador criado com sucesso | username={}", result.getUserName());
        return result;
    }

}