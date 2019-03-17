package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reward {

    @SerializedName("rewardId")
    @Expose
    private Integer rewardId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("pointCost")
    @Expose
    private Integer pointCost;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;

    /**
     * No args constructor for use in serialization
     *
     */
    public Reward() {
    }

    /**
     *
     * @param pointCost
     * @param content
     * @param startDate
     * @param description
     * @param endDate
     * @param quantity
     * @param rewardId
     */
    public Reward(Integer rewardId, String description, String content, Integer quantity, Integer pointCost, String startDate, String endDate) {
        super();
        this.rewardId = rewardId;
        this.description = description;
        this.content = content;
        this.quantity = quantity;
        this.pointCost = pointCost;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getRewardId() {
        return rewardId;
    }

    public void setRewardId(Integer rewardId) {
        this.rewardId = rewardId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPointCost() {
        return pointCost;
    }

    public void setPointCost(Integer pointCost) {
        this.pointCost = pointCost;
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

}