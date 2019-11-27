package com.bgenterprise.helpcentermodule.Network.ModelClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralFeedbackResponse {

    @SerializedName("question_id")
    @Expose
    private String question_id;

    @SerializedName("positive_feedback_count")
    @Expose
    private String positive_feedback_count;

    @SerializedName("negative_feedback_count")
    @Expose
    private String negative_feedback_count;

    public GeneralFeedbackResponse(String question_id, String positive_feedback_count, String negative_feedback_count) {
        this.question_id = question_id;
        this.positive_feedback_count = positive_feedback_count;
        this.negative_feedback_count = negative_feedback_count;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getPositive_feedback_count() {
        return positive_feedback_count;
    }

    public String getNegative_feedback_count() {
        return negative_feedback_count;
    }
}
