package me.gtacraft.plugins.gangs.commands;

import me.gtacraft.plugins.gangs.GTAGangs;
import me.gtacraft.plugins.gangs.Gang;
import me.gtacraft.plugins.gangs.GangInviteManager;
import me.gtacraft.plugins.gangs.GangMember;
import me.gtacraft.plugins.gangs.database.SQLVars;
import me.gtacraft.plugins.gangs.enumeration.GangRole;
import me.gtacraft.plugins.gangs.util.Formatting;
import me.gtacraft.plugins.gangs.util.LocationUtil;
import me.gtacraft.plugins.gangs.util.MessageType;
import me.gtacraft.plugins.gangs.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.print.DocFlavor;

/**
 * Created by Connor on 6/30/14. Designed for the GTA-Factions project.
 */

public class GangStaffCommands {

    public GangStaffCommands() {
        CommandHandler.getInstance().registerCommandChannel(this);
    }

    @CommandHandler.CommandContext(name="sethideout", aliases={"sh", "sethome"}, help = "&5&l/gang sethideout &e- &fSet your gangs hideout at your location")
    public void setHideoutCommand(final Player player, String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                if (member.getGang() == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be in a gang to set the hideout!")));
                    return;
                }

                if (member.getRole().getPermissionLevel() == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be a homie or the leader in your gang to set the hideout!")));
                    return;
                }

                Gang gang = member.getGang();
                Location pLoc = player.getLocation();
                String serialized = LocationUtil.fromLocation(pLoc);

                GTAGangs.sql_query.add(SQLVars.SET_GANG_HIDEOUT.replace("%hideout%", serialized).replace("%name%", gang.getName()));
                gang.setHideout(pLoc);

                String fName = pLoc.getBlockX()+", "+pLoc.getBlockY()+", "+pLoc.getBlockZ();
                player.sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "You set your gangs hideout to: &7"+fName+"&f!")));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="invite", aliases={"i"}, help="&5&l/gang invite [player] &e- &fInvite the given player to your gang")
    public void invitePlayerCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                if (member.getGang() == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be in a gang to invite a member!")));
                    return;
                }

                if (member.getRole().getPermissionLevel() == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be a homie or the leader in your gang to invite a member!")));
                    return;
                }

                if (args.length == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Please specify a player to invite!")));
                    return;
                }

                Player invited = Bukkit.getPlayer(args[0]);
                if (invited == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "No such player with the name &7"+args[0]+" &fexists!")));
                    return;
                }

                GangMember invitedMember = GangMember.fromPlayer(invited);
                if (invitedMember.getGang() != null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "This player is already in a gang!")));
                    return;
                }

                if (!(invited.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You cannot invited this player because they do not have permission to join!")));
                    return;
                }

                GangInviteManager.addPlayerToInvites(invited, member.getGang());
                invited.sendMessage(Formatting.formatMessage(MessageType.INFO.getPattern().replace("%m%", "&7"+player.getName()+" &fhas invited you to &7"+member.getGang().getName()+"&f!")));
                player.sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "You invited &7"+invited.getName()+"&f to your gang!")));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="kick",aliases={"kickplayer"},help="&5&l/gang kick [player] &e- &fKick the given player from your gang")
    public void kickPlayerCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                if (member.getGang() == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be in a gang to kick a player!")));
                    return;
                }

                if (member.getRole().getPermissionLevel() == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be a homie or the leader in your gang to kick a player!")));
                    return;
                }

                if (args.length == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Please specify a player to kick!")));
                    return;
                }

                Gang gang = member.getGang();
                OfflinePlayer kick = Bukkit.getOfflinePlayer(args[0]);
                if (!(kick.hasPlayedBefore())) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "The given player does not exist!")));
                    return;
                }

                if (!gang.getAllMembers().contains(kick)) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "The given player is not in your gang!")));
                    return;
                }

                int permLevel;
                if (kick.isOnline())
                    permLevel = GangMember.fromPlayer(kick).getRole().getPermissionLevel();
                else
                    permLevel = SQLVars.getPermissionLevel(kick);

                if (permLevel >= member.getRole().getPermissionLevel()) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You can only kick a player who is a lower rank than you!")));
                    return;
                }

                gang.getAllMembers().remove(kick);
                gang.getOnlineMembers().remove(GangMember.fromPlayer(kick));

                GTAGangs.sql_query.add(SQLVars.REMOVE_PLAYER_FROM_GANG
                        .replace("%uuid%", kick.getUniqueId().toString()));

                if (kick.isOnline()) {
                    kick.getPlayer().sendMessage(Formatting.formatMessage(MessageType.INFO.getPattern().replace("%m%", "You have been kicked from your gang by &7"+player.getName())));

                    GangMember edit = GangMember.fromPlayer(kick);
                    edit.setGang(null);
                    edit.setRole(null);
                }

                player.sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "You kicked &7"+kick.getName()+" &ffrom the faction!")));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="promote",aliases={"homie"},help="&5&l/gang promote [player] &e- &fPromote the given player to a homie in your faction")
    public void promotePlayerCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                if (member.getGang() == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be in a gang to promote a player!")));
                    return;
                }

                if (member.getRole().getPermissionLevel() == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be a homie or the leader in your gang to promote a player!")));
                    return;
                }

                if (args.length == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Please specify a player to promote!")));
                    return;
                }

                Gang gang = member.getGang();
                OfflinePlayer promote = Bukkit.getOfflinePlayer(args[0]);
                if (!(promote.hasPlayedBefore())) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "The given player does not exist!")));
                    return;
                }

                if (!gang.getAllMembers().contains(promote)) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "The given player is not in your gang!")));
                    return;
                }

                int permLevel;
                if (promote.isOnline())
                    permLevel = GangMember.fromPlayer(promote).getRole().getPermissionLevel();
                else
                    permLevel = SQLVars.getPermissionLevel(promote);

                if (permLevel != 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You can only promote a player with the rank member!")));
                    return;
                }

                GTAGangs.sql_query.add(SQLVars.UPDATE_PLAYER.replace("%gang%", gang.getName()).replace("%role%", 1+"").replace("%uuid%", promote.getUniqueId().toString()));
                if (promote.isOnline()) {
                    GangMember.fromPlayer(promote).setRole(GangRole.HOMIE);
                    promote.getPlayer().sendMessage(Formatting.formatMessage(MessageType.INFO.getPattern().replace("%m%", "You have been promoted in your gang!")));
                }

                player.sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "You have promoted &7"+promote.getName()+" &fto a homie in your faction!")));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="demote",aliases={"fire"},help="&5&l/gang demote [player] &e- &fDemote the given player to a member in your faction")
    public void demotePlayerCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                if (member.getGang() == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be in a gang to demote a player!")));
                    return;
                }

                if (member.getRole().getPermissionLevel() != 2) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be the leader in your gang to demote a player!")));
                    return;
                }

                if (args.length == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Please specify a player to promote!")));
                    return;
                }

                Gang gang = member.getGang();
                OfflinePlayer promote = Bukkit.getOfflinePlayer(args[0]);
                if (!(promote.hasPlayedBefore())) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "The given player does not exist!")));
                    return;
                }

                if (!gang.getAllMembers().contains(promote)) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "The given player is not in your gang!")));
                    return;
                }

                int permLevel;
                if (promote.isOnline())
                    permLevel = GangMember.fromPlayer(promote).getRole().getPermissionLevel();
                else
                    permLevel = SQLVars.getPermissionLevel(promote);

                if (permLevel != 1) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You can only demote a player with the rank homie!")));
                    return;
                }

                GTAGangs.sql_query.add(SQLVars.UPDATE_PLAYER.replace("%gang%", gang.getName()).replace("%role%", 0+"").replace("%uuid%", promote.getUniqueId().toString()));
                if (promote.isOnline()) {
                    GangMember.fromPlayer(promote).setRole(GangRole.MEMBER);
                    promote.getPlayer().sendMessage(Formatting.formatMessage(MessageType.INFO.getPattern().replace("%m%", "You have been demoted in your gang! >;(")));
                }

                player.sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "You have demoted &7"+promote.getName()+" &fback to a member!")));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="friendlyfire", aliases={"ff", "togglefriendlyfire", "setfriendlyfire"},help="&5&l/gang friendlyfire [on/off/toggle] &e- &fSet friendly fire either on or off")
    public void friendlyFireCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                if (member.getGang() == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be in a gang to set friendly fire!")));
                    return;
                }

                if (member.getRole().getPermissionLevel() == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be a homie or higher to set friendly fire!")));
                    return;
                }

                if (args.length == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Please specify either yes or no to friendly fire!")));
                    return;
                }

                Gang gang = member.getGang();
                String ff = args[0];
                switch (ff.toLowerCase()) {
                    case "true":
                    case "on":
                    case "yes": { //on
                        gang.setFriendlyFire(true);
                        break;
                    }
                    case "false":
                    case "off":
                    case "no":  { //off
                        gang.setFriendlyFire(false);
                        break;
                    }
                    case "toggle": { //toggle
                        gang.setFriendlyFire(!gang.isFriendlyFire());
                        break;
                    }
                    default: { //none
                        player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Please specify either yes/no or toggle!")));
                        return;
                    }
                }

                player.sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "Friendly fire toggled: &7"+(gang.isFriendlyFire() ? "on" : "off")+"&f!")));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }
}
