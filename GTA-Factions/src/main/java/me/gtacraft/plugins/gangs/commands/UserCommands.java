package me.gtacraft.plugins.gangs.commands;

import com.google.common.collect.Lists;
import me.gtacraft.plugins.gangs.*;
import me.gtacraft.plugins.gangs.database.SQLVars;
import me.gtacraft.plugins.gangs.enumeration.GangRole;
import me.gtacraft.plugins.gangs.listeners.GTAMovementListener;
import me.gtacraft.plugins.gangs.util.Formatting;
import me.gtacraft.plugins.gangs.util.MessageType;
import me.gtacraft.plugins.gangs.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 6/30/14. Designed for the GTA-Factions project.
 */

public class UserCommands {

    public UserCommands() {
        CommandHandler.getInstance().registerCommandChannel(this);
    }

    @CommandHandler.CommandContext(name="create",aliases={"creategang", "newgang"},help="&5&l/gang create [name] &e- &fCreate a gang with the given name")
    public void createGangCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            @Override
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                if (args.length == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Please specify a gang name!")));
                    return;
                }

                if (member.getGang() != null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must leave your current gang in order to create a new one!")));
                    return;
                }

                if (GangManager.getGang(args[0]) != null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "A gang with the name &7"+args[0]+" &falready exists!")));
                    return;
                }

                if (args[0].length() > 10) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You cannot have a gang name longer than 10 characters!")));
                    return;
                }

                Gang create = new Gang(new ArrayList<OfflinePlayer>(), args[0], 0);
                create.getOnlineMembers().add(member);
                create.getAllMembers().add(player);

                GTAGangs.sql_query.add(SQLVars.INSERT_NEW_GANG.replace("%name%", args[0])
                        .replace("%members%", Util.fromMembers(create.getAllMembers()))
                        .replace("%friendly_fire%", "0"));

                member.setRole(GangRole.LEADER);
                member.setGang(create);

                GTAGangs.sql_query.add(SQLVars.UPDATE_PLAYER
                        .replace("%uuid%", player.getUniqueId().toString())
                        .replace("%gang%", args[0])
                        .replace("%role%", member.getRole().getPermissionLevel() + ""));

                GangManager.insertGang(create);

                player.sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "You created the gang &7"+args[0]+"&f!")));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="leave",aliases={"l"},help="&5&l/gang leave &e- &fLeave your current gang")
    public void leaveGangCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                Gang gang = member.getGang();
                if (gang == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be in a gang to leave one!")));
                    return;
                }

                boolean ownedGang = member.getRole().equals(GangRole.LEADER);

                if (ownedGang) {
                    //delete gang and kick members...
                    List<OfflinePlayer> inGang = gang.getAllMembers();
                    for (OfflinePlayer kick : inGang) {
                        GTAGangs.sql_query.add(SQLVars.REMOVE_PLAYER_FROM_GANG.replace("%uuid%", kick.getUniqueId().toString()));
                        if (kick.isOnline()) {
                            GangMember.fromPlayer(kick).setGang(null);
                            GangMember.fromPlayer(kick).setRole(null);
                            kick.getPlayer().sendMessage(Formatting.formatMessage(MessageType.WARNING.getPattern().replace("%m%", "The gang that you were in is now disbanded! You are forever alone ;(")));
                        }
                    }

                    GTAGangs.sql_query.add(SQLVars.DELETE_GANG.replace("%name%", gang.getName()));

                    GangManager.removeGang(gang);
                } else {
                    GTAGangs.sql_query.add(SQLVars.REMOVE_PLAYER_FROM_GANG.replace("%uuid%", player.getUniqueId().toString()));

                    player.sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "You left your gang! &e;(")));
                    for (GangMember goodbye : gang.getOnlineMembers()) {
                        goodbye.getWrapped().getPlayer().sendMessage(Formatting.formatMessage(MessageType.WARNING.getPattern().replace("%m%", "&7"+player.getName()+" &fhas left the gang! :o")));
                    }

                    member.setGang(null);
                    member.setRole(null);

                    gang.getOnlineMembers().remove(player);
                    gang.getAllMembers().remove(player);
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="join", aliases={"j"}, help="&5&l/gang join [name] &e- &fJoin the given gang if invited")
    public void joinGangCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                Gang gang$ = member.getGang();
                if (gang$ != null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must leave your current gang before joining another one!")));
                    return;
                }

                if (args.length == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Please specify a gang name!")));
                    return;
                }

                Gang gang = GangManager.getGang(args[0]);
                if (gang == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "A gang with the name &7"+args[0]+" &fdoes not exist!")));
                    return;
                }

                List<Gang> invites = GangInviteManager.findInvites(player);
                if (invites == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You have no gang invites!")));
                    return;
                }

                if (!invites.contains(gang)) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "That gang has not invited you!")));
                    return;
                }

                gang.getAllMembers().add(player);
                gang.getOnlineMembers().add(member);
                GangInviteManager.removeFromInvites(player);

                for (GangMember online : gang.getOnlineMembers()) {
                    online.getWrapped().getPlayer().sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "&7"+player.getName()+" &fhas joined the gang!")));
                }

                member.setRole(GangRole.MEMBER);
                member.setGang(gang);
                GTAGangs.sql_query.add(SQLVars.UPDATE_PLAYER
                        .replace("%gang%", gang.getName())
                        .replace("%role%", member.getRole().getPermissionLevel()+"")
                        .replace("%uuid%", player.getUniqueId().toString()));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="chat",aliases={"togglegangchat", "togglechat", "gangchat"}, help="&5&l/gang chat &e- &fToggle gang chat mode")
    public void toggleGangChatCommand(final Player player, String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                Gang gang = member.getGang();
                if (gang == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be in a gang to use gang chat!")));
                    return;
                }

                member.setGangChat(!(member.isGangChat()));

                player.sendMessage(Formatting.formatMessage(MessageType.INFO.getPattern().replace("%m%", "You switched "+(member.isGangChat() ? "&7into" : "&7out of")+" &fgang chat!")));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="hideout",aliases={"home"},help="&5&l/gang hideout &e- &fTeleport to your gang hideout")
    public void goToHideoutCommand(final Player player, String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(player);

                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                final Gang gang = member.getGang();
                if (gang == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You must be in a gang to use gang chat!")));
                    return;
                }

                if (gang.getHideout() == null) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Your gang does not have a hideout set!")));
                    return;
                }

                if (GTAMovementListener.going_home.contains(player)) {
                    player.sendMessage(Formatting.formatMessage(MessageType.INFO.getPattern().replace("%m%", "You are already teleporting to your hideout!")));
                    return;
                }

                GTAMovementListener.going_home.add(player);
                player.sendMessage(Formatting.formatMessage(MessageType.INFO.getPattern().replace("%m%", "Teleportation will commence in &7"+GTAGangs.TELEPORT_TIME+"&f seconds! Do not move!")));
                Runnable sync = new Runnable() {
                    public void run() {
                        if (GTAMovementListener.going_home.contains(player)) {
                            //teleport
                            if (player.isOnline()) {
                                player.teleport(gang.getHideout());
                                player.sendMessage(Formatting.formatMessage(MessageType.SUCCESS.getPattern().replace("%m%", "Teleportation successful!")));
                            }

                            GTAMovementListener.going_home.remove(player);
                        }
                    }
                };
                Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGangs.getInstance(), sync, 20*(GTAGangs.TELEPORT_TIME));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="list", aliases={"listgangs", "all"}, help="&5&l/gang list &e- &fDisplays a list of all the possible gangs")
    public void listGangsCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                //page based loading
                int page = 1;
                if (args.length != 0) {
                    try { page = Integer.parseInt(args[0]); } catch (NumberFormatException err) {
                        player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "&7"+args[0]+" &fis not a valid page!")));
                        return;
                    }
                }
                page--;

                List<Gang> gangs = GangManager.getGangs();
                List<Gang> range = Lists.newArrayList();
                for (int i = page*8; i < (page*8)+8; i++) {
                    if (i >= gangs.size())
                        range.add(null);
                    else
                        range.add(gangs.get(i));
                }

                if (range.size() == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Nothing to show on this page!")));
                    return;
                }

                player.sendMessage(Formatting.formatMessage("&e&m-----&r &5&lGangs &e&m-----"));
                int pos = page*10;
                for (Gang inRange : range) {
                    pos++;
                    if (inRange == null)
                        player.sendMessage("");
                    else
                        player.sendMessage(Formatting.formatMessage("&5&l"+pos+" &eGang: &f"+inRange.getName()+"&e, Players: &f"+inRange.getAllMembers().size()));
                }
                player.sendMessage(Formatting.formatMessage("&e&m-----&r &5/gang list "+(page+2)+" &5for the next page &e&m-----"));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }

    @CommandHandler.CommandContext(name="who", aliases={"whois", "g", "what"}, help="&5&l/gang who [gang] &e- &fDisplay general information about the given player / gang")
    public void whoCommand(final Player player, final String[] args) {
        Runnable async = new Runnable() {
            public void run() {
                if (!(player.hasPermission("gangs.user"))) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "You do not have permission to use this command!")));
                    return;
                }

                if (args.length == 0) {
                    player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Please specify a gang or a player to search!")));
                    return;
                }

                //begin search
                boolean isPlayer = Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore();
                Gang display = null;

                if (!isPlayer) {
                    //find gang
                    display = GangManager.getGang(args[0]);
                    if (display == null) {
                        player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "The given gang does not exist!")));
                        return;
                    }
                } else {
                    //find player
                    OfflinePlayer data = Bukkit.getOfflinePlayer(args[0]);
                    String found = SQLVars.getGang(data);

                    if (found == null) {
                        player.sendMessage(Formatting.formatMessage(MessageType.INFO.getPattern().replace("%m%", "This player is not in any gang!")));
                        return;
                    }

                    display = GangManager.getGang(found);
                }

                player.sendMessage(Formatting.formatMessage("&e&m----- &5&lGang lookup &e&m-----"));
                player.sendMessage("");
                player.sendMessage(Formatting.formatMessage("&5&lGang&e: &f"+display.getName()));
                String players = "";
                for (OfflinePlayer p : display.getAllMembers()) {
                    if (p.isOnline())
                        players+="&a"+p.getName()+"&f, ";
                    else if (!(p.hasPlayedBefore()))
                        players+="&fHasn't joined yet, ";
                    else
                        players+="&f"+p.getName()+", ";
                }
                player.sendMessage(Formatting.formatMessage("&5&lPlayers&e: &f"+players.substring(0, players.length()-2)));
                player.sendMessage("");
                player.sendMessage(Formatting.formatMessage("&e&m----- &5&lGang lookup &e&m-----"));
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }
}
