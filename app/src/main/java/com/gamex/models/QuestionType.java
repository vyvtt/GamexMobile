package com.gamex.models;

import com.google.gson.annotations.SerializedName;

public enum QuestionType {
    @SerializedName("1")
    EDITTEXT,
    @SerializedName("2")
    RADIOBUTTON,
    @SerializedName("3")
    CHECKBOX
}
