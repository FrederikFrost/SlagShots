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
import android.widget.CompoundButton;
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
    private boolean deleteShips = false;
    private ArrayList<Integer> ships = new ArrayList<Integer>();
    private boolean submarineIsPlaced = false;

    private ArrayList<Ship> registeredShips = new ArrayList<Ship>();

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
                if (!deleteShips) {
                    addShipSequence(position);
                } else {
                    deleteShipSequence(position);
                }
            }
        });

        Button ready = (Button) findViewById(R.id.readyButton);
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                Intent gameIntent = new Intent(PrepGameActivity.this, GameActivity.class);
                gameIntent.putExtra("GameroomName",intent.getStringExtra("GameroomName"));
                gameIntent.putExtra("isPlayerOne", isPlayerOne);
                BoardField[] boardTemp = isPlayerOne? model.playerOneBoard:model.playerTwoBoard;
                gameIntent.putExtra("Board",convertFromBoardFieldToArrayList(boardTemp));
                startActivity(gameIntent);
            }
        });

        ToggleButton delete = (ToggleButton) findViewById(R.id.delete_button);
        delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deleteShips = true;
                } else {
                    deleteShips = false;
                }
            }
        });

        TextView shipsNotPlaced = findViewById(R.id.nonplaced_ships);
        shipsNotPlaced.setText("Disse skibe er endnu ikke blevet placeret:");
        TextView carrierShip = findViewById(R.id.carrier);
        carrierShip.setText("Carrier (5 felter)");
        TextView battleshipShip = findViewById(R.id.battleship);
        battleshipShip.setText("Battleship (4 felter)");
        TextView cruiserShip = findViewById(R.id.cruiser);
        cruiserShip.setText("Cruiser (3 felter)");
        TextView submarineShip = findViewById(R.id.submarine);
        submarineShip.setText("Submarine (3 felter)");
        TextView destroyerShip = findViewById(R.id.destroyer);
        destroyerShip.setText("Destroyer (2 felter)");
    }


    private void addShipSequence(int position) {
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
                Ship ship = new Ship(getShipLength(mAdapter.getSelectedPosition(),position),model.addShip(mAdapter.getSelectedPosition(),position));
                if (ship.getCoords() != null) {
                    int shipLength = getShipLength(mAdapter.getSelectedPosition(), position);
                    if (shipLength == 2) {
                        TextView destroyerShip = findViewById(R.id.destroyer);
                        destroyerShip.setVisibility(View.INVISIBLE);
                        Toast.makeText(PrepGameActivity.this, "Din Destroyer (2 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    } else if (shipLength == 3 && submarineIsPlaced == false) {
                        TextView submarineShip = findViewById(R.id.submarine);
                        submarineShip.setVisibility(View.INVISIBLE);
                        submarineIsPlaced = true;
                        Toast.makeText(PrepGameActivity.this, "Din Submarine (3 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    } else if (shipLength == 3 && submarineIsPlaced == true) {
                        TextView cruiserShip = findViewById(R.id.cruiser);
                        cruiserShip.setVisibility(View.INVISIBLE);
                        Toast.makeText(PrepGameActivity.this, "Din Cruiser (3 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    } else if (shipLength == 4) {
                        TextView battleshipShip = findViewById(R.id.battleship);
                        battleshipShip.setVisibility(View.INVISIBLE);
                        Toast.makeText(PrepGameActivity.this, "Dit Battleship (4 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    } else if (shipLength == 5) {
                        TextView carrierShip = findViewById(R.id.carrier);
                        carrierShip.setVisibility(View.INVISIBLE);
                        Toast.makeText(PrepGameActivity.this, "Din Carrier (5 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    }
                    registeredShips.add(ship);
                    positionSelected=false;
                    mAdapter.setSelectedPosition(-1);
                    mAdapter.notifyDataSetChanged();
                } else {
                    //TODO: Unregister ship
                    //unRegisterShip(getShipLength(position,mAdapter.getSelectedPosition()));
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
            Toast.makeText(getApplicationContext(), "Dette skib er allerede placeret, eller også har du placeret det forkert",Toast.LENGTH_SHORT).show();
        }
        Log.i("Place", "Action slut \n");
    }

    private void deleteShipSequence(int pos) {

        Ship temp = null;
        for (Ship s: registeredShips) {
            for (BoardField b: s.getCoords()) {
                if (model.getBoardfieldAtPosition(pos).equals(b)) {
                    temp = s;
                    break;
                }
            }
        }

        if (temp != null) {
            model.deleteShip(temp.getCoords());
            registeredShips.remove(temp);
        } else Toast.makeText(getApplicationContext(),"Ship not found", Toast.LENGTH_LONG).show();

        mAdapter.notifyDataSetChanged();

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
            Toast.makeText(getApplicationContext(), "Dette skib er allerede placeret, eller også har du placeret det forkert", Toast.LENGTH_SHORT).show();
            return false;
        } else { //
            int shipLength = getShipLength(lastPos, chosenPos);
            if (checkRegistered(shipLength)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean checkRegistered(int shipLength) {


        for (int p:ships) {
            Log.i("Place", "Skib registreret på længde " + p);
        }
        if (registeredShips == null) {
             return true;
        } else {
            int extraShip = 0;
            boolean containsShip = false;
            Ship temp = null;
            for (Ship ship:registeredShips) {
                if (ship.getShipLength() == 3) {
                    extraShip++;
                }

                if(ship.getShipLength() == shipLength) {
                    containsShip = true;
                    temp = ship;
                }
            }

            if (!containsShip) {
                return true;
            } else if (temp.getShipLength() == 3 && extraShip == 1) {
                //extraShip++;
                return true;
            } else {
                Log.i("Place", "Skibet er allerede registreret");
                for (int p : ships) {
                    Log.i("Place", "Skib registreret på længde " + p);
                }
                return false;
            }
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
}
