package com.bgenterprise.helpcentermodule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.bgenterprise.helpcentermodule.QuestionActivities.ViewActivityGroups;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomePage extends AppCompatActivity {

    MaterialButton btn_cancel_resource_sync;
    MaterialTextView mtv_success_text, mtv_fail_text, mtv_progress_text;
    ProgressBar progressBar;
    LinearLayoutCompat loading_layout;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;
    String session_app_lang, session_dao_lang;
    HelpCenterDatabase helpcenterdb;
    Boolean writtenToDisk;
    List<QuestionsEnglish> resourceList, downloadList;
    int currentNo, success_count, fail_count;

    String[] appPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE
    };
    private static final int PERMISSIONS_REQUEST_CODE = 4045;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_home_page);
        MaterialToolbar custom_toolbar = findViewById(R.id.local_toolbar);
        CardView tgl_test_card = findViewById(R.id.tgl_test_card);
        CardView tgl_interview_card = findViewById(R.id.tgl_interview_card);
        CardView field_mapping_card = findViewById(R.id.field_mapping_card);
        CardView tfm_card = findViewById(R.id.tfm_card);
        btn_cancel_resource_sync = findViewById(R.id.btn_cancel_resource_sync);
        mtv_progress_text = findViewById(R.id.mtv_progress_text);
        mtv_success_text = findViewById(R.id.mtv_success_text);
        mtv_fail_text = findViewById(R.id.mtv_fail_text);
        loading_layout = findViewById(R.id.loading_layout);
        progressBar = findViewById(R.id.progressBar);
        //TODO---> Use a switch case function to change the Dao being used based on the language stored in shared preferences.

        setSupportActionBar(custom_toolbar);
        helpcenterdb = HelpCenterDatabase.getInstance(HomePage.this);
        sessionM = new HelpSessionManager(HomePage.this);
        help_details = sessionM.getHelpDetails();

        tgl_test_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tgl_test");
            if(checkAndRequestPermissions()){
                startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
            }
        });

        tgl_interview_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tgl_interview");
            if(checkAndRequestPermissions()){
                startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
            }
        });

        field_mapping_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("field_mapping");
            if(checkAndRequestPermissions()){
                startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
            }
        });

        tfm_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tfm");
            if(checkAndRequestPermissions()){
                startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
            }
        });

        btn_cancel_resource_sync.setOnClickListener(view -> {
            //Close the layout of the progressbar, but it doesn't stop the downloading process.
            if(loading_layout.getVisibility() == View.VISIBLE){
                mtv_progress_text.setText("");
                mtv_fail_text.setText("");
                mtv_success_text.setText("");
                loading_layout.setVisibility(View.GONE);
            }
        });

    }

    public boolean checkAndRequestPermissions(){
        //Check which permissions are granted
        List<String> listPermissionsNeeded = new ArrayList<>();
        for(String perm : appPermissions){
            if(ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(perm);
            }
        }

        //Ask for non-granted permissions
        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(HomePage.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PERMISSIONS_REQUEST_CODE);
            return false;
        }

        //All permissions granted.
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sync_app){
            sync();
            return true;
        }else if (item.getItemId() == R.id.sync_resources){
            syncDownResources();
            return true;
        }else if (item.getItemId() == R.id.change_language){
            changeAppLanguage();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void changeAppLanguage(){
        new MaterialAlertDialogBuilder(HomePage.this)
                .setTitle("Choose App Language")
                .setSingleChoiceItems(Utility.app_language, -1, (dialogInterface, i) -> {
                    Toast.makeText(HomePage.this, "Language selected: " + Utility.app_language[i], Toast.LENGTH_LONG).show();

                    //Based on language, set the appropriate application language.
                    String selected_lang = Utility.app_language[i];
                    switch (selected_lang){
                        case "English":
                            sessionM.SET_LANGUAGE("en", "English");
                            break;
                        case "Hausa":
                            sessionM.SET_LANGUAGE("ha", "Hausa");
                            break;
                        default:
                            break;
                    }
                    dialogInterface.dismiss();
                    setAppLanguage();
                }).show();
    }

    public void setAppLanguage(){
        session_app_lang = sessionM.getAppLanguage();
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(session_app_lang);
        res.updateConfiguration(conf, dm);
        recreate();
    }

    /**
     *
     *  Everything below this line refers to the syncing functionality of the app alone.
     *
     */

    public void sync(){
        //For the syncing function, we download all languages to the phone, then switch the tables based on shared pref language.
        if(isConnected()) {
            Toast.makeText(HomePage.this, "Beginning syncing process", Toast.LENGTH_LONG).show();
            syncDownQuestionsEnglish();
            syncDownQuestionsHausa();
            syncUpNegativeFeedback();
            syncDownContactSupport();
        }else{
            //TODO Put snack-bar here with action button to go to data.
            Toast.makeText(HomePage.this, R.string.helpcenter_check_network, Toast.LENGTH_LONG).show();
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
                                    y.getResource_url(),
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

    public void syncDownResources(){
        //Get the list of all the questions on the table.
        resourceList = new ArrayList<>();
        AppExecutors.getInstance().diskIO().execute(() -> {
            resourceList = helpcenterdb.getEnglishDao().getAllQuestions();
            runOnUiThread(() -> {
                initResourceSync(resourceList);
            });
        });
    }

    public void initResourceSync(List<QuestionsEnglish> list){
        //Empty and clear all variables before starting.
        downloadList = new ArrayList<>();
        currentNo = 0;
        fail_count = 0;
        success_count = 0;
        mtv_progress_text.setText("");
        mtv_success_text.setText("");
        mtv_fail_text.setText("");
        btn_cancel_resource_sync.setText(R.string.helpcenter_cancel_operation);

        if(checkAndRequestPermissions()) {
            //Count the amount to be downloaded, for display to user.
            for (QuestionsEnglish x : list) {
                if (!resourceExists(x.getResource_id())) {
                    downloadList.add(x);
                }
            }

            //Now download the list.
            Toast.makeText(HomePage.this, "Total resources to be downloaded: " + downloadList.size(), Toast.LENGTH_SHORT).show();
            if(!downloadList.isEmpty()){
                //If the download list isn't empty, then display progress bar.
                loading_layout.setVisibility(View.VISIBLE);
                progressBar.setMax(downloadList.size());
            }
            for (QuestionsEnglish h : downloadList) {
                //For each individual entry on the list, begin download of the resource.
                downloadImage(h.getResource_url(), h.getResource_id());
            }
        }
    }

    public boolean isConnected(){
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();//Active network info
            return networkInfo != null && networkInfo.isConnected();
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }

    }

    public void downloadImage(String url, String file_name){
        //Initialize the retrofit service.
        RetrofitApiCalls service = RetrofitClient.getApiClient().create(RetrofitApiCalls.class);
        Call<ResponseBody> call = service.downloadFileWithDynamicUrl(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("CHECK", "server contact and has file");

                    //File response has gotten to the phone, now write the file to disk.
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        writtenToDisk = writeResponseBody(response.body(), file_name);

                        runOnUiThread(() -> {
                            Log.d("CHECK", "File download was a success ? ---> " + writtenToDisk);
                            if(writtenToDisk){
                                //If true, log as success and increase sync count.
                                updateSyncProgress();
                                updateSuccessCount();
                            }else{
                                //If false, log as fail and increase sync count.
                                updateSyncProgress();
                                updateFailCount();

                            }
                        });
                    });

                }else{
                    Log.d("CHECK", "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("CHECK", t.getLocalizedMessage());
                Toast.makeText(HomePage.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                updateSyncProgress();
                updateFailCount();
            }
        });
    }

    public static boolean writeResponseBody(ResponseBody body, String fileName){
        String storage_state = Environment.getExternalStorageState();
        if(storage_state.equals(Environment.MEDIA_MOUNTED)){
            try{
                File file = new File(Environment.getExternalStorageDirectory().getPath(), "helpcenter");
                if(!file.exists() && !file.mkdirs()){
                    Log.d("CHECK", "file creation issue");
                }else{

                    InputStream inputStream = null;
                    OutputStream outputStream = null;

                    try{
                        byte[] fileReader = new byte[4096];

                        inputStream = body.byteStream();
                        outputStream = new FileOutputStream(file.getPath() + File.separator + fileName);

                        while(true){
                            int read = inputStream.read(fileReader);

                            if(read == -1){
                                Log.d("CHECK", "finished reading");
                                break;
                            }

                            outputStream.write(fileReader, 0, read);
                        }

                        outputStream.flush();
                        return true;

                    }catch (IOException e){
                        e.printStackTrace();
                        Log.d("CHECK", "IO Exception 1");
                        return false;
                    }finally {
                        if(inputStream != null){
                            inputStream.close();
                        }
                        if(outputStream != null){
                            outputStream.close();
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
                Log.d("CHECK", "IO Exception 2");
                return false;
            }
        }

        Log.d("CHECK", "IT just returned false here");
        return false;
    }

    public boolean resourceExists(String passedFileName){
        //Check if the picture exists in the assign_assets directory
        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "helpcenter");
        try{
            dir.mkdirs();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            File[] listFiles = dir.listFiles();
            for (File listFile : listFiles) {
                if (listFile.isFile()) {
                    String fileName = listFile.getName();
                    if (fileName.equals(passedFileName)) {
                        return true;
                    }
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    public void updateSyncProgress(){
        ++currentNo;
        mtv_progress_text.setText(currentNo + "/" + downloadList.size() + " resources downloaded.");
        progressBar.setProgress(currentNo);

        if(currentNo >= downloadList.size()){
            btn_cancel_resource_sync.setText(R.string.helpcenter_close);
        }
    }

    public void updateSuccessCount(){
        ++success_count;
        mtv_success_text.setText("Success: " + success_count);
    }

    public void updateFailCount(){
        ++fail_count;
        mtv_fail_text.setText("Failed: " + fail_count);
    }

}
