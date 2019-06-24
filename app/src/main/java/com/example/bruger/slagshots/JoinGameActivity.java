package com.example.bruger.slagshots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinGameActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mGameRoomsRoot;
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

        mDatabase = FirebaseDatabase.getInstance();
        mGameRoomsRoot = mDatabase.getReference("GameRooms");

        Intent intent = getIntent();
        final String inputName = intent.getStringExtra("inputName");

        Button findSpilButton = (Button) findViewById(R.id.find_game_button);
        findSpilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String gamePinChecker = editText.getText().toString();
                final String gamePin;
                if(gamePinChecker.trim().equals("") || gamePinChecker.length() != 6) {
                    Toast.makeText(getApplicationContext(), "Indtast venligst et gyldigt gamepin", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Finder spil med gamepin " + gamePinChecker + "...", Toast.LENGTH_SHORT).show();
                    gamePin = formatPin(editText.getText().toString());
                    mGamePin[0] = Integer.parseInt(gamePin);


                    mGameRoomsRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.hasChild("GameRoom"+mGamePin[0]) || !dataSnapshot.child("GameRoom"+mGamePin[0])
                            .child("PlayerTwo").getValue().toString().equals("-1")){
                                Log.i("SpilStart", "Spil kunne ikke startes med GamePin : "+mGamePin[0]);
                                Toast.makeText(getApplicationContext(), "Spillet med gamepin " + gamePin + " kunne ikke findes.", Toast.LENGTH_LONG).show();
                                return;
                            } else{
                                mGameRoom = mGameRoomsRoot.child("GameRoom"+mGamePin[0]);
                                mGameRoom.child("PlayerTwo").setValue(inputName);
                                Intent newGameIntent = new Intent(getApplicationContext(), GameActivity.class);
                                newGameIntent.putExtra("GameroomName","GameRoom"+mGamePin[0]);
                                newGameIntent.putExtra("isPlayerOne",false);
                                startActivity(newGameIntent);
                                Toast.makeText(getApplicationContext(), "Dit spil blev fundet. Held og lykke matros!", Toast.LENGTH_LONG).show();
                                Log.i("SpilStart", "Spil startet med GameRoomID : "+mGamePin[0]);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

    });


    }

    public static String formatPin(String gamePin){
        // Count leading zeros
        int leadingZeroes = 0;
        while (leadingZeroes < 5 && gamePin.charAt(leadingZeroes) == '0')
            leadingZeroes++;

        // Convert str into StringBuffer
        StringBuffer sbPin = new StringBuffer(gamePin);
        //Use replace method to remove the leading zeroes.
        sbPin.replace(0, leadingZeroes, "");
        //Return the string
        return sbPin.toString();

    }

}
