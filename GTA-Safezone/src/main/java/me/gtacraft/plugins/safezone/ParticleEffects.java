package me.gtacraft.plugins.safezone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public enum ParticleEffects {

    HUGE_EXPLOSION("hugeexplosion", 0), LARGE_EXPLODE("largeexplode", 1), FIREWORKS_SPARK(
            "fireworksSpark", 2), BUBBLE("bubble", 3), SUSPEND("suspend", 4), DEPTH_SUSPEND(
            "depthSuspend", 5), TOWN_AURA("townaura", 6), CRIT("crit", 7), MAGIC_CRIT(
            "magicCrit", 8), MOB_SPELL("mobSpell", 9), MOB_SPELL_AMBIENT(
            "mobSpellAmbient", 10), SPELL("spell", 11), INSTANT_SPELL(
            "instantSpell", 12), WITCH_MAGIC("witchMagic", 13), NOTE("note", 14), PORTAL(
            "portal", 15), ENCHANTMENT_TABLE("enchantmenttable", 16), EXPLODE(
            "explode", 17), FLAME("flame", 18), LAVA("lava", 19), FOOTSTEP(
            "footstep", 20), SPLASH("splash", 21), LARGE_SMOKE("largesmoke", 22), CLOUD(
            "cloud", 23), RED_DUST("reddust", 24), SNOWBALL_POOF(
            "snowballpoof", 25), DRIP_WATER("dripWater", 26), DRIP_LAVA(
            "dripLava", 27), SNOW_SHOVEL("snowshovel", 28), SLIME("slime", 29), HEART(
            "heart", 30), ANGRY_VILLAGER("angryVillager", 31), HAPPY_VILLAGER(
            "happyVillager", 32), ICONCRACK("iconcrack", 33), TILECRACK(
            "tilecrack", 34);

    private String name;
    private int id;

    ParticleEffects(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    private static final Map<String, ParticleEffects> NAME_MAP = new HashMap<String, ParticleEffects>();
    private static final Map<Integer, ParticleEffects> ID_MAP = new HashMap<Integer, ParticleEffects>();

    static {
        for (ParticleEffects effect : values()) {
            NAME_MAP.put(effect.name, effect);
            ID_MAP.put(effect.id, effect);
        }
    }

    public static ParticleEffects fromName(String name) {
        if (name == null) {
            return null;
        }
        for (Entry<String, ParticleEffects> e : NAME_MAP.entrySet()) {
            if (e.getKey().equalsIgnoreCase(name)) {
                return e.getValue();
            }
        }
        return null;
    }

    public static ParticleEffects fromId(int id) {
        return ID_MAP.get(id);
    }

    public void sendToPlayer(Player player, Location location, float offsetX,
                             float offsetY, float offsetZ, float speed, int count)
            throws Exception {
        Object packet = createPacket(this, location, offsetX, offsetY, offsetZ,
                speed, count);
        sendPacket(player, packet);
    }

    public static void sendToLocation(ParticleEffects effect,
                                      Location location, float offsetX, float offsetY, float offsetZ,
                                      float speed, int count) {
        Object packet = createPacket(effect, location, offsetX, offsetY,
                offsetZ, speed, count);
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

    public static void sendCrackToPlayer(boolean icon, int id, byte data,
                                         Player player, Location location, float offsetX, float offsetY,
                                         float offsetZ, int count) {
        Object packet = createCrackPacket(icon, id, data, location, offsetX,
                offsetY, offsetZ, count);
        sendPacket(player, packet);
    }

    public static void sendCrackToLocation(boolean icon, int id, byte data,
                                           Location location, float offsetX, float offsetY, float offsetZ,
                                           int count) {
        Object packet = createCrackPacket(icon, id, data, location, offsetX,
                offsetY, offsetZ, count);
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

    public static Object createPacket(ParticleEffects effect,
                                      Location location, float offsetX, float offsetY, float offsetZ,
                                      float speed, int count) {
        try {
            if (count <= 0)
                count = 1;
            Object packet = getPacket63WorldParticles();
            setValue(packet, "a", effect.name);
            setValue(packet, "b", (float) location.getX());
            setValue(packet, "c", (float) location.getY());
            setValue(packet, "d", (float) location.getZ());
            setValue(packet, "e", offsetX);
            setValue(packet, "f", offsetY);
            setValue(packet, "g", offsetZ);
            setValue(packet, "h", speed);
            setValue(packet, "i", count);
            return packet;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static Object createCrackPacket(boolean icon, int id, byte data,
                                           Location location, float offsetX, float offsetY, float offsetZ,
                                           int count) {
        try {
            if (count <= 0)
                count = 1;
            Object packet = getPacket63WorldParticles();
            String modifier = "iconcrack_" + id;
            if (!icon) {
                modifier = "tilecrack_" + id + "_" + data;
            }
            setValue(packet, "a", modifier);
            setValue(packet, "b", (float) location.getX());
            setValue(packet, "c", (float) location.getY());
            setValue(packet, "d", (float) location.getZ());
            setValue(packet, "e", offsetX);
            setValue(packet, "f", offsetY);
            setValue(packet, "g", offsetZ);
            setValue(packet, "h", 0.1F);
            setValue(packet, "i", count);
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void setValue(Object instance, String fieldName, Object value)
            throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    private static Object getEntityPlayer(Player p) throws Exception {
        Method getHandle = p.getClass().getMethod("getHandle");
        return getHandle.invoke(p);
    }

    private static String getPackageName() {
        return "net.minecraft.server."
                + Bukkit.getServer().getClass().getPackage().getName()
                .replace(".", ",").split(",")[3];
    }

    private static Object getPacket63WorldParticles() throws Exception {
        Class<?> packet = Class.forName(getPackageName()
                + ".PacketPlayOutWorldParticles");

        for (Constructor cst : packet.getConstructors()) {
            if (cst.getParameterTypes().length == 0)
                return cst.newInstance();
        }

        return packet.getConstructors()[0].newInstance();
    }

    private static void sendPacket(Player p, Object packet) {
        try {
            Object eplayer = getEntityPlayer(p);
            Field playerConnectionField = eplayer.getClass().getField(
                    "playerConnection");
            Object playerConnection = playerConnectionField.get(eplayer);
            for (Method m : playerConnection.getClass().getMethods()) {
                if (m.getName().equalsIgnoreCase("sendPacket")) {
                    m.invoke(playerConnection, packet);
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}