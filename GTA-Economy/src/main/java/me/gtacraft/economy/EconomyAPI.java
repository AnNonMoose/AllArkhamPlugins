package me.gtacraft.economy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import me.gtacraft.economy.database.SQLConnectionThread;
import me.gtacraft.economy.database.SQLVars;
import me.vaqxine.GPDA.ScoreboardAPI;

import org.bukkit.Bukkit;

/**
 * Created by Connor on 7/6/14. Designed for the GTA-Economy project.
 */

public class EconomyAPI {
    public static double getUserBalance(UUID uuid) {
        if (GTAEconomy.player_balances.containsKey(uuid)) {
            double amount = GTAEconomy.player_balances.get(uuid);
            if (amount < 0) {
                setUserBalance(uuid, 0);
                return 0;
            }

            return amount;
        }

        try {
            PreparedStatement statement = SQLConnectionThread.getConnection().prepareStatement(SQLVars.SELECT_PLAYER
                    .replace("%uuid%", uuid.toString()));
            statement.execute();

            ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException err) {
            err.printStackTrace();
            return 0.0;
        }
        return 0.0;
    }

    public static void setUserBalance(UUID uuid, double value) {
        if (GTAEconomy.player_balances.containsKey(uuid)) {
            GTAEconomy.player_balances.remove(uuid);
            GTAEconomy.player_balances.put(uuid, value);

            if (Bukkit.getPluginManager().getPlugin("GPDA") != null) {
                ScoreboardAPI.updateScoreboard(Bukkit.getOfflinePlayer(uuid).getPlayer(), false);
            }
        }
    }

    public static void pushPlayerDataToSQLSync(UUID uuid) {
        if (!(GTAEconomy.player_balances.containsKey(uuid)))
            return;


        Connection con = null;
        PreparedStatement pst = null;
        String query = SQLVars.UPDATE_PLAYER
                .replace("%balance%", GTAEconomy.player_balances.get(uuid)+"")
                .replace("%uuid%", uuid.toString());
        
        try {
            pst = SQLConnectionThread.getConnection().prepareStatement(query);
            pst.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            GTAEconomy.log.error(query, EconomyAPI.class);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }


        /*GTAEconomy.sql_query.add(SQLVars.UPDATE_PLAYER
                .replace("%balance%", GTAEconomy.player_balances.get(uuid)+"")
                .replace("%uuid%", uuid.toString()));*/
        GTAEconomy.player_balances.remove(uuid);
    }
    
    public static void modifyPlayerSQLEconomyData(UUID uuid, double x){
        Connection con = null;
        PreparedStatement pst = null;
        String query = SQLVars.UPDATE_PLAYER
                .replace("%balance%", "balance" + x) // balance=balance+x
                .replace("%uuid%", uuid.toString());
        
        try {
            pst = SQLConnectionThread.getConnection().prepareStatement(query);
            pst.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            GTAEconomy.log.error(query, EconomyAPI.class);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }
}
