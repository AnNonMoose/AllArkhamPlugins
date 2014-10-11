package me.gtacraft.cars.controllers;

import net.minecraft.server.v1_7_R2.*;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by tacticalsk8er on 4/29/14.
 */
public class ScoreboardController {

    public static void setCarScoreboard(Player player, String carName) {
        if(player == null || !player.isOnline()) {
            return;
        }

        ScoreboardObjective scoreboardObjective;
        Scoreboard scoreboard = new Scoreboard();
        PlayerConnection playerConnection;
        CraftPlayer craftPlayer = (CraftPlayer) player;

        scoreboardObjective = scoreboard.registerObjective(carName, new ScoreboardBaseCriteria("dummy"));
        scoreboardObjective.setDisplayName("");
        playerConnection = craftPlayer.getHandle().playerConnection;
        ScoreboardScore playerCarName = new ScoreboardScore(scoreboard, scoreboardObjective, player.getName());
    }
}
