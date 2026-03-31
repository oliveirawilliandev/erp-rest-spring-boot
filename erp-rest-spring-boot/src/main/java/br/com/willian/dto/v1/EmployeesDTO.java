package br.com.willian.dto.v1;

import br.com.willian.model.PurchaseMock;
import br.com.willian.model.enums.Gender;
import br.com.willian.services.EmployeesService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
// Extends RepresentationModel<EmployeesDTO> :  suporte a HATEOAS

/**
 * Define o nome da coleção exposta no JSON quando este DTO é serializado
 * usando Spring HATEOAS / Spring Data REST (HAL).
 *
 * Sem essa anotação, o framework gera automaticamente o nome da coleção
 * (ex: employeesDTOList), o que pode causar inconsistência no contrato da API.
 *
 * Com @Relation(collectionRelation = "employees"), garantimos que o conteúdo
 * dentro de "_embedded" seja exposto como:
 *
 * "_embedded": {
 *     "employees": [ ... ]
 * }
 *
 * Isso mantém o nome do recurso estável, legível e alinhado ao padrão da API.
 */
@Relation(collectionRelation = "employees")
public class EmployeesDTO extends RepresentationModel<EmployeesDTO> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(EmployeesService.class.getName());

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

    private LocalDate  hireDate;

    private LocalDate  terminationDate;

    private OffsetDateTime createdAt;

    private OffsetDateTime  updatedAt;

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

    //Mock teste isso deve ser deletado !
    @JsonIgnore
    private List<PurchaseMock> items ;

    public void setItems(List<PurchaseMock> items) {
        logger.info("Code: 12312 EmployeesDTO MOCK PurchaseMock DESATIVA ISSO. MOTIVO DA CRIAÇÂO TESTE JASPER");

        this.items = items;
    }

    public List<PurchaseMock> getItems() {
        logger.info("getItems() foi chamado - tamanho da lista: " + (items == null ? 0 : items.size()));
        logger.info("Code: 12312 EmployeesDTO MOCK PurchaseMock DESATIVA ISSO. MOTIVO DA CRIAÇÂO TESTE JASPER");

        return items;
    }

    //CAMPO NECESSARIO PARA GERA O NOME COMPLETO NO PDF
    @JsonIgnore
    public String getName() {
        return (firstName != null ? firstName : "") + " " +
         (lastName != null ? lastName : "");

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
        if (!(o instanceof EmployeesDTO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(cpf, that.cpf) && Objects.equals(email, that.email) && Objects.equals(gender, that.gender) && Objects.equals(phone, that.phone) && Objects.equals(mobilePhone, that.mobilePhone) && Objects.equals(zipCode, that.zipCode) && Objects.equals(street, that.street) && Objects.equals(streetNumber, that.streetNumber) && Objects.equals(addressComplement, that.addressComplement) && Objects.equals(neighborhood, that.neighborhood) && Objects.equals(city, that.city) && Objects.equals(state, that.state) && Objects.equals(jobTitle, that.jobTitle) && Objects.equals(department, that.department) && Objects.equals(active, that.active) && Objects.equals(birthDate, that.birthDate) && Objects.equals(hireDate, that.hireDate) && Objects.equals(terminationDate, that.terminationDate) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(photoUrl, that.photoUrl) && Objects.equals(qrCode, that.qrCode) && Objects.equals(barCode, that.barCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, lastName, cpf, email, gender, phone, mobilePhone, zipCode, street, streetNumber, addressComplement, neighborhood, city, state, jobTitle, department, active, birthDate, hireDate, terminationDate, createdAt, updatedAt, photoUrl, qrCode, barCode);
    }
}
