package com.example.bruger.slagshots;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import org.w3c.dom.Text;

public class PrepGameActivity extends AppCompatActivity {

    private GameModel model;
    private PrepGameAdapter mAdapter;
    private boolean isPlayerOne;
    private boolean positionSelected;
    private boolean deleteShips = false;
    private boolean submarineIsPlaced = false;
    private boolean cruiserIsPlaced = false;
    private boolean submarineIsDeleted = false;
    private boolean cruiserIsDeleted = false;
    private ArrayList<Ship> registeredShips = new ArrayList<Ship>();
    private TextView shipsNotPlaced;
    private TextView destroyerShip;
    private TextView submarineShip;
    private TextView cruiserShip;
    private TextView battleshipShip;
    private TextView carrierShip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prep_game);
        isPlayerOne = getIntent().getExtras().getBoolean("isPlayerOne",false);
        model = new GameModel(isPlayerOne);

        GridView gridView = (GridView) findViewById(R.id.gridView3);
        mAdapter = new PrepGameAdapter(this, model,isPlayerOne);
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
                BoardField[] boardTemp = isPlayerOne ? model.playerOneBoard : model.playerTwoBoard;
                gameIntent.putExtra("Board",convertFromBoardFieldToArrayList(boardTemp));
                startActivity(gameIntent);
            }
        });

        final ToggleButton delete = (ToggleButton) findViewById(R.id.delete_button);
        delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deleteShips = true;
                    delete.setBackgroundResource(R.drawable.button_border_2);
                } else {
                    deleteShips = false;
                    delete.setBackgroundResource(R.drawable.button_border);
                }
            }
        });

        shipsNotPlaced = findViewById(R.id.nonplaced_ships);
        shipsNotPlaced.setText("Disse skibe er endnu ikke blevet placeret:");
        carrierShip = findViewById(R.id.carrier);
        destroyerShip = findViewById(R.id.destroyer);
        cruiserShip = findViewById(R.id.cruiser);
        battleshipShip = findViewById(R.id.battleship);
        submarineShip = findViewById(R.id.submarine);
        carrierShip.setText("Carrier (5 felter)");
        battleshipShip.setText("Battleship (4 felter)");
        cruiserShip.setText("Cruiser (3 felter)");
        submarineShip.setText("Submarine (3 felter)");
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
                        destroyerShip.setVisibility(View.INVISIBLE);
                        Toast.makeText(PrepGameActivity.this, "Din Destroyer (2 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    } else if (shipLength == 3 && !submarineIsPlaced) {
                        submarineShip.setVisibility(View.INVISIBLE);
                        submarineIsPlaced = true;
                        submarineIsDeleted = false;
                        Toast.makeText(PrepGameActivity.this, "Din Submarine (3 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    } else if (shipLength == 3 && submarineIsPlaced && !cruiserIsPlaced) {
                        cruiserShip.setVisibility(View.INVISIBLE);
                        cruiserIsPlaced = true;
                        cruiserIsDeleted = false;
                        Toast.makeText(PrepGameActivity.this, "Din Cruiser (3 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    } else if (shipLength == 4) {
                        battleshipShip.setVisibility(View.INVISIBLE);
                        Toast.makeText(PrepGameActivity.this, "Dit Battleship (4 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    } else if (shipLength == 5) {
                        carrierShip.setVisibility(View.INVISIBLE);
                        Toast.makeText(PrepGameActivity.this, "Din Carrier (5 felter) er nu placeret", Toast.LENGTH_SHORT).show();
                    }
                    if(carrierShip.getVisibility() == View.INVISIBLE && battleshipShip.getVisibility() == View.INVISIBLE && submarineShip.getVisibility() == View.INVISIBLE
                    && cruiserShip.getVisibility() == View.INVISIBLE && destroyerShip.getVisibility() == View.INVISIBLE) {
                        shipsNotPlaced.setText("Alle dine skibe er nu placeret! Tryk på KLAR");
                    }
                    registeredShips.add(ship);
                    positionSelected=false;
                    mAdapter.setSelectedPosition(-1);
                    mAdapter.notifyDataSetChanged();
                } else {
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
                if (model.getPlayersBoardfieldAtPosition(pos, isPlayerOne).equals(b)) {
                    temp = s;
                    break;
                }
            }
        }

        if (temp != null) {
            if(temp.getShipLength() == 2) {
                destroyerShip.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Din Destroyer (2 felter) er nu slettet", Toast.LENGTH_SHORT).show();
            }
            else  if(temp.getShipLength() == 3 && !submarineIsDeleted) {
                submarineIsDeleted = true;
                submarineIsPlaced = false;
                submarineShip.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Din Submarine (3 felter) er nu slettet", Toast.LENGTH_SHORT).show();
            }
            else  if(temp.getShipLength() == 3 && !cruiserIsDeleted) {
                cruiserIsDeleted = true;
                cruiserIsPlaced = false;
                cruiserShip.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Din Cruiser (3 felter) er nu slettet", Toast.LENGTH_SHORT).show();
            }
            else if(temp.getShipLength() == 4) {
                battleshipShip.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Dit Battleship (4 felter) er nu slettet", Toast.LENGTH_SHORT).show();
            }
            else if(temp.getShipLength() == 5) {
                carrierShip.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Din Carrier (5 felter) er nu slettet", Toast.LENGTH_SHORT).show();
            }
            if(carrierShip.getVisibility() == View.INVISIBLE || battleshipShip.getVisibility() == View.INVISIBLE || submarineShip.getVisibility() == View.INVISIBLE
            || cruiserShip.getVisibility() == View.INVISIBLE || destroyerShip.getVisibility() == View.INVISIBLE) {
                shipsNotPlaced.setText("Disse skibe mangler at blive placeret:");
            }
            model.deleteShip(temp.getCoords());
            registeredShips.remove(temp);
        } else Toast.makeText(getApplicationContext(),"Skibet du ønsker at slette, eksisterer ikke", Toast.LENGTH_SHORT).show();

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
            //Toast.makeText(getApplicationContext(), "Dette skib er allerede placeret, eller også har du placeret det forkert", Toast.LENGTH_SHORT).show();
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
                return true;
            } else {
                Log.i("Place", "Skibet er allerede registreret");
                return false;
            }
        }
    }

    public int getShipLength(int start, int end) {
        boolean row = (end <= start+4)&&(start-4 <= end);

        return row? 1 + Math.abs(end-start): 1 + Math.abs((end-start)/10);
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
