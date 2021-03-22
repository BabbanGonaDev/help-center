package com.bgenterprise.helpcentermodule;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Network.HelpCenterSync;
import com.bgenterprise.helpcentermodule.Network.RetrofitApiCalls;
import com.bgenterprise.helpcentermodule.Network.RetrofitClient;
import com.bgenterprise.helpcentermodule.QuestionActivities.ViewActivityGroups;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomePage extends AppCompatActivity {
    CoordinatorLayout cl;
    MaterialButton btn_cancel_resource_sync;
    MaterialTextView mtv_success_text, mtv_fail_text, mtv_progress_text, mtv_app_version;
    ProgressBar progressBar, loading_progressbar;
    LinearLayoutCompat loading_layout;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;
    String session_app_lang;
    HelpCenterDatabase helpcenterdb;
    Boolean writtenToDisk;
    List<QuestionsEnglish> resourceList, downloadList;
    int currentNo, success_count, fail_count;
    private static final int PERMISSIONS_REQUEST_CODE = 4045;
    private static final String CHANNEL_ID = "HELP_CENTER";
    private static final int NOTIFICATION_ID = 32;
    private NotificationCompat.Builder builder;
    private NotificationManager mNotifyManager;
    private int CURRENT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_home_page);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_helpcenter_help_icon);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cl = findViewById(R.id.cl);
        CardView tgl_test_card = findViewById(R.id.tgl_test_card);
        CardView id_card = findViewById(R.id.id_card);
        CardView field_mapping_card = findViewById(R.id.field_mapping_card);
        CardView tfm_card = findViewById(R.id.tfm_card);
        btn_cancel_resource_sync = findViewById(R.id.btn_cancel_resource_sync);
        mtv_progress_text = findViewById(R.id.mtv_progress_text);
        mtv_success_text = findViewById(R.id.mtv_success_text);
        mtv_fail_text = findViewById(R.id.mtv_fail_text);
        mtv_app_version = findViewById(R.id.mtv_app_version);
        loading_layout = findViewById(R.id.loading_layout);
        progressBar = findViewById(R.id.progressBar);
        loading_progressbar = findViewById(R.id.loading_progress_bar);

        helpcenterdb = HelpCenterDatabase.getInstance(HomePage.this);
        sessionM = new HelpSessionManager(HomePage.this);
        help_details = sessionM.getHelpDetails();
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mtv_app_version.setText("\u00A9" + "Faiida Gesse Help Center v" + BuildConfig.VERSION_NAME);

        tgl_test_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("tgl_test");
            if(checkAndRequestPermissions()){
                startActivity(new Intent(HomePage.this, ViewActivityGroups.class));
            }
        });

        id_card.setOnClickListener(view -> {
            sessionM.SET_KEY_APP_ID("id");
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
        for(String perm : Utility.appPermissions){
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
                    Toast.makeText(HomePage.this, R.string.helpcenter_selected_language_toast + " " + Utility.app_language[i], Toast.LENGTH_LONG).show();

                    //Based on language, set the appropriate application language.
                    String selected_lang = Utility.app_language[i];
                    switch (selected_lang){
                        case "English":
                            sessionM.SET_LANGUAGE("en");
                            break;
                        case "Hausa":
                            sessionM.SET_LANGUAGE("ha");
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
        if(isConnected()) {
            Toast.makeText(HomePage.this, R.string.helpcenter_syncing_toast, Toast.LENGTH_LONG).show();
            startService(new Intent(this, HelpCenterSync.class));
        }else{
            Snackbar.make(cl, R.string.helpcenter_check_network, Snackbar.LENGTH_INDEFINITE)
                    .setAction("FIX", view -> HomePage.this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))).show();
        }
    }


    public void syncDownResources(){
        //Get the list of all the questions on the table.
        resourceList = new ArrayList<>();
        getResourceList();
    }

    public void getResourceList(){
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
                if (!resourceExists(x.getResource_id()) && !x.getResource_url().isEmpty()) {
                    //If the resource exists or the link isn't empty, add to the download list.
                    downloadList.add(x);
                }
            }

            //Now download the list.
            Toast.makeText(HomePage.this, R.string.helpcenter_resources_download_toast + " " + downloadList.size(), Toast.LENGTH_SHORT).show();
            if(!downloadList.isEmpty()){
                //If the download list isn't empty, then display progress bar.
                /*loading_layout.setVisibility(View.VISIBLE);
                progressBar.setMax(downloadList.size());*/
                setNotification();
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
                                //updateSyncProgress();
                                sendNotification(1);
                                updateSuccessCount();
                            }else{
                                //If false, log as fail and increase sync count.
                                //updateSyncProgress();
                                sendNotification(1);
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
                //updateSyncProgress();
                sendNotification(1);
                updateFailCount();
            }
        });
    }

    public boolean writeResponseBody(ResponseBody body, String fileName){
        //Changed it from 'public static' to just 'public' --> Just noting in case I meet any bugs.
        String storage_state = Environment.getExternalStorageState();
        if(storage_state.equals(Environment.MEDIA_MOUNTED)){
            try{
                File file = new File(Objects.requireNonNull(getExternalFilesDir(null)).getPath(), Utility.resource_location);

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
        File dir = new File(Objects.requireNonNull(getExternalFilesDir(null)).getPath(), Utility.resource_location);

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

    /*public void updateSyncProgress(){
        ++currentNo;
        mtv_progress_text.setText(currentNo + "/" + downloadList.size() + " resources downloaded.");
        progressBar.setProgress(currentNo);

        if(currentNo >= downloadList.size()){
            btn_cancel_resource_sync.setText(R.string.helpcenter_close);
        }
    }*/

    public void updateSuccessCount(){
        ++success_count;
        //mtv_success_text.setText("Success: " + success_count);
    }

    public void updateFailCount(){
        ++fail_count;
        //mtv_fail_text.setText("Failed: " + fail_count);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel help_channel = new NotificationChannel(
                    CHANNEL_ID, "Download Resources Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            if(mNotifyManager != null){
                mNotifyManager.createNotificationChannel(help_channel);
            }
        }
    }

    private void setNotification(){
        createNotificationChannel();
        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_helpcenter_help_icon)
                .setContentTitle("Downloading Resources")
                .setContentText("Downloading resources for help center content.")
                .setProgress(0,0,true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void sendNotification(int count){
        CURRENT += count;

        if(CURRENT == downloadList.size()){
            //Show result
            builder.setProgress(0,0,false)
                    .setContentText("Success: " + success_count + " " + "Failed: " + fail_count);
        }else{
            builder.setProgress(downloadList.size(), CURRENT, false);
        }

        mNotifyManager.notify(NOTIFICATION_ID, builder.build());
    }

}
