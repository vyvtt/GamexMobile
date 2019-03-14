package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Company {

    @SerializedName("companyId")
    @Expose
    private Integer companyId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("website")
    @Expose
    private String website;

    /**
     * No args constructor for use in serialization
     *
     */
    public Company() {
    }

    /**
     *
     * @param logo
     * @param phone
     * @param website
     * @param address
     * @param email
     * @param description
     * @param name
     * @param companyId
     */
    public Company(Integer companyId, String name, String description, String email, String phone, String address, String logo, String website) {
        super();
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.logo = logo;
        this.website = website;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

}