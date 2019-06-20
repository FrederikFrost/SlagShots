package com.example.bruger.slagshots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JoinGameActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mGameRoom;
    private int[] mGamePin = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        final EditText editText = (EditText) findViewById(R.id.editText);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(6);
        editText.setFilters(filters);
        editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Button findSpilButton = (Button) findViewById(R.id.find_game_button);
        findSpilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String gamepin = editText.getText().toString();
                if(gamepin.trim().equals("") || gamepin.length() != 6) {
                    Toast.makeText(getApplicationContext(), "Indtast venligst et gyldigt gamepin", Toast.LENGTH_SHORT).show();

                } else {
                    int gamepinNumber = Integer.parseInt(gamepin);

                   finish();
                }
            }

    });


        /* Intent s
           TODO : Create GameScreenActivity class, which will basically be the same as NewGameActivity, for the person creating the game
           intent.putExtra("gamepinNumber",gamepinNumber);
            if(gamepinNumber == whatever gamepin som firebase har givet den anden bruger) {
                 startActivity(intent);
    }
    */

    }
}
