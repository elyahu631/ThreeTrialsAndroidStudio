package com.example.gameschoroulette;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gameschoroulette.clases.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    AlertDialog.Builder builder;
    FirebaseAuth mAuth;
    TextView viewUser;
    private Button guessNumber,sevenBoom,game;

    FirebaseDatabase database; // All DB obj
    DatabaseReference myRef; // my current DB area

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();

        GetUserFromFireBase();

        initParameters();

        ButtonsAction();
    }

    private void initViews() {

        guessNumber = findViewById(R.id.guessNumber);
        sevenBoom = findViewById(R.id.sevenBoom);
        game = findViewById(R.id.sudoku);
        viewUser = findViewById(R.id.userLoginEmail);////////
    }

    private void ButtonsAction() {
        guessNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,GuessNumber.class));
            }
        });

        sevenBoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,SevenBoom.class));
            }
        });

        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,SnakeGame.class));
            }
        });
    }

    private void GetUserFromFireBase(){
        mAuth = FirebaseAuth.getInstance();//
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("Users").child(mAuth.getUid());
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                viewUser.setText("Welcome " + user.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initParameters() {
        builder = new AlertDialog.Builder(this);
    }

    @Override
    public void onBackPressed() {
        builder.setTitle("Sign out").setMessage("Do you want to log out?").setCancelable(true).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
            }
        }).setNegativeButton("No",null).show();

    }
}


