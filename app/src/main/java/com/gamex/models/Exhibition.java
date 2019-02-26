package com.gamex.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exhibition {

    @SerializedName("exhibitionId")
    @Expose
    private Integer exhibitionId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("organizerId")
    @Expose
    private String organizerId;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("qr")
    @Expose
    private String qr;

    /**
     * No args constructor for use in serialization
     *
     */
    public Exhibition() {
    }

    /**
     *
     * @param logo
     * @param startDate
     * @param location
     * @param address
     * @param description
     * @param name
     * @param endDate
     * @param exhibitionId
     * @param organizerId
     * @param qr
     */
    public Exhibition(Integer exhibitionId, String name, String description, String address, String organizerId, String startDate, String endDate, String location, String logo, String qr) {
        super();
        this.exhibitionId = exhibitionId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.organizerId = organizerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.logo = logo;
        this.qr = qr;
    }

    public Integer getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(Integer exhibitionId) {
        this.exhibitionId = exhibitionId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Object getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

}