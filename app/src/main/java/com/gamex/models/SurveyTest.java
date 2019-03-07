package com.gamex.models;

public class SurveyTest {
    private int surveyId;
    private String title;
    private int points;
    private String description;
    private int totalQuestion;

    public SurveyTest(int surveyId, String title, int points, String description, int totalQuestion) {
        this.surveyId = surveyId;
        this.title = title;
        this.points = points;
        this.description = description;
        this.totalQuestion = totalQuestion;
    }

    public int getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
