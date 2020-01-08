package com.bgenterprise.helpcentermodule.QuestionActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

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
import com.bgenterprise.helpcentermodule.RecyclerAdapters.ActivityAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewActivities extends AppCompatActivity {

    RecyclerView view_activities_rv;
    public List<QuestionsEnglish> questionsList_en;
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
        helpCenterDb = HelpCenterDatabase.getInstance(ViewActivities.this);
        help_details = sessionM.getHelpDetails();

        view_activities_rv = findViewById(R.id.view_activities_rv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        displayActivitiesEnglish();
    }

    public void displayActivitiesEnglish(){
        //Get Activities using App Executor
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                questionsList_en = helpCenterDb.getEnglishDao().getActivities(help_details.get(HelpSessionManager.KEY_ACTIVITY_GROUP_ID), help_details.get(HelpSessionManager.KEY_APP_LANG));
            }catch (Exception e){
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                adapter = new ActivityAdapter(ViewActivities.this, questionsList_en, questionsEnglish -> {
                    //Save only activity_id.
                    sessionM.SET_ACTIVITY_ID(questionsEnglish.getActivity_id());
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

}
