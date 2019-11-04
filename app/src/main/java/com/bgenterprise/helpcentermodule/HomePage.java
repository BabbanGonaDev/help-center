package com.bgenterprise.helpcentermodule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Network.ApiClientHelpCenter;
import com.bgenterprise.helpcentermodule.Network.ApiCalls;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomePage extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    HelpSessionManager sessionM;
    String app_id;
    ApiCalls apiInterface;
    List<QuestionsEnglish> newTable = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_home_page);

        CardView tgl_test_card = findViewById(R.id.tgl_test_card);
        CardView tgl_interview_card = findViewById(R.id.tgl_interview_card);
        CardView field_mapping_card = findViewById(R.id.field_mapping_card);
        CardView tfm_card = findViewById(R.id.tfm_card);
        Button btnSync = findViewById(R.id.btnSync);

        sessionM = new HelpSessionManager(HomePage.this);
        checkMemory();
        makeCall();

        tgl_test_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tgl_test");
            beginNewActivity(app_id);
        });

        tgl_interview_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tgl_interview");
            beginNewActivity(app_id);
        });

        field_mapping_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("field_mapping");
            beginNewActivity(app_id);
        });

        tfm_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tfm");
            beginNewActivity(app_id);
        });

        btnSync.setOnClickListener(view -> sync());
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

    public void beginNewActivity(String app_id){
        //This is an Intent function moves into the Activity group activity.
        startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
    }

    public void sync(){
        apiInterface = ApiClientHelpCenter.getApiClient().create(ApiCalls.class);
        Call<List<QuestionsEnglish>> call = apiInterface.getString();
        Toast.makeText(HomePage.this, "Syncing data", Toast.LENGTH_SHORT).show();
        call.enqueue(new Callback<List<QuestionsEnglish>>() {
            @Override
            public void onResponse(@NonNull Call<List<QuestionsEnglish>> call, @NonNull Response<List<QuestionsEnglish>> response) {

                Context mCtx = null;
                HelpCenterDatabase helpCenterDb;

                if (response.isSuccessful()) {
                    List<QuestionsEnglish> responseList = response.body();

                    if (responseList != null) {
                        for (int i = 0; i < responseList.size(); i++) {
                            QuestionsEnglish questionsEnglish = responseList.get(i);
                            DownloadData downloadData = new DownloadData();
                            downloadData.execute(questionsEnglish);
                        }
                    }

                    Log.d("Retrofit_response", Objects.requireNonNull(responseList).get(0).getIssue_answer());
                    Toast.makeText(HomePage.this, "Syncing Successful", Toast.LENGTH_SHORT).show();
                }
                else {
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            Log.e("Error 400", "Bad Request");
                            Toast.makeText(getApplicationContext(), "Error 400: Network Error Please Reconnect",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case 404:
                            Log.e("Error 404", "Not Found");
                            Toast.makeText(getApplicationContext(), "Error 404: Page not found",
                                    Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Log.e("Error", "Generic Error");
                            Toast.makeText(getApplicationContext(), "Error: Network Error Please Reconnect",
                                    Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<QuestionsEnglish>> call, @NonNull Throwable t) {
                Log.d("tobi", t.toString());
                Toast.makeText(HomePage.this, "Error "  + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadData extends AsyncTask<QuestionsEnglish, Void, Void>{

        HelpCenterDatabase helpCenterDatabase;

        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(QuestionsEnglish... questionsEnglishes) {
            QuestionsEnglish questionsEnglish = questionsEnglishes[0];

            try {
                helpCenterDatabase = HelpCenterDatabase.getInstance(HomePage.this);
                helpCenterDatabase.getEnglishDao().InsertFromOnline(questionsEnglish);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
