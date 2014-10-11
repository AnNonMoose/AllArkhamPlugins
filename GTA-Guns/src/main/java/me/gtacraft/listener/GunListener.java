package me.gtacraft.listener;

import com.google.common.collect.Lists;
import me.gtacraft.GTAGuns;
import me.gtacraft.event.*;
import me.gtacraft.gun.BulletData;
import me.gtacraft.gun.GunData;
import me.gtacraft.player.GunHolder;
import me.gtacraft.plugins.melondrop.task.MelonDropTask;
import me.gtacraft.util.*;
import me.vaqxine.VNPC.tasks.CrimeResponseTask;
import me.vaqxine.WorldRegeneration.RegenerationAPI;
import net.minecraft.server.v1_7_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;


/**
 * Created by Connor on 4/27/14. Designed for the GTA-Guns project.
 */

public class GunListener implements Listener {

    @EventHandler
    public void onGunFireEvent(final PlayerInteractEvent e) {
        Runnable async = new Runnable() {
            @Override
            public void run() {
                final int slot = e.getPlayer().getInventory().getHeldItemSlot();
                final GunHolder holder = GunHolder.getHolder(e.getPlayer());
                if (holder.getCurrentWeapon() == null)
                    return;

                final GunData held = GunUtil.getGunData(holder.getContainedGuns(), holder.getHolder().getItemInHand());
                if (held == null || held.isFiring())
                    return;

                if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && acceptable(e.getClickedBlock()))
                        return;

                    //shoot

                    if (held.isReloading())
                        return;

                    if (held.cantShoot())
                        return;

                    final Player player = holder.getHolder();

                    int $bullets = held.getDefaultAttribute("bulletsfired", 1).getIntValue();
                    final int id = held.getAttribute("ammotype.id").getIntValue();
                    final byte data = (byte)(int)held.getAttribute("ammotype.data").getIntValue();
                    int merge = GunUtil.hasEnough(id, data, player);
                    final int bullets = merge > $bullets ? $bullets : merge;
                    if (bullets == 0)
                        return;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), new Runnable() {
                        @Override
                        public void run() {

                            final double accuracy = held.getAttribute("accuracy").getDoubleValue();

                            final double bulletSpeed = held.getDefaultAttribute("bulletspeed", 2.0).getDoubleValue();
                            final int burst = held.getDefaultAttribute("bullets", 1).getIntValue();

                            final int bulletDelay = held.getDefaultAttribute("bulletdelay", 0).getIntValue();

                            PreFireEvent preFire = new PreFireEvent(player.getEyeLocation(), holder, false);
                            Bukkit.getPluginManager().callEvent(preFire);

                            if (preFire.isCancelled())
                                return;

                            if (held.getDefaultAttribute("usage.single", false).getBooleanValue()) {
                                int amount = player.getItemInHand().getAmount();
                                if (amount == 1) {
                                    player.getInventory().remove(player.getItemInHand());
                                }
                                else {
                                    player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount()-1);
                                }
                                player.updateInventory();

                                //parse single usage purpose

                                String special = held.getDefaultAttribute("usage.special", "").getStringValue();

                                if (special.equalsIgnoreCase("grenade")) {
                                    special(player, held, false, false);
                                } else if (special.equalsIgnoreCase("flashbang")) {
                                    special(player, held, true, false);
                                } else if (special.equalsIgnoreCase("molotov")) {
                                    special(player, held, false, true);
                                }

                                return;
                            }

                            held.setFiring(bullets*bulletDelay);

                            for (int i = 0; i < bullets; i++) {
                                new BukkitRunnable() {
                                    public void run() {
                                        if (held.isReloading())
                                            return;
                                        if (held.updateAmmo(player) || !GunUtil.removeOne(id, data, player))
                                            return;

                                        for (int j = 0; j < burst; j++) {
                                            SoundUtil.playSound(player.getLocation(), held.getDefaultAttribute("sounds.shoot", "").getStringValue(), Bukkit.getOnlinePlayers());

                                            Projectile proj = player.launchProjectile(GunUtil.getProjectile(held.getAttribute("bulletprojectile").getStringValue()));
                                            if (proj instanceof WitherSkull) {
                                                WitherSkull ws = (WitherSkull)proj;

                                                ws.setCharged(false);
                                                ws.setYield(0.0f);
                                            } else if (proj instanceof Fireball) {
                                                final Fireball fb = (Fireball)proj;

                                                fb.setYield(0);
                                            }

                                            BulletData fired = new BulletData(proj, held);

                                            WeaponFireEvent firedEvent = new WeaponFireEvent(fired);
                                            Bukkit.getPluginManager().callEvent(firedEvent);

                                            Vector v = proj.getVelocity();
                                            v = v.multiply(bulletSpeed);
                                            v = v.add(new Vector((Math.random()*accuracy)-(accuracy/2), (Math.random()*accuracy)-(accuracy/2), (Math.random()*accuracy)-(accuracy/2)));

                                            proj.setVelocity(v);
                                        }

                                        SoundUtil.playSound(holder.getHolder(), held.getDefaultAttribute("sounds.cooldown", "").getStringValue(), Bukkit.getOnlinePlayers());

                                        holder.doKnockback(held);
                                        if (scope.containsKey(holder.getHolder())) {
                                            PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(holder.getHolder().getEntityId(), 3, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(86))));
                                            ((CraftPlayer)holder.getHolder()).getHandle().playerConnection.sendPacket(equip);
                                        }
                                    }
                                }.runTaskLater(GTAGuns.getInstnace(), (long) (bulletDelay*i));
                            }

                            new BukkitRunnable() {
                                public void run() {
                                    if (held != null && holder != null && held.getAttribute("clipammo").getIntValue() == 0)
                                        held.forceReload(holder.getHolder());
                                }
                            }.runTaskLater(GTAGuns.getInstnace(), (long) (bulletDelay*bullets)+3);
                            held.setNextAvailable(held.getDefaultAttribute("shotdelay", 0).getIntValue());
                        }
                    });
                } else if (e.getAction().equals(Action.LEFT_CLICK_AIR)) {
                    held.forceReload(holder.getHolder());
                }

                e.setCancelled(true);
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGuns.getInstnace(), async);
    }

    private static boolean acceptable(Block clickedBlock) {
        int id = clickedBlock.getTypeId();
        switch (id) {
            case 54:
            case 69:
            case 77:
            case 84:
            case 85:
            case 96:
            case 107:
            case 113:
            case 116:
            case 120:
            case 131:
            case 137:
            case 138:
            case 143:
            case 145:
            case 146:
            case 154:
            case 158:
            case 323:
            case 68:
            case 63:  return true;
            default:  return false;
        }
    }

    private HashMap<Player, ItemStack> scope = new HashMap<Player, ItemStack>();
    private List<Player> deny_scope = Lists.newArrayList();

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        final Player p = e.getPlayer();
        if (deny_scope.contains(p))
            return;

        GunHolder holder = GunHolder.getHolder(p);
        GunData held = holder.getCurrentWeapon();
        if (p.hasPotionEffect(PotionEffectType.SLOW) && held == null) {
            if (scope.containsKey(p)) {
                PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(e.getPlayer().getEntityId(), 3, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
                ((CraftPlayer)e.getPlayer()).getHandle().playerConnection.sendPacket(equip);
                ItemStack reset = scope.remove(p);
                p.getEquipment().setHelmet(reset);
                p.updateInventory();
                deny_scope.add(p);
                Runnable delay = new Runnable() {
                    public void run() {
                        deny_scope.remove(p);
                    }
                };
                Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), delay, 5);

                if (holder.getLastWeapon() != null) {
                    SoundUtil.playSound(p, holder.getLastWeapon().getDefaultAttribute("sounds.zoom_off", "").getStringValue(), p);
                    holder.getLastWeapon().setZoomed(false);
                }
            }
            p.removePotionEffect(PotionEffectType.SLOW);
        } else if (held == null)
            return;

        if (held == null) {
            p.removePotionEffect(PotionEffectType.SLOW);
            return;
        }

        if (e.isSneaking() && held.getDefaultAttribute("zoom.allowed", false).getBooleanValue() && !held.isZoomed()) {
            int zoom = held.getDefaultAttribute("zoom.amount", 2).getIntValue();
            if (zoom >= 10) {
                //scope
                scope.put(p, p.getEquipment().getHelmet());
                PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(holder.getHolder().getEntityId(), 3, CraftItemStack.asNMSCopy(new ItemStack(Material.getMaterial(86))));
                ((CraftPlayer)holder.getHolder()).getHandle().playerConnection.sendPacket(equip);
            }
            holder.getHolder().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, zoom));
            SoundUtil.playSound(holder.getHolder(), held.getDefaultAttribute("sounds.zoom_on", "").getStringValue(), p);
            e.setCancelled(true);
            held.setZoomed(true);
            return;
        }
        else {
            e.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            if (holder.getCurrentWeapon() == null)
                return;

            if (!holder.getCurrentWeapon().isZoomed())
                return;

            SoundUtil.playSound(holder.getHolder(), held.getDefaultAttribute("sounds.zoom_off", "").getStringValue(), p);
            if (held.getDefaultAttribute("zoom.amount", 0).getIntValue() >= 10) {
                PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(e.getPlayer().getEntityId(), 3, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
                ((CraftPlayer)e.getPlayer()).getHandle().playerConnection.sendPacket(equip);
                ItemStack reset = scope.remove(p);
                p.getEquipment().setHelmet(reset);
                p.updateInventory();

                deny_scope.add(p);
                Runnable delay = new Runnable() {
                    public void run() {
                        deny_scope.remove(p);
                    }
                };
                Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), delay, 5);
            }
            held.setZoomed(false);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity))
            return;

        if (e.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile)e.getDamager();

            BulletData gd = BulletData.getBulletData(proj);
            ExplosionUtil.explode(gd);

            if (gd == null)
                return;

            WeaponDamageEntityEvent wdee = new WeaponDamageEntityEvent(gd, e.getEntity());
            Bukkit.getPluginManager().callEvent(wdee);

            if (wdee.isCancelled())
                return;

            double rangedRemoval = gd.getGunData().getDefaultAttribute("range", 0.0).getDoubleValue();
            double damage = gd.getGunData().getAttribute("damage").getDoubleValue();
            e.setDamage(ArmorUtil.recomputeDamage((LivingEntity)e.getEntity(), damage)-(rangedRemoval*gd.getInitialLocation().distance(gd.getProjectile().getLocation())));
        }
    }

    static int totalBreakTicks = Integer.MIN_VALUE;

    private static void sendBreakPacket(double speed, Block chip) {
        int x = chip.getX();
        int y = chip.getY();
        int z = chip.getZ();

        Location at = chip.getLocation();

        if (speed < 20)
            return;

        int damage = (int)((speed/200.0)*9);
        damage = damage > 9 ? 9 : damage;

        PacketPlayOutBlockBreakAnimation ppobba = new PacketPlayOutBlockBreakAnimation(totalBreakTicks++, x, y, z, damage);
        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.playEffect(chip.getLocation(), Effect.STEP_SOUND, chip.getTypeId());
            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(ppobba);
        }

    }

    public static List<FallingBlock> fall_blocks_explode = Lists.newArrayList();

    @EventHandler
    public void onEntitySpawnBlock(EntityChangeBlockEvent event) {
        if (fall_blocks_explode.contains(event.getEntity())) {
            fall_blocks_explode.remove(event.getEntity());

            FallingBlock cast = (FallingBlock)event.getEntity();
            int id = cast.getBlockId();
            byte data = cast.getBlockData();

            Location hit = event.getBlock().getLocation();
            event.setCancelled(true);

            hit.getWorld().playEffect(hit, Effect.STEP_SOUND, id, data);
        }
    }

    @EventHandler
    public void onItemDrop(ItemSpawnEvent event) {
        Item i = event.getEntity();
        int x = i.getItemStack().getTypeId();
        if(x == 6 || x == 37 || x == 38 || x == 81 || x == 175)
            event.setCancelled(true);
    }

    @EventHandler
    public void projHit(ProjectileHitEvent e) {
        Projectile proj = e.getEntity();
        final Block hit = getHit(proj);

        BulletHitBlockEvent bhbe = new BulletHitBlockEvent(hit.getLocation());
        Bukkit.getPluginManager().callEvent(bhbe);

        e.getEntity().remove();
        if (bhbe.isCancelled())
            return;

        if (molotov.containsKey(proj)) {
            Runnable run = molotov.remove(proj);
            Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), run);
            CrimeResponseTask.addCrimeLocation(proj.getLocation(), e.getEntity().getShooter());
            return;
        }

        BulletData gd = BulletData.getBulletData(proj);
        ExplosionUtil.explode(gd);

        if (gd == null)
            return;

        final int id = hit.getTypeId();
        Runnable async = new Runnable() {
            @Override
            public void run() {
                sendCrackPacket(hit, id);
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGuns.getInstnace(), async);

        if (break_chance.containsKey(hit.getTypeId())) {
            double roll = Math.random()*100;
            if (roll <= break_chance.get(hit.getTypeId())) {
                Location add = checkDoor(hit.getLocation());
                if (add != null) {
                    RegenerationAPI.queueBlockForRegeneration(add.getBlock(), 60);
                    add.getWorld().playEffect(add, Effect.STEP_SOUND, add.getBlock().getTypeId(), add.getBlock().getData());
                    add.getBlock().setType(Material.AIR);
                }
                RegenerationAPI.queueBlockForRegeneration(hit, 60);
                hit.setType(Material.AIR);
            }
            else
                sendBreakPacket(gd.getBulletSpeed(), hit);
        } else {
            Location add = checkDoor(hit.getLocation());
            if (add != null) {
                RegenerationAPI.queueBlockForRegeneration(add.getBlock(), 60);
                add.getWorld().playEffect(add, Effect.STEP_SOUND, add.getBlock().getTypeId(), add.getBlock().getData());
                add.getBlock().setType(Material.AIR);
            }
            RegenerationAPI.queueBlockForRegeneration(hit, 60);
            hit.setType(Material.AIR);
        }
    }

    private Location checkDoor(Location hit) {
        if (hit.getBlock().getType().equals(Material.WOODEN_DOOR) || hit.getBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
            Location up = hit.clone().add(0, 1, 0);
            Location down = hit.clone().add(0, -1, 0);
            if (up.getBlock().getType().equals(Material.WOODEN_DOOR) || up.getBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
                return up;
            } else if (down.getBlock().getType().equals(Material.WOODEN_DOOR) || down.getBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
                return down;
            }
        }
        return null;
    }

    public void sendCrackPacket(Block b, int typeID) {
        Packet particles = new PacketPlayOutWorldEvent(2001, Math.round(b.getX()), Math.round(b.getY()), Math.round(b.getZ()), typeID, false);
        ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().sendPacketNearby(b.getX(), b.getY(), b.getZ(), 16, ((CraftWorld) b.getWorld()).getHandle().dimension, particles);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!(event.getPlayer().getGameMode().equals(GameMode.CREATIVE))) {
            Location add = checkDoor(event.getBlock().getLocation());
            if (Bukkit.getPluginManager().getPlugin("GTA-Melondrop") != null) {
                if (MelonDropTask.wrapped != null && MelonDropTask.wrapped.equals(event.getBlock()))
                    return;
            }
            if (add != null) {
                RegenerationAPI.queueBlockForRegeneration(add.getBlock(), 30);
                sendCrackPacket(add.getBlock(), add.getBlock().getTypeId());
                add.getBlock().setType(Material.AIR);
            }
            event.setCancelled(true);
            RegenerationAPI.queueBlockForRegeneration(event.getBlock(), 30);
            event.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity().getType().equals(EntityType.FALLING_BLOCK))
            event.setCancelled(true);
    }

    static List<Material> redstone_material = Lists.newArrayList();
    static {
        redstone_material.add(Material.REDSTONE_BLOCK);
        redstone_material.add(Material.STONE_BUTTON);
        redstone_material.add(Material.WOOD_BUTTON);
        redstone_material.add(Material.STONE_PLATE);
        redstone_material.add(Material.WOOD_PLATE);
        redstone_material.add(Material.IRON_PLATE);
        redstone_material.add(Material.GOLD_PLATE);
    }

    @EventHandler
    public void onPhysicsEvent(BlockPhysicsEvent event) {
        Material t = event.getChangedType();
        if (redstone_material.contains(t))
            return;

        if (!(t.equals(Material.WATER)) && !(t.equals(Material.STATIONARY_WATER)))
            event.setCancelled(true);
    }

    private static Block getHit(Projectile proj) {
        BlockIterator bi = new BlockIterator(proj.getWorld(), proj.getLocation().toVector(), proj.getVelocity().normalize(), 0.0D, 7);
        Block hit = null;
        while (bi.hasNext()) {
            hit = bi.next();
            if (hit.getTypeId() != 0)
                break;
        }
        return hit;
    }

    private static HashMap<Projectile, Runnable> molotov = new HashMap<Projectile, Runnable>();

    private static void special(final Player player, final GunData held, final boolean flash, final boolean fire) {
        PreFireEvent preFire = new PreFireEvent(player.getEyeLocation(), GunHolder.getHolder(player), (!flash && !fire));
        Bukkit.getPluginManager().callEvent(preFire);

        if (preFire.isCancelled())
            return;

        String preShoot = held.getDefaultAttribute("sound.use", "").getStringValue();

        if (preShoot != "")
            SoundUtil.playSound(player.getLocation(), preShoot, player);

        final double radius = held.getDefaultAttribute("usage.radius", 2.0).getDoubleValue();
        final double damage = held.getAttribute("damage").getDoubleValue();
        final int flashDuration = held.getDefaultAttribute("usage.duration", 4).getIntValue();
        final double accuracy = held.getAttribute("accuracy").getDoubleValue();
        final double bulletSpeed = held.getDefaultAttribute("bulletspeed", 2.0).getDoubleValue();

        int $id = held.getDefaultAttribute("usage.drop.id", 1).getIntValue();
        byte $data = (byte)held.getDefaultAttribute("usage.drop.data", 0).getIntValue();

        if (fire) {
            final Projectile proj = player.launchProjectile(Snowball.class);

            SpecialFireEvent fireEvent = new SpecialFireEvent(proj.getLocation(), proj, null);
            Bukkit.getPluginManager().callEvent(fireEvent);

            Vector v = proj.getVelocity();
            v = v.multiply(bulletSpeed);
            v = v.add(new Vector((Math.random()*accuracy)-(accuracy/2), (Math.random()*accuracy)-(accuracy/2), (Math.random()*accuracy)-(accuracy/2)));

            proj.setVelocity(v);
            molotov.put(proj, new Runnable() {
                public void run() {
                    for (LivingEntity player : proj.getWorld().getLivingEntities()) {
                        if (player.getLocation().distance(proj.getLocation()) <= radius) {
                            player.setFireTicks(held.getDefaultAttribute("usage.fire", 10).getIntValue()*20);
                        }
                    }

                    ParticleEffects.sendToLocation(ParticleEffects.FLAME, proj.getLocation(), (float)radius/2, (float)radius/3, (float)radius/2, 0f, (int)(radius*50));
                    ParticleEffects.sendToLocation(ParticleEffects.LARGE_SMOKE, proj.getLocation(), (float)radius/2, (float)radius/3, (float)radius/2, 0f, (int)(radius*50));
                    SoundUtil.playSound(proj.getLocation(), held.getDefaultAttribute("sounds.flash", "GLASS-1-0-0").getStringValue(), Bukkit.getOnlinePlayers());
                }
            });

            return;
        }

        ItemStack stack = new ItemStack($id, 1, (short)0, $data);


        final Item proj = player.getWorld().dropItem(player.getEyeLocation(), stack);

        SpecialFireEvent fireEvent = new SpecialFireEvent(proj.getLocation(), null, proj);
        Bukkit.getPluginManager().callEvent(fireEvent);

        Vector v = player.getEyeLocation().getDirection();
        v.normalize();
        v = v.multiply(bulletSpeed);
        v = v.add(new Vector((Math.random()*accuracy)-(accuracy/2), (Math.random()*accuracy)-(accuracy/2), (Math.random()*accuracy)-(accuracy/2)));

        ExplosionUtil.deny_pickup.add(proj);

        proj.setVelocity(v);

        Runnable explode = new Runnable() {
            public void run() {
                final Location current = proj.getLocation();
                proj.remove();

                if (flash) {
                    SoundUtil.playSound(current, held.getDefaultAttribute("sounds.special", "FIREWORK_TWINKLE-1-2-0").getStringValue(), Bukkit.getOnlinePlayers());
                    ParticleEffects.sendToLocation(ParticleEffects.CLOUD, current, (float)radius, (float)radius, (float)radius, 0.1f, 50);
                }
                else if (!fire) {
                    SoundUtil.playSound(current, held.getDefaultAttribute("sounds.special", "EXPLODE-1-0-0").getStringValue(), Bukkit.getOnlinePlayers());
                    ParticleEffects.sendToLocation(ParticleEffects.HUGE_EXPLOSION, current, 0f, 0f, 0f, 1f, 1);

                    //compute kaboom
                    ExplosionUtil.destroyBlocks(radius, current, 30);
                }
                else {
                    ParticleEffects.sendToLocation(ParticleEffects.FLAME, current, (float) radius, 2f, (float) radius, 0f, (int) (radius * 50));
                    SoundUtil.playSound(current, held.getDefaultAttribute("sounds.special", "GLASS-1-0-0").getStringValue(), Bukkit.getOnlinePlayers());
                }
                final List<LivingEntity> online = proj.getWorld().getLivingEntities();
                Runnable inRadius = new Runnable() {
                    @Override
                    public void run() {
                        List<LivingEntity> inRange = Lists.newArrayList();

                        for (LivingEntity player : online) {
                            if (player.getLocation().distance(current) <= radius)
                                inRange.add(player);
                        }

                        final List<LivingEntity> clone = new ArrayList<LivingEntity>(inRange);
                        Runnable sync = new Runnable() {
                            public void run() {
                                for (LivingEntity _player : clone) {
                                    if (flash)
                                        _player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*flashDuration, 1));
                                    else {
                                        PreFireEvent pfe = new PreFireEvent(_player.getLocation(), GunHolder.getHolder(player), true);
                                        Bukkit.getPluginManager().callEvent(pfe);

                                        if (!(pfe.isCancelled()))
                                            _player.damage(ArmorUtil.recomputeDamage(_player, damage-(_player.getLocation().distance(current)/2)));
                                    }
                                }
                            }
                        };

                        ExplosionUtil.deny_pickup.remove(proj);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), sync);
                    }
                };

                Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGuns.getInstnace(), inRadius);
            }
        };

        Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), explode, held.getDefaultAttribute("usage.timer", 5).getIntValue()*20);
    }

    //default 100%
    static HashMap<Integer, Integer> break_chance = new HashMap<Integer, Integer>();
    static {
        break_chance.put(1, 20);
        break_chance.put(2, 70);
        break_chance.put(3, 70);
        break_chance.put(4, 35);
        break_chance.put(5, 60);
        break_chance.put(7, 0);
        break_chance.put(12, 90);
        break_chance.put(17, 45);
        break_chance.put(22, 40);
        break_chance.put(35, 95);
        break_chance.put(41, 15);
        break_chance.put(42, 10);
        break_chance.put(43, 50);
        break_chance.put(44, 75);
        break_chance.put(45, 30);
        break_chance.put(47, 60);
        break_chance.put(48, 20);
        break_chance.put(49, 7);
        break_chance.put(53, 60);
        break_chance.put(57, 2);
        break_chance.put(58, 60);
        break_chance.put(61, 30);
        break_chance.put(82, 65);
        break_chance.put(84, 50);
        break_chance.put(87, 20);
        break_chance.put(88, 15);
        break_chance.put(89, 85);
        break_chance.put(98, 20);
        break_chance.put(101, 50);
        break_chance.put(103, 0);
        break_chance.put(108, 30);
        break_chance.put(109, 20);
        break_chance.put(112, 15);
        break_chance.put(113, 15);
        break_chance.put(114, 15);
        break_chance.put(116, 5);
        break_chance.put(120, 60);
        break_chance.put(121, 30);
        break_chance.put(123, 90);
        break_chance.put(128, 50);
        break_chance.put(130, 5);
        break_chance.put(133, 4);
        break_chance.put(134, 60);
        break_chance.put(135, 60);
        break_chance.put(136, 60);
        break_chance.put(138, 85);
        break_chance.put(139, 40);
        break_chance.put(145, 10);
        break_chance.put(154, 10);
        break_chance.put(155, 35);
        break_chance.put(156, 50);
        break_chance.put(159, 65);
        break_chance.put(162, 45);
        break_chance.put(172, 65);
        break_chance.put(173, 40);
        break_chance.put(174, 95);
    }
}
