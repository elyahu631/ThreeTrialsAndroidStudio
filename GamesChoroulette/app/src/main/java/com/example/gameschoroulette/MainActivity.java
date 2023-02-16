package com.example.gameschoroulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText userPass,userEmail;
    Button btnLogin;
    FirebaseAuth mAuth;
    TextView forgetPass;
    Intent intent;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//Connect to activity_main.xml

        mAuth = FirebaseAuth.getInstance();

        initViews();
        buttonsAction();
        LoginAction();

    }

    private void LoginAction(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputEmailAndPassword();
                if(isInputCorrect()){
                    mAuth.signInWithEmailAndPassword(userEmail.getText().toString(),userPass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MainActivity.this,task.getException().toString() , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public boolean isInputCorrect(){
        if(email.isEmpty()){
            userEmail.setError("Enter correct Email");
            return false;
        }
        else if(password.isEmpty()){
            userPass.setError("Enter Proper password");
            return false;
        }
        return true;
    }

    private void showRecoverPasswordDialog () {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reset Password");
            LinearLayout linearLayout = new LinearLayout(this);
            final EditText rEmail = new EditText(this);

            // write the email using which you registered
            rEmail.setHint("Email");
            rEmail.setMinEms(16);
            rEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            linearLayout.addView(rEmail);
            linearLayout.setPadding(10, 10, 10, 10);
            builder.setView(linearLayout);



            // Click on Recover and a email will be sent to your registered email id
            builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //////////
                    String emailRes;
                    emailRes = rEmail.getText().toString().trim();
                    try {
                        beginRecovery(emailRes);
                    }
                    catch (Exception e) {
                         Toast.makeText(MainActivity.this, "No email entered", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    private void beginRecovery(String emailRes) {

        // calling sendPasswordResetEmail
        // open your email and write the new
        // password and then you can login
        mAuth.sendPasswordResetEmail(emailRes).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    Toast.makeText(MainActivity.this,"The email has been successfully sent",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this,"Error Occurred",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this,"Error Failed",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void buttonsAction() {
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        (findViewById(R.id.createNewAccount)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });
    }

    private void inputEmailAndPassword() {
        email = userEmail.getText().toString();
        password = userPass.getText().toString();
    }

    private void initViews() {
        userEmail = findViewById(R.id.inputEmail);
        userPass = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        forgetPass = findViewById(R.id.forgotPassword);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            //get the mail from fire base
            intent = new Intent(getApplicationContext(), HomeActivity.class);
//            intent.putExtra("user_mail",  user.getEmail().toString());
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}