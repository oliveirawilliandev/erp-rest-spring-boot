package br.com.willian.integrationtests.dto;

import br.com.willian.dto.v1.security.enums.RoleEnum;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
@XmlRootElement

public class AccountCredentialAdminDTO implements Serializable {

    private static final long serialVersionUID = 1L; // Versão da serialização

    private String userName;   // Username usado para autenticação
    private String fullName;   // Nome completo do usuário
    private String password;   // Senha em texto puro (apenas para login/cadastro)
    private List<RoleEnum> roles;

    public AccountCredentialAdminDTO() {
        // Construtor padrão necessário para frameworks de serialização (Jackson)
    }

    public AccountCredentialAdminDTO(
            String password,  // Senha informada pelo usuário
            String fullName,  // Nome completo
            String userName   // Username
    ) {
        this.password = password; // Define senha
        this.fullName = fullName; // Define nome completo
        this.userName = userName; // Define username
    }

    public AccountCredentialAdminDTO(
            String password,  // Senha informada pelo usuário
            String userName   // Username
    ) {
        this.password = password; // Define senha
        this.userName = userName; // Define username
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

    public List<RoleEnum> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEnum> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccountCredentialAdminDTO that)) return false;
        return Objects.equals(userName, that.userName) && Objects.equals(fullName, that.fullName) && Objects.equals(password, that.password) && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, fullName, password, roles);
    }
}
