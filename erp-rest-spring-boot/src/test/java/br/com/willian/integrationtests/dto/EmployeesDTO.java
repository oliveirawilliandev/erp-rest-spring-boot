package br.com.willian.integrationtests.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;

// Indica que esta classe pode ser usada como elemento raiz de um XML.
// Essa anotação é do JAXB e define que a classe representa a tag raiz
// do documento XML durante a serialização e desserialização.
@XmlRootElement

// Define que o JAXB deve acessar diretamente os atributos da classe
// (campos) para fazer o mapeamento XML, em vez de usar getters e setters.
// Isso evita a necessidade de anotar cada metodo individualmente.

@XmlAccessorType(XmlAccessType.FIELD)

public class EmployeesDTO  implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String firstName;

    private String lastName;

    private String cpf;

    private String email;

    private String gender;

    private String phone;

    private String mobilePhone;

    private String zipCode;

    private String street;

    private String streetNumber;

    private String addressComplement;

    private String neighborhood;

    private String city;

    private String state;

    private String jobTitle;

    private String department;

    private Boolean active;
    //DATE
    private LocalDate birthDate;

    private LocalDate hireDate;

    private LocalDate terminationDate;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    // photo - QrCode - bar Codes
    private String photoUrl;

    private String qrCode;

    private String barCode;

    public EmployeesDTO() {
    }

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    //CAMPO NECESSARIO PARA GERA O NOME COMPLETO NO PDF
    @JsonIgnore
    public String getName() {
        return (firstName != null ? firstName : "") + " " +
                (lastName != null ? lastName : "");

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EmployeesDTO dto)) return false;
        return Objects.equals(id, dto.id) && Objects.equals(firstName, dto.firstName) && Objects.equals(lastName, dto.lastName) && Objects.equals(cpf, dto.cpf) && Objects.equals(email, dto.email) && Objects.equals(gender, dto.gender) && Objects.equals(phone, dto.phone) && Objects.equals(mobilePhone, dto.mobilePhone) && Objects.equals(zipCode, dto.zipCode) && Objects.equals(street, dto.street) && Objects.equals(streetNumber, dto.streetNumber) && Objects.equals(addressComplement, dto.addressComplement) && Objects.equals(neighborhood, dto.neighborhood) && Objects.equals(city, dto.city) && Objects.equals(state, dto.state) && Objects.equals(jobTitle, dto.jobTitle) && Objects.equals(department, dto.department) && Objects.equals(active, dto.active) && Objects.equals(birthDate, dto.birthDate) && Objects.equals(hireDate, dto.hireDate) && Objects.equals(terminationDate, dto.terminationDate) && Objects.equals(createdAt, dto.createdAt) && Objects.equals(updatedAt, dto.updatedAt) && Objects.equals(photoUrl, dto.photoUrl) && Objects.equals(qrCode, dto.qrCode) && Objects.equals(barCode, dto.barCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, cpf, email, gender, phone, mobilePhone, zipCode, street, streetNumber, addressComplement, neighborhood, city, state, jobTitle, department, active, birthDate, hireDate, terminationDate, createdAt, updatedAt, photoUrl, qrCode, barCode);
    }
}
