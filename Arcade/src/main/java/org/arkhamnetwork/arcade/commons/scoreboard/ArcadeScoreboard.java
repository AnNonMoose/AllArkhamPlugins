/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.scoreboard;

import org.arkhamnetwork.arcade.commons.storage.ArcadeHashMap;
import org.arkhamnetwork.arcade.commons.utils.MessageUtils;
import org.arkhamnetwork.arcade.core.Arcade;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author devan_000
 */
public class ArcadeScoreboard {

    private Scoreboard scoreboard;
    private ArcadeHashMap<Integer, String> storedLines = new ArcadeHashMap<>();

    public ArcadeScoreboard() {
        scoreboard = Arcade.getInstance().getServer().getScoreboardManager()
                .getNewScoreboard();
        scoreboard.registerNewObjective("Arcade", "Arcade");
    }

    public boolean hasLine(int lineID) {
        return storedLines.get(lineID) != null;
    }

    public void setSlot(DisplaySlot slot) {
        scoreboard.getObjective("Arcade").setDisplaySlot(slot);
    }

    public void setName(String name) {
        scoreboard.getObjective("Arcade").setDisplayName(
                MessageUtils.translateToColorCode(name));
    }

    public String getName() {
        return scoreboard.getObjective("Arcade").getDisplayName();
    }

    public void addLine(int id, String name, int scoreValue) {
        storedLines.put(id, name);
        scoreboard.getObjective("Arcade")
                .getScore(MessageUtils.translateToColorCode(name))
                .setScore(scoreValue);
    }

    public void removeLine(int id) {
        scoreboard.resetScores(storedLines.get(id));
        storedLines.remove(id);
    }

    public void updateLine(int id, String newName, int newScoreValue) {
        scoreboard.resetScores(storedLines.get(id));
        addLine(id, newName, newScoreValue);
    }

    public void setForPlayer(Player player) {
        player.setScoreboard(scoreboard);
    }
}
