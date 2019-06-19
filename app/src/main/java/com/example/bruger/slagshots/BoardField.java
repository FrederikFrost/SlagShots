package com.example.bruger.slagshots;

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
        isHit = true;
    }

    public void setShip(boolean isShip) {
        this.isShip = isShip;
    }

    public boolean getShip() {
        return isShip;
    }

    public boolean getHit() { return isHit; }
}
