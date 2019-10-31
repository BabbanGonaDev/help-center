package com.bgenterprise.helpcentermodule.Database.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.IssuesEnglish;

import java.util.List;

@Dao
public interface EnglishDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertIssues(IssuesEnglish... issuesEnglishT);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromCSV(List<IssuesEnglish> issuesList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromOnline(IssuesEnglish issuesList);

    @Update
    void UpdateIssue(IssuesEnglish... issuesEnglishT);

    @Delete
    void DeleteIssue(IssuesEnglish issuesEnglishT);

    @Query("SELECT * FROM issues_english WHERE app_id = :appID GROUP BY activity_group_name")
    List<IssuesEnglish> getActivityGroups(String appID);

    @Query("SELECT * FROM issues_english WHERE activity_group_name = :groupName GROUP BY activity_id")
    List<IssuesEnglish> getActivities(String groupName);

    @Query("SELECT * FROM issues_english WHERE activity_id = :activityID")
    List<IssuesEnglish> getActivityIssues(String activityID);

    @Query("SELECT * FROM issues_english WHERE unique_question_id = :questionID")
    List<IssuesEnglish> getAllQuestionSolution (int questionID);

    @Query("SELECT * FROM issues_english WHERE app_id = :appID ORDER BY faq_status DESC LIMIT 3")
    List<IssuesEnglish> getAllFAQIssues(String appID);


}
