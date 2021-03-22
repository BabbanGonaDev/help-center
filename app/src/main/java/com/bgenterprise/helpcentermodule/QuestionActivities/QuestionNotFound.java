package com.bgenterprise.helpcentermodule.QuestionActivities;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bgenterprise.helpcentermodule.BuildConfig;
import com.bgenterprise.helpcentermodule.HelpSessionManager;
import com.bgenterprise.helpcentermodule.R;
import com.bgenterprise.helpcentermodule.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;
import java.util.Locale;

public class QuestionNotFound extends AppCompatActivity {
    MaterialTextView mtv_app_version;
    HelpSessionManager sessionM;
    HashMap<String, String> help_details;
    String session_app_lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_not_found);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionM = new HelpSessionManager(QuestionNotFound.this);
        mtv_app_version = findViewById(R.id.mtv_app_version);
        mtv_app_version.setText("\u00A9" + "Faiida Gesse Help Center v" + BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_issues_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else if(item.getItemId() == R.id.change_language){
            changeAppLanguage();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void changeAppLanguage(){
        new MaterialAlertDialogBuilder(QuestionNotFound.this)
                .setTitle("Choose App Language")
                .setSingleChoiceItems(Utility.app_language, -1, (dialogInterface, i) -> {
                    Toast.makeText(QuestionNotFound.this, R.string.helpcenter_selected_language_toast + Utility.app_language[i], Toast.LENGTH_LONG).show();

                    //Based on language, set the appropriate application language.
                    String selected_lang = Utility.app_language[i];
                    switch (selected_lang){
                        case "English":
                            sessionM.SET_LANGUAGE("en");
                            break;
                        case "Hausa":
                            sessionM.SET_LANGUAGE("ha");
                            break;
                        default:
                            break;
                    }
                    dialogInterface.dismiss();
                    setAppLanguage();
                }).show();
    }

    public void setAppLanguage(){
        session_app_lang = sessionM.getAppLanguage();
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(session_app_lang);
        res.updateConfiguration(conf, dm);
        recreate();
    }
}
