package com.bgenterprise.helpcentermodule;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    String session_app_lang, passed_staff_id, passed_user_location, passed_language;
    HelpSessionManager helpSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_splash_screen);
        helpSession = new HelpSessionManager(SplashScreen.this);
        LinearLayout splashscreen_next = findViewById(R.id.splashscreen_next_layout);

        try{
            helpSession.CLEAR_SESSION();
            passed_staff_id = getIntent().getStringExtra("staff_id");
            passed_language = getIntent().getStringExtra("app_language");
            passed_user_location = getIntent().getStringExtra("user_location"); //Get this from Access Control.

            if(!passed_staff_id.isEmpty() && !passed_user_location.isEmpty() && !passed_language.isEmpty()){
                helpSession.SET_STAFF_ID(passed_staff_id);
                helpSession.SET_USER_LOCATION(passed_user_location);
                helpSession.SET_LANGUAGE(passed_language);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        //Create a delay function of 1 second.
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            setAppLanguage();
            startActivity(new Intent(SplashScreen.this, HomePage.class));
        }, 250);

    }

    public void RedirectToHomepage(View v){
        startActivity(new Intent(SplashScreen.this, HomePage.class));
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
