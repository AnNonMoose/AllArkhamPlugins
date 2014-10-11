package me.gtacraft.util;

import me.gtacraft.gun.Gun;
import me.gtacraft.gun.GunData;
import me.gtacraft.gun.GunFactory;
import me.gtacraft.player.GunHolder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 4/28/14. Designed for the GTA-Guns project.
 */
public class GunUtil {

    public static ItemStack getGunStack(Gun model) {
        if (model == null)
            return null;

        Material stack = Material.getMaterial(model.getAttribute("type.id").getIntValue());
        byte data = (byte)model.getAttribute("type.data").getIntValue();

        ItemStack forge = new ItemStack(stack, 1, (short) 0, data);
        ItemMeta meta = forge.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+model.getAttribute("name").getStringValue());

        forge.setItemMeta(meta);

        return forge;
    }

    public static <T> List<T> newList(List<T> list) {
        return new ArrayList<T>(list);
    }

    public static boolean isGun(ItemStack stack) {
        for (Gun g : GunFactory.getGuns().values()) {
            int type = g.getAttribute("type.id").getIntValue();
            byte data = (byte)g.getAttribute("type.data").getIntValue();

            if (stack.getTypeId() == type && stack.getData().getData() == data)
                return true;
        }
        return false;
    }

    public static GunData isInList(List<GunData> containedGuns, Gun model) {
        for (GunData d : containedGuns) {
            if (d.getModel().equals(model))
                return d;
        }
        return null;
    }

    public static GunData getGunData(List<GunData> possible, ItemStack inHand) {
        if (possible == null)
            return null;

        for (GunData p : possible) {
            if (matchesUUID(p, inHand))
                return p;
        }
        return null;
    }

    private static boolean matchesUUID(GunData data, ItemStack other) {
        List<String> lore = data.getStack().getItemMeta().getLore();
        List<String> lore2;

        if (other == null || other.getItemMeta() == null)
            return false;

        if (other.getItemMeta().getLore() == null)
            return false;
        lore2 = other.getItemMeta().getLore();

        for (String s : lore2) {
            if (lore.contains(s))
                return true;
        }

        return false;
    }

    public static ItemStack updateStack(GunData holding, int amount) {
        ItemStack i = holding.getStack();
        i.setAmount(amount);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(holding.getStackName());
        m.setLore(add(new ArrayList<String>(), ChatColor.translateAlternateColorCodes('&', "&0"+holding.getUUID())));
        i.setItemMeta(m);
        holding.setStack(i);
        return i;
    }

    private static List<String> add(List<String> orig, String value) {
        orig.add(value);
        return orig;
    }

    public static boolean removeOne(int id, byte data, Player player) {
        GunHolder holder = GunHolder.getHolder(player);
        if (holder.getCurrentWeapon() == null)
            return false;

        if (GunHolder.getHolder(player).getCurrentWeapon().isReloading())
            return false;

        for (ItemStack i : player.getInventory().getContents()) {
            if (i == null)
                continue;
            if (i.getTypeId() == id && i.getData().getData() == data) {
                if (i.getAmount() == 1) {
                    player.getInventory().remove(i);
                    player.updateInventory();
                    return true;
                } else {
                    i.setAmount(i.getAmount()-1);
                    player.updateInventory();
                    return true;
                }
            }
        }
        return false;
    }

    public static int getPossibleClipAmmo(int id, byte data, Player player) {
        if (id == 0)
            return player.getItemInHand().getAmount();

        int tot = 0;
        for (ItemStack i : player.getInventory().getContents()) {
            if (i == null)
                continue;

            if (i.getTypeId() == id && i.getData().getData() == data) {
                tot+=i.getAmount();
            }
        }
        return tot;
    }

    public static Class<? extends Projectile> getProjectile(String bulletprojectile) {
        String s = bulletprojectile.toLowerCase();
        if (s.equals("snowball")) {
            return Snowball.class;
        } else if (s.equals("fireball")) {
            return Fireball.class;
        } else if (s.equals("arrow")) {
            return Arrow.class;
        } else if (s.equals("witherskull")) {
            return WitherSkull.class;
        } else {
            return Snowball.class;
        }
    }

    public static int hasEnough(int id, byte data, Player player) {
        if (id == 0)
            return player.getItemInHand().getAmount();

        int ret = 0;
        for (ItemStack i : player.getInventory().getContents()) {
            if (i == null)
                continue;

            if (i.getTypeId() == id && i.getData().getData() == data) {
                int tot = i.getAmount();
                ret+=tot;
            }
        }
        return ret;
    }
}
