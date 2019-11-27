package com.bgenterprise.helpcentermodule.Database.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;

import java.util.List;

@Dao
public interface HausaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertIssues(QuestionsHausa... questionsHausaT);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromCSV(List<QuestionsHausa> issuesList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromOnline(List<QuestionsHausa> issuesList);

    @Update
    void UpdateIssue(QuestionsHausa... questionsHausas);

    @Delete
    void DeleteIssue(QuestionsHausa questionsHausa);

    @Query("SELECT * FROM questions_hausa WHERE app_id = :appID GROUP BY activity_group_id")
    List<QuestionsHausa> getActivityGroups(String appID);

    @Query("SELECT * FROM questions_hausa WHERE activity_group_id = :groupId GROUP BY activity_id")
    List<QuestionsHausa> getActivities(String groupId);

    @Query("SELECT * FROM questions_hausa WHERE activity_id = :activityID")
    List<QuestionsHausa> getActivityQuestions(String activityID);

    @Query("SELECT * FROM questions_hausa WHERE unique_question_id = :questionID")
    List<QuestionsHausa> getAllQuestionSolution (String questionID);

    @Query("SELECT * FROM questions_hausa WHERE app_id = :appID ORDER BY faq_status DESC LIMIT 3")
    List<QuestionsHausa> getAllFAQQuestions(String appID);

    @Query("UPDATE questions_hausa SET positive_feedback_count = positive_feedback_count + 1 WHERE unique_question_id = :questionID")
    void updateThumbsUp(String questionID);

    @Query("UPDATE questions_hausa SET negative_feedback_count = negative_feedback_count + 1 WHERE unique_question_id = :questionID")
    void updateThumbsDown(String questionID);

    @Query("SELECT * FROM questions_hausa")
    List<QuestionsHausa> getAllQuestions();

    @Query("SELECT * FROM questions_hausa WHERE (positive_feedback_count != 0 OR negative_feedback_count != 0)")
    List<QuestionsHausa> getQuestionFeedback();

    @Query("UPDATE questions_hausa SET positive_feedback_count = :positive, negative_feedback_count = :negative WHERE unique_question_id = :question_id")
    void updateSyncedFeedback(String question_id, String positive, String negative);
}
