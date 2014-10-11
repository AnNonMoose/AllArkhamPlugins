/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.bar;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.arkhamnetwork.arcade.commons.utils.NMSUtil;
import org.bukkit.Location;

/**
 *
 * @author devan_000
 */
public class NMSv1_7 extends FakeDragon {

    private Object dragon;
    private int id;

    public NMSv1_7(String name, Location loc) {
        super(name, loc);
    }

    @Override
    public Object getSpawnPacket() {
        Class<?> Entity = NMSUtil.getCraftClass("Entity");
        Class<?> EntityLiving = NMSUtil.getCraftClass("EntityLiving");
        Class<?> EntityEnderDragon = NMSUtil.getCraftClass("EntityEnderDragon");
        Object packet = null;
        try {
            dragon = EntityEnderDragon.getConstructor(
                    NMSUtil.getCraftClass("World")).newInstance(getWorld());

            Method setLocation = NMSUtil.getMethod(EntityEnderDragon,
                    "setLocation", new Class<?>[]{double.class, double.class,
                        double.class, float.class, float.class});
            setLocation.invoke(dragon, getX(), getY(), getZ(), getPitch(),
                    getYaw());

            Method setInvisible = NMSUtil.getMethod(EntityEnderDragon,
                    "setInvisible", new Class<?>[]{boolean.class});
            setInvisible.invoke(dragon, isVisible());

            Method setCustomName = NMSUtil.getMethod(EntityEnderDragon,
                    "setCustomName", new Class<?>[]{String.class});
            setCustomName.invoke(dragon, name);

            Method setHealth = NMSUtil.getMethod(EntityEnderDragon,
                    "setHealth", new Class<?>[]{float.class});
            setHealth.invoke(dragon, health);

            Field motX = NMSUtil.getField(Entity, "motX");
            motX.set(dragon, getXvel());

            Field motY = NMSUtil.getField(Entity, "motX");
            motY.set(dragon, getYvel());

            Field motZ = NMSUtil.getField(Entity, "motX");
            motZ.set(dragon, getZvel());

            Method getId = NMSUtil.getMethod(EntityEnderDragon, "getId",
                    new Class<?>[]{});
            this.id = (Integer) getId.invoke(dragon);

            Class<?> PacketPlayOutSpawnEntityLiving = NMSUtil
                    .getCraftClass("PacketPlayOutSpawnEntityLiving");

            packet = PacketPlayOutSpawnEntityLiving.getConstructor(
                    new Class<?>[]{EntityLiving}).newInstance(dragon);
        } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        }

        return packet;
    }

    @Override
    public Object getDestroyPacket() {
        Class<?> PacketPlayOutEntityDestroy = NMSUtil
                .getCraftClass("PacketPlayOutEntityDestroy");

        Object packet = null;
        try {
            packet = PacketPlayOutEntityDestroy.newInstance();
            Field a = PacketPlayOutEntityDestroy.getDeclaredField("a");
            a.setAccessible(true);
            a.set(packet, new int[]{id});
        } catch (SecurityException | NoSuchFieldException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
        }

        return packet;
    }

    @Override
    public Object getMetaPacket(Object watcher) {
        Class<?> DataWatcher = NMSUtil.getCraftClass("DataWatcher");

        Class<?> PacketPlayOutEntityMetadata = NMSUtil
                .getCraftClass("PacketPlayOutEntityMetadata");

        Object packet = null;
        try {
            packet = PacketPlayOutEntityMetadata.getConstructor(
                    new Class<?>[]{int.class, DataWatcher, boolean.class})
                    .newInstance(id, watcher, true);
        } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        }

        return packet;
    }

    @Override
    public Object getTeleportPacket(Location loc) {
        Class<?> PacketPlayOutEntityTeleport = NMSUtil
                .getCraftClass("PacketPlayOutEntityTeleport");

        Object packet = null;

        try {
            packet = PacketPlayOutEntityTeleport.getConstructor(
                    new Class<?>[]{int.class, int.class, int.class,
                        int.class, byte.class, byte.class}).newInstance(
                            this.id, loc.getBlockX() * 32, loc.getBlockY() * 32,
                            loc.getBlockZ() * 32,
                            (byte) ((int) loc.getYaw() * 256 / 360),
                            (byte) ((int) loc.getPitch() * 256 / 360));
        } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        }

        return packet;
    }

    @Override
    public Object getWatcher() {
        Class<?> Entity = NMSUtil.getCraftClass("Entity");
        Class<?> DataWatcher = NMSUtil.getCraftClass("DataWatcher");

        Object watcher = null;
        try {
            watcher = DataWatcher.getConstructor(new Class<?>[]{Entity})
                    .newInstance(dragon);
            Method a = NMSUtil.getMethod(DataWatcher, "a", new Class<?>[]{
                int.class, Object.class});

            a.invoke(watcher, 0, isVisible() ? (byte) 0 : (byte) 0x20);
            a.invoke(watcher, 6, (Float) health);
            a.invoke(watcher, 7, (Integer) 0);
            a.invoke(watcher, 8, (Byte) (byte) 0);
            a.invoke(watcher, 10, name);
            a.invoke(watcher, 11, (Byte) (byte) 1);
        } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        }
        return watcher;
    }
}
