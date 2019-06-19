package com.example.bruger.slagshots;

/**
 * Created by erknt on 18-06-2019.
 */

public enum Draw {
    BLANK(0), BLANK_HIT(1), ENEMY_SHIP_HIT(2), OWN_SHIP(3);
    private int value;

    private Draw(int value) { this.value = value; }

    public int getValue() {
        return value;
    }
}
