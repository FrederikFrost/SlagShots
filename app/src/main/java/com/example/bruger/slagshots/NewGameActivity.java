package com.example.bruger.slagshots;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
    private DatabaseReference mTemp;
    private DatabaseReference mGameRoomsRoot;
    private int[] mGamePin = new int[1];
    private DatabaseReference mPlayerTwo;
    private boolean PlayerTwoJoined = false;
    private boolean spilStartet = false;
    private boolean spilLukket;
    private TextView mSpillerNavne;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

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
        final String inputName = intent.getStringExtra("inputName");
        mSpillerNavne = (TextView) findViewById(R.id.textView2);
        opstilSpillerNavne(inputName, "Venter på Spiller");

        mDatabase = FirebaseDatabase.getInstance();
        mGameRoomsRoot = mDatabase.getReference("GameRooms");

        // Get a reference to the GameRoomCounter
        final DatabaseReference counterRef = mDatabase.getReference("GameRoomCounter");

        // Attach a listener to read the data at our posts reference
        counterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String counter = dataSnapshot.getValue().toString();
                int counterInt = Integer.parseInt(counter);
                mGamePin[0] = counterInt;
                counter = String.format("%06d", counterInt);
                gamePinView.setText(counter);

                //increment GameRoomCounter
                counterRef.setValue(counterInt+1);

                mGameRoom = mGameRoomsRoot.child("GameRoom"+mGamePin[0]);
                mGameRoom.child("PlayerOne").setValue(inputName);
                mGameRoom.child("PlayerTwo").setValue(-1);

                mPlayerTwo = mDatabase.getReference("GameRooms/GameRoom"+mGamePin[0]+"/PlayerTwo");
                mPlayerTwo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(spilLukket){
                            return;
                        }
                        String playerTwoName = dataSnapshot.getValue().toString();
                        if(checkPlayerJoin(playerTwoName)){
                            opstilSpillerNavne(inputName, playerTwoName);
                            PlayerTwoJoined = true;
                            Toast.makeText(getApplicationContext(),"En spiller har joinet. Starter spillet...", Toast.LENGTH_SHORT).show();
                            startSpil();
                        } else {
                            if(PlayerTwoJoined){
                                Toast.makeText(getApplicationContext(),"En spiller har forladt GameRoom", Toast.LENGTH_SHORT).show();
                            } else {PlayerTwoJoined = false;}

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Fejl med gamepin", Toast.LENGTH_SHORT).show();
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        lukGameRoom();
    }

    public void startSpil() {
        if(PlayerTwoJoined){
            spilStartet = true;
            Intent newGameIntent = new Intent(getApplicationContext(), GameActivity.class);
            newGameIntent.putExtra("GameroomName","GameRoom"+mGamePin[0]);
            newGameIntent.putExtra("isPlayerOne",true);
            startActivity(newGameIntent);
        } else {
            Toast.makeText(getApplicationContext(),"Mangler en modstander", Toast.LENGTH_SHORT).show();
        }
    }

    public void afbrydSpil() {
        spilLukket = true;
        lukGameRoom();
        finish();
    }

    public void lukGameRoom(){
        if(!spilStartet){
            mGameRoom.removeValue();
        }
    }

    public void opstilSpillerNavne(String playerOne, String playerTwo){
        if(!checkPlayerJoin(playerTwo)){
            mSpillerNavne.setText("1.    " + playerOne + '\n' + "2.    " + "Venter på Spiller");
        } else {
            mSpillerNavne.setText("1.    " + playerOne + '\n' + "2.    " + playerTwo);
        }

    }

    public boolean checkPlayerJoin(String playerTwo){
        if(playerTwo.equals("-1")){
            PlayerTwoJoined = false;
            return false;
        } else{
            PlayerTwoJoined = true;
            return true;
        }
    }

}
