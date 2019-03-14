package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {

    @SerializedName("accountId")
    @Expose
    private String accountId;
    @SerializedName("activity")
    @Expose
    private String activity;
    @SerializedName("time")
    @Expose
    private String time;

    /**
     * No args constructor for use in serialization
     *
     */
    public History() {
    }

    /**
     *
     * @param time
     * @param accountId
     * @param activity
     */
    public History(String accountId, String activity, String time) {
        super();
        this.accountId = accountId;
        this.activity = activity;
        this.time = time;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}