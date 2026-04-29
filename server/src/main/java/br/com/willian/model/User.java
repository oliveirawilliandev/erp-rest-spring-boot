package br.com.willian.model; // Pacote da camada de modelo/entidade

import jakarta.persistence.*; // Anotações JPA para mapeamento objeto-relacional
import org.springframework.security.core.GrantedAuthority; // Interface do Spring Security para autoridades
import org.springframework.security.core.userdetails.UserDetails; // Interface do Spring Security para detalhes do usuário

import java.io.Serializable; // Interface para serialização
import java.util.*;

@Entity // Define a classe como uma entidade JPA
@Table(name = "users") // Mapeia para a tabela "users" no banco de dados
public class User implements UserDetails, Serializable { // Implementa interfaces do Spring Security e Serializable
    private static final long serialVersionUID = 1L; // Versão de serialização

    @Id // Define como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID (auto-increment)
    private Long id; // Identificador único do usuário

    @Column(name = "user_name", unique = true, length = 80) // Coluna única com tamanho máximo 80
    private String userName; // Nome de usuário para login

    @Column(name = "password", length = 25) // Coluna com tamanho máximo 25
    private String password; // Senha do usuário (criptografada)

    @Column(name = "full_name") // Coluna para nome completo
    private String fullName; // Nome completo do usuário

    @Column(name = "email", unique = true, length = 150) // Coluna única email com tamanho máximo 150
    private String email; // email

    @Column(name = "account_non_expired", length = 10) // Coluna com tamanho 10
    private Boolean accountNonExpired; // Indica se a conta não expirou

    @Column(name = "account_non_locked", length = 10) // Coluna com tamanho 10
    private Boolean accountNonLocked; // Indica se a conta não está bloqueada

    @Column(name = "credentials_non_expired", length = 10) // Coluna com tamanho 10
    private Boolean credentialsNonExpired; // Indica se as credenciais não expiraram

    @Column(name = "enabled", length = 10) // Coluna com tamanho 10
    private Boolean enabled; // Indica se a conta está habilitada

    @Column(name = "photo_url", length = 500) // Coluna com tamanho 500
    private String photoUrl; // Edição de foto

    @ManyToMany(fetch = FetchType.EAGER) // Relacionamento muitos-para-muitos com carregamento imediato
    @JoinTable( // Tabela de junção para o relacionamento
            name = "user_permission", // Nome da tabela de junção
            joinColumns = @JoinColumn(name = "id_user"), // Coluna que referencia esta entidade
            inverseJoinColumns = @JoinColumn(name = "id_permission") // Coluna que referencia a outra entidade
    )
    private Set<Permission> permissions; // Lista de permissões do usuário

    // Converte a lista de permissões para uma lista de descrições (roles)
    public List<String> getRoles() {
        List<String> roles = new ArrayList<>(); // Inicializa lista de roles
        for (Permission permission : this.permissions) { // Itera sobre as permissões
            roles.add(permission.getDescription()); // Adiciona a descrição da permissão
        }
        return roles; // Retorna lista de roles
    }

    // Construtor padrão (obrigatório para JPA)
    public User() {
    }

    // Método do Spring Security que retorna as autoridades (permissões) do usuário
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.permissions; // Retorna a lista de permissões
    }

    // Método do Spring Security que retorna a senha
    @Override
    public String getPassword() {
        return this.password; // Retorna a senha
    }

    // Método do Spring Security que retorna o nome de usuário
    @Override
    public String getUsername() {
        return this.userName; // Retorna o nome de usuário
    }

    // Método do Spring Security que indica se a conta não expirou
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired; // Retorna o status
    }

    // Método do Spring Security que indica se a conta não está bloqueada
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked; // Retorna o status
    }

    // Método do Spring Security que indica se as credenciais não expiraram
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired; // Retorna o status
    }

    // Método do Spring Security que indica se a conta está habilitada
    @Override
    public boolean isEnabled() {
        return this.enabled; // Retorna o status
    }

    // Getter para nome completo
    public String getFullName() {
        return fullName; // Retorna nome completo
    }

    // Setter para nome completo
    public void setFullName(String fullName) {
        this.fullName = fullName; // Define nome completo
    }

    // Getter para ID
    public Long getId() {
        return id; // Retorna ID
    }

    // Setter para ID
    public void setId(Long id) {
        this.id = id; // Define ID
    }

    // Getter para nome de usuário
    public String getUserName() {
        return userName; // Retorna nome de usuário
    }

    // Setter para nome de usuário
    public void setUserName(String userName) {
        this.userName = userName; // Define nome de usuário
    }

    // Setter para senha
    public void setPassword(String password) {
        this.password = password; // Define senha
    }

    // Getter para accountNonExpired
    public Boolean getAccountNonExpired() {
        return accountNonExpired; // Retorna status
    }

    // Setter para accountNonExpired
    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired; // Define status
    }

    // Getter para accountNonLocked
    public Boolean getAccountNonLocked() {
        return accountNonLocked; // Retorna status
    }

    // Setter para accountNonLocked
    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked; // Define status
    }

    // Getter para credentialsNonExpired
    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired; // Retorna status
    }

    // Setter para credentialsNonExpired
    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired; // Define status
    }

    // Getter para enabled
    public Boolean getEnabled() {
        return enabled; // Retorna status
    }

    // Setter para enabled
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled; // Define status
    }

    // Getter para permissões
    public Set<Permission> getPermissions() {
        return permissions; // Retorna lista de permissões
    }

    // Setter para permissões
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions; // Define lista de permissões
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    // equals baseado em todos os campos


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id) && Objects.equals(userName, user.userName) && Objects.equals(password, user.password) && Objects.equals(fullName, user.fullName) && Objects.equals(email, user.email) && Objects.equals(accountNonExpired, user.accountNonExpired) && Objects.equals(accountNonLocked, user.accountNonLocked) && Objects.equals(credentialsNonExpired, user.credentialsNonExpired) && Objects.equals(enabled, user.enabled) && Objects.equals(photoUrl, user.photoUrl) && Objects.equals(permissions, user.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, password, fullName, email, accountNonExpired, accountNonLocked, credentialsNonExpired, enabled, photoUrl, permissions);
    }
}