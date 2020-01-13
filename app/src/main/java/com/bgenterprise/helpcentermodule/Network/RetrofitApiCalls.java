package com.bgenterprise.helpcentermodule.Network;

import com.bgenterprise.helpcentermodule.Database.Tables.NegativeDropdown;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.ContactSupportSyncDown;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.GeneralFeedbackResponse;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.NegativeFeedbackResponse;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.QuestionsEnglishSyncDown;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitApiCalls {

    @GET("sync_down_questions.php")
    Call<List<QuestionsEnglishSyncDown>> syncDownQuestionsEnglish(@Query("last_sync_down_questions_english") String last_sync_down);

    @FormUrlEncoded
    @POST("sync_up_feedback.php")
    Call<List<GeneralFeedbackResponse>> syncUpEnglishFeedback(@Field("english_feedback_list") String english_feedback_list);

    @FormUrlEncoded
    @POST("sync_up_negative_feedback.php")
    Call<List<NegativeFeedbackResponse>> syncUpNegativeFeedback(@Field("negative_feedback_list") String negative_feedback_list);

    @GET("sync_down_contact_support.php")
    Call<List<ContactSupportSyncDown>> syncDownContactSupport();

    //Removed the @Streaming, so that resources don't get "half" downloaded and un-use-able due to bad network.
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrl(@Url String fileUrl);

    @GET("sync_down_negative_dropdown.php")
    Call<List<NegativeDropdown>> syncDownNegativeDropdown();
}
