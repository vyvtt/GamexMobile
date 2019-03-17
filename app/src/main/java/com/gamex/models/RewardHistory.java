package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RewardHistory {

    @SerializedName("exchangedDate")
    @Expose
    private String exchangedDate;
    @SerializedName("rewardContent")
    @Expose
    private String rewardContent;

    /**
     * No args constructor for use in serialization
     *
     */
    public RewardHistory() {
    }

    /**
     *
     * @param rewardContent
     * @param exchangedDate
     */
    public RewardHistory(String exchangedDate, String rewardContent) {
        super();
        this.exchangedDate = exchangedDate;
        this.rewardContent = rewardContent;
    }

    public String getExchangedDate() {
        return exchangedDate;
    }

    public void setExchangedDate(String exchangedDate) {
        this.exchangedDate = exchangedDate;
    }

    public String getRewardContent() {
        return rewardContent;
    }

    public void setRewardContent(String rewardContent) {
        this.rewardContent = rewardContent;
    }

}