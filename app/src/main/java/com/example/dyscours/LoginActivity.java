package com.example.dyscours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextLog);
        password = findViewById(R.id.editText2Log);
        btnSignIn = findViewById(R.id.button);
        tvSignUp = findViewById(R.id.textView);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser!=null){
                    Toast.makeText(LoginActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please login",Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Fields Are Empty!", Toast.LENGTH_SHORT);
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,"Login error, Please login again.", Toast.LENGTH_SHORT);
                            }
                            else {
                                Intent intToMain = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intToMain);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,"Error Occured!", Toast.LENGTH_SHORT);

                }
            }

        });

        tvSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intSignUp);
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.logInImage);
        final TransitionDrawable loginBackground = (TransitionDrawable) imageView.getDrawable();
        changeDrawableTint(getResources().getColor(R.color.messageTextColor), emailId, getResources().getDrawable(R.drawable.ic_user));
        changeDrawableTint(getResources().getColor(R.color.messageTextColor), password, getResources().getDrawable(R.drawable.ic_password));

        emailId.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus){
                    loginBackground.startTransition(300);
                    changeDrawableTint(getResources().getColor(R.color.colorAccent), emailId, getResources().getDrawable(R.drawable.ic_user));
                }
                else{
                    changeDrawableTint(getResources().getColor(R.color.messageTextColor), emailId, getResources().getDrawable(R.drawable.ic_user));
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    loginBackground.reverseTransition(300);
                    changeDrawableTint(getResources().getColor(R.color.colorPrimary), password, getResources().getDrawable(R.drawable.ic_password));
                }
                else{
                    changeDrawableTint(getResources().getColor(R.color.messageTextColor), password, getResources().getDrawable(R.drawable.ic_password));
                }
            }
        });

    }

    public void changeDrawableTint(int color, EditText editText, Drawable icon){
        Drawable drawable = icon;
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        editText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }
}
