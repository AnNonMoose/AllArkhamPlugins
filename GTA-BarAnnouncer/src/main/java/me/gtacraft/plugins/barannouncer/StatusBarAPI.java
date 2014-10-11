package me.gtacraft.plugins.barannouncer;

import java.lang.reflect.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;

/**
 *
 * Assorted methods for manipulating packets to spawn fake Ender Dragons and show players a
 * status bar at the top of the screen using their health bar.  This class uses reflection, so
 * even though it accesses NSM methods it should be version-safe (assuming the names of classes
 * don't change).
 *
 * This is a clean-up/fix-up/refactoring of SoThatsIt's code, which originally did nearly the
 * same thing, but was less readable, had less features, and was broken in some places.  It also
 * uses a trimmed down version of my own {@link PlayerMap} class to store the fake dragons.
 *
 * @author AmoebaMan
 *
 */
public class StatusBarAPI {

    private static PlayerMap<FakeDragon> DRAGONS = new PlayerMap<FakeDragon>();

    /**
     * Checks to see if the player is currently being displayed a status bar via fake Ender Dragon.
     * <br><br>
     * This may sometimes return a false positive.  Specifically, if a player is sent a fake dragon, and
     * subsequently logs off and back on and the bar is not restored, the record of the dragon will remain
     * here even though the client no longer has the entity.  To avoid this, be sure to remove the bar
     * manually using {@link #removeStatusBar(Player)} when the player leaves the server
     * ({@link org.bukkit.event.player.PlayerQuitEvent} and {@link org.bukkit.event.player.PlayerKickEvent})
     *
     * @param player a player
     * @return true if this API has a record of the player being sent a bar
     */
    public static boolean hasStatusBar(Player player){
        return DRAGONS.containsKey(player) && DRAGONS.get(player) != null;
    }

    /**
     * Removes a player's status bar by destroying their fake dragon (if they have one).
     *
     * @param player a player
     */
    public static void removeStatusBar(Player player){
        if(hasStatusBar(player)){
            sendPacket(player, DRAGONS.get(player).getDestroyPacket());
            DRAGONS.remove(player);
        }
    }

    /**
     * Sets a player's status bar to display a specific message and fill amount.  The fill amount is in
     * decimal percent (i.e. 1 = 100%, 0 = 0%, 0.5 = 50%, 0.775 = 77.5%, etc.).
     * <br><br>
     * <code>text</code> is limited to 64 characters, and <code>percent</code> must be greater than zero
     * and less than or equal to one.  If either argument is outside its constraints, it will be quietly
     * trimmed to match.
     *
     * @param player a player
     * @param text some text with 64 characters or less
     * @param percent a decimal percent in the range (0,1]
     */
    public static void setStatusBar(Player player, String text, float percent) {

        FakeDragon dragon = DRAGONS.containsKey(player) ? DRAGONS.get(player) : null;

        if(percent > 1.0f)
            percent = 1.0f;
        if(percent < 0.05f)
            percent = 0.05f;

        if (text.isEmpty() && dragon != null)
            removeStatusBar(player);

        if (dragon == null) {
            dragon = new FakeDragon(player.getLocation().add(0, -200, 0), text, percent);
            sendPacket(player, dragon.getSpawnPacket());
            DRAGONS.put(player, dragon);
        }
        else {
            dragon.setName(text);
            dragon.setHealth(percent);
            sendPacket(player, dragon.getMetaPacket(dragon.getWatcher()));
            sendPacket(player, dragon.getTeleportPacket(player.getLocation().add(0, -200, 0)));
        }

    }

    /**
     * Removes the status bar for all players on the server.  See {@link #removeStatusBar(Player)}.
     */
    public static void removeAllStatusBars(){
        for(Player each : BarAnnouncer.async_player_map.values())
            removeStatusBar(each);
    }

    /**
     * Sets the status bar for all players on the server.  See {@link #setStatusBar(Player, String, float)}.
     * @param text some text with 64 characters or less
     * @param percent a decimal percent in the range (0,1]
     */
    public static void setAllStatusBars(String text, float percent){
        for(Player each : BarAnnouncer.async_player_map.values())
            setStatusBar(each, text, percent);
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object nmsPlayer = ReflectionUtils.getHandle(player);
            Field connectionField = nmsPlayer.getClass().getField("playerConnection");
            Object connection = connectionField.get(nmsPlayer);
            Method sendPacket = ReflectionUtils.getMethod(connection.getClass(), "sendPacket");
            sendPacket.invoke(connection, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class FakeDragon {

        private static final int MAX_HEALTH = 200;
        private int              id;
        private int              x;
        private int              y;
        private int              z;
        private int              pitch      = 0;
        private int              yaw        = 0;
        private byte             xvel       = 0;
        private byte             yvel       = 0;
        private byte             zvel       = 0;
        private float            health;
        private boolean          visible    = false;
        private String           name;
        private Object           world;

        private Object           dragon;

        public FakeDragon(Location loc, String name, float percent) {
            this.name = name;
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
            this.health = percent * MAX_HEALTH;
            this.world = ReflectionUtils.getHandle(loc.getWorld());
        }

        public void setHealth(float percent) {
            this.health = percent / MAX_HEALTH;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getSpawnPacket() {
            Class<?> Entity = ReflectionUtils.getCraftClass("Entity");
            Class<?> EntityLiving = ReflectionUtils.getCraftClass("EntityLiving");
            Class<?> EntityEnderDragon = ReflectionUtils.getCraftClass("EntityEnderDragon");

            try{
                dragon = EntityEnderDragon.getConstructor(ReflectionUtils.getCraftClass("World")).newInstance(world);

                ReflectionUtils.getMethod(EntityEnderDragon, "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(dragon, x, y, z, pitch, yaw);
                ReflectionUtils.getMethod(EntityEnderDragon, "setInvisible", boolean.class).invoke(dragon, visible);
                ReflectionUtils.getMethod(EntityEnderDragon, "setCustomName", String.class ).invoke(dragon, name);
                ReflectionUtils.getMethod(EntityEnderDragon, "setHealth", float.class).invoke(dragon, health);

                ReflectionUtils.getField(Entity, "motX").set(dragon, xvel);
                ReflectionUtils.getField(Entity, "motY").set(dragon, yvel);
                ReflectionUtils.getField(Entity, "motZ").set(dragon, zvel);

                this.id = (Integer) ReflectionUtils.getMethod(EntityEnderDragon, "getId").invoke(dragon);

                Class<?> packetClass = ReflectionUtils.getCraftClass("PacketPlayOutSpawnEntityLiving");
                return packetClass.getConstructor(new Class<?>[]{ EntityLiving }).newInstance(dragon);
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public Object getDestroyPacket(){
            try{
                Class<?> packetClass = ReflectionUtils.getCraftClass("PacketPlayOutEntityDestroy");
                return packetClass.getConstructor(new Class<?>[]{int[].class}).newInstance(new int[]{id});
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public Object getMetaPacket(Object watcher){
            try{
                Class<?> watcherClass = ReflectionUtils.getCraftClass("DataWatcher");
                Class<?> packetClass = ReflectionUtils.getCraftClass("PacketPlayOutEntityMetadata");
                return packetClass.getConstructor(new Class<?>[] { int.class, watcherClass, boolean.class }).newInstance(id, watcher, true);
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public Object getTeleportPacket(Location loc){
            try{
                Class<?> packetClass = ReflectionUtils.getCraftClass("PacketPlayOutEntityTeleport");
                return packetClass.getConstructor(new Class<?>[] { int.class, int.class, int.class, int.class, byte.class, byte.class }).newInstance(
                        this.id, loc.getBlockX() * 32, loc.getBlockY() * 32, loc.getBlockZ() * 32, (byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360));
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public Object getWatcher(){
            Class<?> Entity = ReflectionUtils.getCraftClass("Entity");
            Class<?> DataWatcher = ReflectionUtils.getCraftClass("DataWatcher");

            try{
                Object watcher = DataWatcher.getConstructor(new Class<?>[] { Entity }).newInstance(dragon);
                Method a = ReflectionUtils.getMethod(DataWatcher, "a", new Class<?>[] { int.class, Object.class });

                a.invoke(watcher, 0, visible ? (byte) 0 : (byte) 0x20);
                a.invoke(watcher, 6, (Float) health);
                a.invoke(watcher, 7, (Integer) 0);
                a.invoke(watcher, 8, (Byte) (byte) 0);
                a.invoke(watcher, 10, name);
                a.invoke(watcher, 11, (Byte) (byte) 1);
                return watcher;
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

    }

    private static class ReflectionUtils {

        public static Class<?> getCraftClass(String ClassName) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String version = name.substring(name.lastIndexOf('.') + 1) + ".";
            String className = "net.minecraft.server." + version + ClassName;
            Class<?> c = null;
            try {
                c = Class.forName(className);
            }
            catch (Exception e) { e.printStackTrace(); }
            return c;
        }

        public static Object getHandle(Entity entity) {
            try {
                return getMethod(entity.getClass(), "getHandle").invoke(entity);
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public static Object getHandle(World world) {
            try {
                return getMethod(world.getClass(), "getHandle").invoke(world);
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public static Field getField(Class<?> cl, String field_name) {
            try {
                return cl.getDeclaredField(field_name);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static Method getMethod(Class<?> cl, String method, Class<?>... args) {
            for (Method m : cl.getMethods())
                if (m.getName().equals(method) && ClassListEqual(args, m.getParameterTypes()))
                    return m;
            return null;
        }

        public static Method getMethod(Class<?> cl, String method) {
            for (Method m : cl.getMethods())
                if (m.getName().equals(method))
                    return m;
            return null;
        }

        public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
            boolean equal = true;
            if (l1.length != l2.length)
                return false;
            for (int i = 0; i < l1.length; i++)
                if (l1[i] != l2[i]) {
                    equal = false;
                    break;
                }
            return equal;
        }

    }

    /**
     * A class that attempts to overcome the traditional aversion to using org.bukkit.entity.Player as
     * the key type for a Map, namely that Player references can be quite large and we don't want to
     * keep them around after they're gone unless necessary.
     * <br><br>
     * This class is externally typed with {@link org.bukkit.entity.Player} as the key type, but internally
     * uses {@link java.lang.String} as the key type, using the player's name.
     * <br><br>
     * In addition to this memory-saving measure, this map also allows the contents to be accessed through
     * either the player's name or the player object itself, meaning no more hassle with {@link Player#getName()}
     * or {@link Bukkit#getPlayer(String)} when you want to pull out of a map.
     *
     * @author AmoebaMan
     *
     * @param <V> whatever you want to store
     */
    private static class PlayerMap<V> implements Map<Player, V> {

        private final V defaultValue;
        private final Map<String, V> contents;

        public PlayerMap(){
            contents = new HashMap<String, V>();
            defaultValue = null;
        }

        public void clear() {
            contents.clear();
        }

        public boolean containsKey(Object key) {
            if(key instanceof Player)
                return contents.containsKey(((Player) key).getName());
            if(key instanceof String)
                return contents.containsKey(key);
            return false;
        }

        public boolean containsValue(Object value){
            return contents.containsValue(value);
        }

        public Set<Entry<Player, V>> entrySet() {
            Set<Entry<Player, V>> toReturn = new HashSet<Entry<Player, V>>();
            for(String name : contents.keySet())
                toReturn.add(new PlayerEntry(Bukkit.getPlayer(name), contents.get(name)));
            return toReturn;
        }

        public V get(Object key) {
            V result = null;
            if(key instanceof Player)
                result = contents.get(((Player) key).getName());
            if(key instanceof String)
                result = contents.get(key);
            return (result == null) ? defaultValue : result;
        }

        public boolean isEmpty(){
            return contents.isEmpty();
        }

        public Set<Player> keySet(){
            Set<Player> toReturn = new HashSet<Player>();
            for(String name : contents.keySet())
                toReturn.add(Bukkit.getPlayer(name));
            return toReturn;
        }

        public V put(Player key, V value) {
            if(key == null)
                return null;
            return contents.put(key.getName(), value);
        }

        public void putAll(Map<? extends Player, ? extends V> map) {
            for(Entry<? extends Player, ? extends V> entry : map.entrySet())
                put(entry.getKey(), entry.getValue());
        }

        public V remove(Object key) {
            if(key instanceof Player)
                return contents.remove(((Player) key).getName());
            if(key instanceof String)
                return contents.remove(key);
            return null;
        }

        public int size() {
            return contents.size();
        }

        public Collection<V> values() {
            return contents.values();
        }

        public String toString(){
            return contents.toString();
        }

        public class PlayerEntry implements Map.Entry<Player, V>{

            private Player key;
            private V value;

            public PlayerEntry(Player key, V value){
                this.key = key;
                this.value = value;
            }

            public Player getKey() {
                return key;
            }

            public V getValue() {
                return value;
            }

            public V setValue(V value) {
                V toReturn = this.value;
                this.value = value;
                return toReturn;
            }

        }

    }

}