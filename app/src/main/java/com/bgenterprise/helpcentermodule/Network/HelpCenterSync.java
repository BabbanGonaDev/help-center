package com.bgenterprise.helpcentermodule.Network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bgenterprise.helpcentermodule.AppExecutors;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.ContactSupport;
import com.bgenterprise.helpcentermodule.Database.Tables.NegativeDropdown;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.HelpSessionManager;
import com.bgenterprise.helpcentermodule.HomePage;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.ContactSupportSyncDown;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.GeneralFeedbackResponse;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.NegativeFeedbackResponse;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.QuestionsEnglishSyncDown;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpCenterSync extends Service {
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;
    HelpCenterDatabase helpcenterdb;
    int x;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        helpcenterdb = HelpCenterDatabase.getInstance(this);
        sessionM = new HelpSessionManager(this);
        help_details = sessionM.getHelpDetails();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Trigger the sync functions here.
        //For the syncing function, we download all languages to the phone, then switch the tables based on shared pref language.
        x = 0;

        syncDownQuestionsEnglish();
        syncUpNegativeFeedback();
        syncUpEnglishFeedback();
        syncDownContactSupport();
        syncDownNegativeDropdown();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void syncDownQuestionsEnglish(){
        RetrofitApiCalls service = RetrofitClient.getApiClient().create(RetrofitApiCalls.class);
        Call<List<QuestionsEnglishSyncDown>> call = service.syncDownQuestionsEnglish(help_details.get(HelpSessionManager.KEY_LAST_SYNC_QUESTIONS_ENGLISH));
        call.enqueue(new Callback<List<QuestionsEnglishSyncDown>>() {
            @Override
            public void onResponse(Call<List<QuestionsEnglishSyncDown>> call, Response<List<QuestionsEnglishSyncDown>> response) {
                if(response.isSuccessful()){
                    List<QuestionsEnglishSyncDown> syncData = response.body();
                    List<QuestionsEnglish> question_eng = new ArrayList<>();

                    try {
                        for (QuestionsEnglishSyncDown x : syncData) {
                            question_eng.add(new QuestionsEnglish(x.getUnique_question_id(),
                                    x.getApp_id(),
                                    x.getLanguage_id(),
                                    x.getActivity_group_id(),
                                    x.getActivity_id(),
                                    x.getActivity_name(),
                                    x.getResource_id(),
                                    x.getResource_url(),
                                    x.getIssue_question(),
                                    x.getIssue_answer(),
                                    0,
                                    0,
                                    Integer.parseInt(x.getFaq_status())));
                            sessionM.SET_LAST_SYNC_QUESTIONS_ENGLISH(x.getLast_sync_time());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    AppExecutors.getInstance().diskIO().execute(() -> {
                        helpcenterdb.getEnglishDao().InsertFromOnline(question_eng);
                    });

                    checkServiceStatus(1);
                }
            }

            @Override
            public void onFailure(Call<List<QuestionsEnglishSyncDown>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Help Center Questions: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void syncUpEnglishFeedback(){
        //Get feedback values of all questions that have been edited.
        Gson json = new Gson();
        AppExecutors.getInstance().diskIO().execute(() -> {
            String feedbackValues = json.toJson(helpcenterdb.getEnglishDao().getQuestionFeedback());
            initFeedbackSync(feedbackValues);
        });
    }

    public void initFeedbackSync(String values){
        Log.d("CHECK", values);
        Log.d("CHECK", "We have started initGeneralFeedbackSync");
        //This function syncs up the values of the feedback for the questions in the app.
        RetrofitApiCalls service = RetrofitClient.getApiClient().create(RetrofitApiCalls.class);
        Call<List<GeneralFeedbackResponse>> call = service.syncUpEnglishFeedback(values);
        call.enqueue(new Callback<List<GeneralFeedbackResponse>>() {
            @Override
            public void onResponse(Call<List<GeneralFeedbackResponse>> call, Response<List<GeneralFeedbackResponse>> response) {
                if(response.isSuccessful()){
                    List<GeneralFeedbackResponse> responseData = response.body();
                    try{
                        for(GeneralFeedbackResponse h: responseData){
                            AppExecutors.getInstance().diskIO().execute(() -> {
                                helpcenterdb.getEnglishDao().updateSyncedFeedback(h.getQuestion_id(), h.getPositive_feedback_count(), h.getNegative_feedback_count());
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    checkServiceStatus(1);
                }
            }

            @Override
            public void onFailure(Call<List<GeneralFeedbackResponse>> call, Throwable t) {
                Log.d("CHECK", t.getMessage());
            }
        });
    }

    public void syncUpNegativeFeedback(){
        //Get unsynced etNegativeFeedback then call function to sync up using retrofit
        Gson json = new Gson();
        AppExecutors.getInstance().diskIO().execute(() -> {
            String unsyncedNegativeFeedback = json.toJson(helpcenterdb.getFeedbackDao().unsyncedNegativeFeedback());
            initNegativeFeedbackSync(unsyncedNegativeFeedback);
        });
    }

    public void initNegativeFeedbackSync(String unsynced){
        Log.d("CHECK", "initNegativeFeedbackSync started");
        RetrofitApiCalls service = RetrofitClient.getApiClient().create(RetrofitApiCalls.class);
        Call<List<NegativeFeedbackResponse>> call = service.syncUpNegativeFeedback(unsynced);
        call.enqueue(new Callback<List<NegativeFeedbackResponse>>() {
            @Override
            public void onResponse(Call<List<NegativeFeedbackResponse>> call, Response<List<NegativeFeedbackResponse>> response) {
                if(response.isSuccessful()){
                    List<NegativeFeedbackResponse> responseData = response.body();
                    try{
                        for(NegativeFeedbackResponse z: responseData){
                            AppExecutors.getInstance().diskIO().execute(() -> {
                                helpcenterdb.getFeedbackDao().updateSyncStatus(z.getStaff_id(), z.getApp_id(), z.getSync_status());
                            });
                            Toast.makeText(getApplicationContext(), "Sync Up Completed",Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    checkServiceStatus(1);
                }
            }

            @Override
            public void onFailure(Call<List<NegativeFeedbackResponse>> call, Throwable t) {

            }
        });
    }

    public void syncDownContactSupport(){
        RetrofitApiCalls service = RetrofitClient.getApiClient().create(RetrofitApiCalls.class);
        Call<List<ContactSupportSyncDown>> call = service.syncDownContactSupport();
        call.enqueue(new Callback<List<ContactSupportSyncDown>>() {
            @Override
            public void onResponse(Call<List<ContactSupportSyncDown>> call, Response<List<ContactSupportSyncDown>> response) {
                if(response.isSuccessful()){
                    List<ContactSupportSyncDown> syncData = response.body();
                    List<ContactSupport> contact_support = new ArrayList<>();

                    try{
                        for(ContactSupportSyncDown j: syncData){
                            contact_support.add(new ContactSupport(j.getLocation(),
                                    j.getWhatsapp_number(),
                                    j.getPhone_number()));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    AppExecutors.getInstance().diskIO().execute(() -> {
                        helpcenterdb.getContactDao().InsertFromOnline(contact_support);
                    });

                    checkServiceStatus(1);
                }
            }

            @Override
            public void onFailure(Call<List<ContactSupportSyncDown>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Help Center Contact Support: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void syncDownNegativeDropdown(){
        RetrofitApiCalls service = RetrofitClient.getApiClient().create(RetrofitApiCalls.class);
        Call<List<NegativeDropdown>> call = service.syncDownNegativeDropdown();
        call.enqueue(new Callback<List<NegativeDropdown>>() {
            @Override
            public void onResponse(Call<List<NegativeDropdown>> call, Response<List<NegativeDropdown>> response) {
                if(response.isSuccessful()){
                    List<NegativeDropdown> syncData = response.body();

                    AppExecutors.getInstance().diskIO().execute(() -> {
                        helpcenterdb.getDropdownDao().InsertDropdown(syncData);
                    });

                    checkServiceStatus(1);
                }
            }

            @Override
            public void onFailure(Call<List<NegativeDropdown>> call, Throwable t) {

            }
        });
    }

    public void checkServiceStatus(int value){
        //TODO --> Not sure if this is working yet....
        x += value;
        Log.d("CHECK", String.valueOf(x));
        if(x == 5){
            Log.d("CHECK", "Value: " + x + " Stopping the service here.");
            Toast.makeText(getApplicationContext(), "Syncing Help-center Complete", Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }
}
