package br.com.willian.dto.v1.security;

public class UserProfileDTO {
    private Long id;
    private String userName;
    private String fullName;
    private String email;
    private String photoUrl;

    public UserProfileDTO() {}

    public UserProfileDTO(Long id, String userName, String fullName, String email, String photoUrl) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.photoUrl = photoUrl;
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
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}