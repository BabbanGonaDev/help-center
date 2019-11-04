package com.bgenterprise.helpcentermodule.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.NegativeFeedback;

import java.util.List;

@Dao
public interface NegativeFeedbackDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFeedback(NegativeFeedback negativeFeedback);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromCSV(List<NegativeFeedback> negativeFeedbackList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromOnline(NegativeFeedback negativeFeedback);

    @Update
    void UpdateIssue(NegativeFeedback... negativeFeedback);

    @Delete
    void DeleteIssue(NegativeFeedback negativeFeedback);

    @Query("SELECT * FROM negative_feedback_log WHERE sync_status = 'no'")
    List<NegativeFeedback> getYetToSync();
}
