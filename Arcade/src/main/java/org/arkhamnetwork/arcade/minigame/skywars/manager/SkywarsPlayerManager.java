/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.manager;

import org.arkhamnetwork.arcade.commons.kit.Kit;
import org.arkhamnetwork.arcade.commons.manager.Manager;
import org.arkhamnetwork.arcade.commons.pregame.PreGameManager;
import org.arkhamnetwork.arcade.commons.userstorage.PlayerRank;
import org.arkhamnetwork.arcade.commons.userstorage.UserManager;
import org.arkhamnetwork.arcade.commons.utils.BarUtils;
import org.arkhamnetwork.arcade.commons.utils.PlayerUtils;
import org.arkhamnetwork.arcade.core.Arcade;
import org.arkhamnetwork.arcade.core.configuration.ArcadeConfiguration;
import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;
import org.arkhamnetwork.arcade.minigame.skywars.struct.SkywarsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author devan_000
 */
public class SkywarsPlayerManager extends Manager {

    private static final SkyWars plugin = SkyWars.getSkywars();

    public static void handleLogin(AsyncPlayerPreLoginEvent event) {
        if (!Arcade.isServerOnline() || plugin.getCurrentStage() == null) {
            event.setKickMessage(ChatColor.GREEN + "[Arcade] " + ChatColor.RED
                    + "Server still setting up.");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if (plugin.getCurrentStage().getId() == 1) {
            event.setKickMessage(ChatColor.GREEN + "[Arcade] " + ChatColor.RED
                    + "That server is currently in-game.");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if (plugin.getCurrentStage().getId() == 2) {
            event.setKickMessage(ChatColor.GREEN + "[Arcade] " + ChatColor.RED
                    + "That server is currently restarting.");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }

        if (plugin.isFull()) {
            event.setKickMessage(ChatColor.GREEN + "[Arcade] " + ChatColor.BLUE
                    + "That game is full.");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }
    }

    public static void handleJoin(Player player) {
        if (plugin.getCurrentStage() == null) {
            return;
        }

        // Pre-Game (The only time players should be joining)
        if (plugin.getCurrentStage().getId() == 0) {
            plugin.broadcast(ChatColor.GREEN + player.getName()
                    + ChatColor.AQUA + " has joined the game. "
                    + ChatColor.YELLOW + "(" + (plugin.getPlayers().size() + 1)
                    + "/" + plugin.getCurrentMapData().getMaxPlayers() + ")");

            SkywarsPlayer skywarsPlayer = new SkywarsPlayer(player);
            plugin.getPlayers().put(player.getUniqueId(), skywarsPlayer);

            player.teleport(PreGameManager.getSpawnLocation());
            PlayerUtils.resetPlayer(player);
            BarUtils.setMessage(player, ChatColor.BLUE + "> " + ChatColor.GREEN
                    + "You are currently playing on " + ChatColor.AQUA
                    + ChatColor.UNDERLINE + ArcadeConfiguration.getServerName()
                    + ChatColor.RESET + ChatColor.BLUE + " <");
            for (String descriptionMessage : plugin.getGameDescriptionMessage()) {
                player.sendMessage(descriptionMessage);
            }
        }
    }

    public static void handleLeave(Player player, boolean kicked) {
        if (plugin.getCurrentStage() == null) {
            return;
        }

        plugin.getPlayers().remove(player.getUniqueId());
        UserManager.unregisterUser(player.getUniqueId());

        // If the player logs out in the pre-game stage.
        if (plugin.getCurrentStage().getId() == 0) {
            plugin.broadcast(ChatColor.GREEN + player.getName() + ChatColor.RED
                    + " has left the game. " + ChatColor.YELLOW + "("
                    + (plugin.getPlayers().size() - 1) + "/"
                    + plugin.getCurrentMapData().getMaxPlayers() + ")");
            return;
        }

        // If the player logs out in-game.
        if (plugin.getCurrentStage().getId() == 0) {
            plugin.broadcast(ChatColor.GREEN + player.getName() + ChatColor.RED
                    + " has died. " + ChatColor.YELLOW + "("
                    + (plugin.getPlayers().size() - 1) + "/"
                    + plugin.getCurrentMapData().getMaxPlayers() + ")");
            return;
        }

    }

    public static void attemptSelectKit(Kit kit, SkywarsPlayer player, boolean wasConfirmInventoryClick) {
        //You can only buy kits pre-game.
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 0) {
            return;
        }
        
        if (player.getKit()!= null && player.getKit().getName().equals(kit.getName())) {
            plugin.messagePlayer(player.getBukkitPlayer(), ChatColor.RED+ "You already have that kit equipped.");
            return;
        }
        
        //If the kit needs a rank.
        if (!kit.getRanksWithPermission().isEmpty()) {
            boolean hasPerms = false;
            for (PlayerRank playerRank : player.getUserProfile().getPlayerRanks()) {
                if (kit.getRanksWithPermission().contains(playerRank)) {
                    //The kit allowed use of one of the players ranks.
                    hasPerms = true;
                    break;
                }
            }
            //Return and send nope if they dont have perms.
            if (!hasPerms) {
                StringBuilder noPermBuilder = new StringBuilder();
                int currentCount = 0;
                for (PlayerRank rank : kit.getRanksWithPermission()) {
                    currentCount++;
                    if (rank.isVisibleInDescriptions()) {
                        noPermBuilder.append(rank.getRankName());

                        if (currentCount < kit.getRanksWithPermission().size()) {
                            noPermBuilder.append(", ");
                        }
                    }
                }
                plugin.messagePlayer(player.getBukkitPlayer(), ChatColor.RED + "You must have one of the following ranks to use that kit: " + ChatColor.YELLOW + noPermBuilder.toString());
                return;
            }
        }

        //Kit was free.
        if (kit.getCreditCost() <= 0) {
            selectKit(kit, player);
            return;
        }

        //It costs money, if the player dosent have enough return.
        if (player.getUserProfile().getArcadeCreditBalance() < kit.getCreditCost()) {
            plugin.messagePlayer(player.getBukkitPlayer(), ChatColor.GRAY + "You do not have enough " + ChatColor.GREEN + "Credits" + ChatColor.GRAY + ".");
            return;
        }

        //Now we have to start the buying process.
        if (!wasConfirmInventoryClick) {
            player.getBukkitPlayer().openInventory(kit.getBuyConfirmInventory());
            return;
        }

        player.getUserProfile().updateData(player.getUserProfile().getArcadeCreditBalance() - kit.getCreditCost(), player.getUserProfile().getScore(), player.getUserProfile().getPlayerRanks(), player.getUserProfile().getLastSeen(), player.getUserProfile().getGamesPlayed());
        plugin.messagePlayer(player.getBukkitPlayer(), ChatColor.YELLOW + "You purchased a use of the kit " + ChatColor.AQUA + "" + ChatColor.BOLD + kit.getName() + ChatColor.YELLOW + ".");
        plugin.messagePlayer(player.getBukkitPlayer(), ChatColor.GRAY + "You now have " + ChatColor.AQUA + player.getUserProfile().getArcadeCreditBalance() + " " + ChatColor.GREEN + "Credits" + ChatColor.GRAY + ".");

        selectKit(kit, player);
    }

    private static void selectKit(Kit kit, SkywarsPlayer player) {
        player.setKit(kit);

        PlayerUtils.resetPlayer(player.getBukkitPlayer());

        for (ItemStack stack : kit.getItems()) {
            player.getBukkitPlayer().getInventory().addItem(stack);
        }

        player.getBukkitPlayer().updateInventory();
        plugin.messagePlayer(player.getBukkitPlayer(), ChatColor.YELLOW + "You equipped the kit " + ChatColor.AQUA + "" + ChatColor.BOLD + kit.getName() + ChatColor.YELLOW + ".");
    }
}
