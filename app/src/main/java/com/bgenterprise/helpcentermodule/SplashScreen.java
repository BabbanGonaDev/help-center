package com.bgenterprise.helpcentermodule;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    String session_app_lang;
    HelpSessionManager helpSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_splash_screen);
        helpSession = new HelpSessionManager(SplashScreen.this);
        LinearLayout splashscreen_next = findViewById(R.id.splashscreen_next_layout);

        //Create a delay function of 1 second.
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Re-direct to the Home page activity after 1sec.
            /*if(!helpSession.getImportStatus()){
                @SuppressLint("StaticFieldLeak")
                PopulateDB insertDB = new PopulateDB(SplashScreen.this){
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Toast.makeText(SplashScreen.this, "Database insertion complete", Toast.LENGTH_LONG).show();
                        helpSession.SET_IMPORT_STATUS(true);
                    }
                };insertDB.execute();
            }*/
            helpSession.CLEAR_SESSION();
            setAppLanguage();
            startActivity(new Intent(SplashScreen.this, HomePage.class));
        }, 750);

    }

    public void RedirectToHomepage(View v){
        this.startActivity(new Intent(SplashScreen.this, HomePage.class));
    }

    public void setAppLanguage(){
        session_app_lang = helpSession.getAppLanguage();
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(session_app_lang);
        res.updateConfiguration(conf, dm);
    }
}
