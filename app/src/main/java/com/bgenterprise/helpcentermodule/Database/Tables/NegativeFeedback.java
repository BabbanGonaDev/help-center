package com.bgenterprise.helpcentermodule.Database.Tables;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "negative_feedback_log")
public class NegativeFeedback {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String staff_id;
    private String app_id;
    private String question_id;
    private String comment;
    private String date;
    private String sync_status;

    public NegativeFeedback(int id, String staff_id, String app_id, String question_id, String comment, String date, String sync_status){
        this.id = id;
        this.staff_id = staff_id;
        this.app_id = app_id;
        this.question_id = question_id;
        this.comment = comment;
        this.date = date;
        this.sync_status = sync_status;
    }

    public int getId() {
        return id;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getSync_status() {
        return sync_status;
    }
}
