
package com.gamex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProposedAnswer implements Serializable {

    @SerializedName("proposedAnswerId")
    @Expose
    private Integer proposedAnswerId;
    @SerializedName("content")
    @Expose
    private String content;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ProposedAnswer() {
    }

    /**
     * 
     * @param content
     * @param proposedAnswerId
     */
    public ProposedAnswer(Integer proposedAnswerId, String content) {
        super();
        this.proposedAnswerId = proposedAnswerId;
        this.content = content;
    }

    public Integer getProposedAnswerId() {
        return proposedAnswerId;
    }

    public void setProposedAnswerId(Integer proposedAnswerId) {
        this.proposedAnswerId = proposedAnswerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ProposedAnswer{" +
                "proposedAnswerId=" + proposedAnswerId +
                ", content='" + content + '\'' +
                '}';
    }
}
