package com.example.blue_beast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setView();
        setListeners();
    }

    private void setView() {
        tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("PERSONAL DETAILS"));
        tabLayout.addTab(tabLayout.newTab().setText("PARENTS & GUARDIAN"));
        tabLayout.addTab(tabLayout.newTab().setText("ID PROOF"));
        tabLayout.addTab(tabLayout.newTab().setText("CERTIFICATE"));
    }

    private void setListeners() {
        tabLayout.setOnClickListener(view -> {

        });
    }
}