package com.example.gameschoroulette;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SevenBoom extends AppCompatActivity {
    TextView showNumber,showPoints,showLevel;
    Button btnBoom;
    Integer number,points,level,helpLevel,timeDelay;
    LinearLayout linearLayout;

    boolean gameOn = true;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven_boom);

        initViews();
        initParameters();
        showLevel.setText(String.valueOf("Level " +  level));
        showPoints.setText(String.valueOf("Points\n" +  points));
        SevenBoomOn();

    }



    public void SevenBoomOn(){
       final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                linearLayout.setBackgroundColor(Color.TRANSPARENT);
                if (!gameOn) return;
                number++;
                showNumber.setText("number\n"+ number);
                btnBoomClick();
                handler.postDelayed(this, timeDelay);
            }
        }, 0);
    }




    @SuppressLint("ClickableViewAccessibility")
    public void  btnAffect(){
        int originalColor = Color.parseColor("#000000");
        int pressedColor = Color.parseColor("#191919");

// Create a ColorFilter that applies the pressed color
        ColorFilter pressedFilter = new LightingColorFilter(0xFFFFFFFF, pressedColor);

// Create a ColorFilter that applies the original color
        ColorFilter originalFilter = new LightingColorFilter(0xFFFFFFFF, originalColor);

        btnBoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Apply the pressed color filter
                    view.getBackground().setColorFilter(pressedFilter);
                    view.invalidate();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Apply the original color filter
                    view.getBackground().setColorFilter(originalFilter);
                    view.invalidate();
                }
                return false;
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    public void btnBoomClick(){


        btnAffect();
        btnBoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gameOn) {
                    if (number % 7 == 0 && number.toString().contains("7")) {
                        points += 7 + number;
                        ChangeLevel();
                    } else if (number % 7 == 0 || number.toString().contains("7")) {
                        points += 7;
                        ChangeLevel();
                    }
                    showPoints.setText(String.valueOf("Points\n" +  points));
                }
                gameOn = !gameOn;
                SevenBoomOn();
            }
        });
    }

    public void ChangeLevel(){
        mediaPlayer.start();
        ShowBoom();
        if (points > helpLevel){
            level++;
            helpLevel+=helpLevel;
            showLevel.setText(String.valueOf("Level " +  level));
            timeDelay -= 25;
        }
    }

    public void ShowBoom(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                linearLayout.setBackgroundResource(R.drawable.fire_explosion);
            }
        }, 200);
    }

    private void initViews() {
        btnBoom = findViewById(R.id.boom);
        showNumber = findViewById(R.id.num);
        showPoints = findViewById(R.id.points_seven_boom);
        showLevel = findViewById(R.id.level_seven_boom);
        linearLayout = findViewById(R.id.linearLayout3);
    }

    private void initParameters() {
         number = 0;
         gameOn = true;
         points = 0;
         level = 1;
         helpLevel= 100;
         timeDelay = 300;
         mediaPlayer = MediaPlayer.create(this, R.raw.boom_effect);
    }
}