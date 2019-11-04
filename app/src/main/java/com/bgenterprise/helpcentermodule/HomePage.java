package com.bgenterprise.helpcentermodule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.NegativeFeedbackResponse;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.QuestionsEnglishSyncDown;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.QuestionsHausaSyncDown;
import com.bgenterprise.helpcentermodule.Network.RetrofitClient;
import com.bgenterprise.helpcentermodule.Network.RetrofitApiCalls;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    HelpSessionManager sessionM;
    RetrofitApiCalls apiInterface;
    List<QuestionsEnglish> newTable = new ArrayList<>();
    HashMap<String, String> help_details;
    HelpCenterDatabase helpcenterdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_home_page);
        MaterialToolbar custom_toolbar = findViewById(R.id.local_toolbar);
        CardView tgl_test_card = findViewById(R.id.tgl_test_card);
        CardView tgl_interview_card = findViewById(R.id.tgl_interview_card);
        CardView field_mapping_card = findViewById(R.id.field_mapping_card);
        CardView tfm_card = findViewById(R.id.tfm_card);

        setSupportActionBar(custom_toolbar);
        helpcenterdb = HelpCenterDatabase.getInstance(HomePage.this);
        sessionM = new HelpSessionManager(HomePage.this);
        help_details = sessionM.getHelpDetails();
        checkMemory();
        makeCall();

        tgl_test_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tgl_test");
            startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
        });

        tgl_interview_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tgl_interview");
            startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
        });

        field_mapping_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("field_mapping");
            startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
        });

        tfm_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tfm");
            startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CALL){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkMemory();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == REQUEST_CALL){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makeCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sync_app:
                sync();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkMemory() {
        if (ContextCompat.checkSelfPermission((HomePage.this),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomePage.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CALL);
        }else {
        }
    }

    public void makeCall() {
        if (ContextCompat.checkSelfPermission((this),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
        }
    }

    //TODO ---> Combine all permissions.

    @Override
    public void onBackPressed() {
        finish();
    }

    public void sync(){
        Toast.makeText(HomePage.this, "Beginning syncing process", Toast.LENGTH_LONG).show();
        syncDownQuestionsEnglish();
        syncDownQuestionsHausa();
        syncUpNegativeFeedback();
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
                                    x.getActivity_group_id(),
                                    x.getActivity_group_name(),
                                    x.getActivity_id(),
                                    x.getResource_id(),
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
                        runOnUiThread(() -> Toast.makeText(HomePage.this, "Sync down completed",Toast.LENGTH_LONG).show());
                    });
                }
            }

            @Override
            public void onFailure(Call<List<QuestionsEnglishSyncDown>> call, Throwable t) {
                Toast.makeText(HomePage.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void syncDownQuestionsHausa(){
        RetrofitApiCalls service = RetrofitClient.getApiClient().create(RetrofitApiCalls.class);
        Call<List<QuestionsHausaSyncDown>> call = service.syncDownQuestionsHausa(help_details.get(HelpSessionManager.KEY_LAST_SYNC_QUESTIONS_HAUSA));
        call.enqueue(new Callback<List<QuestionsHausaSyncDown>>() {
            @Override
            public void onResponse(Call<List<QuestionsHausaSyncDown>> call, Response<List<QuestionsHausaSyncDown>> response) {
                if(response.isSuccessful()){
                    List<QuestionsHausaSyncDown> syncData = response.body();
                    List<QuestionsHausa> question_hausa = new ArrayList<>();

                    try{
                        for(QuestionsHausaSyncDown y: syncData){
                            question_hausa.add(new QuestionsHausa(y.getUnique_question_id(),
                                    y.getApp_id(),
                                    y.getActivity_group_id(),
                                    y.getActivity_group_name(),
                                    y.getActivity_id(),
                                    y.getResource_id(),
                                    y.getIssue_question(),
                                    y.getIssue_answer(),
                                    0,
                                    0,
                                    Integer.parseInt(y.getFaq_status())));
                            sessionM.SET_LAST_SYNC_QUESTIONS_HAUSA(y.getLast_sync_time());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    AppExecutors.getInstance().diskIO().execute(() -> {
                        helpcenterdb.getHausaDao().InsertFromOnline(question_hausa);
                        runOnUiThread(() -> Toast.makeText(HomePage.this, "Sync Completed", Toast.LENGTH_LONG).show());
                    });
                }
            }

            @Override
            public void onFailure(Call<List<QuestionsHausaSyncDown>> call, Throwable t) {

            }
        });
    }

    public void syncUpNegativeFeedback(){
        //Get unsynced etNegativeFeedback then call function to sync up using retrofit
        Gson json = new Gson();
        AppExecutors.getInstance().diskIO().execute(() -> {
            String unsyncedNegativeFeedback = json.toJson(helpcenterdb.getFeedbackDao().unsyncedNegativeFeedback());
            runOnUiThread(() -> initNegativeFeedbackSync(unsyncedNegativeFeedback));
        });
    }

    public void initNegativeFeedbackSync(String unsynced){
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
                            Toast.makeText(HomePage.this, "Sync Up Completed",Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<NegativeFeedbackResponse>> call, Throwable t) {

            }
        });
    }
}
