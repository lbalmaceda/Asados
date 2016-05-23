package com.auth0.authsados;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signOutButton = (Button) findViewById(R.id.logoutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forceSignOut();
            }
        });
    }

    private void forceSignOut() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.setAction(LoginActivity.ACTION_REQUEST_SIGN_OUT);
        startActivity(i);
        finish();
    }
}
