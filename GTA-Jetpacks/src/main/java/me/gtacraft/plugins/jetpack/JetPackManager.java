package me.gtacraft.plugins.jetpack;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Connor on 7/13/14. Designed for the GTA-Jetpacks project.
 */

public class JetPackManager {

    public static HashMap<ItemStack, Integer> jetPacks = new HashMap<>();

    public static void assignJetpackAndTier(ItemStack stack, int tier) {
        jetPacks.put(stack, tier);
    }

    public static int getTier(ItemStack jetPack) {
        return (jetPacks.containsKey(jetPack) ? jetPacks.get(jetPack) : -1);
    }

    public static ItemStack getPack(int tier) {
        for (ItemStack s : jetPacks.keySet()) {
            if (jetPacks.get(s) == tier)
                return s;
        }
        return null;
    }

    public static void removeJetpack(ItemStack stack) {
        jetPacks.remove(stack);
    }
}
