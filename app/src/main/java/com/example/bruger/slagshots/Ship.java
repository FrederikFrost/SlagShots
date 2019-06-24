package com.example.bruger.slagshots;

import java.util.ArrayList;

public class Ship {
    private int length;
    private ArrayList<Integer> coords = new ArrayList<Integer>();
    private boolean row;

    public Ship(int length, boolean row, ArrayList<Integer> coords) {
        this.length = length;
        this.row = row;
        this.coords = coords;
    }

    public int getLength(){return length;}

    public boolean getRow(){return row;}

    public ArrayList<Integer> getCoords() {return coords;}
}
