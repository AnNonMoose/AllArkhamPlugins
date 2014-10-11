package org.arkham.cs;

import org.arkham.cs.commands.ColorCommand;
import org.arkham.cs.commands.CosmeticCommand;
import org.arkham.cs.commands.ItemCommand;
import org.arkham.cs.commands.PortalCommand;
import org.arkham.cs.cosmetics.Portal;
import org.arkham.cs.db.Authentication;
import org.arkham.cs.db.SQLQueryThread;
import org.arkham.cs.effects.EffectManager;
import org.arkham.cs.events.BleedListener;
import org.arkham.cs.events.BubbleListener;
import org.arkham.cs.events.CommandPreEvent;
import org.arkham.cs.events.FlyListener;
import org.arkham.cs.events.MoveListener;
import org.arkham.cs.gui.GUIManager;
import org.arkham.cs.handler.ChatColorManager;
import org.arkham.cs.handler.ParticleLibManager;
import org.arkham.cs.interfaces.Button;
import org.arkham.cs.utils.PlayerMetaDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CosmeticSuite extends JavaPlugin {

	private static CosmeticSuite instance;
	private GUIManager guiManager;
	private EffectManager effectManager;
	private CosmeticCommand cCommand;
	private ChatColorManager cManager;

	public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&8[&e&lA&b&lC&8]&e ");

	public void onEnable() {
		instance = this;
		getDataFolder().mkdirs();
		configSQL();
		guiManager = new GUIManager();
		guiManager.loadPages();
		effectManager = new EffectManager();
		cCommand = new CosmeticCommand();
		cManager = new ChatColorManager();
		new BubbleListener();
		new FlyListener();
		new SQLQueryThread();
		new MoveListener();
		new ColorCommand(this);
		new PortalCommand(this);
		new BleedListener(this);
		new CommandPreEvent(this);
		new ParticleLibManager();
		new ItemCommand(this);
		GUIManager.setUp();
		SQLQueryThread.addQuery("CREATE DATABASE IF NOT EXISTS " + Authentication.sqldb);
		SQLQueryThread.addQuery("CREATE TABLE IF NOT EXISTS `purchases` (`player` varchar(64) PRIMARY KEY , `buttons` longtext)");
		SQLQueryThread.addQuery("CREATE TABLE IF NOT EXISTS `colors` (`player` varchar(64) PRIMARY KEY , `code` varchar(2))");
		SQLQueryThread.addQuery("CREATE TABLE IF NOT EXISTS `globalkits` (`player` varchar(64), `id` int, `time` long)");
		getServer().getPluginManager().registerEvents(guiManager, instance);
		// getLogger().info("Printing Permiossion nodes now: ");
		StringBuilder builder = new StringBuilder();
		for (Button button : Button.allButtons) {
			builder.append(button.getPermission() + "  \n");
		}
		getLogger().info("===========================");
		getLogger().info("Writing permiission nodes now.");
		// File file = new File(this.getDataFolder(), "perms.txt");
		// if(!file.exists()){
		// try {
		// file.createNewFile();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// try {
		// PrintWriter writer = new PrintWriter(file);
		// writer.write(builder.toString());
		// writer.close();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }
		getLogger().info("Permission Nodes Wrtiten");
		getLogger().info("===========================");
	}

	public CosmeticCommand getCommand() {
		return cCommand;
	}

	public void onDisable(){
		for(Player player : Bukkit.getOnlinePlayers()){
			PlayerMetaDataUtil.removeFromInGUI(player);
			PlayerMetaDataUtil.removeFromSwitching(player);
			player.removeMetadata("created", CosmeticSuite.getInstance());
			player.removeMetadata("created2", CosmeticSuite.getInstance());
		}
		for(Location loc : Portal.portals){
			Block block = loc.getBlock();
			if(block.hasMetadata("portal")){
				block.setType(Material.AIR);
			}
			if(block.hasMetadata("portal-2")){
				block.setType(Material.AIR);
			}
		}
	}

	public void configSQL() {
		saveDefaultConfig();
		Authentication.sqldb = getConfig().getString("sql.db", Authentication.sqldb);
		Authentication.sqlhost = getConfig().getString("sql.host", Authentication.sqlhost);
		Authentication.sqlpass = getConfig().getString("sql.pass", Authentication.sqlpass);
		Authentication.sqlport = getConfig().getInt("sql.port", Authentication.sqlport);
		Authentication.sqluser = getConfig().getString("sql.user", Authentication.sqluser);
		System.out.println(Authentication.sqluser + " :: " + Authentication.sqldb + " :: " + Authentication.sqlhost + " :: " + Authentication.sqlpass + " :: " + Authentication.sqlport);
		Authentication.sqlurl = "jdbc:mysql://" + Authentication.sqlhost + ":" + Authentication.sqlport + "/" + Authentication.sqldb;

	}

	public static CosmeticSuite getInstance() {
		return instance;
	}

	public GUIManager getGuiManager() {
		return guiManager;
	}

	public EffectManager getEffectManager() {
		return effectManager;
	}

	public ChatColorManager getChatColorManager() {
		return cManager;
	}

}
