package com.example.bruger.slagshots;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class NewGameActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mGameRoom;
    private int mGamePin;


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

        final TextView gamePinView = (TextView) findViewById(R.id.gamepin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String inputName = intent.getStringExtra("inputName");
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText("1.      " + inputName + '\n' + "2.      " + "Frederik");

        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference("message");

        mRef.setValue("Hello, World");

        // Get a reference to the GameRoomCounter
        final DatabaseReference counterRef = mDatabase.getReference("GameRoomCounter");

        // Attach a listener to read the data at our posts reference
        counterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String counter = dataSnapshot.getValue().toString();
                int counterInt = Integer.parseInt(counter);
                mGamePin = counterInt;
                counter = String.format("%06d", counterInt);
                gamePinView.setText(counter);
                counterRef.setValue(counterInt+1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Fejl med gamepin", Toast.LENGTH_SHORT);
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        final DatabaseReference mGameRoom = mDatabase.getReference("GameRoom"+mGamePin);
        mGameRoom.child("PlayerOne").setValue(inputName);
        mGameRoom.child("PlayerTwo").setValue(null);


    }

    public void startSpil() {
        startActivity(new Intent(getApplicationContext(), GameActivity.class));
    }
    public void afbrydSpil() {
        finish();
    }

}
