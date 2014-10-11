package me.gtacraft.plugins.chests.listener;

import com.google.common.collect.Lists;
import me.gtacraft.economy.EconomyAPI;
import me.gtacraft.plugins.chests.ChestManager;
import me.gtacraft.plugins.chests.GTAChests;
import me.gtacraft.plugins.chests.parser.ItemData;
import me.gtacraft.plugins.gtarespawn.GTARespawn;
import me.vaqxine.VNPC.lib.RegionType;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Connor on 7/3/14. Designed for the GTA-Chests project.
 */

public class GTAChestsListener implements Listener {

    public GTAChestsListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, GTAChests.getInstance());
    }

    private static List<Class<?>> blocked = new ArrayList<>();
    static {
        blocked.add(Beacon.class);
        blocked.add(Hopper.class);
        blocked.add(Furnace.class);
        blocked.add(Dropper.class);
        blocked.add(Dispenser.class);
        blocked.add(BrewingStand.class);
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        if (event.getInventory() instanceof CraftingInventory) {
            event.setCancelled(true);
            return;
        }

        //block stupidness
        Player player = (Player)event.getPlayer();
        if (event.getInventory().getName().equals("container.enderchest")) {
            if (!(event.getPlayer().hasPermission("gtachests.enderchest"))) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) &cYou cannot use ender chests!"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eWant access to ender chests?"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePurchase a rank at &nhttp://store.gtacraft.me/&r&e for access"));
                event.setCancelled(true);
            }
            return;
        }

        for (Class<?> validate : blocked) {
            if (validate.isInstance(event.getInventory().getHolder())) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getInventory().getHolder() instanceof Chest) {
            if (cleanChunk(event.getPlayer().getLocation().getChunk()))
                return;

            Chest chest = (Chest)event.getInventory().getHolder();
            if (chest.getBlock().getType().equals(Material.TRAPPED_CHEST)) {
                event.setCancelled(true);
                return;
            }

            if (!(ChestManager.contains(chest.getLocation()))) {
                //construct
                ChestManager.add(chest.getLocation());

                //perform modifiers
                double dist = GTARespawn.distanceToClosest(chest.getLocation());
                double pMod = (dist/100.0)+(event.getPlayer().hasPermission("gtachests.betterloot") ? 2.5 : 0);
                int maxTries = (int)(dist/20);

                //fill
                fill(chest.getLocation(), chest.getInventory(), pMod, maxTries);
            }
            //is chest
        } else if (event.getInventory().getHolder() instanceof DoubleChest) {
            if (cleanChunk(event.getPlayer().getLocation().getChunk()))
                return;

            DoubleChest chest = (DoubleChest)event.getInventory().getHolder();
            if (chest.getLocation().getBlock().getType().equals(Material.ENDER_CHEST)) {
                if (!(event.getPlayer().hasPermission("gtachests.enderchest"))) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) &cYou cannot use ender chests!"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eWant access to ender chests?"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePurchase a rank at &nhttp://store.gtacraft.me/&r&e for access"));
                    event.setCancelled(true);
                }
                return;
            }
            if (chest.getLocation().getBlock().getType().equals(Material.TRAPPED_CHEST)) {
                event.setCancelled(true);
                return;
            }

            if (!(ChestManager.contains(chest.getLocation()))) {
                //construct
                ChestManager.add(chest.getLocation());

                //perform modifiers
                double dist = GTARespawn.distanceToClosest(chest.getLocation());
                double pMod = (dist/100.0)+(event.getPlayer().hasPermission("gtachests.betterloot") ? 2.5 : 0);
                int maxTries = (int)(dist/20);

                //fill
                fill(chest.getLocation(), chest.getInventory(), pMod, maxTries);
            }
            //is double
        }

        // ;(
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        if (event.getInventory().getName().equals("container.enderchest")) {
            //filter cash
            List<ItemStack> removeAll = Lists.newArrayList();
            for (ItemStack stack : event.getInventory().getContents()) {
                if (stack == null)
                    continue;

                if (stack.getType().equals(Material.EMERALD)) {
                    double value = 1;
                    if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName() != null) {
                        value = Double.parseDouble(ChatColor.stripColor(stack.getItemMeta().getDisplayName()).replace("$", ""));
                    }
                    value = value*stack.getAmount();
                    removeAll.add(stack);

                    if (value > 50000 || value < 0)
                        continue;

                    double has = EconomyAPI.getUserBalance(player.getUniqueId());
                    has+=value;
                    EconomyAPI.setUserBalance(player.getUniqueId(), has);
                    player.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+"+ $"+value);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, (float)(Math.random()*2));
                }
            }
            event.getInventory().removeItem(removeAll.toArray(new ItemStack[0]));
        }
    }

    private HashSet<Chunk> scanned = new HashSet<>();

    public boolean cleanChunk(Chunk c) {
        if (scanned.contains(c))
            return false;

        scanned.add(c);
        if (c.getTileEntities().length <= 0)
            return false;

        int remove = 0;
        List<BlockState> chests = Lists.newArrayList();

        for (int x = 0; x < c.getTileEntities().length; x++) {
            BlockState at = c.getTileEntities()[x];
            if (at instanceof Chest || at instanceof DoubleChest) {
                if (at.getBlock().getType().equals(Material.ENDER_CHEST))
                    continue;

                remove++;
                chests.add(at);
            }
        }
        remove = (remove-6);
        if (remove < 0)
            return false;

        for (BlockState s : chests) {
            remove--;
            if (remove > 0) {
                s.getBlock().setType(Material.AIR);
                GTAChests.log.debug("Removing chest at: "+s.getBlock().getX()+", "+s.getBlock().getY()+", "+s.getBlock().getZ(), getClass());
            }
        }
        return true;
    }

    private void fill(Location loc, Inventory inv, double percentModifier, int maxTriesModifier) {
        RegionType rt = RegionType.getRegionType(loc);
        GTAChests.log.debug("Fill chest in region "
                +(rt == null ? "null" : rt.toString())+" at ("
                +loc.getWorld().getName()+","
                +loc.getBlockX()+","
                +loc.getBlockY()+","
                +loc.getBlockZ()+")", getClass());

        for (int i = 0; i < GTAChests.getInstance().getRange().roll(); i++) {
            ItemData select = null;
            int x = 0;
            while (select == null && (x < (10+maxTriesModifier))) {
                if (rt == null)
                    select = GTAChests.getInstance().getGlobalData().get((int)(Math.random() * GTAChests.getInstance().getGlobalData().size()));
                else
                    select = GTAChests.getInstance().getItemsByRegion().get(rt).get((int) (Math.random() * GTAChests.getInstance().getItemsByRegion().get(rt).size()));
                if (!select.getChance().didWin(percentModifier))
                    select = null;
                ++x;
            }

            if (select != null)
                inv.setItem(i, select.rebuild());
        }
    }
}
