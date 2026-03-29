package br.com.willian.dto;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class EmployeesDTO extends RepresentationModel<EmployeesDTO> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String firstName;

    private String lastName;

    private String cpf;

    private String email;

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

    private String active;
    //DATE
    private Date birthDate;


    private Date  hireDate;


    private Date  terminationDate;
    private Date  createdAt;

    private Date  updatedAt;

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

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EmployeesDTO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(cpf, that.cpf) && Objects.equals(email, that.email) && Objects.equals(phone, that.phone) && Objects.equals(mobilePhone, that.mobilePhone) && Objects.equals(zipCode, that.zipCode) && Objects.equals(street, that.street) && Objects.equals(streetNumber, that.streetNumber) && Objects.equals(addressComplement, that.addressComplement) && Objects.equals(neighborhood, that.neighborhood) && Objects.equals(city, that.city) && Objects.equals(state, that.state) && Objects.equals(jobTitle, that.jobTitle) && Objects.equals(department, that.department) && Objects.equals(active, that.active) && Objects.equals(birthDate, that.birthDate) && Objects.equals(hireDate, that.hireDate) && Objects.equals(terminationDate, that.terminationDate) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, lastName, cpf, email, phone, mobilePhone, zipCode, street, streetNumber, addressComplement, neighborhood, city, state, jobTitle, department, active, birthDate, hireDate, terminationDate, createdAt, updatedAt);
    }
}
