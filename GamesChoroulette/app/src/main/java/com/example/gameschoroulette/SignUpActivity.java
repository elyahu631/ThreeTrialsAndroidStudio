package com.example.gameschoroulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gameschoroulette.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView alreadyHaveAccount;
    private EditText userName,userEmail,userPass,userRepass;
    private Button btnSignUp;
    private FirebaseAuth mAuth;


    private String user ;
    private String email ;
    private String password ;
    private String confirmPassword ;
    private String emailPattern;

    FirebaseDatabase database; // All DB obj
    DatabaseReference myRef; // my current DB area

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
            }
        });

        initParameters();

        initButtons();
    }

    private void initParameters() {
        mAuth = FirebaseAuth.getInstance();

        // Write a users to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

    }

    private void initButtons() {
        btnSignUp.setOnClickListener(this);
    }

    private void initViews() {
        userName = findViewById(R.id.inputUserName);
        userEmail = findViewById(R.id.inputEmail);
        userPass = findViewById(R.id.inputPassword);
        userRepass = findViewById(R.id.inputConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        alreadyHaveAccount = findViewById(R.id.haveAccount);
    }

    @Override
    public void onClick(View v) {
         user = userName.getText().toString();
         email = userEmail.getText().toString();
         password = userPass.getText().toString();
         confirmPassword = userRepass.getText().toString();
         emailPattern = "[a-zA-Za-z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (v.getId() == R.id.btnSignUp) {
            signUpAction();
        }
    }

    private void signUpAction() {

        IsUserNameExist(user).addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if (task.isSuccessful()) {
                    boolean isUserExist = task.getResult();

                    if(isUserExist == true){
                        userName.setError("The user is already taken, choose another name");
                    }
                    else if(isInputCorrect()){
                        mAuth.createUserWithEmailAndPassword(userEmail.getText().toString(),
                                userPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    User user = new User(userName.getText().toString());
                                    myRef.child(mAuth.getUid()).setValue(user);//
                                    startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                }
                                else{
                                    Toast.makeText(SignUpActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    // Handle the error here
                }
            }
        });

    }

    public boolean isInputCorrect(){
        if(user.isEmpty() || user.length() < 3){
            userName.setError("Enter correct User");
            return false;
        }
        else if(!email.matches(emailPattern)){
            userEmail.setError("Enter correct Email");
            return false;
        }else if(password.isEmpty() || password.length() < 6){
            userPass.setError("Enter Proper password");
            return false;
        }else if(!password.equals(confirmPassword)){
            userRepass.setError("Password not match Both field");
            return false;
        }
        return true;
    }


    private Task<Boolean> IsUserNameExist(final String userName){
        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isUserExist = false;
                // The onDataChange method will be called every time the data changes
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (userName.equals(user.getUserName())){
//                        Toast.makeText(SignUpActivity.this, user.getUserName(), Toast.LENGTH_SHORT).show();
                        isUserExist = true;
                        break;
                    }
                }
                taskCompletionSource.setResult(isUserExist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setException(databaseError.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

}