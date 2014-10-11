/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.userstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

/**
 *
 * @author devan_000
 */
public class PlayerProfile {

    @Getter
    private UUID playerUUID;
    @Getter
    private int arcadeCreditBalance;
    @Getter
    private int score;
    @Getter
    private List<PlayerRank> playerRanks;
    @Getter
    private String lastSeen;
    @Getter
    private int gamesPlayed;

    public PlayerProfile(UUID playerUUID, int arcadeCreditBalance, int score,
            String[] ranks, String lastSeen, int gamesPlayed) {
        this.playerUUID = playerUUID;
        this.arcadeCreditBalance = arcadeCreditBalance;
        this.playerRanks = new ArrayList<>();

        for (String rank : ranks) {
            if (rank == null || rank.equals("") || rank.equals(" ")) {
                continue;
            }
            PlayerRank foundRank = PlayerRank.valueOf(rank);
            if (foundRank != null) {
                this.playerRanks.add(foundRank);
            }
        }

        this.score = score;
        this.lastSeen = lastSeen;
        this.gamesPlayed = gamesPlayed;
    }

    public void updateData(int arcadeCreditBalance, int score,
            List<PlayerRank> playerRanks, String lastSeen, int gamesPlayed) {
        this.arcadeCreditBalance = arcadeCreditBalance;
        this.score = score;
        this.playerRanks = playerRanks;
        this.lastSeen = lastSeen;
        this.gamesPlayed = gamesPlayed;

        try {
            UserManager.asyncSetProfileData(playerUUID, arcadeCreditBalance,
                    score, playerRanks, lastSeen, gamesPlayed);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
