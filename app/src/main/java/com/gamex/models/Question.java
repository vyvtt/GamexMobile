
package com.gamex.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Question implements Serializable {

    @SerializedName("questionId")
    @Expose
    private Integer questionId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("questionType")
    @Expose
    private QuestionType questionType;
    @SerializedName("proposedAnswers")
    @Expose
    private List<ProposedAnswer> proposedAnswers = null;

    private List<Integer> userAnswerButtonId;
    private String userAnswerText;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Question() {
    }

    /**
     * 
     * @param content
     * @param proposedAnswers
     * @param questionId
     * @param questionType
     */
    public Question(Integer questionId, String content, QuestionType questionType, List<ProposedAnswer> proposedAnswers) {
        super();
        this.questionId = questionId;
        this.content = content;
        this.questionType = questionType;
        this.proposedAnswers = proposedAnswers;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public List<ProposedAnswer> getProposedAnswers() {
        return proposedAnswers;
    }

    public void setProposedAnswers(List<ProposedAnswer> proposedAnswers) {
        this.proposedAnswers = proposedAnswers;
    }

    public List<Integer> getUserAnswerButtonId() {
        return userAnswerButtonId;
    }

    public void setUserAnswerButtonId(List<Integer> userAnswerButtonId) {
        this.userAnswerButtonId = userAnswerButtonId;
    }

    public String getUserAnswerText() {
        return userAnswerText;
    }

    public void setUserAnswerText(String userAnswerText) {
        this.userAnswerText = userAnswerText;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", content='" + content + '\'' +
                ", questionType=" + questionType +
                ", proposedAnswers=" + proposedAnswers +
                ", userAnswerButtonId=" + userAnswerButtonId +
                ", userAnswerText='" + userAnswerText + '\'' +
                '}';
    }
}
