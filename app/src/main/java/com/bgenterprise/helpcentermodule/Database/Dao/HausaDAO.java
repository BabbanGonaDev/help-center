package com.bgenterprise.helpcentermodule.Database.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;

import java.util.List;

@Dao
public interface HausaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertIssues(QuestionsHausa... questionsHausaT);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromCSV(List<QuestionsHausa> issuesList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromOnline(QuestionsHausa issuesList);

    @Update
    void UpdateIssue(QuestionsHausa... questionsHausaT);

    @Delete
    void DeleteIssue(QuestionsHausa questionsHausaT);

    @Query("SELECT * FROM questions_hausa WHERE app_id = :appID GROUP BY activity_group_name")
    List<QuestionsHausa> getActivityGroups(String appID);

    @Query("SELECT * FROM questions_hausa WHERE activity_group_name = :groupName GROUP BY activity_id")
    List<QuestionsHausa> getActivities(String groupName);

    @Query("SELECT * FROM questions_hausa WHERE activity_id = :activityID")
    List<QuestionsHausa> getActivityQuestions(String activityID);

    @Query("SELECT * FROM questions_hausa WHERE unique_question_id = :questionID")
    List<QuestionsHausa> getAllQuestionSolution (String questionID);

    @Query("SELECT * FROM questions_hausa WHERE app_id = :appID ORDER BY faq_status DESC LIMIT 3")
    List<QuestionsHausa> getAllFAQQuestions(String appID);

}
