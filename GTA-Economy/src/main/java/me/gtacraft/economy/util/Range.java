package me.gtacraft.economy.util;

/**
 * Created by Connor on 7/6/14. Designed for the GTA-Economy project.
 */

public class Range {

    private int min;
    private int max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int random() {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
}
