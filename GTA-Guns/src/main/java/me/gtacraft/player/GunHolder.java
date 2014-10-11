package me.gtacraft.player;

import com.google.common.collect.Lists;
import me.gtacraft.GTAGuns;
import me.gtacraft.gun.Gun;
import me.gtacraft.gun.GunData;
import me.gtacraft.gun.GunFactory;
import me.gtacraft.util.GunUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Connor on 4/27/14. Designed for the GTA-Guns project.
 */

public class GunHolder {

    private static ConcurrentHashMap<Player, GunHolder> _holders = new ConcurrentHashMap<Player, GunHolder>();

    private ItemStack itemHolding; public ItemStack getItemHolding() { return itemHolding; }

    private GunData lastWeapon; public GunData getLastWeapon() { return lastWeapon; }
    private GunData currentWeapon; public GunData getCurrentWeapon() { return currentWeapon; }
    private List<GunData> containedGuns = new ArrayList<GunData>(); public List<GunData> getContainedGuns() { return containedGuns; }

    private long ticks; public long getTicks() { return ticks; }

    private final GTAGuns plugin = GTAGuns.getInstnace();
    private Player holder; public Player getHolder() { return holder; }

    public GunHolder(Player holder) {
        this.holder = holder;
        _holders.put(holder, this);
    }

    public static void resetAll() {
        for (GunHolder gh : _holders.values()) {
            gh.containedGuns.clear();
            gh.currentWeapon = null;
        }
    }

    public static GunHolder getHolder(Player player) {
        return _holders.containsKey(player) ? _holders.get(player) : new GunHolder(player);
    }

    public static ConcurrentHashMap<Player, GunHolder> getAllHolders() {
        return _holders;
    }

    public static final long TICK_INTERVAL = 1l;

    public boolean tick() {
        ++ticks;

        if (holder == null) {
            _holders.remove(holder);
            return true;
        }

        ItemStack inHand = holder.getItemInHand();
        itemHolding = inHand;

        if (inHand == null || inHand.getType().equals(Material.AIR)) {
            currentWeapon = null;
            return false;
        }

        GunData data = GunUtil.getGunData(containedGuns, inHand);
        if (data == null) {
            Gun gun = GunFactory.getGun(inHand);
            ItemStack stack = GunUtil.getGunStack(gun);

            if (gun == null || stack == null)
                return false;

            data = new GunData(gun, stack);
            GunUtil.updateStack(data, inHand.getAmount());

            containedGuns.add(data);

            holder.getInventory().setItemInHand(stack);

            if (lastWeapon == null || data != currentWeapon) {
                lastWeapon = data;
            }
        } else {
            if (lastWeapon == null || data != currentWeapon) {
                lastWeapon = data;
            }
            holder.setItemInHand(GunUtil.updateStack(data, inHand.getAmount()));
            currentWeapon = data;
            if (data.wasReloading) {
                data.wasReloading = false;
                ItemStack s = data.getStack();
                ItemMeta m = s.getItemMeta();
                m.setDisplayName(data.getStackName());
                s.setItemMeta(m);
                data.setStack(s);

                holder.setItemInHand(s);
            }
        }

        return false;
    }

    public static void handleLeave(Player player) {
        _holders.remove(player);
    }

    public static void cleanNull() {
        List<Player> clean = Lists.newArrayList();
        for (Player p : _holders.keySet()) {
            if (p == null || _holders.get(p) == null)
                clean.add(p);
        }

        for (Player p : clean) {
            _holders.remove(p);
        }
    }

    public void doKnockback(GunData held) {
        double recoil = held.getDefaultAttribute("recoil", 0.0).getDoubleValue();
        holder.setVelocity(holder.getEyeLocation().getDirection().multiply(-recoil));
    }
}
