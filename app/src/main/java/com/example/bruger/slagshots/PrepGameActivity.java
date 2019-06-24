package com.example.bruger.slagshots;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PrepGameActivity extends AppCompatActivity {

    private GameModel model;
    private PrepGameAdapter mAdapter;
    private boolean isPlayerOne;
    private boolean positionSelected;
    private ArrayList<Integer> ships = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prep_game);
        isPlayerOne = getIntent().getExtras().getBoolean("isPlayerOne",false);
        model = new GameModel(isPlayerOne);

        GridView gridView = (GridView) findViewById(R.id.gridView3);
        mAdapter = new PrepGameAdapter(this, model, isPlayerOne);
        gridView.setAdapter(mAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gridViewControl(position);
            }
        });

        Button ready = (Button) findViewById(R.id.readyButton);
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra("isPlayerOne", isPlayerOne);
                intent.putExtra("Board",isPlayerOne? model.playerOneBoard:model.playerTwoBoard);
            }
        });

        ToggleButton delete = (ToggleButton) findViewById(R.id.delete_button);
        //delete.setOnCheckedChangeListener();

    }


    private void gridViewControl(int position) {
        Log.i("Place","\n\nStarter action\n");


        boolean invalid=false;
        if (!positionSelected) {
            positionSelected = true;
            mAdapter.setSelectedPosition(position);
            mAdapter.notifyDataSetChanged();
            Log.i("Place", "Jeg vælger første position");
        } else {
            if (legalPosition(position)) {
                Log.i("Place", "Positionen er godkendt");
                if (model.addShip(mAdapter.getSelectedPosition(),position)) {
                    positionSelected=false;
                    mAdapter.setSelectedPosition(-1);
                    mAdapter.notifyDataSetChanged();
                } else {
                    //TODO: Unregister ship
                    unRegisterShip(getShipLength(position,mAdapter.getSelectedPosition()));
                    Log.i("Place", "Placeres ikke, da der allerede fandtes et skib");

                    invalid = true;
                }
            } else {
                Log.i("Place", "Positionen er IKKE godkendt");

                invalid = true;
            }
        }
        if (invalid) {
            positionSelected = false;
            mAdapter.setSelectedPosition(-1);
            mAdapter.notifyDataSetChanged();
        }
        Log.i("Place", "Action slut \n");
    }

    public boolean legalPosition(int position) {
        //Set potential ship ends
        int lastPos = mAdapter.getSelectedPosition();
        int chosenPos = position;
        Log.i("Place", "LastPos: " + lastPos + "   chosenPos: " + chosenPos);

        //Booleans for checking the placement
        boolean notEqual = lastPos != chosenPos;
        boolean rowDis = (chosenPos <= lastPos+4)&&(lastPos-4 <= chosenPos);
        boolean colDis = (chosenPos <= lastPos+40)&&(lastPos-40 <= chosenPos);
        boolean sameRow = ((chosenPos%10) <= (lastPos%10)+4)&&((lastPos%10)-4 <= (chosenPos%10));
        boolean sameCol = lastPos%10 == chosenPos%10;

        boolean validRow = sameRow && rowDis;
        boolean validCol = sameCol && colDis;

        //checking placement
        if (!notEqual || !((sameRow && rowDis) || (sameCol && colDis))) {
            Log.i("Place", "Positionen er ikke godkendt i legalPosition");
            Toast.makeText(getApplicationContext(), "Invalid placement",Toast.LENGTH_LONG).show();
            return false;
        } else { //
            int shipLength = getShipLength(lastPos, chosenPos);
            if (registerShip(shipLength)) {
                return true;
            } else {
                return false;
            }
        }

    }

    public boolean registerShip(int shipLength) {
        int extraShip = 0;
        for (int p:ships) {
            if (p == 3) {
                extraShip++;
            }
        }

        for (int p:ships) {
            Log.i("Place", "Skib registreret på længde " + p);
        }
         if (ships == null) {
             ships.add(shipLength);
             Log.i("Place", "Skibet registreres");
             for (int p:ships) {
                 Log.i("Place", "Skib registreret på længde " + p);
             }
             return true;
         } else if (!ships.contains(shipLength)){
             ships.add(shipLength);
             Log.i("Place", "Skibet registreres");
             for (int p:ships) {
                 Log.i("Place", "Skib registreret på længde " + p);
             }
             return true;
         } else if(shipLength == 3 && extraShip == 1) {
             ships.add(shipLength);
             extraShip++;
             return true;
         } else{
             Log.i("Place", "Skibet er allerede registreret" );
             for (int p:ships) {
                 Log.i("Place", "Skib registreret på længde " + p);
             }
             return false;
         }
    }

    public int getShipLength(int start, int end) {
        boolean row = (end <= start+4)&&(start-4 <= end);

        return row? 1 + Math.abs(end-start): 1 + Math.abs((end-start)/10);
    }

    public void unRegisterShip(int shipLength){
        Log.i("Place", "Skibet afregistreres" );
        ships.remove(ships.indexOf(shipLength));
    }
}
