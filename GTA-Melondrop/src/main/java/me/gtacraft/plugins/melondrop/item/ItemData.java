package me.gtacraft.plugins.melondrop.item;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.gtacraft.api.GTAGunsAPI;
import me.gtacraft.gun.Gun;
import me.gtacraft.gun.GunData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 7/4/14. Designed for the GTA-Chests project.
 */

public class ItemData {

    private HashMap<String, Object> data = new HashMap<>();
    @Getter
    private DataTypes.PercentChance chance;

    public ItemData(String loadFrom) {
        String[] blowUp = loadFrom.split(",");
        for (String scan : blowUp) {
            String value = findInsideMap(scan, count(scan, '('));
            String key = scan.replace("("+value+")", "");

            this.data = loadUp(key, value, this.data);
        }

        chance = (DataTypes.PercentChance)data.get("percentselect");
    }

    public ItemStack rebuild() {
        if (data.containsKey("isgun") && (Boolean)data.get("isgun")) {
            Gun g = GTAGunsAPI.getGunFromName((String) data.get("name"));
            GunData fullData = GTAGunsAPI.createData(g);
            return fullData.getStack();
        }

        int amount = 1;
        byte _data = (byte)0;
        if (data.containsKey("data"))
            _data = (Byte)data.get("data");

        if (data.containsKey("amount")) {
            //parse
            Object dat = data.get("amount");
            if (dat instanceof DataTypes.RandomRange) {
                amount = ((DataTypes.RandomRange) dat).roll();

            } else {
                amount = (int)(dat);
            }
        }

        ItemStack stack = new ItemStack(Material.getMaterial((Integer)data.get("id")), amount, (short)0, _data);


        MaterialData md = stack.getData();
        md.setData((data.containsKey("data") ? (Byte)data.get("data") : 0));
        stack.setData(md);

        if (data.containsKey("name")) {
            String name = ChatColor.translateAlternateColorCodes('&', (String) data.get("name"));
            ItemMeta im = stack.getItemMeta();
            im.setDisplayName(name);
            stack.setItemMeta(im);
        }

        if (data.containsKey("lore")) {
            List<String> lore = (List)data.get("lore");
            List<String> realLore = Lists.newArrayList();
            realLore.add(lore.get((int)(Math.random()*lore.size())));
            ItemMeta im = stack.getItemMeta();
            im.setLore(realLore);
            stack.setItemMeta(im);
        }

        if (data.containsKey("enchantments")) {
            HashMap<Enchantment, Integer> map = (HashMap)data.get("enchantments");
            stack.addUnsafeEnchantments(map);
        }
        return stack;
    }

    private static int count(String total, char find) {
        int found = 0;
        for (char c : total.toCharArray()) {
            if (c == find)
                found++;
        }

        return found;
    }

    private static String findInsideMap(String total, int deep) {
        int begin = total.indexOf("(")+1;
        int currentDepth = deep;
        for (int i = begin; i < total.length(); i++) {
            if (total.charAt(i) == ')') {
                if (--currentDepth == 0) {
                    return total.substring(begin, i);
                }
            }
        }

        return "";
    }

    private static HashMap<String, Object> loadUp(String key, String value, HashMap<String, Object> map) {
        key = key.toLowerCase();
        if (!key.equals("name") && !key.equals("lore"))
            value = value.toLowerCase();

        if (key.equals("data")) {
            map.put(key, Byte.parseByte(value));
            return map;
        }
        try { map.put(key, Integer.parseInt(value)); return map; } catch (Exception err) {}
        try { map.put(key, Double.parseDouble(value)); return map; } catch (Exception err) {}

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            map.put(key, Boolean.parseBoolean(value));
            return map;
        }

        if (value.contains("(") && value.startsWith("from")) {
            //deeper
            String inside = findInsideMap(value, count(value, '('));
            DataTypes.RandomRange range = new DataTypes.RandomRange(Integer.parseInt(inside.split("-")[0]), Integer.parseInt(inside.split("-")[1]));
            map.put(key, range);
            return map;
        } else if (value.contains("%") && key.startsWith("percentselect")) {
            //is percent
            value = value.replace("%", "");
            DataTypes.PercentChance pc = new DataTypes.PercentChance(Double.parseDouble(value));
            map.put(key, pc);
            return map;
        } else if (value.contains("|") || key.startsWith("lore")) {
            if (key.startsWith("enchantments")) {
                HashMap<Enchantment, Integer> enchMap = new HashMap<>();
                String[] $1 = value.split("[\\|]");
                for (String s : $1) {
                    enchMap.put(Enchantment.getById(Integer.parseInt(s.split("[\\-]")[0])), Integer.parseInt(s.split("[\\-]")[1]));
                }
                map.put(key, enchMap);
                return map;
            } else if (key.startsWith("lore")) {
                List<String> lore = Lists.newArrayList();
                value = value.replace("possible(", "").replace(")", "");
                for (String s : value.split("[\\|]")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("%comma%", ",")));
                }
                map.put(key, lore);
                return map;
            }
        }

        map.put(key, value);
        return map;
    }
}
