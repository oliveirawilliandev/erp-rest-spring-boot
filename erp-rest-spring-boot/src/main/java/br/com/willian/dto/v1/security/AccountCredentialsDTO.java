package br.com.oliveirawillian.data.dto.v1.security;

import java.io.Serializable;
import java.util.Objects;

public class AccountCredentialsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private String fullName;
    private String password;

    public AccountCredentialsDTO() {
    }

    public AccountCredentialsDTO(String password, String fullName, String userName) {
        this.password = password;
        this.fullName = fullName;
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
        if (!(o instanceof AccountCredentialsDTO that)) return false;
        return Objects.equals(userName, that.userName) && Objects.equals(fullName, that.fullName) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, fullName, password);
    }
}
