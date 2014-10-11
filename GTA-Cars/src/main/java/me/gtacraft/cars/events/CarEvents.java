package me.gtacraft.cars.events;

import me.gtacraft.cars.CarsPlugin;
import me.gtacraft.plugins.safezone.util.SafezoneUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.*;

/**
 * Created by tacticalsk8er on 4/27/14.
 */
public class CarEvents implements Listener {

    private HashMap<Player, Float> playerExperince = new HashMap<>();

    CarsPlugin plugin = CarsPlugin.getInstance();

    @EventHandler
    public void onVehicleMount(EntityMountEvent e) {
        if (e.getMount() instanceof Minecart && e.getEntity() instanceof Player) {
            Minecart car = (Minecart) e.getMount();
            Player player = (Player) e.getEntity();
            CarsPlugin.playersInCars.put(player, car);
        }
    }

    @EventHandler
    public void onVehicleDismount(EntityDismountEvent e) {
        if (e.getDismounted() instanceof Minecart && e.getEntity() instanceof Player) {
            Minecart car = (Minecart) e.getDismounted();
            Player player = (Player) e.getEntity();
            if (CarsPlugin.playersInCars.containsKey(player)) {
                Location carNameLocation = new Location(car.getWorld(), car.getLocation().getX(), car.getLocation().getY() + .75, car.getLocation().getZ());
                CarsPlugin.playersInCars.remove(player);
            }
        }
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {
        ItemStack itemStack = e.getPlayer().getItemInHand();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (itemStack != null && itemStack.getType() == Material.MINECART) {
                if (e.getClickedBlock().getType().toString().toLowerCase().contains("rail"))
                    return;
                Location location = e.getClickedBlock().getLocation();
                location.setY(location.getY() + 1.5);
                Minecart car = (Minecart) e.getClickedBlock().getWorld().spawnEntity(location, EntityType.MINECART);
                car.setMaxSpeed(0);
                Location carNameLocation = new Location(car.getWorld(), car.getLocation().getX(), car.getLocation().getY() + .75, car.getLocation().getZ());
                e.getPlayer().getInventory().remove(itemStack);
            }
        } else if (e.getAction().equals(Action.LEFT_CLICK_AIR)) {
            Player player = e.getPlayer();
            if (player.getVehicle() != null && player.getVehicle() instanceof Minecart) {
                Minecart minecart = (Minecart)player.getVehicle();
                Bukkit.getPluginManager().callEvent(new UpdateCarEvent(minecart, minecart.getVelocity(), player, true));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lNos engaged!"));
            }
        }
    }

    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent e) {
        if (e.getVehicle() instanceof Minecart) {
            final Minecart car = (Minecart) e.getVehicle();
            Block blockUnder = car.getLocation().getBlock().getRelative(BlockFace.DOWN);
            Block blockUnderUnder = car.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
            final Vector newVelocity = carGravity(blockUnder, blockUnderUnder, car.getVelocity());
            car.setMaxSpeed(car.getMaxSpeed()-0.001 < 0 ? 0 : car.getMaxSpeed()-0.03);
            car.setVelocity(newVelocity);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onVehicleHitEntity(VehicleEntityCollisionEvent event) {
        if (SafezoneUtil.isInSafeZone(event.getEntity().getLocation())) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Player && SafezoneUtil.getSubscribedTime(((Player) event.getEntity()).getName()) != -1) {
            event.setCancelled(true);
            return;
        }

        Entity damage = event.getEntity();
        if (!(damage instanceof LivingEntity))
            return;

        LivingEntity le = (LivingEntity)damage;
        if (!(event.getVehicle() instanceof Minecart))
            return;

        Minecart minecart = (Minecart)event.getVehicle();
        if (minecart.getPassenger() == null)
            return;

        double x = minecart.getVelocity().getX();
        double y = minecart.getVelocity().getY();
        double z = minecart.getVelocity().getZ();
        if (x < 0) {
            x = -x;
        }
        if (y < 0) {
            y = -y;
        }
        if (z < 0) {
            z = -z;
        }
        if (x < 0.3 && z < 0.3) {
            return;
        }

        if (((x * z) / 2 <= 0))
            return;

        le.setVelocity(le.getVelocity().add(new Vector((Math.random()*.5)-.25, (Math.random()*.5)+.5, (Math.random()*.5)-.25)));
        le.damage((Math.random()*4)+5);
    }
    @EventHandler
    public void onCarUpdate(UpdateCarEvent e) {

        Vehicle vehicle = e.getCar();

        if (vehicle instanceof Minecart) {
            final Minecart car = (Minecart) vehicle;
            car.setMaxSpeed(car.getMaxSpeed() >= 5 ? 5 : car.getMaxSpeed()+0.1);
            final Location carLocation = car.getLocation();
            int dir = (int) e.getPlayer().getLocation().getYaw();
            Block carBlock = carLocation.getBlock();
            Block blockInFront = getBlockInFront(dir, carLocation.getBlock());
            Block blockUnder = carBlock.getRelative(BlockFace.DOWN);
            Block blockUnderUnder = blockUnder.getRelative(BlockFace.DOWN);
            if (carBlock.getType() == Material.STATIONARY_WATER || carBlock.getType() == Material.STATIONARY_LAVA) {
                car.eject();
                car.remove();
                return;
            }

            Vector vector = carBlockJump(blockInFront, carBlock, e.getMovement());

            if (car.hasMetadata("speed")) {
                float speed = car.getMetadata("speed").get(0).asFloat();
                vector.setX(vector.getX() * speed);
                vector.setZ(vector.getZ() * speed);
            }

            if (CarsPlugin.playersUsingNos.containsKey(e.getPlayer())) {
                float nos = CarsPlugin.playersUsingNos.get(e.getPlayer());
                float nosSpeed = 2; //Just for testing will make it configurable
                if (nos > 0) {
                    e.getPlayer().setExp(nos);
                    nos -= .01;
                    vector.setX(vector.getX() * nosSpeed);
                    vector.setZ(vector.getZ() * nosSpeed);
                    if(Math.random() * 4 > 3)
                        car.getWorld().playSound(e.getPlayer().getLocation(), Sound.FIZZ, 1, 0.0f);
                    CarsPlugin.playersUsingNos.put(e.getPlayer(), nos);
                } else {
                    float exp = playerExperince.get(e.getPlayer());
                    e.getPlayer().setExp(exp);
                    CarsPlugin.playersUsingNos.remove(e.getPlayer());
                    playerExperince.remove(e.getPlayer());
                }
            }

            if (e.isNos() && !CarsPlugin.playersUsingNos.containsKey(e.getPlayer())) {
                float nosSpeed = 2; //Just for testing will make it configurable
                vector.setX(vector.getX() * nosSpeed);
                vector.setZ(vector.getZ() * nosSpeed);
                CarsPlugin.playersUsingNos.put(e.getPlayer(), 1.0f);
                playerExperince.put(e.getPlayer(), e.getPlayer().getExp());
            }

            vector = carGravity(blockUnder, blockUnderUnder, vector);

            car.setVelocity(vector);
        }
    }

    /*@EventHandler
    public void onEntityDeath(VehicleDamageEvent e) {
        if (e.getVehicle() instanceof Minecart) {
            final Minecart car = (Minecart) e.getVehicle();
            car.remove();
            Bukkit.getScheduler().runTask(CarsPlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    car.getWorld().dropItemNaturally(car.getLocation(), new ItemStack(Material.MINECART));
                }
            });
        }
    }*/

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) e.getEntity();
            if (CarsPlugin.playersInCars.containsKey(player)) {
                e.setCancelled(true);
            }
        }
    }

    private Block getBlockInFront(int yaw, Block block) {
        yaw += 45;
        while (yaw < 0)
            yaw += 360;
        while (yaw > 360)
            yaw -= 360;
        int direction = yaw / 90;

        switch (direction) {
            case 0:
                return block.getRelative(BlockFace.SOUTH);
            case 1:
                return block.getRelative(BlockFace.WEST);
            case 2:
                return block.getRelative(BlockFace.NORTH);
            case 3:
                return block.getRelative(BlockFace.EAST);
            default:
                return null;
        }
    }

    static List<Material> transparent = new ArrayList<>();
    static {
        transparent.add(Material.AIR);
        transparent.add(Material.LONG_GRASS);
        transparent.add(Material.RED_ROSE);
        transparent.add(Material.DEAD_BUSH);
        transparent.add(Material.STATIONARY_LAVA);
        transparent.add(Material.STATIONARY_WATER);
        transparent.add(Material.BROWN_MUSHROOM);
        transparent.add(Material.RED_MUSHROOM);
        transparent.add(Material.YELLOW_FLOWER);
        transparent.add(Material.SUGAR_CANE);
        transparent.add(Material.LONG_GRASS);
        transparent.add(Material.SNOW);
    }

    private Vector carBlockJump(Block block, Block carBlock, Vector vector) {
        if (block == null)
            return new Vector();

        Material material = block.getType();
        Material materialUp = block.getRelative(BlockFace.UP).getType();
        Material materialCarBlock = carBlock.getType();

        if (!transparent.contains(materialCarBlock)) {

            if (materialCarBlock.toString().toLowerCase().contains("stairs") || materialCarBlock.toString().toLowerCase().contains("step")) {
                vector.setY(1.0);
                return vector;
            }

            vector.setY(1.25);
            return vector;
        }
        if (!(transparent.contains(material))) {
            if (!(transparent.contains(materialUp))) {
                return vector;
            }
            if (material.toString().toLowerCase().contains("stairs") || material.toString().toLowerCase().contains("step")) {
                vector.setY(1.0);
                return vector;
            }
            vector.setY(1.25);
            return vector;
        }
        return vector;
    }

    private Vector carGravity(Block underBlock, Block underUnderBlock, Vector vector) {
        if (underBlock.getType() == Material.AIR && underUnderBlock.getType() == Material.AIR) {
            return vector.setY(-2.0);
        }
        return vector;
    }
}
