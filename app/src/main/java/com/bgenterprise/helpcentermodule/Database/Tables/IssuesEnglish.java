package com.bgenterprise.helpcentermodule.Database.Tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.RoomDatabase;

@Entity(tableName = "issues_english")
public class IssuesEnglish {

    @PrimaryKey
    private int unique_question_id;

    private String app_id;
    private String activity_group_id;
    private String activity_id;
    private String resource_id;
    private String issue_question;
    private String issue_answer;
    private int faq_status;

    public IssuesEnglish(int unique_question_id, String app_id, String activity_group_id,
                         String activity_id, String resource_id, String issue_question,
                         String issue_answer, int faq_status) {
        this.unique_question_id = unique_question_id;
        this.app_id = app_id;
        this.activity_group_id = activity_group_id;
        this.activity_id = activity_id;
        this.resource_id = resource_id;
        this.issue_question = issue_question;
        this.issue_answer = issue_answer;
        this.faq_status = faq_status;
    }

    public int getUnique_question_id() {
        return unique_question_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getActivity_group_id() {
        return activity_group_id;
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

    public int getFaq_status() {
        return faq_status;
    }


}
