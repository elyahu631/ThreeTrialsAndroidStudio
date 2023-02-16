package com.example.gameschoroulette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GuessNumber extends AppCompatActivity implements View.OnClickListener{

    MediaPlayer mediaPlayer;
    EditText guessNumberInput;
    Button btnGuess, btnReset;
    TextView remainGuessLabel,labelTop, hints,showLevel,showScore;
    Random r;
    int remainGuesses;
    int randomNumber;
    int level,score;
    int guessNumber;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    private String EditGuess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_number);

        // init views
        initViews();

        // init parameters
        initParameters();

        // init buttons
        initButtons();
    }



    private void initButtons() {
        btnGuess.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    private void initParameters() {
        mediaPlayer = MediaPlayer.create(this, R.raw.ipad_click);
        r = new Random();
        remainGuesses = 5;
        level = 1;
        score = 0;
        sharedPreferences = GuessNumber.this.getPreferences(MODE_PRIVATE);
        randomNumber = r.nextInt(5) + 1;
        EditGuess =  guessNumberInput.getText().toString();
        remainGuessLabel.setText("Remain Guesses is: " + remainGuesses);
//        Toast.makeText(GuessNumber.this,"random number is: " + randomNumber,Toast.LENGTH_SHORT).show();
        level = sharedPreferences.getInt("level",1);
        score = sharedPreferences.getInt("score",0);
        modifyGame(remainGuesses,5*level,"1-" + String.valueOf(5*level),level,score);
    }

    private void initViews() {
        guessNumberInput = findViewById(R.id.guessNumberInput);
        guessNumberInput.setShowSoftInputOnFocus(false);//
        hints = findViewById(R.id.guessHints);
        btnGuess = findViewById(R.id.btnGuess);
        remainGuessLabel = findViewById(R.id.remainGuessLabel);
        btnReset = findViewById(R.id.btnReset);
        labelTop = findViewById(R.id.labelTop);
        showLevel = findViewById(R.id.level_guess_number);
        showScore = findViewById(R.id.score_guess_number);
    }

    public void modifyGame(int remainGuesses,int randomNumber,String labeltop,int level,int score){
        // save level on the device storage
        editor = sharedPreferences.edit();
        editor.putInt("level",level);
        editor.putInt("score",score);
        editor.apply();
        labelTop.setText("Guess Number Between " + labeltop);
        showLevel.setText("Level: " + level);
        showScore.setText("Score: " + score);
        this.remainGuesses = remainGuesses;

        remainGuessLabel.setText("Remain Guesses is: " + remainGuesses);

        this.randomNumber = r.nextInt(randomNumber) + 1;
        guessNumberInput.setText("");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnGuess:
                try {
                    btnGuessAction();
                } catch (Exception e) {
                    // Handle the exception
                    e.printStackTrace();
                    // Display an error message to the user
                    hints.setText("You didn't guess");
                }
                break;
            case R.id.btnReset:
                btnResetAction();
                break;
        }
    }

    private void btnResetAction() {
        hints.setText("");
        EditGuess = "";
        level = 1;
        score = 0;
        modifyGame(5,5*level,"1-" + String.valueOf(5*level),level,score);
    }

    private void btnGuessAction() {
        if(remainGuesses != 0) {
            remainGuesses--;
            guessNumber = Integer.parseInt(guessNumberInput.getText().toString());
            remainGuessLabel.setText("Remain Guesses is: " + remainGuesses);
            if (randomNumber == guessNumber) {
                hints.setText("You guessed it right");
                score += level * (remainGuesses+1);
                level++;
                showLevel.setText("Level: " + level);
                showScore.setText("Score: " + score);
                remainGuesses = 5;

                modifyGame(remainGuesses,5*level,"1-" + String.valueOf(5*level),level,score);
            }
            else{
                if(remainGuesses == 0) {
                    hints.setText("You Lose");
                    return;
                }
                HowClose();

            }
        }else{
            hints.setText("Reset the game to play again ");

        }
        EditGuess = "";
        guessNumberInput.setText(EditGuess);
    }


    public void HowClose(){
        int num = randomNumber - guessNumber;
        if (num > 0)
            hints.setText("need to go Up");// go down
        else if(num < 0)
            hints.setText("need to go down");
    }

    public void btnZeroPress(View v){
        guessNumberInput.setText( EditGuess += "0");
        mediaPlayer.start();
    }
    public void btnOnePress(View v){
        guessNumberInput.setText( EditGuess += "1");
        mediaPlayer.start();
    }
    public void btnTwoPress(View v){
        guessNumberInput.setText(EditGuess += "2");
        mediaPlayer.start();
    }
    public void btnThreePress(View v){
        guessNumberInput.setText(EditGuess += "3");
        mediaPlayer.start();
    }
    public void btnFourPress(View v){
        guessNumberInput.setText(EditGuess += "4");
        mediaPlayer.start();
    }
    public void btnFivePress(View v){
        guessNumberInput.setText(EditGuess += "5");
        mediaPlayer.start();
    }
    public void btnSixPress(View v){
        guessNumberInput.setText(EditGuess += "6");
        mediaPlayer.start();
    }
    public void btnSevenPress(View v){
        guessNumberInput.setText(EditGuess += "7");
        mediaPlayer.start();
    }
    public void btnEightPress(View v){
        guessNumberInput.setText(EditGuess += "8");
        mediaPlayer.start();
    }
    public void btnNinePress(View v){
        guessNumberInput.setText(EditGuess += "9");
        mediaPlayer.start();
    }

    public void btnDeleteGuess(View v){
        EditGuess = "";
        guessNumberInput.setText(EditGuess);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (remainGuesses == 0){
            btnResetAction();
        }
    }
}