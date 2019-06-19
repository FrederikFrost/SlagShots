package com.example.bruger.slagshots;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NewGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        final Button startSpilButton = (Button) findViewById(R.id.start_spil_button);
        startSpilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpil();
            }
        });
        final Button afbrydSpilButton = (Button) findViewById(R.id.afbryd_spil_button);
        afbrydSpilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afbrydSpil();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String inputName = intent.getStringExtra("inputName");
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText("1.      " + inputName + '\n' + "2.      " + "Frederik");


    }
    public void startSpil() {
        startActivity(new Intent(getApplicationContext(), GameActivity.class));
    }
    public void afbrydSpil() {
        finish();
    }

}
