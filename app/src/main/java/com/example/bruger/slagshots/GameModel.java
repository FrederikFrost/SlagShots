package com.example.bruger.slagshots;

import android.text.BoringLayout;
import android.util.Log;

/**
 * Created by erknt on 18-06-2019.
 */

public class GameModel {

    public BoardField[] playerOne;
    public BoardField[] playerTwo;
    public final String TAG = "DEBUGGER";
    public final String DEBUGGER_STRING = "This object is null!";


    public GameModel() {
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

    public BoardField getBoardfieldAtPosition(int pos, boolean isPlayerOne) {
        return isPlayerOne ? playerOne[pos]:playerTwo[pos];
    }

    public void setBoardfieldAtPosition(BoardField boardField, int pos, boolean isPlayerOne) {
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

    public int getView(int pos, boolean isPlayerOne) {

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
