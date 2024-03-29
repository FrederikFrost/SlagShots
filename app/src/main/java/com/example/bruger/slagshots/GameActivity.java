package com.example.bruger.slagshots;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
    private boolean playerOne = true;
    private boolean playerTwo = false;

    private String gameroomName;
    private FirebaseDatabase mDatabaseRoot;
    private DatabaseReference mGamerooms;
    private DatabaseReference mGameroom;
    private DatabaseReference checkInOne;
    private DatabaseReference checkInTwo;

    private boolean playerOneReady = false;
    private boolean playerTwoReady = false;

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
    private int nShipsHitPlayerOne = 0;
    private int nShipsHitPlayerTwo = 0;

    private boolean gameDone = false;
    private boolean playerLeftGame = false;

    private String state = "LOADING";

    private ValueEventListener playerOneBoardListener;
    private ValueEventListener playerTwoBoardListener;
    private ValueEventListener turnListener;
    private ValueEventListener playerLeftGameListener;

    private boolean bothConnected = false;
    private boolean progressDialogRunning = true;
    private boolean toastActivated = false;
    private Context context;

    private ArrayList<Integer> boardTemp;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //get context for toast messages
        context = getApplicationContext();

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
        //get own board
        boardTemp = (ArrayList<Integer>) intent.getExtras().getSerializable("Board");
        model = new GameModel(isPlayerOne);

        //get own board from intent
        if (isPlayerOne){
            model.playerOneBoard = convertFromArrayListToBoardField(boardTemp);
        } else {
            model.playerTwoBoard = convertFromArrayListToBoardField(boardTemp);
        }

        Log.i("Oliver","Got the following string from intent:"+gameroomName);
        Log.i("Oliver","I am player " + (isPlayerOne ?1:2));

        //get ref to database
        mDatabaseRoot = FirebaseDatabase.getInstance();
        mGamerooms = mDatabaseRoot.getReference("GameRooms");
        mGameroom = mGamerooms.child(gameroomName);
        checkInOne = mGameroom.child("CheckInOne");
        checkInTwo = mGameroom.child("CheckInTwo");

        //sets turn variable
        mGameroom.child("Turn").setValue(0);
        mGameroom.child("PlayerOnesBoard");
        mGameroom.child("PlayerTwosBoard");

        //upload board
        if (isPlayerOne) {
            mGameroom.child("PlayerOnesBoard").setValue(convertFromBoardFieldToArrayList(model.playerOneBoard));
            checkInOne.setValue("True");
        } else {
            mGameroom.child("PlayerTwosBoard").setValue(convertFromBoardFieldToArrayList(model.playerTwoBoard));
            checkInTwo.setValue("True");
        }

        final String[] playerOneName = new String[1];
        final String[] playerTwoName = new String[1];

        //pause screen while waiting for other to connect
        if (!bothConnected) {
            progressDialog = new ProgressDialog(this);
            // Setting Title
            progressDialog.setTitle("Venter på modstanderen");
            progressDialog.setMessage("Du kan aflyse spillet ved at trykke på tilbage knappen");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    Log.i("FINISH","Cancel wait finish");
                    finish();
                }
            });
            progressDialog.show(); // Display Progress Dialog
        }

        checkInOne.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue().toString().equals("True")) {
                        /*
                        playerOneReady = true;
                        bothConnected = playerOneReady && playerTwoReady;
                        if (bothConnected){
                            progressDialog.dismiss();
                            updateBoard(isPlayerOne);
                            updateBoard(!isPlayerOne);
                        }
                        */
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        checkInTwo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue().toString().equals("True")) {
                        /*
                        playerTwoReady = true;
                        bothConnected = playerOneReady && playerTwoReady;
                        if(bothConnected){
                            progressDialog.dismiss();
                            updateBoard(isPlayerOne);
                            updateBoard(!isPlayerOne);
                        }
                        */

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mGameroom.child("PlayerOne").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    playerOneName[0] = dataSnapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mGameroom.child("PlayerTwo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    playerTwoName[0] = dataSnapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //create listener that updates player one's board
        playerOneBoardListener = mGameroom.child("PlayerOnesBoard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){
                    playerOneReady = true;
                    bothConnected = playerOneReady && playerTwoReady;
                }

                if (!bothConnected){
                    return;
                }

                if (progressDialogRunning){
                    progressDialogRunning = false;
                    progressDialog.dismiss();
                    mGameroom.child("Turn").setValue(1);
                }

                //checks goal so not to display messages if game has ended
                checkGoal();
                if(gameDone || playerLeftGame){
                    return;
                }

                //converts data back to BoardField array
                GenericTypeIndicator<ArrayList<Integer>> t = new GenericTypeIndicator<ArrayList<Integer>>() {
                };
                ArrayList<Integer> temp = dataSnapshot.getValue(t);
                //updates the board
                model.playerOneBoard = convertFromArrayListToBoardField(temp);

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
                        if (toastActivated){
                            Toast.makeText(getApplicationContext(), "Du missede dit skud", Toast.LENGTH_SHORT).show();
                            mpSplash.start();
                        } else {
                            toastActivated = true;
                        }
                    }
                }
                Log.i("Oliver", "Updated model.playerTwoBoard");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //create listener that updates player two's board
        playerTwoBoardListener = mGameroom.child("PlayerTwosBoard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){
                    playerTwoReady = true;
                    bothConnected = playerOneReady && playerTwoReady;
                }

                if (!bothConnected){
                    return;
                }

                if (progressDialogRunning) {
                    progressDialogRunning = false;
                    progressDialog.dismiss();
                    mGameroom.child("Turn").setValue(1);
                }

                //checks goal so not to display messages if game has ended
                checkGoal();
                if(gameDone || playerLeftGame){
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
                        if (toastActivated) {
                            Toast.makeText(getApplicationContext(), "Du missede dit skud", Toast.LENGTH_SHORT).show();
                            mpSplash.start();
                        } else {
                            toastActivated = true;
                        }
                    }
                }


                Log.i("Oliver","Updated model.playerOneBoard");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //create listener for turn variable. This code will be run at the start of each turn
        turnListener = mGameroom.child("Turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(gameDone || playerLeftGame || !bothConnected){
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
                        Toast.makeText(getApplicationContext(),playerTwoName[0]+" har vundet!!", Toast.LENGTH_SHORT).show();
                    } else if (checkGoal == 1){
                        //one has won
                        Toast.makeText(getApplicationContext(), playerOneName[0] +" har vundet!!", Toast.LENGTH_SHORT).show();
                    }
                    gameDone = true;
                    Log.i("FINISH","Someone won finish");
                    finish();
                } else if(isPlayersTurn(isPlayerOne)){
                    if (bothConnected) {
                        Toast.makeText(getApplicationContext(), "Det er din tur!", Toast.LENGTH_SHORT).show();
                    }
                }


                Log.i("Oliver","We got the turn value "+turn);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //idk
            }
        });

        //Create listener that checks if a player left the game.
        playerLeftGameListener = mGameroom.child("PlayerLeftGame").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!playerLeftGame && !gameDone) {
                    if (dataSnapshot.exists() && dataSnapshot.getValue().toString().equals("TRUE")) {
                        Toast.makeText(getApplicationContext(), "Din modstander har forladt spillet. Lukker ned.", Toast.LENGTH_LONG).show();
                        playerLeftGame = true;
                        Log.i("FINISH","Modstander har forladt spillet finish");
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //arranging the layout and creating adapters

        //creating view for the smaller board, that is the players own board
        GridView gridView1 = (GridView) findViewById(R.id.gridView1);
        adapter2 = new GridViewAdapter(this, model, isPlayerOne, false);
        gridView1.setAdapter(adapter2);

        //creating the view for the bigger board, that is the enemy's board
        final GridView gridView2 = (GridView) findViewById(R.id.gridView2);
        final GridViewAdapter adapter = new GridViewAdapter(this, model, !isPlayerOne, true);
        gridView2.setAdapter(adapter);
        gridView2.setOnItemClickListener(getListener(adapter));

        //when pressing the fire button
        Button fireShot = (Button) findViewById(R.id.fireShot);
        fireShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check that it is the players turn
                if (!isPlayersTurn(isPlayerOne)) {
                    Toast.makeText(getApplicationContext(), "Det er ikke din tur - vent venligst", Toast.LENGTH_LONG).show();
                } else if (!positionSelected) {
                    Toast.makeText(getApplicationContext(), "Der er ikke valgt et felt at skyde på", Toast.LENGTH_LONG).show();
                } else if(model.getPlayersBoardfieldAtPosition(adapter.getSelectedPosition(),!isPlayerOne).getHit()) {
                    Toast.makeText(getApplicationContext(), "Der er allerede skudt her", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("SKUD", "Jeg affyrer skud.");

                    //updates the board
                    model.getPlayersBoardfieldAtPosition(adapter.getSelectedPosition(),!isPlayerOne).hit();

                    //converts and uploads the enemy's updated board to firebase
                    if (isPlayerOne) {
                        mGameroom.child("PlayerTwosBoard").setValue(convertFromBoardFieldToArrayList(model.playerTwoBoard));
                        mGameroom.child("Turn").setValue(2);
                    } else {
                        mGameroom.child("PlayerOnesBoard").setValue(convertFromBoardFieldToArrayList(model.playerOneBoard));
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Er du sikker på at du vil forlade spillet?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("FINISH","Are you sure you wanna exit finish");
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("Oliver",isPlayerOne+", destroying listeners");
        mGameroom.removeEventListener(playerOneBoardListener);
        mGameroom.removeEventListener(playerTwoBoardListener);
        mGameroom.removeEventListener(turnListener);
        mGameroom.removeEventListener(playerLeftGameListener);

        if (!playerLeftGame) { //if you are first player to exit game
            playerLeftGame = true;
            mGameroom.child("PlayerLeftGame").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mGameroom.child("PlayerLeftGame").setValue("TRUE");
                    if (!gameDone){
                        Toast.makeText(getApplicationContext(), "Du har forladt spillet.", Toast.LENGTH_LONG).show();
                    }
                    return;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else { // if last player to exit game
            //delete gameroom
            mGameroom.removeValue();
            return;
        }

        /*if(!gameDone && mGameroom.child("PlayerLeftGame").getKey().equals("FALSE")){
            gameDone = true;
            mGameroom.child("PlayerLeftGame").setValue("TRUE");
            Toast.makeText(getApplicationContext(),"Du har forladt spillet. Lukker ned.", Toast.LENGTH_LONG).show();
            mGameroom.removeValue();
            finish();
        } else if(!gameDone && mGameroom.child("PlayerLeftGame").getKey().equals("TRUE")){
            gameDone = true;
            Toast.makeText(getApplicationContext(),"Din modstander har forladt spillet. Lukker ned.", Toast.LENGTH_LONG).show();
            mGameroom.removeValue();
            finish();
        } else {
            finish();
        } */
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

    private boolean hasBeenHit(boolean player){
        int newNShipsHit = 0;
        BoardField[] playerBoard;
        if (player == playerOne) {
            playerBoard = model.playerOneBoard;
        } else {
            playerBoard = model.playerTwoBoard;
        }

        for (BoardField bf : playerBoard){
            if (bf.getShip() && bf.getHit()){
                newNShipsHit++;
            }
        }

        if (player == playerOne) {
            if (newNShipsHit > nShipsHitPlayerOne) {
                nShipsHitPlayerOne = newNShipsHit;
                return true;
            }
        } else {
            if (newNShipsHit > nShipsHitPlayerTwo) {
                nShipsHitPlayerTwo = newNShipsHit;
                return true;
            }
        }
        return false;
    }

    private void updateBoard(boolean player){
        if (player){
            //create listener that updates player one's board
            mGameroom.child("PlayerOnesBoard").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //converts data back to BoardField array
                    GenericTypeIndicator<ArrayList<Integer>> t = new GenericTypeIndicator<ArrayList<Integer>>() {
                    };
                    ArrayList<Integer> temp = dataSnapshot.getValue(t);
                    //updates the board
                    model.playerOneBoard = convertFromArrayListToBoardField(temp);

                    Log.i("Oliver", "Updated model.playerOneBoard");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            //create listener that updates player one's board
            mGameroom.child("PlayerTwoBoard").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //converts data back to BoardField array
                    GenericTypeIndicator<ArrayList<Integer>> t = new GenericTypeIndicator<ArrayList<Integer>>() {
                    };
                    ArrayList<Integer> temp = dataSnapshot.getValue(t);
                    //updates the board
                    model.playerTwoBoard = convertFromArrayListToBoardField(temp);

                    Log.i("Oliver", "Updated model.playerTwoBoard");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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
