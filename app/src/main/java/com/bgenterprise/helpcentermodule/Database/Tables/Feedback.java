package com.bgenterprise.helpcentermodule.Database.Tables;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "negative_feedback_log")
public class Feedback {

    @PrimaryKey(autoGenerate = true)
    private int counter;

    private String staff_id;
    private String app_id;
    private String question_id;
    private String comment;
    private String date;
    private int sync_status;

    public Feedback(int counter, String staff_id, String app_id, String question_id, String comment, String date, int sync_status){
        this.counter = counter;
        this.staff_id = staff_id;
        this.app_id = app_id;
        this.question_id = question_id;
        this.comment = comment;
        this.date = date;
        this.sync_status = sync_status;
    }

    public int getCounter() {
        return counter;
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

    public int getSync_status() {
        return sync_status;
    }
}
