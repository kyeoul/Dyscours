package com.example.dyscours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * An activity where a user can see various information about their Dyscours accounts
 */
public class UserAccountActivity extends AppCompatActivity {
    private TextView textViewEmailAddress;
    private TextView textViewUserScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUserAccount);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = toolbar.findViewById(R.id.titleTextUserAccount);
        title.setText("Account Management");
        //various views
        textViewEmailAddress = findViewById(R.id.textViewEmailAddress);
        textViewUserScore = findViewById(R.id.textViewUserScore);
        // starts a listener that will populate the information
        FirebaseHelper.getInstance().startUserAccountListener(this);
        setEmailAddress(FirebaseHelper.getInstance().getUserEmail());
    }

    public void setEmailAddress(String text){
        textViewEmailAddress.setText(text);
    }

    public void setUserScore(String text){
        textViewUserScore.setText(text);
    }

}
