package br.com.willian.dto.v1.security;

import br.com.willian.dto.v1.security.enums.RoleEnum;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class AccountCredentialAdminDTO implements Serializable {

    private static final long serialVersionUID = 1L; // Versão da serialização

    private String userName;   // Username usado para autenticação
    private String fullName;   // Nome completo do usuário
    private String password;   // Senha em texto puro (apenas para login/cadastro)
    private String email;
    private Set<RoleEnum> roles;

    public AccountCredentialAdminDTO() {
        // Construtor padrão necessário para frameworks de serialização (Jackson)
    }

    public AccountCredentialAdminDTO(String userName, String fullName, String password, String email) {
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
    }

    public String getFullName() {
        return fullName; // Retorna nome completo do usuário
    }

    public void setFullName(String fullName) {
        this.fullName = fullName; // Define nome completo
    }

    public String getUserName() {
        return userName; // Retorna username
    }

    public void setUserName(String userName) {
        this.userName = userName; // Define username
    }

    public String getPassword() {
        return password; // Retorna senha (uso exclusivo no processo de autenticação)
    }

    public void setPassword(String password) {
        this.password = password; // Define senha (não persistir em texto puro)
    }

    public Set<RoleEnum> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEnum> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccountCredentialAdminDTO that)) return false;
        return Objects.equals(userName, that.userName) && Objects.equals(fullName, that.fullName) && Objects.equals(password, that.password) && Objects.equals(email, that.email) && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, fullName, password, email, roles);
    }
}
