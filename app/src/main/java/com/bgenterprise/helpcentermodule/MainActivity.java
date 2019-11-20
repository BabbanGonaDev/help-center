package com.bgenterprise.helpcentermodule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bgenterprise.helpcentermodule.QuestionActivities.ViewActivityIssues;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton materialButton = findViewById(R.id.btn_helpCenter);
        materialButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ViewActivityIssues.class));
        });
    }
}
