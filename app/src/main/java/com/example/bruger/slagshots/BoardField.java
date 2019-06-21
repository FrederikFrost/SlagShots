package com.example.bruger.slagshots;

import android.util.Log;

/**
 * Created by erknt on 19-06-2019.
 */

public class BoardField {
    private boolean isShip;
    private boolean isHit;

    public BoardField(boolean isShip) {
        this.isShip = isShip;
        isHit = false;
    }

    public void hit() {
        Log.i("SKUD", "Jeg skydder!");
        isHit = true;
    }

    public void setShip(boolean isShip) {
        this.isShip = isShip;
    }

    public boolean getShip() {
        return isShip;
    }

    public boolean getHit() { return isHit; }

    public String toString(){
        String msg = "";
        if (isShip){
            msg = msg + "isShip";
        }
        if (isHit){
            msg = msg + "isHit";
        }
        return msg;
    }
}
