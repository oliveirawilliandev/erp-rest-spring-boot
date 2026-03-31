package br.com.willian.model;

import br.com.willian.model.enums.Gender;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

import java.util.List;
import java.util.Objects;
@Entity
@Table(name = "employees")
@DynamicInsert
public class Employees implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 150)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    @Column(nullable = false, length = 14, unique = true)
    private String cpf;

    @Column(nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Column( length = 20)
    private String phone;

    @Column(name = "mobile_phone", nullable = false, length = 20)
    private String mobilePhone;

    @Column(name = "zip_code",nullable = false, length = 10)
    private String zipCode;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(name = "street_number",nullable = false, length = 20)
    private String streetNumber;

    @Column(name = "address_complement", length = 100)
    private String addressComplement;

    @Column(nullable = false, length = 100)
    private String neighborhood;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;


    @Column(name = "job_title",nullable = false, length = 100)
    private String jobTitle;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(length = 100)
    private Boolean active;
    //DATE
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "hire_date",nullable = false)
    private LocalDate  hireDate;

    @Column(name = "termination_date")
    private LocalDate  terminationDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // photo - QrCode - bar Codes
    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "qr_code", length = 255)
    private String qrCode;

    @Column(name = "bar_code", length = 255)
    private String barCode;

    public Employees() {
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }


    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Employees employees)) return false;
        return Objects.equals(id, employees.id) && Objects.equals(firstName, employees.firstName) && Objects.equals(lastName, employees.lastName) && Objects.equals(cpf, employees.cpf) && Objects.equals(email, employees.email) && gender == employees.gender && Objects.equals(phone, employees.phone) && Objects.equals(mobilePhone, employees.mobilePhone) && Objects.equals(zipCode, employees.zipCode) && Objects.equals(street, employees.street) && Objects.equals(streetNumber, employees.streetNumber) && Objects.equals(addressComplement, employees.addressComplement) && Objects.equals(neighborhood, employees.neighborhood) && Objects.equals(city, employees.city) && Objects.equals(state, employees.state) && Objects.equals(jobTitle, employees.jobTitle) && Objects.equals(department, employees.department) && Objects.equals(active, employees.active) && Objects.equals(birthDate, employees.birthDate) && Objects.equals(hireDate, employees.hireDate) && Objects.equals(terminationDate, employees.terminationDate) && Objects.equals(createdAt, employees.createdAt) && Objects.equals(updatedAt, employees.updatedAt) && Objects.equals(photoUrl, employees.photoUrl) && Objects.equals(qrCode, employees.qrCode) && Objects.equals(barCode, employees.barCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, cpf, email, gender, phone, mobilePhone, zipCode, street, streetNumber, addressComplement, neighborhood, city, state, jobTitle, department, active, birthDate, hireDate, terminationDate, createdAt, updatedAt, photoUrl, qrCode, barCode);
    }
}
