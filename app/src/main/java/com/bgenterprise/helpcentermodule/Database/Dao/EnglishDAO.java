package com.bgenterprise.helpcentermodule.Database.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;

import java.util.List;

@Dao
public interface EnglishDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertIssues(QuestionsEnglish... questionsEnglishT);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromCSV(List<QuestionsEnglish> issuesList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromOnline(List<QuestionsEnglish> issuesList);

    @Update
    void UpdateIssue(QuestionsEnglish... questionsEnglishT);

    @Delete
    void DeleteIssue(QuestionsEnglish questionsEnglishT);

    @Query("SELECT * FROM questions_english WHERE app_id = :appID GROUP BY activity_group_name")
    List<QuestionsEnglish> getActivityGroups(String appID);

    @Query("SELECT * FROM questions_english WHERE activity_group_id = :groupId GROUP BY activity_id")
    List<QuestionsEnglish> getActivities(String groupId);

    @Query("SELECT * FROM questions_english WHERE activity_id = :activityID")
    List<QuestionsEnglish> getActivityQuestions(String activityID);

    @Query("SELECT * FROM questions_english WHERE unique_question_id = :questionID")
    List<QuestionsEnglish> getAllQuestionSolution (String questionID);

    @Query("SELECT * FROM questions_english WHERE app_id = :appID ORDER BY faq_status DESC LIMIT 3")
    List<QuestionsEnglish> getAllFAQQuestions(String appID);

    @Query("UPDATE questions_english SET positive_feedback_count = positive_feedback_count + 1 WHERE unique_question_id = :questionID")
    void updateThumbsUp(String questionID);

    @Query("UPDATE questions_english SET negative_feedback_count = negative_feedback_count + 1 WHERE unique_question_id = :questionID")
    void updateThumbsDown(String questionID);
}
