package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exhibition {

    @SerializedName("exhibitionId")
    @Expose
    private String exhibitionId;
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
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;

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
     * @param address
     * @param description
     * @param name
     * @param lng
     * @param endDate
     * @param exhibitionId
     * @param organizerId
     * @param lat
     */
    public Exhibition(String exhibitionId, String name, String description, String address, String organizerId, String startDate, String endDate, String logo, String lat, String lng) {
        super();
        this.exhibitionId = exhibitionId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.organizerId = organizerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.logo = logo;
        this.lat = lat;
        this.lng = lng;
    }

    public String getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(String exhibitionId) {
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

}