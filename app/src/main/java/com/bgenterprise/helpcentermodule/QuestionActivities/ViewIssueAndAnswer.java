package com.bgenterprise.helpcentermodule.QuestionActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bgenterprise.helpcentermodule.AppExecutors;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.NegativeFeedback;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;
import com.bgenterprise.helpcentermodule.HelpSessionManager;
import com.bgenterprise.helpcentermodule.QuestionsAll;
import com.bgenterprise.helpcentermodule.R;
import com.bgenterprise.helpcentermodule.Utility;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViewIssueAndAnswer extends AppCompatActivity {

    MaterialTextView qandaContent;
    MaterialTextView qandaHeader;
    TextView tvRatingHeader;
    EditText etNegativeFeedback;
    MaterialButton btnThumbsDown;
    MaterialButton btnThumbsUp;
    MaterialButton btnSubmitNegativeFeedback;
    LinearLayout layoutRatingSection;
    ProgressDialog progressDialog;
    VideoView resource_video_view;
    AppCompatImageView resource_image_view;
    HelpSessionManager sessionM;
    HelpCenterDatabase helpCenterDb;
    public List<QuestionsEnglish> questionsList_en;
    public List<QuestionsHausa> questionsList_ha;
    public List<QuestionsAll> questionsList_all;
    HashMap<String, String> help_details;
    String issue_content, issue_header, issue_resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_issue_and_answer);
        getSupportActionBar().setTitle("View Answer");
        questionsList_en = new ArrayList<>();
        questionsList_ha = new ArrayList<>();
        questionsList_all = new ArrayList<>();
        sessionM = new HelpSessionManager(ViewIssueAndAnswer.this);
        helpCenterDb = HelpCenterDatabase.getInstance(ViewIssueAndAnswer.this);
        help_details = sessionM.getHelpDetails();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        qandaContent = findViewById(R.id.qandaContent);
        qandaHeader = findViewById(R.id.qandaHeader);
        btnSubmitNegativeFeedback = findViewById(R.id.submitfeedback);
        layoutRatingSection = findViewById(R.id.ratingsection);
        tvRatingHeader = findViewById(R.id.ratingheader);
        etNegativeFeedback = findViewById(R.id.feedback);
        btnThumbsDown = findViewById(R.id.rating2);
        btnThumbsUp = findViewById(R.id.rating);
        resource_video_view = findViewById(R.id.resource_video_view);
        resource_image_view = findViewById(R.id.resource_image_view);


        //Get and display questions using AppExecutor.
        AppExecutors.getInstance().diskIO().execute(() -> {
            switch (sessionM.getAppLanguage()){
                case "en":
                    questionsList_en = helpCenterDb.getEnglishDao().getAllQuestionSolution(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));
                    questionsList_all = convertEnglishToAll(questionsList_en);
                    break;
                case "ha":
                    questionsList_ha = helpCenterDb.getHausaDao().getAllQuestionSolution(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));
                    questionsList_all = convertHausaToAll(questionsList_ha);
                    break;
            }

            runOnUiThread(() -> {
                issue_content = questionsList_all.get(0).getIssue_answer();
                issue_header = questionsList_all.get(0).getIssue_question();
                issue_resource = questionsList_all.get(0).getResource_id();

                qandaHeader.setText(issue_header);
                qandaContent.setText(Html.fromHtml(issue_content));

                //Get resource path based on app language.
                String path;
                switch (sessionM.getAppLanguage()){
                    case "en":
                        path = Environment.getExternalStorageDirectory().getPath() + Utility.resource_location_en;
                        break;
                    case "ha":
                        path = Environment.getExternalStorageDirectory().getPath() + Utility.resource_location_ha;
                        break;
                    default:
                        path = Environment.getExternalStorageDirectory().getPath() + Utility.resource_location;
                        break;

                }

                //Display appropriate view based on resource type.
                if(!issue_resource.isEmpty()){
                    String resource_path = path + issue_resource;
                    switch (getFileExtension(issue_resource)){
                        case "gif":
                        case "mp4":
                            displayVideoResource(resource_path);
                            break;
                        case "jpg":
                        case "png":
                            displayImageResource(resource_path);
                            break;
                    }
                }

                progressDialog.dismiss();
            });
        });

        btnSubmitNegativeFeedback.setOnClickListener(view -> {
            negativeFeedback();
        });

        btnThumbsUp.setOnClickListener(view -> {
            ratingUp();
        });

        btnThumbsDown.setOnClickListener(view -> {
            ratingDown();
        });
    }

    public void ratingUp(){
        //Thumbs Up
        AppExecutors.getInstance().diskIO().execute(() -> {
            switch (sessionM.getAppLanguage()){
                case "en":
                    helpCenterDb.getEnglishDao().updateThumbsUp(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));
                    break;
                case "ha":
                    helpCenterDb.getHausaDao().updateThumbsUp(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));
                    break;
            }

            runOnUiThread(() -> {
                Toast.makeText(ViewIssueAndAnswer.this,R.string.helpcenter_feedback_appreciate, Toast.LENGTH_SHORT).show();
                layoutRatingSection.setVisibility(View.GONE);
                tvRatingHeader.setVisibility(View.GONE);
            });
        });
    }

    public void ratingDown(){
        //Thumbs Down
        AppExecutors.getInstance().diskIO().execute(() -> {
            switch (sessionM.getAppLanguage()){
                case "en":
                    helpCenterDb.getEnglishDao().updateThumbsDown(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));
                    break;
                case "ha":
                    helpCenterDb.getHausaDao().updateThumbsDown(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));
                    break;
            }

            runOnUiThread(() -> {
                layoutRatingSection.setVisibility(View.GONE);
                tvRatingHeader.setVisibility(View.GONE);
                etNegativeFeedback.setVisibility(View.VISIBLE);
                btnSubmitNegativeFeedback.setVisibility(View.VISIBLE);
            });
        });
    }

    public void negativeFeedback(){
        //Insert into db, only display string in log to ensure right data
        String feedback_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String result = etNegativeFeedback.getText().toString();
        String feedback_result = "APP_ID: " + "\n" + help_details.get(HelpSessionManager.KEY_APP_ID) + "\n" +
                "QUESTION_ID: "+"\n" + help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID) + "\n" +
                "FEEDBACK: "+"\n" + result + "\n" +
                "DATE: "+"\n" + feedback_date + "\n" +
                "SYNC_STATUS: " + "\n" + " - ";
        Log.d("CHECK", feedback_result);
        btnSubmitNegativeFeedback.setVisibility(View.GONE);
        etNegativeFeedback.setVisibility(View.GONE);

        AppExecutors.getInstance().diskIO().execute(() -> {
            //Insert into negative feedback here.
            helpCenterDb.getFeedbackDao().InsertFeedback(new NegativeFeedback(help_details.get(HelpSessionManager.KEY_STAFF_ID),
                    help_details.get(HelpSessionManager.KEY_APP_ID),
                    help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID),
                    result,
                    feedback_date,
                    "no"));

            runOnUiThread(() -> Toast.makeText(ViewIssueAndAnswer.this, R.string.helpcenter_feedback_appreciate, Toast.LENGTH_LONG).show());
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

    public String getFileExtension(String name){
        if(!name.isEmpty()){
            return name.substring(name.length() - 3).toLowerCase();
        }else{
            return "";
        }
    }

    public void displayVideoResource(String path){
        File dir = new File(path);
        if(dir.exists()) {
            try {
                resource_video_view.setVisibility(View.VISIBLE);
                resource_video_view.setVideoPath(path);
                resource_video_view.invalidate();
                resource_video_view.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    resource_video_view.start();
                });
            } catch (Exception e) {
                e.printStackTrace();
                resource_video_view.setVisibility(View.GONE);
            }
        }
    }

    public void displayImageResource(String path){
        File imgFile = new File(path);
        if(imgFile.exists()){
            try {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                resource_image_view.setVisibility(View.VISIBLE);
                resource_image_view.setImageBitmap(myBitmap);
            }catch (Exception e){
                e.printStackTrace();
                resource_image_view.setVisibility(View.GONE);
            }
        }
    }

}
