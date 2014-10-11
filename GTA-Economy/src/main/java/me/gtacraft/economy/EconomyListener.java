package me.gtacraft.economy;

import me.gtacraft.economy.database.SQLConnectionThread;
import me.gtacraft.economy.database.SQLVars;
import me.gtacraft.economy.util.Range;
import me.gtacraft.economy.util.Util;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Connor on 7/6/14. Designed for the GTA-Economy project.
 */

public class EconomyListener implements Listener {

    private static EconomyListener instance;

    public static EconomyListener get() {
        return instance;
    }

    public EconomyListener() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, GTAEconomy.get());
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        try {
            PreparedStatement statement = SQLConnectionThread.getConnection().prepareStatement(SQLVars.SELECT_PLAYER
                    .replace("%uuid%", uuid.toString()));
            statement.execute();

            ResultSet rs = statement.getResultSet();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                GTAEconomy.log.debug("Finding balance for "+uuid.toString()+". Found $"+balance, getClass());
                GTAEconomy.player_balances.put(uuid, balance);
            } else {
                GTAEconomy.sql_query.add(SQLVars.INSERT_PLAYER
                        .replace("%uuid%", uuid.toString()));
                GTAEconomy.player_balances.put(uuid, 0.0);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            event.setKickMessage(Util.f("&c&lERROR&f: Could not load your balance!"));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }

    private static Map<EntityType, Range> entityDeathMap = new HashMap<>();
    private static Map<Skeleton.SkeletonType, Range> tangentSkeletonMap = new HashMap<>();
    static {
        entityDeathMap.put(EntityType.PLAYER, new Range(1, 5));
        entityDeathMap.put(EntityType.ZOMBIE, new Range(5, 10));
        entityDeathMap.put(EntityType.ENDERMAN, new Range(50, 100));
        entityDeathMap.put(EntityType.PIG_ZOMBIE, new Range(15, 30));
        entityDeathMap.put(EntityType.IRON_GOLEM, new Range(30, 50));
        entityDeathMap.put(EntityType.BLAZE, new Range(25, 30));

        tangentSkeletonMap.put(Skeleton.SkeletonType.NORMAL, new Range(15, 25));
        tangentSkeletonMap.put(Skeleton.SkeletonType.WITHER, new Range(20, 25));
    }

    public static volatile HashMap<Entity, EntityDamageEvent.DamageCause> lastDamageCause = new HashMap<>();
    public static volatile HashMap<Entity, Entity> lastEntityDamageByEntity = new HashMap<>();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        if (lastDamageCause.containsKey(event.getEntity()))
            lastDamageCause.remove(event.getEntity());

        lastDamageCause.put(event.getEntity(), event.getCause());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        if (lastEntityDamageByEntity.containsKey(event.getEntity()))
            lastEntityDamageByEntity.remove(event.getEntity());

        lastEntityDamageByEntity.put(event.getEntity(), event.getDamager());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(doDrop(event)))
            return;

        if (event.getEntity().getType().equals(EntityType.SKELETON)) {
            Skeleton.SkeletonType st = ((Skeleton)event.getEntity()).getSkeletonType();
            int drop = tangentSkeletonMap.get(st).random();
            dropItem(event.getEntity().getLocation(), drop);
        } else if (entityDeathMap.containsKey(event.getEntity().getType())) {
            if (event.getEntity().getType().equals(EntityType.PLAYER)) {
                //discover
                Player p = (Player)event.getEntity();
                if (p.hasMetadata("NPC")) {
                    dropItem(p.getLocation(), entityDeathMap.get(p.getType()).random());
                } else {
                    double has = EconomyAPI.getUserBalance(p.getUniqueId());
                    double drop = .1;
                    if (p.hasPermission("gtaeconomy.droplesscash"))
                        drop = .05;
                    dropItem(p.getLocation(), has*drop);
                    EconomyAPI.setUserBalance(p.getUniqueId(), has-(has*drop));
                }
                return;
            }
            int drop = entityDeathMap.get(event.getEntity().getType()).random();
            dropItem(event.getEntity().getLocation(), drop);
        }
    }

    private boolean doDrop(EntityDeathEvent e) {
        LivingEntity le = e.getEntity();
        if (lastEntityDamageByEntity.containsKey(le)) {
            Entity damager = lastEntityDamageByEntity.remove(le);
            if (lastDamageCause.containsKey(le))
                lastDamageCause.remove(le);

            if (damager instanceof Projectile) {
                if (((Projectile) damager).getShooter() instanceof Player)
                    return true;
            }

            return ((damager instanceof Player) || (le instanceof Player));
        }
        EntityDamageEvent.DamageCause cause = (lastDamageCause.containsKey(le) ? lastDamageCause.remove(le) : null);
        if (cause == null)
            return false;

        return (cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || cause.equals(EntityDamageEvent.DamageCause.PROJECTILE) || cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION));
    }

    private void dropItem(Location at, double cashValue) {
        if (cashValue > 50000 || cashValue < 0)
            return;

        ItemStack cash = new ItemStack(Material.EMERALD);
        ItemMeta meta = cash.getItemMeta();
        cashValue = cashValue*100;
        cashValue = Math.round(cashValue);
        cashValue = cashValue/100;
        meta.setDisplayName(cashValue+"");
        cash.setItemMeta(meta);

        Item end = at.getWorld().dropItem(at, cash);
        end.setPickupDelay(0);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack().getType().equals(Material.EMERALD)) {
            event.setCancelled(true);

            double value = 1;
            ItemStack stack = event.getItem().getItemStack();
            if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName() != null) {
                value = Double.parseDouble(ChatColor.stripColor(stack.getItemMeta().getDisplayName()).replace("$", ""));
            }
            event.getItem().remove();
            value = value*stack.getAmount();

            if (value > 50000 || value < 0)
                return;

            UUID pickup = event.getPlayer().getUniqueId();
            double has = EconomyAPI.getUserBalance(pickup);
            has+=value;
            EconomyAPI.setUserBalance(pickup, has);
            event.getPlayer().sendMessage(ChatColor.GREEN+""+ ChatColor.BOLD+"+ $"+value);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, (float)(Math.random()*2));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().equals(Material.EMERALD)) {
            ItemStack click = event.getPlayer().getItemInHand();
            if (click.getItemMeta() == null)
                return;

            if (click.getItemMeta().getDisplayName() != null) {
                String name = click.getItemMeta().getDisplayName();
                name = name.replace("$", "");
                name = ChatColor.stripColor(name);

                try {
                    Double value = Double.parseDouble(name)*click.getAmount();
                    if (value > 50000 || value < 0)
                        return;

                    double has = EconomyAPI.getUserBalance(event.getPlayer().getUniqueId());
                    EconomyAPI.setUserBalance(event.getPlayer().getUniqueId(), has+value);
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l(!) &7You deposited &a&l$"+value+" &7into your bank account!"));
                    event.getPlayer().getInventory().remove(click);
                    event.getPlayer().updateInventory();
                } catch (Exception ex) {
                    return;
                }
            }
        }
    }
}
