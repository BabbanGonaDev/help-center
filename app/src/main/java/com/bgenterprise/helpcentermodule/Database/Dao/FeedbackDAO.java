package com.bgenterprise.helpcentermodule.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.Feedback;

import java.util.List;

@Dao
public interface FeedbackDAO{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFeedback(Feedback feedback);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromCSV(List<Feedback> feedbackList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromOnline(Feedback feedback);

    @Update
    void UpdateIssue(Feedback... feedbacks);

    @Delete
    void DeleteIssue(Feedback feedback);

    @Query("SELECT * FROM negative_feedback_log WHERE sync_status = 0")
    List<Feedback> getYetToSync();
}
