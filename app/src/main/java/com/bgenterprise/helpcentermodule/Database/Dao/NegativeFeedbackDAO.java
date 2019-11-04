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

    @Update
    void UpdateIssue(NegativeFeedback... negativeFeedback);

    @Delete
    void DeleteIssue(NegativeFeedback negativeFeedback);

    @Query("SELECT * FROM negative_feedback_log WHERE sync_status = 'no'")
    List<NegativeFeedback> unsyncedNegativeFeedback();

    @Query("UPDATE negative_feedback_log SET sync_status = :sync_status WHERE staff_id = :staff_id AND app_id = :app_id")
    void updateSyncStatus(String staff_id, String app_id, String sync_status);
}
