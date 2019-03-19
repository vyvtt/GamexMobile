
package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LeaderBoard {

    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("totalPointEarned")
    @Expose
    private Integer totalPointEarned;

    /**
     * No args constructor for use in serialization
     * 
     */
    public LeaderBoard() {
    }

    /**
     * 
     * @param totalPointEarned
     * @param fullname
     */
    public LeaderBoard(String fullname, Integer totalPointEarned) {
        super();
        this.fullname = fullname;
        this.totalPointEarned = totalPointEarned;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getTotalPointEarned() {
        return totalPointEarned;
    }

    public void setTotalPointEarned(Integer totalPointEarned) {
        this.totalPointEarned = totalPointEarned;
    }

}
