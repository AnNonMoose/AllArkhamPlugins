package me.gtacraft.plugins.chests;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.gtacraft.plugins.chests.listener.GTAChestsListener;
import me.gtacraft.plugins.chests.parser.DataTypes;
import me.gtacraft.plugins.chests.parser.ItemData;
import me.gtacraft.plugins.chests.util.Logger;
import me.vaqxine.VNPC.lib.RegionType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 7/3/14. Designed for the GTA-Chests project.
 */

public class GTAChests extends JavaPlugin {

    public static Logger log = new Logger();

    @Getter
    private static GTAChests instance;
    @Getter
    private DataTypes.RandomRange range;
    @Getter
    private List<ItemData> globalData = Lists.newArrayList();
    @Getter
    private int resetTime;
    @Getter
    public HashMap<RegionType, List<ItemData>> itemsByRegion = new HashMap<>();

    public void onEnable() {
        this.instance = this;

        saveDefaultConfig();

        resetTime = getConfig().getInt("resetTime");
        String range = getConfig().getString("itemCount");
        this.range = new DataTypes.RandomRange(Integer.parseInt(range.split("-")[0]), Integer.parseInt(range.split("-")[1]));

        for (String item : getConfig().getStringList("items")) {
            ItemData id = new ItemData(item);
            if (id.getApplicableRegions().size() == 0) {
                globalData.add(id);
                for (RegionType t : RegionType.values()) {
                    if (itemsByRegion.containsKey(t)) {
                        List<ItemData> reCreate = itemsByRegion.remove(t);
                        reCreate.add(id);
                        itemsByRegion.put(t, reCreate);
                    } else {
                        List<ItemData> construct = Lists.newArrayList();
                        construct.add(id);
                        itemsByRegion.put(t, construct);
                    }
                }
            } else {
                for (RegionType t : id.getApplicableRegions()) {
                    if (itemsByRegion.containsKey(t)) {
                        List<ItemData> reCreate = itemsByRegion.remove(t);
                        reCreate.add(id);
                        itemsByRegion.put(t, reCreate);
                    } else {
                        List<ItemData> construct = Lists.newArrayList();
                        construct.add(id);
                        itemsByRegion.put(t, construct);
                    }
                }
            }
        }

        new GTAChestsListener();
        ChestManager.tickDown();
    }

    public void onDisable() {
        saveDefaultConfig();
        ChestManager.clear();
    }
}
