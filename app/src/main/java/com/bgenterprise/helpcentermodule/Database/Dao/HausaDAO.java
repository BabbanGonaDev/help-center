package com.bgenterprise.helpcentermodule.Database.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.IssuesEnglish;
import com.bgenterprise.helpcentermodule.Database.Tables.IssuesHausa;

import java.util.List;

@Dao
public interface HausaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertIssues(IssuesHausa... issuesHausaT);

    @Update
    void UpdateIssue(IssuesHausa... issuesHausaT);

    @Delete
    void DeleteIssue(IssuesHausa issuesHausaT);

    @Query("SELECT * FROM issues_hausa WHERE app_id = :appID")
    List<IssuesHausa> getAllAppIssueH(String appID);

    @Query("SELECT * FROM issues_hausa WHERE app_id = :appID and activity_group_id = :groupID")
    List<IssuesHausa> getAllGroupIssuesH(String appID, String groupID);

    @Query("SELECT * FROM issues_hausa WHERE app_id = :appID and activity_group_id = :groupID and activity_id = :activityID")
    List<IssuesHausa> getActivityIssuesH(String appID, String groupID, String activityID);

    @Query("SELECT * FROM issues_hausa WHERE app_id = :appID and activity_group_id = :groupID and issue_question = :questionID")
    List<IssuesHausa> getQuestionSolutionH(String appID, String groupID, String questionID);

    @Query("SELECT * FROM issues_hausa WHERE app_id = :appID and issue_question = :questionID")
    List<IssuesHausa> getAllFAQIssuesH(String appID, String questionID);

}
