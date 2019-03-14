package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Exhibition implements Serializable {

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
    @SerializedName("listCompany")
    @Expose
    private List<CompanyInExhibition> listCompany;

    /**
     * No args constructor for use in serialization
     */
    public Exhibition() {
    }

    public Exhibition(String exhibitionId, String name, String startDate, String endDate, String logo) {
        this.exhibitionId = exhibitionId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.logo = logo;
    }

    // For Company details
    public Exhibition(String exhibitionId, String name, String description, String address, String startDate, String endDate, String logo, List<CompanyInExhibition> listCompany) {
        this.exhibitionId = exhibitionId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.logo = logo;
        this.listCompany = listCompany;
    }

    /**
     * @param logo
     * @param startDate
     * @param address
     * @param description
     * @param name
     * @param lng
     * @param endDate
     * @param exhibitionId
     * @param lat
     * @param listCompany
     */
    public Exhibition(String exhibitionId, String name, String description, String address, String startDate, String endDate, String logo, String lat, String lng, List<CompanyInExhibition> listCompany) {
        super();
        this.exhibitionId = exhibitionId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.logo = logo;
        this.lat = lat;
        this.lng = lng;
        this.listCompany = listCompany;
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

    public List<CompanyInExhibition> getListCompany() {
        return listCompany;
    }

    public void setListCompany(List<CompanyInExhibition> listCompany) {
        this.listCompany = listCompany;
    }

    @Override
    public String toString() {
        return "Exhibition{" +
                "exhibitionId='" + exhibitionId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", logo='" + logo + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", listCompany=" + listCompany +
                '}';
    }
}