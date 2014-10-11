package me.gtacraft.gun;

import me.gtacraft.GTAGuns;
import me.gtacraft.attribute.AttributeUtil;
import me.gtacraft.attribute.DynamicAttribute;
import me.gtacraft.util.GunUtil;
import me.gtacraft.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Connor on 4/28/14. Designed for the GTA-Guns project.
 */

public class GunData {

    private UUID gunID;
    private final Gun model;
    private Map<String, DynamicAttribute> attributes = new HashMap<String, DynamicAttribute>();
    private ItemStack stack;

    private boolean reloading = false;
    private int firing;

    public GunData(Gun model, ItemStack stack) {
        this.model = model;
        this.stack = stack;

        gunID = UUID.randomUUID();

        attributes = AttributeUtil.makeDynamic(model.attributes());

        attributes.put("clipammo", new DynamicAttribute(0));
        attributes.put("totalammo", new DynamicAttribute(0));
    }

    public DynamicAttribute getDefaultAttribute(String label, Object dne) {
        if (attributes.containsKey(label))
            return attributes.get(label);
        return new DynamicAttribute(dne);
    }

    public void setAttribute(String label, Object value) {
        attributes.get(label).setValue(value);
    }

    public DynamicAttribute getAttribute(String label) {
        return attributes.get(label);
    }

    public ItemStack getStack() {
        return stack;
    }

    public Gun getModel() {
        return model;
    }

    public boolean updateAmmo(Player player) {
        if (reloading)
            return true;

        int id = attributes.get("ammotype.id").getIntValue();
        byte data = (byte)(int)attributes.get("ammotype.data").getIntValue();

        int clipmax = attributes.get("clipmax").getIntValue();

        int total = GunUtil.getPossibleClipAmmo(id, data, player);
        int clip = attributes.get("clipammo").getIntValue();

        boolean b = false;
        if (clip > 0) {
            //fire
            clip--;
        } else if (total-clipmax <= 0) {
            reload(player);
            clip = total;
            b = true;
        } else {
            reload(player);
            clip = clipmax;
            b = true;
        }

        total = GunUtil.getPossibleClipAmmo(id, data, player);

        setAttribute("totalammo", total);
        setAttribute("clipammo", clip);

        return b;
    }

    public void forceReload(Player player) {
        if (reloading)
            return;

        int id = attributes.get("ammotype.id").getIntValue();
        byte data = (byte)(int)attributes.get("ammotype.data").getIntValue();

        int clipmax = attributes.get("clipmax").getIntValue();

        int total = GunUtil.getPossibleClipAmmo(id, data, player);
        int clip = attributes.get("clipammo").getIntValue();

        if (clip != clipmax && (clip > 0 || !(total-clipmax <= 0)) && clip != total) {
            reload(player);
            clip = clipmax > total ? total : clipmax;
        }

        setAttribute("totalammo", total);
        setAttribute("clipammo", clip);
    }

    public boolean canReload() {
        return attributes.get("clipammo").getIntValue() == 0;
    }

    public String getStackName() {
        int clip = attributes.get("clipammo").getIntValue();
        int total = attributes.get("totalammo").getIntValue();

        if (attributes.containsKey("usage.single") && attributes.get("usage.single").getBooleanValue()) {
            return ChatColor.translateAlternateColorCodes('&', GTAGuns.GUN_NAME_TEXT
                    .replace("{0}", attributes.get("name").getStringValue())
                    .replace("{1}", total+"")
                    .replace("{2}", "1"));
        }

        return ChatColor.translateAlternateColorCodes('&', GTAGuns.GUN_NAME_TEXT
                .replace("{0}", attributes.get("name").getStringValue())
                .replace("{1}", clip + "")
                .replace("{2}", total + "") + (reloading ? GTAGuns.RELOAD_TEXT : ""));
    }

    private boolean zoomed;

    public boolean isZoomed() {
        return zoomed;
    }

    public void setZoomed(boolean zoomed) {
        this.zoomed = zoomed;
    }

    public void reload(final Player player) {
        if (getDefaultAttribute("reload.shotgun", false).getBooleanValue()) {
            reloading = true;

            final int amount = player.getItemInHand().getAmount();

            stack = GunUtil.updateStack(this, amount);
            //shotgun styled reload

            final int ammo = (getAttribute("clipmax").getIntValue()-getAttribute("clipammo").getIntValue())+1;
            final int reloadDiff = getDefaultAttribute("reload.diff", 10).getIntValue();

            final String intro = getDefaultAttribute("reload.sounds.intro", "").getStringValue();
            final String conc = getDefaultAttribute("reload.sounds.conclusion", "").getStringValue();

            int frt = reloadDiff*ammo;
            frt+=(Integer.parseInt(intro.substring(intro.lastIndexOf("-"))));
            frt+=(Integer.parseInt(conc.substring(conc.lastIndexOf("-"))));

            final String mid = getDefaultAttribute("reload.sounds.middle", "").getStringValue();

            //sound runnables
            SoundUtil.playSound(player, intro, Bukkit.getOnlinePlayers());

            Runnable first = new Runnable() {
                public void run() {
                    for (int i = 0; i < ammo; i++) {
                        Runnable go = new Runnable() {
                            @Override
                            public void run() {
                                SoundUtil.playSound(player, mid, Bukkit.getOnlinePlayers());
                            }
                        };
                        Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), go, i*reloadDiff);
                    }
                    Runnable last = new Runnable() {
                        @Override
                        public void run() {
                            SoundUtil.playSound(player, conc, Bukkit.getOnlinePlayers());
                        }
                    };
                    Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), last, ammo*reloadDiff);
                }
            };
            Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), first, Integer.parseInt(intro.substring(intro.lastIndexOf("-"))));
            new BukkitRunnable() {
                public void run() {
                    reloading = false;
                    wasReloading = true;

                    stack = GunUtil.updateStack(GunData.this, amount);
                }
            }.runTaskLater(GTAGuns.getInstnace(), frt);

            return;
        }
        reloading = true;

        final int amount = player.getItemInHand().getAmount();

        stack = GunUtil.updateStack(this, amount);

        SoundUtil.playSound(player, getDefaultAttribute("sounds.reload", "").getStringValue(), Bukkit.getOnlinePlayers());

        new BukkitRunnable() {
            public void run() {
                reloading = false;
                wasReloading = true;

                stack = GunUtil.updateStack(GunData.this, amount);

            }
        }.runTaskLater(GTAGuns.getInstnace(), getDefaultAttribute("reloadtime", "").getIntValue());
    }

    public boolean wasReloading = false;

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public String getUUID() {
        return gunID.toString();
    }

    private int shotDelay;

    public void setNextAvailable(int nextAvailable) {
        attributes.put("CAN_SHOOT", new DynamicAttribute(false));
        new BukkitRunnable() {
            public void run() {
                attributes.remove("CAN_SHOOT");
            }
        }.runTaskLater(GTAGuns.getInstnace(), nextAvailable);
    }

    public boolean cantShoot() {
        return attributes.containsKey("CAN_SHOOT");
    }

    public void setFiring(int firing) {
        this.firing = firing;
        new BukkitRunnable() {
            public void run() {
                GunData.this.firing = 0;
            }
        }.runTaskLater(GTAGuns.getInstnace(), firing);
    }

    public boolean isFiring() {
        return firing != 0;
    }
}