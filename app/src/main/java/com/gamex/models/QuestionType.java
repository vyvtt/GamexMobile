package com.gamex.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum QuestionType implements Serializable {
    @SerializedName("1")
    EDITTEXT,
    @SerializedName("2")
    RADIOBUTTON,
    @SerializedName("3")
    CHECKBOX
}
