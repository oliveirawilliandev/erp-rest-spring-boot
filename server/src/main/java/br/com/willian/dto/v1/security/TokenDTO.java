package br.com.willian.dto.v1.security;

import java.util.Date; // Representa datas de criação e expiração do token
import java.util.Objects; // Utilitário para equals e hashCode

public class TokenDTO {

    private static final long serialVersionUID = 1L; // Versão da serialização

    private String userName;          // Username do usuário autenticado
    private Boolean authenticated;    // Indica se a autenticação foi bem-sucedida
    private String fullName; //Nome Completo do usuário autenticado
    private String photoUrl;  // Foto
    private Date created;             // Data/hora de criação do token
    private Date expiration;          // Data/hora de expiração do token
    private String accessToken;       // Token JWT de acesso
    private String refreshToken;      // Token usado para renovação do access token


    public TokenDTO() {
        // Construtor padrão necessário para frameworks de serialização (Jackson)
    }

    public TokenDTO(
            String username,          // Nome do usuário
            Boolean authenticated,    // Status da autenticação
            Date created,             // Data de criação
            Date expiration,          // Data de expiração
            String accessToken,       // JWT de acesso
            String refreshToken,      // Token de refresh
            String fullName,          // nome completo
            String photoUrl          // foto
    ) {
        this.userName = username;     // Define username
        this.authenticated = authenticated; // Define status de autenticação
        this.created = created;       // Define data de criação
        this.expiration = expiration; // Define data de expiração
        this.accessToken = accessToken; // Define access token
        this.refreshToken = refreshToken; // Define refresh token
        this.fullName = fullName; // Define o nome completo
        this.photoUrl = photoUrl; // foto
    }

    public Boolean getAuthenticated() {
        return authenticated; // Retorna status de autenticação
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated; // Define status de autenticação
    }

    public Date getCreated() {
        return created; // Retorna data de criação do token
    }

    public void setCreated(Date created) {
        this.created = created; // Define data de criação
    }

    public Date getExpiration() {
        return expiration; // Retorna data de expiração
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration; // Define data de expiração
    }

    public String getAccessToken() {
        return accessToken; // Retorna access token JWT
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken; // Define access token
    }

    public String getRefreshToken() {
        return refreshToken; // Retorna refresh token
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken; // Define refresh token
    }

    public String getUserName() {
        return userName; // Retorna username
    }

    public void setUserName(String userName) {
        this.userName = userName; // Define username
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TokenDTO tokenDTO)) return false;
        return Objects.equals(userName, tokenDTO.userName) && Objects.equals(authenticated, tokenDTO.authenticated) && Objects.equals(fullName, tokenDTO.fullName) && Objects.equals(photoUrl, tokenDTO.photoUrl) && Objects.equals(created, tokenDTO.created) && Objects.equals(expiration, tokenDTO.expiration) && Objects.equals(accessToken, tokenDTO.accessToken) && Objects.equals(refreshToken, tokenDTO.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, authenticated, fullName, photoUrl, created, expiration, accessToken, refreshToken);
    }
}
