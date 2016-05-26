package com.auth0.authsados;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forceSignOut();
            }
        });


        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFragment();
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContent, new AsadoFragment())
                .commit();
    }

    private void showAddFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContent, new CreateAsadoFragment())
                .addToBackStack(null)
                .commit();
    }

    private void forceSignOut() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.setAction(LoginActivity.ACTION_REQUEST_SIGN_OUT);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
