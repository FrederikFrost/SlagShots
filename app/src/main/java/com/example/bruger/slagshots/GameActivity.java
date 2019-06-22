package com.example.bruger.slagshots;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Vibrator;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GameActivity extends AppCompatActivity {

    private GameModel model;
    private boolean positionSelected = false;
    private boolean isPlayerOne;

    private String gameroomName;
    private FirebaseDatabase mDatabaseRoot;
    private DatabaseReference mGamerooms;
    private DatabaseReference mGameroom;

    private ImageView mShipView;
    private ImageView mFireBallView;
    private Animation mAnimationShoot;
    private Animation mAnimationGotHit;

    private Vibrator vibrator;

    private MediaPlayer mpCannon;
    private MediaPlayer mpExplosion;
    private MediaPlayer mpSplash;

    GridViewAdapter adapter2;

    private int turn = 1;
    private int nShipsHit = 0;

    private boolean playerOne = true;
    private boolean playerTwo = false;

    private boolean gameDone = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        model = new GameModel();
        setContentView(R.layout.activity_game);

        //init vibrator
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        //init mediaplayer
        final MediaPlayer mpCannon = MediaPlayer.create(this, R.raw.cannon_new);
        final MediaPlayer mpExplosion = MediaPlayer.create(this, R.raw.explosion);
        final MediaPlayer mpSplash = MediaPlayer.create(this, R.raw.splash);

        //load animations
        mFireBallView = (ImageView) findViewById(R.id.fireball);
        mShipView = (ImageView) findViewById(R.id.slagskib);
        mAnimationShoot = AnimationUtils.loadAnimation(this, R.anim.animation_shoot);
        mAnimationGotHit = AnimationUtils.loadAnimation(this, R.anim.animation_got_hit);

        //get ref to database from newGameActivity through the intent
        Intent intent = getIntent();
        gameroomName = intent.getStringExtra("GameroomName");
        isPlayerOne = intent.getBooleanExtra("isPlayerOne",false);
        Log.i("Oliver","Got the following string from intent:"+gameroomName);
        Log.i("Oliver","I am player " + (isPlayerOne ?1:2));

        //get ref to database
        mDatabaseRoot = FirebaseDatabase.getInstance();
        mGamerooms = mDatabaseRoot.getReference("GameRooms");
        mGameroom = mGamerooms.child(gameroomName);

        //set values of game variables
        mGameroom.child("Turn").setValue(1);
        mGameroom.child("PlayerOnesBoard").setValue(convertFromBoardFieldToArrayList(model.playerOneBoard));
        mGameroom.child("PlayerTwosBoard").setValue(convertFromBoardFieldToArrayList(model.playerTwoBoard));

        //create listener that updates player one's board
        mGameroom.child("PlayerOnesBoard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(gameDone){
                    return;
                }

                //converts data back to BoardField array
                GenericTypeIndicator<ArrayList<Integer>> t = new GenericTypeIndicator<ArrayList<Integer>>() {};
                ArrayList<Integer> temp = dataSnapshot.getValue(t);
                //updates the board
                model.playerOneBoard = convertFromArrayListToBoardField(temp);

                //checking for player two
                if (isPlayerOne) {
                    //if you has been hit
                    if (hasBeenHit(playerOne)) {
                        Toast.makeText(getApplicationContext(), "Dit skib er blevet ramt!", Toast.LENGTH_SHORT).show();
                        vibrate();
                        mpExplosion.start();
                        mShipView.startAnimation(mAnimationGotHit);
                    } else {
                        Toast.makeText(getApplicationContext(), "Din modstander missede sit skud", Toast.LENGTH_SHORT).show();
                        mpSplash.start();
                    }
                } else {
                    //if enemy has been hit
                    if (hasBeenHit(playerOne)) {
                        Toast.makeText(getApplicationContext(), "Du ramte din modstanders skib!", Toast.LENGTH_SHORT).show();
                        vibrate();
                        mpExplosion.start();
                    } else {
                        Toast.makeText(getApplicationContext(), "Du missede dit skud", Toast.LENGTH_SHORT).show();
                        mpSplash.start();
                    }
                }
                Log.i("Oliver","Updated model.playerTwoBoard");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //create listener that updates player two's board
        mGameroom.child("PlayerTwosBoard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(gameDone){
                    return;
                }
                //converts data back to BoardField array
                GenericTypeIndicator<ArrayList<Integer>> t = new GenericTypeIndicator<ArrayList<Integer>>() {};
                ArrayList<Integer> temp = dataSnapshot.getValue(t);
                //updates the board
                model.playerTwoBoard = convertFromArrayListToBoardField(temp);

                //checking for player two
                if (!isPlayerOne) {
                    //if you has been hit
                    if (hasBeenHit(playerTwo)) {
                        Toast.makeText(getApplicationContext(), "Dit skib er blevet ramt!", Toast.LENGTH_SHORT).show();
                        vibrate();
                        mpExplosion.start();
                        mShipView.startAnimation(mAnimationGotHit);
                    } else {
                        Toast.makeText(getApplicationContext(), "Din modstander missede sit skud", Toast.LENGTH_SHORT).show();
                        mpSplash.start();
                    }
                } else {
                    //if enemy has been hit
                    if (hasBeenHit(playerTwo)) {
                        Toast.makeText(getApplicationContext(), "Du ramte din modstanders skib!", Toast.LENGTH_SHORT).show();
                        vibrate();
                        mpExplosion.start();
                    } else {
                        Toast.makeText(getApplicationContext(), "Du missede dit skud", Toast.LENGTH_SHORT).show();
                        mpSplash.start();
                    }
                }


                Log.i("Oliver","Updated model.playerOneBoard");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //create listener for turn variable. This code will be run at the start of each turn
        mGameroom.child("Turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(gameDone){
                    return;
                }
                //recieve turn variable
                turn = Integer.parseInt(dataSnapshot.getValue().toString());

                //update the small gamewindow
                if (adapter2 != null){
                    adapter2.notifyDataSetChanged();
                }

                //check for goal state
                int checkGoal = checkGoal();
                if (checkGoal != 0){
                    if (checkGoal == 2){
                        //two has won
                        Toast.makeText(getApplicationContext(),"Player two has won!", Toast.LENGTH_SHORT).show();
                    } else if (checkGoal == 1){
                        //one has won
                        Toast.makeText(getApplicationContext(),"Player one has won!", Toast.LENGTH_SHORT).show();
                    }
                    gameDone = true;
                    mGameroom.removeValue();
                    finish();
                }

                Log.i("Oliver","We got the turn value "+turn);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //idk
            }
        });

        //arranging the layout and creating adapters
        GridView gridView1 = (GridView) findViewById(R.id.gridView1);
        adapter2 = new GridViewAdapter(this, model, !isPlayerOne, false);
        gridView1.setAdapter(adapter2);

        final GridView gridView2 = (GridView) findViewById(R.id.gridView2);
        final GridViewAdapter adapter = new GridViewAdapter(this, model, isPlayerOne, true);
        gridView2.setAdapter(adapter);
        gridView2.setOnItemClickListener(getListener(adapter));

        //when pressing the fire button
        Button fireShot = (Button) findViewById(R.id.fireShot);
        fireShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check that it is the players turn
                if (!isPlayersTurn(isPlayerOne)){
                    Toast.makeText(getApplicationContext(),"Det er ikke din tur - vent venligst", Toast.LENGTH_LONG).show();
                } else if (!positionSelected) {
                    Toast.makeText(getApplicationContext(),"Der er ikke valgt et felt at skyde på", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("SKUD", "Jeg affyrer skud.");

                    //updates the board
                    model.getBoardfieldAtPosition(adapter.getSelectedPosition(), isPlayerOne).hit();

                    //converts and uploads the board to firebase
                    if (isPlayerOne) {
                        mGameroom.child("PlayerOnesBoard").setValue(convertFromBoardFieldToArrayList(model.playerTwoBoard));
                        mGameroom.child("Turn").setValue(2);
                    } else {
                        mGameroom.child("PlayerTwosBoard").setValue(convertFromBoardFieldToArrayList(model.playerOneBoard));
                        mGameroom.child("Turn").setValue(1);
                    }

                    //plays shoot animation
                    mFireBallView.setVisibility(View.VISIBLE);
                    mFireBallView.startAnimation(mAnimationShoot);
                    mFireBallView.setVisibility(View.INVISIBLE);

                    //play cannon sound
                    mpCannon.start();

                    //resets the selected position
                    adapter.setSelectedPosition(-1);
                    positionSelected = false;

                    //updates the big board
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @NonNull
    private OnItemClickListener getListener(final GridViewAdapter adapter) {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Log.i("SKUD", "Jeg sætter markerings positionen");
                positionSelected = true;
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();
            }
        };
    }

    private boolean isPlayersTurn(boolean isPlayerOne){
        //returns true if it is the given player's turn
        if (isPlayerOne){
            return (turn == 1);
        } else {
            return (turn == 2);
        }
    }

    public ArrayList<Integer> convertFromBoardFieldToArrayList(BoardField[] playerBoard){
        //converts from array to firebase-friendly data
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (BoardField bf: playerBoard){
            if (!bf.getHit() && !bf.getShip()){
                temp.add(0);
            } else if (bf.getHit() && !bf.getShip()){
                temp.add(1);
            } else if (!bf.getHit() && bf.getShip()){
                temp.add(2);
            } else if (bf.getHit() && bf.getShip()){
                temp.add(3);
            }
        }
        return temp;
    }

    public BoardField[] convertFromArrayListToBoardField(ArrayList<Integer> playerBoard){
        //converts from firebase-friendly data to array
        BoardField[] temp = new BoardField[100];
        for (int i=0; i<100; i++){
            if (playerBoard.get(i) == 0){
                temp[i] = new BoardField(false);
            } else if (playerBoard.get(i) == 1){
                temp[i] = new BoardField(false);
                temp[i].hit();
            } else if (playerBoard.get(i) == 2){
                temp[i] = new BoardField(false);
                temp[i].setShip(true);
            } else if (playerBoard.get(i) == 3){
                temp[i] = new BoardField(false);
                temp[i].hit();
                temp[i].setShip(true);
            }
        }
        return temp;
    }

    private int checkGoal(){
        //check if a player has won. 0 is that no one has won, 1 is player one has won and 2 is player two has won.
        boolean oneHasLost = true;
        for (BoardField bf: model.playerOneBoard){
            //if there is an unhit ship
            if (bf.getShip() && !bf.getHit()){
                oneHasLost = false;
                break;
            }
        }

        if (oneHasLost){
            Log.i("Oliver","Player two has won");
            return 2;
        }

        boolean twoHasLost = true;
        for (BoardField bf: model.playerTwoBoard){
            //if there is an unhit ship
            if (bf.getShip() && !bf.getHit()){
                twoHasLost = false;
                break;
            }
        }

        if (twoHasLost){
            Log.i("Oliver","Player one has won");
            return 1;
        }

        return 0;
    }

    private boolean hasBeenHit(boolean isPlayerOne){
        int newNShipsHit = 0;
        BoardField[] playerBoard;
        if (isPlayerOne) {
            playerBoard = model.playerOneBoard;
        } else {
            playerBoard = model.playerTwoBoard;
        }

        for (BoardField bf : playerBoard){
            if (bf.getShip() && bf.getHit()){
                newNShipsHit++;
            }
        }
        if (newNShipsHit>nShipsHit){
            nShipsHit = newNShipsHit;
            return true;
        }
        return false;
    }

    private void vibrate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(500);
        }
    }
}
