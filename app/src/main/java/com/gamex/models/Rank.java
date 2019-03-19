
package com.gamex.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rank {

    @SerializedName("leaderBoard")
    @Expose
    private List<LeaderBoard> leaderBoard = null;
    @SerializedName("currentUserRank")
    @Expose
    private Integer currentUserRank;
    @SerializedName("currentUserPoint")
    @Expose
    private Integer currentUserPoint;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Rank() {
    }

    /**
     * 
     * @param currentUserRank
     * @param currentUserPoint
     * @param leaderBoard
     */
    public Rank(List<LeaderBoard> leaderBoard, Integer currentUserRank, Integer currentUserPoint) {
        super();
        this.leaderBoard = leaderBoard;
        this.currentUserRank = currentUserRank;
        this.currentUserPoint = currentUserPoint;
    }

    public List<LeaderBoard> getLeaderBoard() {
        return leaderBoard;
    }

    public void setLeaderBoard(List<LeaderBoard> leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    public Integer getCurrentUserRank() {
        return currentUserRank;
    }

    public void setCurrentUserRank(Integer currentUserRank) {
        this.currentUserRank = currentUserRank;
    }

    public Integer getCurrentUserPoint() {
        return currentUserPoint;
    }

    public void setCurrentUserPoint(Integer currentUserPoint) {
        this.currentUserPoint = currentUserPoint;
    }

}
