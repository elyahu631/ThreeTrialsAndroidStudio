package com.example.gameschoroulette;

import android.os.Bundle;
import android.view.WindowManager;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;



public class SnakeGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the activity to full screen mode
        getWindow().setFlags
                (WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the content view to the activity_snake_game layout
        setContentView(R.layout.activity_snake_game);

        // Hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}

