package br.com.willian.dto.v1.security;

import java.io.Serializable; // Permite serialização do objeto (transporte/cache)
import java.util.Objects; // Utilitário para equals e hashCode

public class AccountCredentialsDTO implements Serializable {

    private static final long serialVersionUID = 1L; // Versão da serialização

    private String userName;   // Username usado para autenticação
    private String fullName;   // Nome completo do usuário
    private String password;   // Senha em texto puro (apenas para login/cadastro)

    public AccountCredentialsDTO() {
        // Construtor padrão necessário para frameworks de serialização (Jackson)
    }

    public AccountCredentialsDTO(
            String password,  // Senha informada pelo usuário
            String fullName,  // Nome completo
            String userName   // Username
    ) {
        this.password = password; // Define senha
        this.fullName = fullName; // Define nome completo
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccountCredentialsDTO that)) return false; // Verifica tipo
        return Objects.equals(userName, that.userName) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(password, that.password); // Compara campos
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                userName,
                fullName,
                password
        ); // Gera hash consistente com equals
    }
}
