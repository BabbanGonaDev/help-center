package com.bgenterprise.helpcentermodule;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_splash_screen);
        HelpSessionManager helpSession = new HelpSessionManager(SplashScreen.this);
        LinearLayout splashscreen_next = findViewById(R.id.splashscreen_next_layout);

        //Create a delay function of 1 second.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Re-direct to the Home page activity after 1sec.
                if(!helpSession.getImportStatus()){
                    @SuppressLint("StaticFieldLeak")
                    PopulateDB insertDB = new PopulateDB(SplashScreen.this){
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Toast.makeText(SplashScreen.this, "Database insertion complete", Toast.LENGTH_LONG).show();
                            helpSession.SET_IMPORT_STATUS(true);
                        }
                    };insertDB.execute();
                }
                helpSession.CLEAR_SESSION();
                startActivity(new Intent(SplashScreen.this, HomePage.class));
            }
        }, 750);

    }

    public void RedirectToHomepage(View v){
        this.startActivity(new Intent(SplashScreen.this, HomePage.class));
    }
}
