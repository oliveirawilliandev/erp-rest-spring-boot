package br.com.oliveirawillian.data.dto.v1.security;


import java.util.Date;
import java.util.Objects;

public class TokenDTO {
    private static final long serialVersionUID = 1L;
    private String userName;
    private Boolean authenticated;
    private Date created;
    private Date expiration;
    private String accessToken;
    private String refreshToken;
    private String password;

    public TokenDTO() {
    }

    public TokenDTO(String username, Boolean authenticated,
                    Date created, Date expiration, String accessToken,
                    String refreshToken) {
        this.userName = username;
        this.authenticated = authenticated;
        this.created = created;
        this.expiration = expiration;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TokenDTO tokenDTO)) return false;
        return Objects.equals(userName, tokenDTO.userName) && Objects.equals(authenticated, tokenDTO.authenticated) && Objects.equals(created, tokenDTO.created) && Objects.equals(expiration, tokenDTO.expiration) && Objects.equals(accessToken, tokenDTO.accessToken) && Objects.equals(refreshToken, tokenDTO.refreshToken) && Objects.equals(password, tokenDTO.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, authenticated, created, expiration, accessToken, refreshToken, password);
    }
}
