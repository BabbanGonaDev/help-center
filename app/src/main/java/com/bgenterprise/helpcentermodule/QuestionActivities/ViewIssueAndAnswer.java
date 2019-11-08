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
    MaterialButton btnThumbsDown;
    MaterialButton btnThumbsUp;
    LinearLayout layoutRatingSection;
    TextView tvRatingHeader;
    EditText etNegativeFeedback;
    MaterialButton btnSubmitNegativeFeedback;
    private static final int REQUEST_CALL = 1;
    HelpSessionManager sessionM;
    HelpCenterDatabase helpCenterDb;
    public List<QuestionsEnglish> IssuesList;
    HashMap<String, String> help_details;
    ProgressDialog progressDialog;
    String source2;


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

        @SuppressLint("StaticFieldLeak")
        getAnswers get_answers = new getAnswers(ViewIssueAndAnswer.this) {
            @Override
            protected void onPostExecute(List<QuestionsEnglish> issuesEnglishes) {
                super.onPostExecute(issuesEnglishes);
                IssuesList = issuesEnglishes;
                String htmlText = IssuesList.get(0).getIssue_answer();
                String issue_header = IssuesList.get(0).getIssue_question();
                String gifName = IssuesList.get(0).getResource_id();
                makeCall();
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

                VideoView gif = findViewById(R.id.gif);

                source2 = gifName;
//                File path = new File(context.getFilesDir()+"/helpcenter/", source2);
                String path = Environment.getExternalStoragePublicDirectory("") + "/helpcenter/" + source2;

                File dir = new File(path);
                if(dir.exists() && source2 != null && !source2.matches("")) {
                    Log.d("Dami","Getting1");
                    try {
                            /*Uri uri = Uri.fromFile(new File(path));*/
                            gif.setVideoPath(path);
                            gif.invalidate();
                            //gif.getHolder().setFixedSize(200,200);
                            gif.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.setLooping(true);
                                    gif.start();
                                }
                            });
                        } catch (Exception e) {
                            Log.d("Dami","Getting3");
                            e.printStackTrace();
                            gif.setVisibility(View.GONE);
                    }
                }else{
                    Log.d("Dami","Getting2");
                    gif.setVisibility(View.GONE);
                }
            }
        };get_answers.execute(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));
        progressDialog.dismiss();

        btnSubmitNegativeFeedback.setOnClickListener(view -> {
            try {
                negativeFeedback();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnThumbsUp.setOnClickListener(view -> {
            try {
                ratingUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnThumbsDown.setOnClickListener(view -> {
            try {
                ratingDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    @SuppressLint("StaticFieldLeak")
    public class getAnswers extends AsyncTask <String, Void, List<QuestionsEnglish>>{
        Context context;
        List<QuestionsEnglish> answers = new ArrayList<>();
        public getAnswers(Context context) {
            this.context = context;
        }

        @Override
        protected List<QuestionsEnglish> doInBackground(String... strings) {
            try{
                answers = helpCenterDb.getEnglishDao().getAllQuestionSolution(strings[0]);
                return answers;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    public void makeCall() {
        if (ContextCompat.checkSelfPermission((ViewIssueAndAnswer.this),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewIssueAndAnswer.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CALL);
        } else {

        }
    }
}
