package com.bgenterprise.helpcentermodule.QuestionActivities;

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

import com.bgenterprise.helpcentermodule.AppExecutors;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;
import com.bgenterprise.helpcentermodule.HelpSessionManager;
import com.bgenterprise.helpcentermodule.QuestionsAll;
import com.bgenterprise.helpcentermodule.R;
import com.bgenterprise.helpcentermodule.RecyclerAdapters.ActivityGroupAdapter;
import com.bgenterprise.helpcentermodule.RecyclerAdapters.FAQAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewActivityGroups extends AppCompatActivity {
    RecyclerView faq_recyclerView;
    RecyclerView group_recyclerView;
    String app_id;
    public List<QuestionsEnglish> issuesList_en, faqList_en;
    public List<QuestionsHausa> issuesList_ha, faqList_ha;
    public List<QuestionsAll> issuesList_all, faqList_all;
    FAQAdapter faqAdapter;
    ActivityGroupAdapter activityGroupAdapter;
    HelpCenterDatabase helpCenterDb;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_activity_groups);
        issuesList_en = new ArrayList<>();
        issuesList_ha = new ArrayList<>();
        issuesList_all = new ArrayList<>();
        faqList_en = new ArrayList<>();
        faqList_ha = new ArrayList<>();
        faqList_all = new ArrayList<>();
        helpCenterDb = HelpCenterDatabase.getInstance(ViewActivityGroups.this);
        sessionM = new HelpSessionManager(ViewActivityGroups.this);
        help_details = sessionM.getHelpDetails();

        faq_recyclerView = findViewById(R.id.faq_recycler);
        group_recyclerView = findViewById(R.id.group_recycler);

        switch (sessionM.getAppLanguage()){
            case "en":
                displayActivityGroupEnglish();
                displayFAQEnglish();
                break;
            case "ha":
                displayActivityGroupHausa();
                displayFAQHausa();
                break;
        }

    }

    public void displayFAQEnglish(){
        //Get FAQs using App Executor.
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                faqList_en = helpCenterDb.getEnglishDao().getAllFAQQuestions(help_details.get(HelpSessionManager.KEY_APP_ID));
            }catch (Exception e){
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                //Initialize and populate the FAQ recycler view.
                faqList_all = convertEnglishToAll(faqList_en);
                faqAdapter = new FAQAdapter(ViewActivityGroups.this, faqList_all, questionsAll -> {
                    //Save just app_id & unique_question_id.
                    sessionM.SET_KEY_APP_ID(questionsAll.getApp_id());
                    sessionM.SET_UNIQUE_QUESTION_ID(questionsAll.getUnique_question_id());
                    ViewActivityGroups.this.startActivity(new Intent(ViewActivityGroups.this, ViewIssueAndAnswer.class));
                });

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                faq_recyclerView.setLayoutManager(mLayoutManager);
                faq_recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                faq_recyclerView.setAdapter(faqAdapter);
                faqAdapter.notifyDataSetChanged();
            });
        });
    }

    public void displayFAQHausa(){
        //Get FAQs using App Executor.
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                faqList_ha = helpCenterDb.getHausaDao().getAllFAQQuestions(help_details.get(HelpSessionManager.KEY_APP_ID));
            }catch (Exception e){
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                //Initialize and populate the FAQ recycler view.
                faqList_all = convertHausaToAll(faqList_ha);
                faqAdapter = new FAQAdapter(ViewActivityGroups.this, faqList_all, questionsAll -> {
                    //Save just app_id & unique_question_id.
                    sessionM.SET_KEY_APP_ID(questionsAll.getApp_id());
                    sessionM.SET_UNIQUE_QUESTION_ID(questionsAll.getUnique_question_id());
                    ViewActivityGroups.this.startActivity(new Intent(ViewActivityGroups.this, ViewIssueAndAnswer.class));
                });

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                faq_recyclerView.setLayoutManager(mLayoutManager);
                faq_recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                faq_recyclerView.setAdapter(faqAdapter);
                faqAdapter.notifyDataSetChanged();
            });
        });
    }

    public void displayActivityGroupEnglish(){
        //Get Activity groups using App Executor.
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                issuesList_en = helpCenterDb.getEnglishDao().getActivityGroups(help_details.get(HelpSessionManager.KEY_APP_ID));
            }catch (Exception e){
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                issuesList_all = convertEnglishToAll(issuesList_en);
                if(issuesList_all == null || issuesList_all.isEmpty()){
                    Log.d("CHECK:", "List is very empty");
                    finish();
                    startActivity(new Intent(ViewActivityGroups.this, QuestionNotFound.class));
                }

                //Initialize and populate the Group activities recycler view.
                activityGroupAdapter = new ActivityGroupAdapter(ViewActivityGroups.this, issuesList_all, questionsAll -> {
                    //TODO save just app_id & activity_group_id
                    sessionM.SET_KEY_APP_ID(questionsAll.getApp_id());
                    sessionM.SET_ACTIVITY_GROUP_ID(questionsAll.getActivity_group_id());
                    ViewActivityGroups.this.startActivity(new Intent(ViewActivityGroups.this, ViewActivities.class));
                });

                RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                group_recyclerView.setLayoutManager(nLayoutManager);
                group_recyclerView.setItemAnimator(new DefaultItemAnimator());
                group_recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                group_recyclerView.setAdapter(activityGroupAdapter);
                activityGroupAdapter.notifyDataSetChanged();
            });
        });
    }

    public void displayActivityGroupHausa(){
        //Get Activity groups using App Executor.
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                issuesList_ha = helpCenterDb.getHausaDao().getActivityGroups(help_details.get(HelpSessionManager.KEY_APP_ID));
            }catch (Exception e){
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                issuesList_all = convertHausaToAll(issuesList_ha);
                if(issuesList_all == null || issuesList_all.isEmpty()){
                    Log.d("CHECK:", "List is very empty");
                    finish();
                    startActivity(new Intent(ViewActivityGroups.this, QuestionNotFound.class));
                }

                //Initialize and populate the Group activities recycler view.
                activityGroupAdapter = new ActivityGroupAdapter(ViewActivityGroups.this, issuesList_all, questionsAll -> {
                    //TODO save just app_id & activity_group_id
                    sessionM.SET_KEY_APP_ID(questionsAll.getApp_id());
                    sessionM.SET_ACTIVITY_GROUP_ID(questionsAll.getActivity_group_id());
                    ViewActivityGroups.this.startActivity(new Intent(ViewActivityGroups.this, ViewActivities.class));
                });

                RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                group_recyclerView.setLayoutManager(nLayoutManager);
                group_recyclerView.setItemAnimator(new DefaultItemAnimator());
                group_recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                group_recyclerView.setAdapter(activityGroupAdapter);
                activityGroupAdapter.notifyDataSetChanged();
            });
        });
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
