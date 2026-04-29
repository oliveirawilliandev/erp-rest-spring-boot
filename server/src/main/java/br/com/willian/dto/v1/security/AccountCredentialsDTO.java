package br.com.willian.dto.v1.security;

import java.io.Serializable; // Permite serialização do objeto (transporte/cache)
import java.util.Objects; // Utilitário para equals e hashCode

public class AccountCredentialsDTO implements Serializable {

    private static final long serialVersionUID = 1L; // Versão da serialização

    private String userName;   // Username usado para autenticação
    private String fullName;   // Nome completo do usuário
    private String password;   // Senha em texto puro (apenas para login/cadastro)
    private String email;      // Email Cadastrado

    public AccountCredentialsDTO() {
        // Construtor padrão necessário para frameworks de serialização (Jackson)
    }

    public AccountCredentialsDTO(String userName, String fullName, String password, String email) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccountCredentialsDTO that)) return false;
        return Objects.equals(userName, that.userName) && Objects.equals(fullName, that.fullName) && Objects.equals(password, that.password) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, fullName, password, email);
    }
}
