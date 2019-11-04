package com.bgenterprise.helpcentermodule;

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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.google.android.material.button.MaterialButton;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewIssueAndAnswer extends AppCompatActivity {

    TextView qandaContent;
    TextView qandaHeader;
    MaterialButton rating2;
    MaterialButton rating;
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
        rating2 = findViewById(R.id.rating2);
        rating = findViewById(R.id.rating);

        @SuppressLint("StaticFieldLeak")
        getAnswers get_answers = new getAnswers(ViewIssueAndAnswer.this) {
            @Override
            protected void onPostExecute(List<QuestionsEnglish> issuesEnglishes) {
                super.onPostExecute(issuesEnglishes);
                IssuesList = issuesEnglishes;
                qandaHeader = findViewById(R.id.qandaHeader);
                qandaHeader.setText(help_details.get(HelpSessionManager.KEY_ACTIVITY_ISSUE));
                String htmlText = IssuesList.get(0).getIssue_answer();
                String gifName = IssuesList.get(0).getResource_id();
                makeCall();
                qandaContent = findViewById(R.id.qandaContent);
                qandaContent.setText(Html.fromHtml(htmlText, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        String path = "/storage/emulated/0/helpcenter/" + source;
                        try {
                            Drawable bmp = Drawable.createFromPath(path);
                            bmp.setBounds(0, 0, bmp.getIntrinsicWidth(), bmp.getIntrinsicHeight());
                            return bmp;
                        }
                        catch (Exception e){
                           Drawable bmp =  getResources().getDrawable(R.drawable.no_image);
                           bmp.setBounds(0, 0, 100, 100);
                            return bmp;
                        }
                    }
                }, null));

                VideoView gif = findViewById(R.id.gif);

                source2 = gifName;
                String path = "/storage/emulated/0/helpcenter/" + source2;

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
        };get_answers.execute(help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID),help_details.get(HelpSessionManager.KEY_ACTIVITY_ISSUE));
        progressDialog.dismiss();

        rating.setOnClickListener(view -> {
            rating();
        });

        rating2.setOnClickListener(view -> {
            rating();
        });
    }

    public void rating(){
        Log.d("CHECK", "We toasted");
        Toast.makeText(ViewIssueAndAnswer.this,"Thanks for your feedback", Toast.LENGTH_SHORT).show();
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
                Log.d("CHECK", "Loading Answers");
                Log.d("CHECK", help_details.get(HelpSessionManager.KEY_APP_ID)+ " " +help_details.get(HelpSessionManager.KEY_ACTIVITY_GROUP_ID) + " " + help_details.get(HelpSessionManager.KEY_UNIQUE_QUESTION_ID));
                answers = helpCenterDb.getEnglishDao().getAllQuestionSolution(strings[0]);
                Log.d("CHECK", answers.get(0).getIssue_answer());
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
