/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.EntityLiving;
import net.minecraft.server.v1_7_R3.EntityVillager;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalLookAtTradingPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R3.PathfinderGoalTradeWithPlayer;
import org.arkhamnetwork.arcade.commons.bar.FakeDragon;
import org.arkhamnetwork.arcade.commons.bar.NMSv1_7;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 *
 * @author devan_000
 */
public class NMSUtil {

    public static boolean newProtocol = false;
    public static String version;
    public static Class<?> fakeDragonClass = NMSv1_7.class;

    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String mcVersion = name.substring(name.lastIndexOf('.') + 1);
        String[] versions = mcVersion.split("_");

        if (versions[0].equals("v1") && Integer.parseInt(versions[1]) > 6) {
            newProtocol = true;
            fakeDragonClass = NMSv1_7.class;
        }

        version = mcVersion + ".";
    }

    public static FakeDragon newDragon(String message, Location loc) {
        FakeDragon fakeDragon = null;

        try {
            fakeDragon = (FakeDragon) fakeDragonClass.getConstructor(
                    String.class, Location.class).newInstance(message, loc);
        } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        }

        return fakeDragon;
    }

    public static void sendNMSPacket(Player p, Object packet) {
        ((CraftPlayer) p).getHandle().playerConnection
                .sendPacket((Packet) packet);
    }

    public static Class<?> getCraftClass(String ClassName) {
        String className = "net.minecraft.server." + version + ClassName;
        Class<?> c = null;
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e) {
        }
        return c;
    }

    public static Field getField(Class<?> cl, String field_name) {
        try {
            Field field = cl.getDeclaredField(field_name);
            return field;
        } catch (SecurityException | NoSuchFieldException e) {
        }
        return null;
    }

    public static Method getMethod(Class<?> cl, String method, Class<?>[] args) {
        for (Method m : cl.getMethods()) {
            if (m.getName().equals(method)
                    && ClassListEqual(args, m.getParameterTypes())) {
                return m;
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> cl, String method, Integer args) {
        for (Method m : cl.getMethods()) {
            if (m.getName().equals(method)
                    && args.equals(m.getParameterTypes().length)) {
                return m;
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> cl, String method) {
        for (Method m : cl.getMethods()) {
            if (m.getName().equals(method)) {
                return m;
            }
        }
        return null;
    }

    public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;

        if (l1.length != l2.length) {
            return false;
        }
        for (int i = 0; i < l1.length; i++) {
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        }

        return equal;
    }

    public static void overwriteLivingEntityAI(LivingEntity entity) {
        try {
            EntityLiving ev = ((CraftLivingEntity) entity).getHandle();

            Field goalsField = EntityInsentient.class.getDeclaredField("goalSelector");
            goalsField.setAccessible(true);
            PathfinderGoalSelector goals = (PathfinderGoalSelector) goalsField.get(ev);

            Field listField = PathfinderGoalSelector.class.getDeclaredField("b");
            listField.setAccessible(true);
            List<?> list = (List<?>) listField.get(goals);
            list.clear();
            listField = PathfinderGoalSelector.class.getDeclaredField("c");
            listField.setAccessible(true);
            list = (List<?>) listField.get(goals);
            list.clear();

            goals.a(0, new PathfinderGoalFloat((EntityInsentient) ev));
            goals.a(1, new PathfinderGoalLookAtPlayer((EntityInsentient) ev, EntityHuman.class, 12.0F, 1.0F));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void overwriteVillagerAI(LivingEntity villager) {
        try {
            EntityVillager ev = ((CraftVillager) villager).getHandle();

            Field goalsField = EntityInsentient.class.getDeclaredField("goalSelector");
            goalsField.setAccessible(true);
            PathfinderGoalSelector goals = (PathfinderGoalSelector) goalsField.get(ev);

            Field listField = PathfinderGoalSelector.class.getDeclaredField("b");
            listField.setAccessible(true);
            List<?> list = (List<?>) listField.get(goals);
            list.clear();
            listField = PathfinderGoalSelector.class.getDeclaredField("c");
            listField.setAccessible(true);
            list = (List<?>) listField.get(goals);
            list.clear();

            goals.a(0, new PathfinderGoalFloat(ev));
            goals.a(1, new PathfinderGoalTradeWithPlayer(ev));
            goals.a(1, new PathfinderGoalLookAtTradingPlayer(ev));
            goals.a(2, new PathfinderGoalLookAtPlayer(ev, EntityHuman.class, 12.0F, 1.0F));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
