package com.example.dyscours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

public class SignUpActivity extends AppCompatActivity {

    public EditText emailId, password;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        btnSignUp = findViewById(R.id.button2);
        tvSignIn = findViewById(R.id.textView);
        cardView = findViewById(R.id.cardView);

        btnSignUp.setOnClickListener(new View.OnClickListener(){
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
                    Toast.makeText(SignUpActivity.this,"Fields Are Empty!", Toast.LENGTH_SHORT);
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
                            firebaseHelper.initUser(mFirebaseAuth.getUid());
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT);
                            }
                            else{
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Error Occured!", Toast.LENGTH_SHORT);
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.signInImage);
        final TransitionDrawable loginBackground = (TransitionDrawable) imageView.getDrawable();

        changeDrawableTint(getResources().getColor(R.color.messageTextColor), emailId, getResources().getDrawable(R.drawable.ic_user));
        changeDrawableTint(getResources().getColor(R.color.messageTextColor), password, getResources().getDrawable(R.drawable.ic_password));

        emailId.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus){
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
                    loginBackground.startTransition(300);
                    changeDrawableTint(getResources().getColor(R.color.colorPrimary), password, getResources().getDrawable(R.drawable.ic_password));
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                else{
                    loginBackground.reverseTransition(300);
                    changeDrawableTint(getResources().getColor(R.color.messageTextColor), password, getResources().getDrawable(R.drawable.ic_password));
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
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
