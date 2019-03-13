
package com.gamex.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Survey {

    @SerializedName("surveyId")
    @Expose
    private Integer surveyId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("point")
    @Expose
    private Integer point;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("questions")
    @Expose
    private List<Question> questions = null;
    @SerializedName("isTaken")
    @Expose
    private Boolean isTaken;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Survey() {
    }

    /**
     *
     * @param isTaken
     * @param point
     * @param title
     * @param description
     * @param questions
     * @param surveyId
     */
    public Survey(Integer surveyId, String title, Integer point, String description, Boolean isTaken, List<Question> questions) {
        super();
        this.surveyId = surveyId;
        this.title = title;
        this.point = point;
        this.description = description;
        this.isTaken = isTaken;
        this.questions = questions;
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Boolean getTaken() {
        return isTaken;
    }

    public void setTaken(Boolean taken) {
        isTaken = taken;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "surveyId=" + surveyId +
                ", title='" + title + '\'' +
                ", point=" + point +
                ", description='" + description + '\'' +
                ", questions=" + questions +
                '}';
    }
}
