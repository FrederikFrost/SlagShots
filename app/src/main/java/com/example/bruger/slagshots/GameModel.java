package com.example.bruger.slagshots;

import android.text.BoringLayout;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by erknt on 18-06-2019.
 */

public class GameModel {

    public BoardField[] playerOne;
    public BoardField[] playerTwo;
    public final String TAG = "DEBUGGER";
    public final String DEBUGGER_STRING = "This object is null!";
    private boolean isPlayerOne;


    public GameModel(boolean isPlayerOne) {
        this.isPlayerOne = isPlayerOne;
        Log.i(TAG, "I made it in the GameModel constructer");
        playerOne = new BoardField[100];
        createBoardField(playerOne);
        //getTestBoard(playerOne);
        playerTwo = new BoardField[100];
        createBoardField(playerTwo);
        //getTestBoard2(playerTwo);
    }

    public int getCount() {
        return playerOne.length;
    }

    public BoardField getBoardfieldAtPosition(int pos) {
        return isPlayerOne ? playerOne[pos]:playerTwo[pos];
    }

    public void setBoardfieldAtPosition(BoardField boardField, int pos) {
        Log.i(TAG,"I set a boardfield");
        if (isPlayerOne){
            playerOne[pos] = boardField;
        } else {
            playerTwo[pos] = boardField;
        }
    }

    public void createBoardField(BoardField[] board) {
        for (int i = 0; i < board.length;++i) {
            board[i] = new BoardField(false);
        }
        Log.i(TAG, "I created a board");
    }

    public void getTestBoard(BoardField[] board) {
        board[3].hit();
        board[14].setShip(true);
        board[15].setShip(true);
        board[15].hit();
        Log.i(TAG, "I inserted ship and shots");
    }

    public void getTestBoard2(BoardField[] board) {
            board[55].hit();
            board[12].setShip(true);
            board[19].setShip(true);
            board[19].hit();
            Log.i(TAG, "I inserted ship and shots");
    }

    public int getView(int pos) {

        BoardField boardField = isPlayerOne ? playerOne[pos]:playerTwo[pos];
        if (boardField == null) {
            Log.i(TAG,DEBUGGER_STRING);
        }
        if (!boardField.getHit()) {
            if (!boardField.getShip()) {
                return Draw.BLANK.getValue();
            } else {
                return Draw.OWN_SHIP.getValue();
            }
        } else {
            if (!boardField.getShip()) {
                return Draw.BLANK_HIT.getValue();
            } else {
                return Draw.ENEMY_SHIP_HIT.getValue();
            }
        }
    }

    public boolean addShip(int start, int end) {
        //TODO: add ship
        Log.i("Place", "Skibet placeres");
        BoardField[] board = isPlayerOne? playerOne:playerTwo; //TODO: Maybe change this..?
        ArrayList<BoardField> ship = new ArrayList<BoardField>();
        boolean row = Math.abs(start - end) < 10;
        int count = row ? 1:10;

        for(int i = Math.min(start,end); i <= Math.max(start,end);) {
            BoardField temp = getBoardfieldAtPosition(i);
            if (!temp.getShip()) {
                Log.i("Place", "Tilføjer ship part på " + i);
                temp.setShip(true);
                ship.add(temp);
            } else {
                Log.i("Place", "Der var allerede et skib på " + i);
                deleteShip(ship);
                return false;
            }

            i = i+count;
        }

        return true;
    }

    public void deleteShip(ArrayList<BoardField> ship) {
        /*
        for (BoardField temp:ship) {
            if (ship.size()!=1) {
                temp.setShip(false);
                ship.remove(temp);
            } else {
                temp.setShip(false);
                ship.clear();
            }
        }

        BoardField temp;
        for (int i = 0; i < ship.size(); ++i) {
            temp = ship.get(i);
            temp.setShip(false);
            ship.remove(temp);
        }*/
        for (BoardField temp:ship) {
            temp.setShip(false);
        }
    }

    public void printBoard(){
        for (int i=0; i<100; i=i+10){
            String msg = "";
            for (int e=0; e<10; e++){
                msg = msg + (playerTwo[i+e].toString()+" ");
            }

            Log.i("Oliver",msg);
        }
    }

}
