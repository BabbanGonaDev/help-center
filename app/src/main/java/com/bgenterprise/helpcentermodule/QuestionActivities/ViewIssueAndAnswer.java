package com.bgenterprise.helpcentermodule.QuestionActivities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.bgenterprise.helpcentermodule.AppExecutors;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.NegativeDropdown;
import com.bgenterprise.helpcentermodule.Database.Tables.NegativeFeedback;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.HelpSessionManager;
import com.bgenterprise.helpcentermodule.R;
import com.bgenterprise.helpcentermodule.Utility;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ViewIssueAndAnswer extends AppCompatActivity {

    MaterialTextView qandaContent, qandaHeader, tvRatingHeader;
    MaterialButton btnThumbsDown, btnThumbsUp, btnSubmitNegativeFeedback;
    TextInputLayout input_negative_reason;
    AutoCompleteTextView atv_negative_reason;
    LinearLayout layoutRatingSection, negativeLayout;
    ProgressDialog progressDialog;
    VideoView resource_video_view;
    AppCompatImageView resource_image_view;
    HelpSessionManager sessionM;
    HelpCenterDatabase helpCenterDb;
    public List<QuestionsEnglish> questionsList;
    public List<String> negativeList;
    HashMap<String, String> help_details;
    String issue_content, issue_header, issue_resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_issue_and_answer);
        getSupportActionBar().setTitle("View Answer");
        questionsList = new ArrayList<>();
        negativeList = new ArrayList<>();
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
        negativeLayout = findViewById(R.id.negativeLayout);
        input_negative_reason = findViewById(R.id.input_negative_reason);
        atv_negative_reason = findViewById(R.id.atv_negative_reason);
        tvRatingHeader = findViewById(R.id.ratingheader);
        btnThumbsDown = findViewById(R.id.rating2);
        btnThumbsUp = findViewById(R.id.rating);
        resource_video_view = findViewById(R.id.resource_video_view);
        resource_image_view = findViewById(R.id.resource_image_view);


        //Get and display questions using AppExecutor.
        AppExecutors.getInstance().diskIO().execute(() -> {
            questionsList = helpCenterDb.getEnglishDao().getAllQuestionSolution(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID), help_details.get(HelpSessionManager.KEY_APP_LANG));

            runOnUiThread(() -> {
                issue_content = questionsList.get(0).getIssue_answer();
                issue_header = questionsList.get(0).getIssue_question();
                issue_resource = questionsList.get(0).getResource_id();

                qandaHeader.setText(issue_header);
                qandaContent.setText(Html.fromHtml(issue_content));

                //Get resource path based on app language.
                String path = Objects.requireNonNull(getExternalFilesDir(null)).getPath() + Utility.resource_location;

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
            helpCenterDb.getEnglishDao().updateThumbsUp(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));

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
            helpCenterDb.getEnglishDao().updateThumbsDown(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));
            negativeList = helpCenterDb.getDropdownDao().getNegativeReason(help_details.get(HelpSessionManager.KEY_APP_LANG));

            runOnUiThread(() -> {
                layoutRatingSection.setVisibility(View.GONE);
                tvRatingHeader.setVisibility(View.GONE);
                btnSubmitNegativeFeedback.setVisibility(View.VISIBLE);
                negativeLayout.setVisibility(View.VISIBLE);

                //Populate reasons for thumbs down.
                ArrayAdapter<String> negAdapter =
                        new ArrayAdapter<>(ViewIssueAndAnswer.this,
                                R.layout.dropdown_menu_popup_item,
                                negativeList);
                atv_negative_reason.setAdapter(negAdapter);
                atv_negative_reason.requestFocus();
            });
        });
    }

    public void negativeFeedback(){
        //Insert into db, only display string in log to ensure right data
        negativeLayout.setVisibility(View.GONE);
        String feedback_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String result = atv_negative_reason.getText().toString();

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
