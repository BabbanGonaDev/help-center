package com.bgenterprise.helpcentermodule;

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

import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.RecyclerAdapters.ActivityIssuesAdapter;

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
    public String WhatsappMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_activity_issues);
        IssuesList = new ArrayList<>();
        sessionM = new HelpSessionManager(ViewActivityIssues.this);
        helpCenterDb = HelpCenterDatabase.getInstance(ViewActivityIssues.this);
        help_details = sessionM.getHelpDetails();

        myDialog = new Dialog(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        recyclerView2 = findViewById(R.id.recyclerView2);
        contactUs = findViewById(R.id.contactUs);

        @SuppressLint("StaticFieldLeak") getIssues get_issues = new getIssues(ViewActivityIssues.this) {
            @Override
            protected void onPostExecute(List<QuestionsEnglish> issuesEnglishes) {
                super.onPostExecute(issuesEnglishes);
                IssuesList = issuesEnglishes;
                adapter = new ActivityIssuesAdapter(ViewActivityIssues.this, IssuesList, new ActivityIssuesAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(QuestionsEnglish issuesEnglish) {
                        sessionM.SET_UNIQUE_QUESTION_ID(issuesEnglish.getUnique_question_id());
                        sessionM.SET_ACTIVITY_ISSUE(issuesEnglish.getIssue_question());
                        startActivity(new Intent(ViewActivityIssues.this, ViewIssueAndAnswer.class));
                    }
                });

                RecyclerView.LayoutManager vLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView2.setLayoutManager(vLayoutManager);
                recyclerView2.setItemAnimator(new DefaultItemAnimator());
                recyclerView2.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                recyclerView2.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        get_issues.execute(help_details.get(HelpSessionManager.KEY_ACTIVITY_ID));
        progressDialog.dismiss();

        contactUs = findViewById(R.id.contactUs);
        contactUs.setOnClickListener(view -> {
            onButtonShowPopupWindowClick(view);
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

        whatsappCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendWhatsappMessage();
            }
        });
        phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall();
            }
        });

    }

    public void sendWhatsappMessage() {
        String versionName = BuildConfig.VERSION_NAME;
        WhatsappMessage = "APP ID: "+"\n" + help_details.get(HelpSessionManager.KEY_APP_ID) + "\n" +
                "ACTIVITY ISSUE: "+"\n" + help_details.get(HelpSessionManager.KEY_ACTIVITY_ISSUE) + "\n" +
                help_details.get(HelpSessionManager.KEY_USERNAME)+ "\n" +
                help_details.get(HelpSessionManager.KEY_LAST_SYNC_DATE)+ "\n" +
                "APP VERSION: " + versionName;
        if(!appInstalledOrNot("com.whatsapp")){
            Toast.makeText(this, "Install whatsapp", Toast.LENGTH_SHORT).show();
        }else {
            String toNumber = "+2349095657536"; // contains spaces.
            toNumber = toNumber.replace("+", "").replace(" ", "");
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            // sendIntent.setComponent(new ComponentName(“com.whatsapp”, “com.whatsapp.Conversation”));
            sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
            sendIntent.putExtra(Intent.EXTRA_TEXT, WhatsappMessage);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void makeCall() {
            if (ContextCompat.checkSelfPermission((this),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:+2349095657536"));
                startActivity(intent);
            }
    }

    @SuppressLint("StaticFieldLeak")
    public class getIssues extends AsyncTask<String, Void, List<QuestionsEnglish>> {
        Context myContext;
        List<QuestionsEnglish> issues = new ArrayList<>();

        public getIssues(Context context) {
            this.myContext = context;
        }

        @Override
        protected List<QuestionsEnglish> doInBackground(String... strings) {
            try {
                issues = helpCenterDb.getEnglishDao().getActivityQuestions(strings[0]);
                return issues;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
}
