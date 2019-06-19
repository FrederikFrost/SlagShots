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
        getTestBoard(playerOne);
    }

    public int getCount() {
        return playerOne.length;
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

    public int getView(int pos) {

        BoardField temp = playerOne[pos];
        if (temp == null) {
            Log.i(TAG,DEBUGGER_STRING);
        }
        if (!temp.getHit()) {
            if (!temp.getShip()) {
                return Draw.BLANK.getValue();
            } else {
                return Draw.OWN_SHIP.getValue();
            }
        } else {
            if (!temp.getShip()) {
                return Draw.BLANK_HIT.getValue();
            } else {
                return Draw.ENEMY_SHIP_HIT.getValue();
            }
        }
    }
}
