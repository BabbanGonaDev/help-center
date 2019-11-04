package com.bgenterprise.helpcentermodule.Database.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions_hausa")
public class QuestionsHausa {

    @PrimaryKey
    @NonNull
    private String unique_question_id;

    private String app_id;
    private String activity_group_id;
    private String activity_group_name;
    private String activity_id;
    private String resource_id;
    private String issue_question;
    private String issue_answer;
    private int positive_feedback_count;
    private int negative_feedback_count;
    private int faq_status;

    public QuestionsHausa(@NonNull String unique_question_id, String app_id, String activity_group_id, String activity_group_name,
                          String activity_id, String resource_id, String issue_question,
                          String issue_answer, int positive_feedback_count, int negative_feedback_count, int faq_status) {
        this.unique_question_id = unique_question_id;
        this.app_id = app_id;
        this.activity_group_id = activity_group_id;
        this.activity_group_name = activity_group_name;
        this.activity_id = activity_id;
        this.resource_id = resource_id;
        this.issue_question = issue_question;
        this.issue_answer = issue_answer;
        this.positive_feedback_count = positive_feedback_count;
        this.negative_feedback_count = negative_feedback_count;
        this.faq_status = faq_status;
    }

    @NonNull
    public String getUnique_question_id() {
        return unique_question_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getActivity_group_id() {
        return activity_group_id;
    }

    public String getActivity_group_name() {
        return activity_group_name;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public String getResource_id() {
        return resource_id;
    }

    public String getIssue_question() {
        return issue_question;
    }

    public String getIssue_answer() {
        return issue_answer;
    }

    public int getPositive_feedback_count() {
        return positive_feedback_count;
    }

    public int getNegative_feedback_count() {
        return negative_feedback_count;
    }

    public int getFaq_status() {
        return faq_status;
    }

}
