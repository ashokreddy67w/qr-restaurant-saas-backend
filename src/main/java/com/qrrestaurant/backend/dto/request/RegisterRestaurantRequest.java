package com.qrrestaurant.backend.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class RegisterRestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    @NotBlank(message = "Pincode is required")
    private String pincode;

    @NotBlank(message = "GST number is required")
    private String gstNumber;

    @NotBlank(message = "FSSAI license is required")
    private String fssaiLicense;

    private MultipartFile logo;
    private MultipartFile gstCertificate;
    private MultipartFile fssaiCertificate;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getFssaiLicense() { return fssaiLicense; }
    public void setFssaiLicense(String fssaiLicense) { this.fssaiLicense = fssaiLicense; }

    public MultipartFile getLogo() { return logo; }
    public void setLogo(MultipartFile logo) { this.logo = logo; }

    public MultipartFile getGstCertificate() { return gstCertificate; }
    public void setGstCertificate(MultipartFile gstCertificate) { this.gstCertificate = gstCertificate; }

    public MultipartFile getFssaiCertificate() { return fssaiCertificate; }
    public void setFssaiCertificate(MultipartFile fssaiCertificate) { this.fssaiCertificate = fssaiCertificate; }
}