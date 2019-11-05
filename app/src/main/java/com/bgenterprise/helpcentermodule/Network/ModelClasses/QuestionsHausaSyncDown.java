package com.bgenterprise.helpcentermodule.Network.ModelClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionsHausaSyncDown {

    @SerializedName("unique_question_id")
    @Expose
    private String unique_question_id;

    @SerializedName("app_id")
    @Expose
    private String app_id;

    @SerializedName("activity_group_id")
    @Expose
    private String activity_group_id;

    @SerializedName("activity_id")
    @Expose
    private String activity_id;

    @SerializedName("activity_name")
    @Expose
    private String activity_name;

    @SerializedName("resource_id")
    @Expose
    private String resource_id;

    @SerializedName("issue_question")
    @Expose
    private String issue_question;

    @SerializedName("issue_answer")
    @Expose
    private String issue_answer;

    @SerializedName("faq_status")
    @Expose
    private String faq_status;

    @SerializedName("last_sync_time")
    @Expose
    private String last_sync_time;

    public QuestionsHausaSyncDown() {
    }

    public QuestionsHausaSyncDown(String unique_question_id, String app_id, String activity_group_id, String activity_id, String activity_name, String resource_id, String issue_question, String issue_answer, String faq_status, String last_sync_time) {
        this.unique_question_id = unique_question_id;
        this.app_id = app_id;
        this.activity_group_id = activity_group_id;
        this.activity_id = activity_id;
        this.activity_name = activity_name;
        this.resource_id = resource_id;
        this.issue_question = issue_question;
        this.issue_answer = issue_answer;
        this.faq_status = faq_status;
        this.last_sync_time = last_sync_time;
    }

    public String getUnique_question_id() {
        return unique_question_id;
    }

    public void setUnique_question_id(String unique_question_id) {
        this.unique_question_id = unique_question_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getActivity_group_id() {
        return activity_group_id;
    }

    public void setActivity_group_id(String activity_group_id) {
        this.activity_group_id = activity_group_id;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getIssue_question() {
        return issue_question;
    }

    public void setIssue_question(String issue_question) {
        this.issue_question = issue_question;
    }

    public String getIssue_answer() {
        return issue_answer;
    }

    public void setIssue_answer(String issue_answer) {
        this.issue_answer = issue_answer;
    }

    public String getFaq_status() {
        return faq_status;
    }

    public void setFaq_status(String faq_status) {
        this.faq_status = faq_status;
    }

    public String getLast_sync_time() {
        return last_sync_time;
    }

    public void setLast_sync_time(String last_sync_time) {
        this.last_sync_time = last_sync_time;
    }
}
