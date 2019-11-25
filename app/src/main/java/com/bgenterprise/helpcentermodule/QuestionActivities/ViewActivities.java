package com.bgenterprise.helpcentermodule.QuestionActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bgenterprise.helpcentermodule.AppExecutors;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;
import com.bgenterprise.helpcentermodule.HelpSessionManager;
import com.bgenterprise.helpcentermodule.QuestionsAll;
import com.bgenterprise.helpcentermodule.R;
import com.bgenterprise.helpcentermodule.RecyclerAdapters.ActivityAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewActivities extends AppCompatActivity {

    RecyclerView view_activities_rv;
    public List<QuestionsEnglish> questionsList_en;
    public List<QuestionsHausa> questionsList_ha;
    public List<QuestionsAll> questionsList_all;
    ActivityAdapter adapter;
    HelpCenterDatabase helpCenterDb;
    ProgressDialog progressDialog;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_activities);
        sessionM = new HelpSessionManager(ViewActivities.this);
        getSupportActionBar().setTitle("View Activities");
        questionsList_en = new ArrayList<>();
        questionsList_ha = new ArrayList<>();
        questionsList_all = new ArrayList<>();
        helpCenterDb = HelpCenterDatabase.getInstance(ViewActivities.this);
        help_details = sessionM.getHelpDetails();

        view_activities_rv = findViewById(R.id.view_activities_rv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        switch (sessionM.getAppLanguage()){
            case "en":
                displayActivitiesEnglish();
                break;
            case "ha":
                displayActivitiesHausa();
                break;
        }

    }

    public void displayActivitiesEnglish(){
        //Get Activities using App Executor
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                questionsList_en = helpCenterDb.getEnglishDao().getActivities(help_details.get(HelpSessionManager.KEY_ACTIVITY_GROUP_ID));
            }catch (Exception e){
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                questionsList_all = convertEnglishToAll(questionsList_en);
                adapter = new ActivityAdapter(ViewActivities.this, questionsList_all, questionsAll -> {
                    //Save only activity_id.
                    sessionM.SET_ACTIVITY_ID(questionsAll.getActivity_id());
                    startActivity(new Intent(ViewActivities.this, ViewActivityIssues.class));
                });

                RecyclerView.LayoutManager vLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                view_activities_rv.setLayoutManager(vLayoutManager);
                view_activities_rv.setItemAnimator(new DefaultItemAnimator());
                view_activities_rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                view_activities_rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            });
        });
    }

    public void displayActivitiesHausa(){
        //Get Activities using App Executor
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                questionsList_ha = helpCenterDb.getHausaDao().getActivities(help_details.get(HelpSessionManager.KEY_ACTIVITY_GROUP_ID));
            }catch (Exception e){
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                questionsList_all = convertHausaToAll(questionsList_ha);
                adapter = new ActivityAdapter(ViewActivities.this, questionsList_all, questionsAll -> {
                    //Save only activity_id.
                    sessionM.SET_ACTIVITY_ID(questionsAll.getActivity_id());
                    startActivity(new Intent(ViewActivities.this, ViewActivityIssues.class));
                });

                RecyclerView.LayoutManager vLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                view_activities_rv.setLayoutManager(vLayoutManager);
                view_activities_rv.setItemAnimator(new DefaultItemAnimator());
                view_activities_rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                view_activities_rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
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
