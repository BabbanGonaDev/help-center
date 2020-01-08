package com.bgenterprise.helpcentermodule.QuestionActivities;

import android.content.Intent;
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
import com.bgenterprise.helpcentermodule.HelpSessionManager;
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
    public List<QuestionsEnglish> issuesList, faqList;
    FAQAdapter faqAdapter;
    ActivityGroupAdapter activityGroupAdapter;
    HelpCenterDatabase helpCenterDb;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_activity_groups);
        issuesList = new ArrayList<>();
        faqList = new ArrayList<>();
        helpCenterDb = HelpCenterDatabase.getInstance(ViewActivityGroups.this);
        sessionM = new HelpSessionManager(ViewActivityGroups.this);
        help_details = sessionM.getHelpDetails();

        faq_recyclerView = findViewById(R.id.faq_recycler);
        group_recyclerView = findViewById(R.id.group_recycler);

        displayActivityGroup();
        displayFAQ();
    }

    public void displayFAQ(){
        //Get FAQs using App Executor.
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                faqList = helpCenterDb.getEnglishDao().getAllFAQQuestions(help_details.get(HelpSessionManager.KEY_APP_ID), help_details.get(HelpSessionManager.KEY_APP_LANG));
            }catch (Exception e){
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                //Initialize and populate the FAQ recycler view.
                faqAdapter = new FAQAdapter(ViewActivityGroups.this, faqList, questionsEnglish -> {
                    //Save just app_id & unique_question_id.
                    sessionM.SET_KEY_APP_ID(questionsEnglish.getApp_id());
                    sessionM.SET_UNIQUE_QUESTION_ID(questionsEnglish.getUnique_question_id());
                    startActivity(new Intent(ViewActivityGroups.this, ViewIssueAndAnswer.class));
                });

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                faq_recyclerView.setLayoutManager(mLayoutManager);
                faq_recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                faq_recyclerView.setAdapter(faqAdapter);
                faqAdapter.notifyDataSetChanged();
            });
        });
    }

    public void displayActivityGroup(){
        //Get Activity groups using App Executor.
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                issuesList = helpCenterDb.getEnglishDao().getActivityGroups(help_details.get(HelpSessionManager.KEY_APP_ID), help_details.get(HelpSessionManager.KEY_APP_LANG));
            }catch (Exception e){
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                if(issuesList == null || issuesList.isEmpty()){
                    Log.d("CHECK:", "List is very empty");
                    finish();
                    startActivity(new Intent(ViewActivityGroups.this, QuestionNotFound.class));
                }

                //Initialize and populate the Group activities recycler view.
                activityGroupAdapter = new ActivityGroupAdapter(ViewActivityGroups.this, issuesList, questionsAll -> {
                    //TODO save just app_id & activity_group_id
                    sessionM.SET_ACTIVITY_GROUP_ID(questionsAll.getActivity_group_id());
                    startActivity(new Intent(ViewActivityGroups.this, ViewActivities.class));
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
}
