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

    @Query("SELECT * FROM questions_english WHERE app_id = :appID AND language_id = :languageId GROUP BY activity_group_id")
    List<QuestionsEnglish> getActivityGroups(String appID, String languageId);

    @Query("SELECT * FROM questions_english WHERE activity_group_id = :groupId AND language_id = :languageId GROUP BY activity_id")
    List<QuestionsEnglish> getActivities(String groupId, String languageId);

    @Query("SELECT * FROM questions_english WHERE activity_id = :activityID AND language_id = :languageId")
    List<QuestionsEnglish> getActivityQuestions(String activityID, String languageId);

    @Query("SELECT * FROM questions_english WHERE unique_question_id = :questionID AND language_id = :languageId")
    List<QuestionsEnglish> getAllQuestionSolution (String questionID, String languageId);

    @Query("SELECT * FROM questions_english WHERE app_id = :appID AND language_id = :languageId ORDER BY faq_status DESC LIMIT 3")
    List<QuestionsEnglish> getAllFAQQuestions(String appID, String languageId);

    @Query("UPDATE questions_english SET positive_feedback_count = positive_feedback_count + 1 WHERE unique_question_id = :questionID")
    void updateThumbsUp(String questionID);

    @Query("UPDATE questions_english SET negative_feedback_count = negative_feedback_count + 1 WHERE unique_question_id = :questionID")
    void updateThumbsDown(String questionID);

    @Query("SELECT * FROM questions_english")
    List<QuestionsEnglish> getAllQuestions();

    @Query("SELECT * FROM questions_english WHERE (positive_feedback_count != 0 OR negative_feedback_count != 0)")
    List<QuestionsEnglish> getQuestionFeedback();

    @Query("UPDATE questions_english SET positive_feedback_count = :positive, negative_feedback_count = :negative WHERE unique_question_id = :question_id")
    void updateSyncedFeedback(String question_id, String positive, String negative);
}
