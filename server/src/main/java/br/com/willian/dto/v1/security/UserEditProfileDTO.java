package br.com.willian.dto.v1.security;

import java.util.Objects;

public class UserEditProfileDTO {
    private Long id;
    private String userName;
    private String fullName;
    private String email;
    private String photoUrl;
    private String password;


    public UserEditProfileDTO() {}

    public UserEditProfileDTO(Long id, String userName, String fullName, String email, String photoUrl, String password) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.password = password;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() { return photoUrl; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl;

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserEditProfileDTO that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(userName, that.userName) && Objects.equals(fullName, that.fullName) && Objects.equals(email, that.email) && Objects.equals(photoUrl, that.photoUrl) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, fullName, email, photoUrl, password);
    }
}