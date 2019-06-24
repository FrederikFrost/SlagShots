package com.example.bruger.slagshots;

import android.text.BoringLayout;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by erknt on 18-06-2019.
 */

public class GameModel {

    public BoardField[] playerTwoBoard;
    public BoardField[] playerOneBoard;

    public final String TAG = "DEBUGGER";
    public final String DEBUGGER_STRING = "This object is null!";
    private boolean isPlayerOne;


    public GameModel(boolean isPlayerOne) {
        this.isPlayerOne = isPlayerOne;
        Log.i(TAG, "I made it in the GameModel constructer");
        playerTwoBoard = new BoardField[100];
        createBoardField(playerTwoBoard);
       // getTestBoard(playerTwoBoard);
        playerOneBoard = new BoardField[100];
        createBoardField(playerOneBoard);
        //getTestBoard2(playerOneBoard);
    }

    public int getCount() {
        return playerTwoBoard.length;
    }

    public BoardField getPlayersBoardfieldAtPosition(int pos,boolean player) {
        return player ? playerOneBoard[pos]: playerTwoBoard[pos];
    }

    public void setBoardfieldAtPosition(BoardField boardField, int pos) {
        Log.i(TAG,"I set a boardfield");
        if (isPlayerOne){
            playerOneBoard[pos] = boardField;
        } else {
            playerTwoBoard[pos] = boardField;
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
        board[12].setShip(true);
        board[13].setShip(true);
        board[14].setShip(true);
        board[15].setShip(true);
        board[15].hit();
        Log.i(TAG, "I inserted ship and shots");
    }

    public void getTestBoard2(BoardField[] board) {
            board[55].hit();
            board[1].setShip(true);
            board[2].setShip(true);
            board[12].setShip(true);
            board[19].setShip(true);
            board[19].hit();
            Log.i(TAG, "I inserted ship and shots");
    }

    public int getView(int pos, boolean player) {

        BoardField boardField = player ? playerOneBoard[pos]: playerTwoBoard[pos];
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

    public ArrayList<BoardField> addShip(int start, int end) {
        //TODO: add ship
        Log.i("Place", "Skibet placeres");
        BoardField[] board = isPlayerOne? playerOneBoard:playerTwoBoard; //TODO: Maybe change this..?
        ArrayList<BoardField> ship = new ArrayList<BoardField>();
        boolean row = Math.abs(start - end) < 10;
        int count = row ? 1:10;

        for(int i = Math.min(start,end); i <= Math.max(start,end);) {
            BoardField temp = getPlayersBoardfieldAtPosition(i,isPlayerOne);
            if (!temp.getShip()) {
                Log.i("Place", "Tilføjer ship part på " + i);
                temp.setShip(true);
                ship.add(temp);
            } else {
                Log.i("Place", "Der var allerede et skib på " + i);
                deleteShip(ship);
                return null;
            }

            i = i+count;
        }

        return ship;
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
                msg = msg + (playerOneBoard[i+e].toString()+" ");
            }

            Log.i("Oliver",msg);
        }
    }

}
