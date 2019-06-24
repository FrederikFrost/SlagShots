package com.example.bruger.slagshots;

import java.util.ArrayList;

public class Ship {
    private ArrayList<BoardField> coords = new ArrayList<BoardField>();
    private int shipLength;

    public Ship(int shipLength, ArrayList<BoardField> coords) {
        this.coords = coords;
        this.shipLength = shipLength;
    }

    public ArrayList<BoardField> getCoords() {return coords;}

    public int getShipLength() {return shipLength;}
}
