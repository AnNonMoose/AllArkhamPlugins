package me.gtacraft.economy;

import me.gtacraft.economy.database.SQLConnectionThread;
import me.gtacraft.economy.database.SQLQueryThread;
import me.gtacraft.economy.database.SQLVars;
import me.gtacraft.economy.util.Util;
import me.gtacraft.plugins.safezone.util.SafezoneUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GTAEconomy extends JavaPlugin {

    public static Logger log = new Logger();

    private static GTAEconomy plugin;

    public static volatile CopyOnWriteArrayList<String> sql_query = new CopyOnWriteArrayList<>();
    public static volatile HashMap<UUID, Double> player_balances = new HashMap<>();

    public SQLQueryThread sql_worker;

    public static GTAEconomy get() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        FileConfiguration f = getConfig();
        SQLVars.SQL_HOST = f.getString("SQL.Host");
        SQLVars.SQL_DB = f.getString("SQL.DB");
        SQLVars.SQL_USER = f.getString("SQL.User");
        SQLVars.SQL_PASS = f.getString("SQL.Pass");
        SQLVars.SQL_PORT = f.getInt("SQL.Port");
        SQLVars.SQL_URL = SQLVars.formatSqlCall(f.getString("SQL.URL"));

        sql_worker = new SQLQueryThread();
        sql_worker.start();

        sql_query.add(SQLVars.formatSqlCall(SQLVars.CREATE_DATABASE));
        sql_query.add(SQLVars.formatSqlCall(SQLVars.CREATE_ECO_TABLE));
        doSQLWork();

        new EconomyListener();

        //hah no async u fool
        for (Player player : Bukkit.getOnlinePlayers()) {
            EconomyListener.get().onAsyncPreLogin(new AsyncPlayerPreLoginEvent(player.getName(), player.getAddress().getAddress(), player.getUniqueId()));
        }
    }

    public void doSQLWork() {
        for(String query : sql_query){
            Connection con = null;
            PreparedStatement pst = null;

            try {
                pst = SQLConnectionThread.getConnection().prepareStatement(query);
                pst.executeUpdate();

            } catch (Exception ex) {
                ex.printStackTrace();
                log.error(query, this.getClass());

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

            sql_query.remove(query);
        }
    }

    public boolean onCommand(final CommandSender sender_, final Command cmd, final String label, final String[] args) {
        final CommandSender player = sender_;
        Runnable async = new Runnable() {
            public void run() {
                if (cmd.getName().equalsIgnoreCase("eco")) {
                    if (!player.isOp()) {
                        player.sendMessage(Util.f("&cYou do not have permission to use this command!"));
                        return;
                    }

                    if (args.length < 2) {
                        player.sendMessage(Util.f("&eFormat: &7/"+label+" <set,give,take,reset,check> [user] (balance)"));
                        return;
                    }

                    String swap = args[0].toLowerCase();
                    Player find = Bukkit.getPlayer(args[1]);
                    if (find == null) {
                        player.sendMessage(Util.f("&e"+args[1]+" &cis not online!"));
                        return;
                    }

                    switch (swap) {
                        case "clean": {
                            try {
                                PreparedStatement ps = SQLConnectionThread.getConnection().prepareCall("SELECT * FROM `gta_economy`;");
                                ps.execute();

                                ResultSet set = ps.getResultSet();
                                int clean = 0;
                                while (set.next()) {
                                    String uuid = set.getString("uuid");
                                    double balance = set.getDouble("balance");

                                    if (balance < 0 || balance > 200000) {
                                        ++clean;
                                        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline()) {
                                            Player p = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer();
                                            p.sendMessage(ChatColor.RED+"Balance reset!");
                                            EconomyAPI.setUserBalance(p.getUniqueId(), 0);
                                        }
                                        sql_query.add("UPDATE `gta_economy` SET balance=0 WHERE uuid='"+uuid+"'");
                                    }
                                }

                                player.sendMessage("Cleaned: "+clean+" accounts!");
                            } catch  (Exception ex) {
                                player.sendMessage(ChatColor.RED+"Error!");
                                ex.printStackTrace();
                            }
                            return;
                        }
                        case "set": {
                            if (args.length < 3) {
                                player.sendMessage(Util.f("&eFormat: &7/"+label+" set [user] [balance]"));
                                return;
                            }

                            double set = 0.0;

                            try { set = Double.parseDouble(args[2]); } catch (NumberFormatException err) {
                                player.sendMessage(Util.f("&c"+args[2]+" is not a valid number!"));
                                return;
                            }

                            EconomyAPI.setUserBalance(find.getUniqueId(), set);
                            player.sendMessage(Util.f("&aUser &e"+find.getName()+" &aset to: &e$"+format.format(set)));
                            break;
                        }
                        case "give": {
                            if (args.length < 3) {
                                player.sendMessage(Util.f("&eFormat: &7/"+label+" give [user] [balance]"));
                                return;
                            }

                            double set = 0.0;
                            try { set = Double.parseDouble(args[2]); } catch (NumberFormatException err) {
                                player.sendMessage(Util.f("&c"+args[2]+" is not a valid number!"));
                                return;
                            }

                            double has = EconomyAPI.getUserBalance(find.getUniqueId());
                            double before = set;
                            set = set+has;
                            EconomyAPI.setUserBalance(find.getUniqueId(), set);

                            player.sendMessage(Util.f("&aAdded &e$"+format.format(before)+" &ato &e"+find.getName()+"&a's balance! Current balance: &e$"+format.format(set)));
                            break;
                        }
                        case "take": {
                            if (args.length < 3) {
                                player.sendMessage(Util.f("&eFormat: &7/"+label+" take [user] [balance]"));
                                return;
                            }

                            double take = 0.0;
                            try { take = Double.parseDouble(args[2]); } catch (NumberFormatException err) {
                                player.sendMessage(Util.f("&c"+args[2]+" is not a valid number!"));
                                return;
                            }

                            double has = EconomyAPI.getUserBalance(find.getUniqueId());
                            double before = take;
                            take = has-take;
                            EconomyAPI.setUserBalance(find.getUniqueId(), take);

                            player.sendMessage(Util.f("&aTook &e$"+format.format(before)+" &ato &e"+find.getName()+"&a's balance! Current balance: &e$"+format.format(take)));
                            break;
                        }
                        case "reset": {
                            EconomyAPI.setUserBalance(find.getUniqueId(), 0.0);
                            player.sendMessage(Util.f("&aReset &e"+find.getName()+"&a's balance back to &e$0.00"));
                            break;
                        }
                        case "check": {
                            double has = EconomyAPI.getUserBalance(find.getUniqueId());
                            player.sendMessage(Util.f("&e"+find.getName()+"&a's balance is: &e$"+format.format(has)));
                            break;
                        }
                    }
                } else if (cmd.getName().equalsIgnoreCase("bal")) {
                    if (!(player instanceof Player)) {
                        log.error("You must be a player to use this command!", getClass());
                        return;
                    }

                    Player p = (Player)player;
                    double bal = player_balances.get(p.getUniqueId());
                    String display = format.format(bal);
                    player.sendMessage(Util.f("&aYour balance: &e$"+display));
                    return;
                } else if (cmd.getName().equalsIgnoreCase("withdraw")) {
                    if (!(player instanceof Player)) {
                        log.error("You must be a player to use this command!", getClass());
                        return;
                    }

                    Player p = (Player)player;
                    if (Bukkit.getPluginManager().getPlugin("Safezone") == null) {
                        player.sendMessage(Util.f("&c&l(!) &cYou cannot use this command on a hub server!"));
                        return;
                    }

                    if (!(SafezoneUtil.isInSafeZone(p.getLocation()))) {
                        player.sendMessage(Util.f("&c&l(!) &cYou must be in a safe zone to withdraw cash."));
                        return;
                    }

                    boolean hasSpace = space(p);
                    if (args.length < 1) {
                        player.sendMessage(Util.f("&eFormat: &7/"+label+" [amount]"));
                        return;
                    }

                    double take = 0.0;
                    try { take = Math.abs(Double.parseDouble(args[0])); } catch (NumberFormatException err) {
                        player.sendMessage(Util.f("&c"+args[0]+" is not a valid number!"));
                        return;
                    }

                    if (take > 50000 || take <= 0) {
                        player.sendMessage(Util.f("&cYou cannot withdraw this amount of money!"));
                        return;
                    }

                    double cashHas = EconomyAPI.getUserBalance(p.getUniqueId());
                    if (cashHas < take) {
                        player.sendMessage(Util.f("&c&l(!) &cYou do not have enough money to complete this transaction!"));
                        return;
                    }

                    if (!(hasSpace)) {
                        player.sendMessage(Util.f("&c&l(!) &cYou do not have enough room in your inventory!"));
                        return;
                    }

                    EconomyAPI.setUserBalance(p.getUniqueId(), cashHas-take);

                    ItemStack stack = cash_item.clone();
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(Util.f("&a&l$"+take));
                    stack.setItemMeta(meta);

                    p.getInventory().addItem(stack);

                    player.sendMessage(Util.f("&a&l(!) &aWithdraw complete! You now have &e$"+format.format(take)+"&a in cash!"));
                }
                return;
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, async);
        return true;
    }

    static ItemStack cash_item = new ItemStack(Material.EMERALD);
    static {
        ItemMeta cashMeta = cash_item.getItemMeta();
        cashMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aMoney"));
        cashMeta.setLore(Arrays.asList("Directly deposited into bank account when picked up!"));
        cash_item.setItemMeta(cashMeta);
    }

    static boolean space(Player player) {
        return player.getInventory().firstEmpty() != -1;
    }

    static DecimalFormat format = new DecimalFormat("#,##0.00");

    public void onDisable() {
        saveDefaultConfig();

        try {
            for (UUID u : player_balances.keySet()) {
                PreparedStatement statement = SQLConnectionThread.getConnection().prepareStatement(SQLVars.UPDATE_PLAYER
                        .replace("%balance%", player_balances.get(u)+"")
                        .replace("%uuid%", u.toString()));
                statement.execute();
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }
}
