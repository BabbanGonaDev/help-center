package com.bgenterprise.helpcentermodule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bgenterprise.helpcentermodule.Database.FileDownloadClient;
import com.bgenterprise.helpcentermodule.Database.Tables.ContactSupport;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.ContactSupportSyncDown;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.NegativeFeedbackResponse;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.QuestionsEnglishSyncDown;
import com.bgenterprise.helpcentermodule.Network.ModelClasses.QuestionsHausaSyncDown;
import com.bgenterprise.helpcentermodule.Network.RetrofitApiCalls;
import com.bgenterprise.helpcentermodule.Network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HomePage extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    HelpSessionManager sessionM;
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

        createFolder("TestOnlineFile");

        final String url = "https://futurestud.io/images/futurestudio-university-logo.png";

        downloadFile(url);

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

    public void downloadImages () {
        if (ContextCompat.checkSelfPermission((this),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
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
        syncDownContactSupport();
    }

    public void createFolder(String fname){
        File f = new File(Environment.getExternalStorageDirectory()+"/"+fname);
        if(f.exists()) {
            Toast.makeText(this,  " already exits.", Toast.LENGTH_SHORT).show();
        } else
        if (!f.exists()) {
            try {
                f.mkdirs();
                Toast.makeText(this,  " created ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("folder_creation", "didnt work for some reason");
            }
        }
    }

    public void downloadFile(String url) {

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://futurestud.io/");
        Retrofit retrofit = builder.build();

        FileDownloadClient fileDownloadClient = retrofit.create((FileDownloadClient.class));
        Call<ResponseBody> call = fileDownloadClient.downloadFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("download", "working");
                boolean success = writeResponseBodyToDisk(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("download", "didnt work for some reason");
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory("TestOnlineFile") + File.separator + "FutureStudio.png");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("TAG", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
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
                                    x.getActivity_id(),
                                    x.getActivity_name(),
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
                                    y.getActivity_id(),
                                    y.getActivity_name(),
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
                }
            }

            @Override
            public void onFailure(Call<List<ContactSupportSyncDown>> call, Throwable t) {
                Toast.makeText(HomePage.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
