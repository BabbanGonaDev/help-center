package com.bgenterprise.helpcentermodule.QuestionActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bgenterprise.helpcentermodule.AppExecutors;
import com.bgenterprise.helpcentermodule.BuildConfig;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.HelpSessionManager;
import com.bgenterprise.helpcentermodule.HomePage;
import com.bgenterprise.helpcentermodule.PopulateDB;
import com.bgenterprise.helpcentermodule.R;
import com.bgenterprise.helpcentermodule.RecyclerAdapters.ActivityIssuesAdapter;

import com.bgenterprise.helpcentermodule.SplashScreen;
import com.bgenterprise.helpcentermodule.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewActivityIssues extends AppCompatActivity {
    private static final int REQUEST_CALL = 1;
    RecyclerView recyclerView2;
    Button contactUs;
    public List<QuestionsEnglish> IssuesList;
    ActivityIssuesAdapter adapter;
    HelpCenterDatabase helpCenterDb;
    ProgressDialog progressDialog;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;
    Dialog myDialog;
    public String whatsapp_message;
    String passed_activity_id, passed_app_id, passed_staff_id;
    private static final int PERMISSIONS_REQUEST_CODE = 2048;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_activity_issues);
        IssuesList = new ArrayList<>();
        sessionM = new HelpSessionManager(ViewActivityIssues.this);
        helpCenterDb = HelpCenterDatabase.getInstance(ViewActivityIssues.this);
        help_details = sessionM.getHelpDetails();

        recyclerView2 = findViewById(R.id.recyclerView2);
        contactUs = findViewById(R.id.contactUs);
        myDialog = new Dialog(this);

        //TODO--- Also request all app permissions here including phone call.
        //Receive intent call from external apps.
        try{
            passed_activity_id = getIntent().getStringExtra("activity_id");
            passed_app_id = getIntent().getStringExtra("app_id");
            passed_staff_id = getIntent().getStringExtra("staff_id");

            sessionM.SET_ACTIVITY_ID(passed_activity_id);
            sessionM.SET_KEY_APP_ID(passed_app_id);
            sessionM.SET_STAFF_ID(passed_staff_id);
        }catch(Exception e){
            e.printStackTrace();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        contactUs.setOnClickListener(view -> {
            onButtonShowPopupWindowClick(view);
        });

        //Get the Activity Issues using AppExecutors.
        AppExecutors.getInstance().diskIO().execute(() -> {
            IssuesList = helpCenterDb.getEnglishDao().getActivityQuestions(help_details.get(HelpSessionManager.KEY_ACTIVITY_ID));

            runOnUiThread(() -> {
                //TODO save unique_question_id.
                adapter = new ActivityIssuesAdapter(ViewActivityIssues.this, IssuesList, issuesEnglish -> {
                    sessionM.SET_UNIQUE_QUESTION_ID(issuesEnglish.getUnique_question_id());
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
        phoneCall.setOnClickListener(view12 -> makeCall());

    }

    public void sendWhatsappMessage() {
        whatsapp_message = "Application ID: " + help_details.get(HelpSessionManager.KEY_APP_ID) + "\n" +
                "Activity Issue: " + help_details.get(HelpSessionManager.KEY_ACTIVITY_ISSUE) + "\n" +
                "Staff ID: " + help_details.get(HelpSessionManager.KEY_STAFF_ID)+ "\n" +
                "Version: " + BuildConfig.VERSION_NAME;

        if(!isAppInstalled("com.whatsapp")){
            Toast.makeText(this, "Kindly install WhatsApp", Toast.LENGTH_SHORT).show();
        }else {

            //TODO ----> Add the part to pick phone number from DB.
            String toNumber = "+2349095657536"; // contains spaces.
            toNumber = toNumber.replace("+", "").replace(" ", "");

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
            sendIntent.putExtra(Intent.EXTRA_TEXT, whatsapp_message);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

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

    public void makeCall() {
        if (checkAndRequestPermissions()){
            //TODO---> Add phone number function here.
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+2349095657536"));
            startActivity(intent);
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
}
