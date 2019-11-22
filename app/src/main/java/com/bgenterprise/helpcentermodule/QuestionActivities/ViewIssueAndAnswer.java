package com.bgenterprise.helpcentermodule.QuestionActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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
import com.bgenterprise.helpcentermodule.HelpSessionManager;
import com.bgenterprise.helpcentermodule.R;
import com.google.android.material.button.MaterialButton;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViewIssueAndAnswer extends AppCompatActivity {

    TextView qandaContent;
    TextView qandaHeader;
    TextView tvRatingHeader;
    EditText etNegativeFeedback;
    MaterialButton btnThumbsDown;
    MaterialButton btnThumbsUp;
    MaterialButton btnSubmitNegativeFeedback;
    LinearLayout layoutRatingSection;
    ProgressDialog progressDialog;
    VideoView gifView;
    HelpSessionManager sessionM;
    HelpCenterDatabase helpCenterDb;
    public List<QuestionsEnglish> IssuesList;
    HashMap<String, String> help_details;
    String htmlText, issue_header, gif_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_view_issue_and_answer);
        getSupportActionBar().setTitle("Help Center");
        IssuesList = new ArrayList<>();
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
        gifView = findViewById(R.id.gif);


        //Get and display questions using AppExecutor.
        AppExecutors.getInstance().diskIO().execute(() -> {
            IssuesList = helpCenterDb.getEnglishDao().getAllQuestionSolution(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));

            runOnUiThread(() -> {
                htmlText = IssuesList.get(0).getIssue_answer();
                issue_header = IssuesList.get(0).getIssue_question();
                gif_name = IssuesList.get(0).getResource_id();
                qandaHeader.setText(issue_header);
                qandaContent.setText(Html.fromHtml(htmlText, source -> {
                    String path = Environment.getExternalStoragePublicDirectory("")+"/helpcenter/";
                    try {
                        Drawable bmp = Drawable.createFromPath(path);
                        bmp.setBounds(0, 0, bmp.getIntrinsicWidth(), bmp.getIntrinsicHeight());
                        return bmp;
                    } catch (Exception e){
                        Drawable bmp =  getResources().getDrawable(R.drawable.helpcenter_no_image);
                        bmp.setBounds(0, 0, 100, 100);
                        return bmp;
                    }
                }, null));

                String path = Environment.getExternalStoragePublicDirectory("") + "/helpcenter/" + gif_name;
                File dir = new File(path);
                if(dir.exists() && gif_name != null && !gif_name.matches("")) {
                    Log.d("Dami","Getting1");
                    try {
                        gifView.setVideoPath(path);
                        gifView.invalidate();
                        //gifView.getHolder().setFixedSize(200,200);
                        gifView.setOnPreparedListener(mp -> {
                            mp.setLooping(true);
                            gifView.start();
                        });
                    } catch (Exception e) {
                        Log.d("Dami","Getting3");
                        e.printStackTrace();
                        gifView.setVisibility(View.GONE);
                    }
                }else{
                    Log.d("Dami","Getting2");
                    gifView.setVisibility(View.GONE);
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

}
