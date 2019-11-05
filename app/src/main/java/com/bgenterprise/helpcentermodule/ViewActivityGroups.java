package com.bgenterprise.helpcentermodule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.RecyclerAdapters.ActivityGroupAdapter;
import com.bgenterprise.helpcentermodule.RecyclerAdapters.FAQAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewActivityGroups extends AppCompatActivity {
    RecyclerView faq_recyclerView;
    RecyclerView group_recyclerView;
    String app_id;
    public List<QuestionsEnglish> issuesList, faqList;
    FAQAdapter faqAdapter;
    ActivityGroupAdapter activityGroupAdapter;
    HelpCenterDatabase helpCenterDb;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialization of all local variables.
        setContentView(R.layout.activity_help_view_activity_groups);
        issuesList = new ArrayList<>();
        faqList = new ArrayList<>();
        helpCenterDb = HelpCenterDatabase.getInstance(ViewActivityGroups.this);
        sessionM = new HelpSessionManager(ViewActivityGroups.this);
        help_details = sessionM.getHelpDetails();

        faq_recyclerView = findViewById(R.id.faq_recycler);
        group_recyclerView = findViewById(R.id.group_recycler);

        //Get Recycler Lists.
        @SuppressLint("StaticFieldLeak")
        getActivityGroups getActivityGroupIssues = new getActivityGroups(ViewActivityGroups.this){
            @Override
            protected void onPostExecute(List<QuestionsEnglish> issuesEnglishes) {
                super.onPostExecute(issuesEnglishes);
                issuesList = issuesEnglishes;
                initGroupAdapter();
                if(issuesList == null || issuesList.isEmpty()){
                    Log.d("CHECK:", "List is very empty");
                    startActivity(new Intent(ViewActivityGroups.this, QuestionNotFound.class));
                }else if(issuesList != null && !issuesList.isEmpty()){
                    Log.d("CHECK:", issuesEnglishes.get(0).getActivity_group_name());
                }
            }
        };
        getActivityGroupIssues.execute(help_details.get(HelpSessionManager.KEY_APP_ID));

        @SuppressLint("StaticFieldLeak")
        getFAQuestions getFaq = new getFAQuestions(ViewActivityGroups.this){
            @Override
            protected void onPostExecute(List<QuestionsEnglish> FAQIssues) {
                super.onPostExecute(FAQIssues);
                faqList = FAQIssues;
                initFAQRecycler();
            }
        };
        getFaq.execute(help_details.get(HelpSessionManager.KEY_APP_ID));
    }

    @SuppressLint("StaticFieldLeak")
    public class getActivityGroups extends AsyncTask<String, Void, List<QuestionsEnglish>>{
        Context context;
        List<QuestionsEnglish> englishIssues = new ArrayList<>();

        public getActivityGroups(Context context) {
            this.context = context;
        }

        @Override
        protected List<QuestionsEnglish> doInBackground(String... AppID) {
            try{
                Log.d("CHECK", "App id: " + AppID[0]);
                englishIssues = helpCenterDb.getEnglishDao().getActivityGroups(AppID[0]);
                return englishIssues;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class getFAQuestions extends AsyncTask<String, Void, List<QuestionsEnglish>>{
        Context context;
        List<QuestionsEnglish> faqIssues = new ArrayList<>();

        public getFAQuestions(Context context) {
            this.context = context;
        }

        @Override
        protected List<QuestionsEnglish> doInBackground(String... strings) {
            try{
                Log.d("CHECK", "Loading FAQ");
                faqIssues = helpCenterDb.getEnglishDao().getAllFAQQuestions(strings[0]);

                return faqIssues;
            }catch (Exception e){
                e.printStackTrace();
                return null;

            }
        }
    }

    public void initFAQRecycler(){
        //Initialize and populate the FAQ recycler view.
        faqAdapter = new FAQAdapter(ViewActivityGroups.this, faqList, issuesList -> {
            //TODO Save just app_id & unique_question_id.
            sessionM.SET_KEY_APP_ID(issuesList.getApp_id());
            sessionM.SET_UNIQUE_QUESTION_ID(issuesList.getUnique_question_id());
            startActivity(new Intent(ViewActivityGroups.this, ViewIssueAndAnswer.class));
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        faq_recyclerView.setLayoutManager(mLayoutManager);
        faq_recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        faq_recyclerView.setAdapter(faqAdapter);
        faqAdapter.notifyDataSetChanged();
    }

    public void initGroupAdapter(){
        //Initialize and populate the Group activities recycler view.
        activityGroupAdapter = new ActivityGroupAdapter(ViewActivityGroups.this, issuesList, issuesList -> {
            //TODO save just app_id & activity_group_id
            sessionM.SET_KEY_APP_ID(issuesList.getApp_id());
            sessionM.SET_ACTIVITY_GROUP_ID(issuesList.getActivity_group_id());
            startActivity(new Intent(ViewActivityGroups.this, ViewActivities.class));
        });

        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        group_recyclerView.setLayoutManager(nLayoutManager);
        group_recyclerView.setItemAnimator(new DefaultItemAnimator());
        group_recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        group_recyclerView.setAdapter(activityGroupAdapter);
        activityGroupAdapter.notifyDataSetChanged();
    }

}
