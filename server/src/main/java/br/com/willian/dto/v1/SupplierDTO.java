package br.com.willian.dto.v1;

import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

public class SupplierDTO extends RepresentationModel<SupplierDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    // =====================================================
    // INFORMAÇÕES BÁSICAS
    // =====================================================

    @NotBlank(message = "Supplier name is required")
    @Size(max = 150, message = "Name must be up to 150 characters")
    private String name;

    @NotBlank(message = "Document is required")
    @Size(max = 20, message = "Document must be up to 20 characters")
    private String document;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must be up to 150 characters")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must be up to 20 characters")
    private String phone;

    // =====================================================
    // DADOS DE ENDEREÇO
    // =====================================================

    @NotBlank(message = "Zip code is required")
    @Size(max = 10, message = "Zip code must be up to 10 characters")
    private String zipCode;

    @NotBlank(message = "Street is required")
    @Size(max = 200, message = "Street must be up to 200 characters")
    private String street;

    @NotBlank(message = "Street number is required")
    @Size(max = 20, message = "Street number must be up to 20 characters")
    private String streetNumber;

    @Size(max = 100, message = "Address complement must be up to 100 characters")
    private String addressComplement;

    @NotBlank(message = "Neighborhood is required")
    @Size(max = 100, message = "Neighborhood must be up to 100 characters")
    private String neighborhood;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be up to 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "State must be exactly 2 characters")
    private String state;

    // =====================================================
    // STATUS E AUDITORIA
    // =====================================================

    private Boolean active;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    // =====================================================
    // FOTOS E CÓDIGOS
    // =====================================================

    private String photoUrl;

    private String qrCode;

    private String barCode;

    public SupplierDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SupplierDTO that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(document, that.document)
                && Objects.equals(email, that.email)
                && Objects.equals(phone, that.phone)
                && Objects.equals(zipCode, that.zipCode)
                && Objects.equals(street, that.street)
                && Objects.equals(streetNumber, that.streetNumber)
                && Objects.equals(addressComplement, that.addressComplement)
                && Objects.equals(neighborhood, that.neighborhood)
                && Objects.equals(city, that.city)
                && Objects.equals(state, that.state)
                && Objects.equals(active, that.active)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt)
                && Objects.equals(photoUrl, that.photoUrl)
                && Objects.equals(qrCode, that.qrCode)
                && Objects.equals(barCode, that.barCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                id,
                name,
                document,
                email,
                phone,
                zipCode,
                street,
                streetNumber,
                addressComplement,
                neighborhood,
                city,
                state,
                active,
                createdAt,
                updatedAt,
                photoUrl,
                qrCode,
                barCode
        );
    }
}