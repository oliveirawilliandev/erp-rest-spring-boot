package br.com.willian.dto.v1;

import br.com.willian.model.PurchaseMock;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "employees")
public class EmployeeDTO extends RepresentationModel<EmployeeDTO> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeDTO.class);

    private Long id;

    // =====================================================
    // DADOS PESSOAIS
    // =====================================================

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 150, message = "First name must be between 2 and 150 characters")
    private String firstName;

    @Size(min = 2, max = 150, message = "Last name must be between 2 and 150 characters")
    private String lastName;

    @Pattern(
            regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11}",
            message = "Invalid CPF format"
    )
    private String cpf;

    @NotBlank(message = "Gender is required")
    @Pattern(
            regexp = "^(MALE|FEMALE|OTHER)$",
            message = "Invalid gender value"
    )
    private String gender;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    // =====================================================
    // CONTATO
    // =====================================================

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^\\+?[0-9\\s\\-\\(\\)]{10,20}$",
            message = "Invalid phone format"
    )
    private String phone;

    @Pattern(
            regexp = "^\\+?[0-9\\s\\-\\(\\)]{10,20}$",
            message = "Invalid mobile phone format"
    )
    @JsonProperty("mobile_phone")
    private String mobilePhone;

    // =====================================================
    // ENDEREÇO
    // =====================================================

    @Pattern(
            regexp = "^\\d{5}-?\\d{3}$",
            message = "Invalid zip code format"
    )
    private String zipCode;

    private String street;

    private String streetNumber;

    private String addressComplement;

    private String neighborhood;

    private String city;

    @Size(min = 2, max = 2, message = "State must be 2 characters")
    private String state;

    // =====================================================
    // PROFISSIONAL
    // =====================================================

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    private String department;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    private LocalDate terminationDate;

    private Boolean active;

    // =====================================================
    // AUDITORIA
    // =====================================================

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // =====================================================
    // FOTO / CÓDIGOS
    // =====================================================

    private String photoUrl;
    private String qrCode;
    private String barCode;

    public EmployeeDTO() {
    }

    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getAddressComplement() {
        return addressComplement;
    }

    public void setAddressComplement(String addressComplement) {
        this.addressComplement = addressComplement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    // =====================================================
    // MOCK
    // =====================================================

    @JsonIgnore
    private List<PurchaseMock> items;

    public List<PurchaseMock> getItems() {
        return items;
    }

    public void setItems(List<PurchaseMock> items) {
        logger.info("PurchaseMock usado temporariamente.");
        this.items = items;
    }

    @JsonIgnore
    public String getName() {
        return (firstName != null ? firstName : "") + " " +
                (lastName != null ? lastName : "");
    }

    // =====================================================
    // EQUALS / HASHCODE
    // =====================================================

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EmployeeDTO that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}