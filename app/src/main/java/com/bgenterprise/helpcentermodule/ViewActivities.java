package com.bgenterprise.helpcentermodule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.RecyclerAdapters.ActivityAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewActivities extends AppCompatActivity {

    RecyclerView view_activities_rv;
    public List<QuestionsEnglish> questionsList;
    ActivityAdapter adapter;
    HelpCenterDatabase helpCenterDb;
    ProgressDialog progressDialog;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_activities);
        questionsList = new ArrayList<>();
        sessionM = new HelpSessionManager(ViewActivities.this);
        helpCenterDb = HelpCenterDatabase.getInstance(ViewActivities.this);
        help_details = sessionM.getHelpDetails();

        view_activities_rv = findViewById(R.id.view_activities_rv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        @SuppressLint("StaticFieldLeak")
        getActivities get_activities = new getActivities(ViewActivities.this){
            @Override
            protected void onPostExecute(List<QuestionsEnglish> issuesEnglishes) {
                super.onPostExecute(issuesEnglishes);
                questionsList = issuesEnglishes;
                adapter = new ActivityAdapter(ViewActivities.this, questionsList, issuesEnglish -> {
                    sessionM.SET_ACTIVITY_ID(issuesEnglish.getActivity_id());
                    sessionM.SET_ACTIVITY_ISSUE(issuesEnglish.getIssue_question());
                    sessionM.SET_KEY_APP_ID(issuesEnglish.getApp_id());
                    startActivity(new Intent(ViewActivities.this, ViewActivityIssues.class));
                });

                RecyclerView.LayoutManager vLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                view_activities_rv.setLayoutManager(vLayoutManager);
                view_activities_rv.setItemAnimator(new DefaultItemAnimator());
                view_activities_rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                view_activities_rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };get_activities.execute(help_details.get(HelpSessionManager.KEY_ACTIVITY_GROUP_ID));
        progressDialog.dismiss();
    }

    @SuppressLint("StaticFieldLeak")
    public class getActivities extends AsyncTask<String, Void, List<QuestionsEnglish>>{
        Context mCtx;
        List<QuestionsEnglish> activity_issues = new ArrayList<>();

        public getActivities(Context context) {
            this.mCtx = context;
        }

        @Override
        protected List<QuestionsEnglish> doInBackground(String... strings) {
            try{
                activity_issues = helpCenterDb.getEnglishDao().getActivities(strings[0]);
                return activity_issues;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }


}
