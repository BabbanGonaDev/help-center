package com.bgenterprise.helpcentermodule.QuestionActivities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bgenterprise.helpcentermodule.AppExecutors;
import com.bgenterprise.helpcentermodule.BuildConfig;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;
import com.bgenterprise.helpcentermodule.HelpSessionManager;
import com.bgenterprise.helpcentermodule.QuestionsAll;
import com.bgenterprise.helpcentermodule.R;
import com.bgenterprise.helpcentermodule.RecyclerAdapters.ActivityIssuesAdapter;
import com.bgenterprise.helpcentermodule.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViewActivityIssues extends AppCompatActivity {
    RecyclerView recyclerView2;
    Button contactUs;
    MaterialTextView mtv_app_version;
    public List<QuestionsEnglish> questionsList_en;
    public List<QuestionsHausa> questionsList_ha;
    public List<QuestionsAll> questionsList_all;
    ActivityIssuesAdapter adapter;
    HelpCenterDatabase helpCenterDb;
    ProgressDialog progressDialog;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;
    Dialog myDialog;
    String passed_activity_id, passed_app_id, passed_staff_id, passed_user_location, whatsapp_message, contact_no, whatsapp_no, session_app_lang;
    private static final int PERMISSIONS_REQUEST_CODE = 2048;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_activity_issues);
        getSupportActionBar().setTitle("View Activity Issues");
        questionsList_en = new ArrayList<>();
        questionsList_ha = new ArrayList<>();
        questionsList_all = new ArrayList<>();
        helpCenterDb = HelpCenterDatabase.getInstance(ViewActivityIssues.this);
        sessionM = new HelpSessionManager(ViewActivityIssues.this);

        recyclerView2 = findViewById(R.id.recyclerView2);
        contactUs = findViewById(R.id.contactUs);
        myDialog = new Dialog(this);
        mtv_app_version = findViewById(R.id.mtv_app_version);

        //Receive intent call from external apps.
        try{
            passed_activity_id = getIntent().getStringExtra("activity_id");
            passed_app_id = getIntent().getStringExtra("app_id");
            passed_staff_id = getIntent().getStringExtra("staff_id");
            passed_user_location = getIntent().getStringExtra("user_location"); //Get this from Access Control.

            if(!passed_activity_id.isEmpty() && !passed_app_id.isEmpty() && !passed_staff_id.isEmpty()){
                sessionM.SET_ACTIVITY_ID(passed_activity_id);
                sessionM.SET_KEY_APP_ID(passed_app_id);
                sessionM.SET_STAFF_ID(passed_staff_id);
                sessionM.SET_USER_LOCATION(passed_user_location);
            }

            checkAndRequestPermissions();
        }catch(Exception e){
            e.printStackTrace();
        }

        //Get the values from the shared prefs now.
        help_details = sessionM.getHelpDetails();

        mtv_app_version.setText("\u00A9" + "BG Enterprise Systems v" + BuildConfig.VERSION_NAME);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        switch (sessionM.getAppLanguage()){
            case "en":
                displayActivityIssuesEnglish();
                break;
            case "ha":
                displayActivityIssuesHausa();
                break;
        }

        contactUs.setOnClickListener(view -> {
            onButtonShowPopupWindowClick(view);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_issues_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.change_language){
            changeAppLanguage();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void changeAppLanguage(){
        new MaterialAlertDialogBuilder(ViewActivityIssues.this)
                .setTitle("Choose App Language")
                .setSingleChoiceItems(Utility.app_language, -1, (dialogInterface, i) -> {
                    Toast.makeText(ViewActivityIssues.this, "Language selected: " + Utility.app_language[i], Toast.LENGTH_LONG).show();

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

    public void displayActivityIssuesEnglish(){
        //Get the Activity Issues using AppExecutors.
        AppExecutors.getInstance().diskIO().execute(() -> {
            questionsList_en = helpCenterDb.getEnglishDao().getActivityQuestions(help_details.get(HelpSessionManager.KEY_ACTIVITY_ID));

            if(questionsList_en == null || questionsList_en.isEmpty()){
                Log.d("CHECK:", "List is very empty");
                finish();
                startActivity(new Intent(ViewActivityIssues.this, QuestionNotFound.class));
            }

            runOnUiThread(() -> {
                questionsList_all = convertEnglishToAll(questionsList_en);
                adapter = new ActivityIssuesAdapter(ViewActivityIssues.this, questionsList_all, questionsAll -> {
                    //Save only unique_question_id.
                    sessionM.SET_UNIQUE_QUESTION_ID(questionsAll.getUnique_question_id());
                    startActivity(new Intent(ViewActivityIssues.this, ViewIssueAndAnswer.class));
                });

                RecyclerView.LayoutManager vLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView2.setLayoutManager(vLayoutManager);
                recyclerView2.setItemAnimator(new DefaultItemAnimator());
                recyclerView2.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                recyclerView2.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            });
        });
    }

    public void displayActivityIssuesHausa(){
        //Get the Activity Issues using AppExecutors.
        AppExecutors.getInstance().diskIO().execute(() -> {
            questionsList_ha = helpCenterDb.getHausaDao().getActivityQuestions(help_details.get(HelpSessionManager.KEY_ACTIVITY_ID));

            if(questionsList_ha == null || questionsList_ha.isEmpty()){
                Log.d("CHECK:", "List is very empty");
                finish();
                startActivity(new Intent(ViewActivityIssues.this, QuestionNotFound.class));
            }

            runOnUiThread(() -> {
                questionsList_all = convertHausaToAll(questionsList_ha);
                adapter = new ActivityIssuesAdapter(ViewActivityIssues.this, questionsList_all, questionsAll -> {
                    //Save only unique_question_id.
                    sessionM.SET_UNIQUE_QUESTION_ID(questionsAll.getUnique_question_id());
                    startActivity(new Intent(ViewActivityIssues.this, ViewIssueAndAnswer.class));
                });

                RecyclerView.LayoutManager vLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView2.setLayoutManager(vLayoutManager);
                recyclerView2.setItemAnimator(new DefaultItemAnimator());
                recyclerView2.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                recyclerView2.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            });
        });
    }

    public void onButtonShowPopupWindowClick(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_contactus_options, null);
        MaterialAlertDialogBuilder builder =  new MaterialAlertDialogBuilder(this);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button whatsappCall  = popupView.findViewById(R.id.whatsappcall);
        Button phoneCall = popupView.findViewById(R.id.phoneCall);

        whatsappCall.setOnClickListener(view1 -> sendWhatsappMessage());
        phoneCall.setOnClickListener(view12 -> makePhoneCall());

    }

    public void sendWhatsappMessage() {
        whatsapp_message = "Application ID: " + help_details.get(HelpSessionManager.KEY_APP_ID) + "\n" +
                "Activity ID: " + help_details.get(HelpSessionManager.KEY_ACTIVITY_ID) + "\n" +
                "Staff ID: " + help_details.get(HelpSessionManager.KEY_STAFF_ID)+ "\n" +
                "Version: " + BuildConfig.VERSION_NAME;

        if(!isAppInstalled("com.whatsapp")){
            Toast.makeText(this, "Kindly install WhatsApp", Toast.LENGTH_SHORT).show();
        }else {

            AppExecutors.getInstance().diskIO().execute(() -> {
                whatsapp_no = helpCenterDb.getContactDao().getWhatsappNumber(help_details.get(HelpSessionManager.KEY_USER_LOCATION));
                runOnUiThread(() -> {
                    if(whatsapp_no.isEmpty()){
                        Toast.makeText(this, "Whatsapp Contact not found", Toast.LENGTH_LONG).show();
                    }

                    whatsapp_no = whatsapp_no.replace("+", "").replace(" ", "");
                    try {
                        Intent urlMsg = new Intent(Intent.ACTION_VIEW);
                        urlMsg.setPackage("com.whatsapp");
                        urlMsg.setData(Uri.parse("https://wa.me/" + whatsapp_no + "?text=" + URLEncoder.encode(whatsapp_message, "UTF-8")));
                        startActivity(urlMsg);
                    } catch (UnsupportedEncodingException e) {
                        Toast.makeText(this, "Unable to send whatsapp message", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    public void makePhoneCall() {
        if (checkAndRequestPermissions()){
            AppExecutors.getInstance().diskIO().execute(() -> {
                contact_no = helpCenterDb.getContactDao().getPhoneNumber(help_details.get(HelpSessionManager.KEY_USER_LOCATION));
                runOnUiThread(() -> {
                    contact_no = contact_no.replace(" ", "");
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact_no)));
                });
            });
        }
    }

    private boolean isAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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
            ActivityCompat.requestPermissions(ViewActivityIssues.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PERMISSIONS_REQUEST_CODE);
            return false;
        }

        //All permissions granted.
        return true;
    }

    public List<QuestionsAll> convertHausaToAll(List<QuestionsHausa> qHausa){
        List<QuestionsAll> y = new ArrayList<>();
        for(QuestionsHausa x: qHausa){
            y.add(new QuestionsAll(x.getUnique_question_id(),
                    x.getApp_id(),
                    x.getActivity_group_id(),
                    x.getActivity_id(),
                    x.getActivity_name(),
                    x.getResource_id(),
                    x.getResource_url(),
                    x.getIssue_question(),
                    x.getIssue_answer(),
                    x.getPositive_feedback_count(),
                    x.getNegative_feedback_count(),
                    x.getFaq_status()));
        }

        return y;
    }

    public List<QuestionsAll> convertEnglishToAll(List<QuestionsEnglish> qEnglish){
        List<QuestionsAll> y = new ArrayList<>();
        for(QuestionsEnglish x: qEnglish){
            y.add(new QuestionsAll(x.getUnique_question_id(),
                    x.getApp_id(),
                    x.getActivity_group_id(),
                    x.getActivity_id(),
                    x.getActivity_name(),
                    x.getResource_id(),
                    x.getResource_url(),
                    x.getIssue_question(),
                    x.getIssue_answer(),
                    x.getPositive_feedback_count(),
                    x.getNegative_feedback_count(),
                    x.getFaq_status()));
        }

        return y;
    }
}
