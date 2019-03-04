package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanyOverview {

    @SerializedName("companyId")
    @Expose
    private Integer companyId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("logo")
    @Expose
    private String logo;

    /**
     * No args constructor for use in serialization
     *
     */
    public CompanyOverview() {
    }

    /**
     *
     * @param logo
     * @param name
     * @param companyId
     */
    public CompanyOverview(Integer companyId, String name, String logo) {
        super();
        this.companyId = companyId;
        this.name = name;
        this.logo = logo;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

}