package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CompanyInExhibition implements Serializable {

    @SerializedName("companyId")
    @Expose
    private String companyId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("booths")
    @Expose
    private List<String> booths = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public CompanyInExhibition() {
    }

    /**
     *
     * @param logo
     * @param booths
     * @param name
     * @param companyId
     */
    public CompanyInExhibition(String companyId, String name, String logo, List<String> booths) {
        super();
        this.companyId = companyId;
        this.name = name;
        this.logo = logo;
        this.booths = booths;
    }


    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<String> getBooths() {
        return booths;
    }

    public void setBooths(List<String> booths) {
        this.booths = booths;
    }
}