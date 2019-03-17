package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RewardHistory {

    @SerializedName("exchangedDate")
    @Expose
    private String exchangedDate;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("description")
    @Expose
    private String description;

    /**
     * No args constructor for use in serialization
     *
     */
    public RewardHistory() {
    }

    /**
     *
     * @param content
     * @param description
     * @param exchangedDate
     */
    public RewardHistory(String exchangedDate, String content, String description) {
        super();
        this.exchangedDate = exchangedDate;
        this.content = content;
        this.description = description;
    }

    public String getExchangedDate() {
        return exchangedDate;
    }

    public void setExchangedDate(String exchangedDate) {
        this.exchangedDate = exchangedDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}