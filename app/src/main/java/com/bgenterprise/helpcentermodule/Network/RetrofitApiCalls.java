package com.bgenterprise.helpcentermodule.Network;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.NegativeFeedbackResponse;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.QuestionsEnglishSyncDown;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.QuestionsHausaSyncDown;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitApiCalls {

    @GET("sync_down_questions_english.php")
    Call<List<QuestionsEnglishSyncDown>> syncDownQuestionsEnglish(@Query("last_sync_down_questions_english") String last_sync_down);

    @GET("sync_down_questions_hausa.php")
    Call<List<QuestionsHausaSyncDown>> syncDownQuestionsHausa(@Query("last_sync_down_questions_hausa") String last_sync_down);

    @FormUrlEncoded
    @POST("sync_up_negative_feedback.php")
    Call<List<NegativeFeedbackResponse>> syncUpNegativeFeedback(@Field("negative_feedback_list") String negative_feedback_list);

}
