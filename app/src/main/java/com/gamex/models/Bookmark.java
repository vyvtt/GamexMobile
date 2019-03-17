package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bookmark {

    @SerializedName("targetType")
    @Expose
    private String targetType;
    @SerializedName("targetName")
    @Expose
    private String targetName;
    @SerializedName("bookmarkDate")
    @Expose
    private String bookmarkDate;
    @SerializedName("targetId")
    @Expose
    private String targetId;

    /**
     * No args constructor for use in serialization
     *
     */
    public Bookmark() {
    }

    /**
     *
     * @param targetId
     * @param targetName
     * @param targetType
     * @param bookmarkDate
     */
    public Bookmark(String targetType, String targetName, String bookmarkDate, String targetId) {
        super();
        this.targetType = targetType;
        this.targetName = targetName;
        this.bookmarkDate = bookmarkDate;
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getBookmarkDate() {
        return bookmarkDate;
    }

    public void setBookmarkDate(String bookmarkDate) {
        this.bookmarkDate = bookmarkDate;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

}