package me.gtacraft.plugins.chests.parser;

import org.bukkit.Bukkit;

import java.util.Random;

/**
 * Created by Connor on 7/4/14. Designed for the GTA-Chests project.
 */

public class DataTypes {

    public static class RandomRange {

        private int from;
        private int to;

        public RandomRange(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public int roll() {
            Random rand = new Random();

            int randomNum = rand.nextInt((to - from) + 1) + from;

            return randomNum;
        }

        public String toString() {
            return "from: "+from+", to: "+to;
        }
    }

    public static class PercentChance {

        private double chance;

        public PercentChance(double chance) {
            this.chance = chance;
        }

        public boolean didWin(double modifier) {
            double rand = Math.random()*100;
            return ((chance+modifier) >= rand);
        }

        public boolean didWin(boolean verbose) {
            double rand = (Math.random()*100);
            Bukkit.broadcastMessage(chance + ">= " + rand);
            return (chance >= rand);
        }

        public String toString() {
            return chance+"%";
        }
    }
}
