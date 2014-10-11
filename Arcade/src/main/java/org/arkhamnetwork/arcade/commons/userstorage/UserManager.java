/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.userstorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;
import org.arkhamnetwork.arcade.commons.async.Callback;
import org.arkhamnetwork.arcade.commons.event.ProfileLoadEvent;
import org.arkhamnetwork.arcade.commons.storage.ArcadeHashMap;
import org.arkhamnetwork.arcade.core.Arcade;
import org.arkhamnetwork.arcade.core.configuration.ArcadeConfiguration;
import org.arkhamnetwork.arcade.miniplugin.mysqlconnector.mySQLDatabaseConnection;
import org.arkhamnetwork.arcade.miniplugin.mysqlconnector.mySQLStatementStore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author devan_000
 */
public class UserManager implements Listener {

    private static Arcade plugin = Arcade.getInstance();
    private static ArcadeHashMap<UUID, PlayerProfile> cachedStoredUsers = new ArcadeHashMap<>();

    public static void registerUser(final UUID uuid,
            final Callback<PlayerProfile> callback) {
        if (cachedStoredUsers.containsKey(uuid)) {
            callback.done(cachedStoredUsers.get(uuid));
            return;
        }

        plugin.getServer().getScheduler()
                .runTaskAsynchronously(plugin, new Runnable() {
                    public void run() {
                        try {
                            if (!asyncPlayerDataExists(uuid)) {
                                asyncCreatePlayerData(uuid);
                            }

                            PlayerProfile done = asyncLoadPlayerData(uuid);

                            cachedStoredUsers.put(uuid, done);
                            callback.done(done);

                            plugin.getServer().getPluginManager()
                            .callEvent(new ProfileLoadEvent(done));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    public static void unregisterUser(final UUID uuid) {
        cachedStoredUsers.remove(uuid);
    }
    
    public static void asyncSetProfileData(UUID uuid, int credits, int score,
            List<PlayerRank> ranks, String lastSeen, int gamesPlayed)
            throws Exception {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = mySQLDatabaseConnection.getConnection()
                    .prepareStatement(mySQLStatementStore.updateUserStatement);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, credits);
            preparedStatement.setInt(3, score);
            preparedStatement.setString(4, ranks.toString().replace("[", "")
                    .replace("]", ""));
            preparedStatement.setString(5, lastSeen);
            preparedStatement.setInt(6, gamesPlayed);
            preparedStatement.setString(7, uuid.toString());
            preparedStatement.execute();

        } finally {
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
        }
    }

    private static boolean asyncPlayerDataExists(UUID uuid) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet results = null;

        try {
            preparedStatement = mySQLDatabaseConnection.getConnection()
                    .prepareStatement(mySQLStatementStore.getUserStatement);
            preparedStatement.setString(1, uuid.toString());
            results = preparedStatement.executeQuery();

            while (results.next()) {
                return true;
            }

            return false;
        } finally {
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }

            if (results != null && !results.isClosed()) {
                results.close();
            }
        }
    }

    private static void asyncCreatePlayerData(UUID uuid) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet results = null;

        try {
            preparedStatement = mySQLDatabaseConnection.getConnection()
                    .prepareStatement(
                            mySQLStatementStore.createUserDataStatement);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, 0);
            preparedStatement.setInt(3, 0);
            preparedStatement.setString(4, " ");
            preparedStatement.setString(5, " ");
            preparedStatement.setInt(6, 0);

            preparedStatement.execute();
        } finally {
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }

            if (results != null && !results.isClosed()) {
                results.close();
            }
        }
    }

    private static PlayerProfile asyncLoadPlayerData(UUID uuid)
            throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet results = null;

        try {
            preparedStatement = mySQLDatabaseConnection.getConnection()
                    .prepareStatement(mySQLStatementStore.getUserStatement);
            preparedStatement.setString(1, uuid.toString());
            results = preparedStatement.executeQuery();

            while (results.next()) {
                return new PlayerProfile(uuid, results.getInt("credits"),
                        results.getInt("score"), results.getString("ranks")
                        .split(","), results.getString("last_seen"),
                        results.getInt("games_played"));
            }

            return null;
        } finally {
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }

            if (results != null && !results.isClosed()) {
                results.close();
            }
        }
    }

    @EventHandler
    public void onProfileLoad(final ProfileLoadEvent event) {
        plugin.getServer().getScheduler()
                .runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        event.getProfile()
                        .updateData(
                                event.getProfile()
                                .getArcadeCreditBalance(),
                                event.getProfile().getScore(),
                                event.getProfile().getPlayerRanks(),
                                (ArcadeConfiguration.getServerName()
                                + "," + (int) (System
                                .currentTimeMillis() / 1000L)),
                                event.getProfile().getGamesPlayed());
                    }
                });
    }
}
