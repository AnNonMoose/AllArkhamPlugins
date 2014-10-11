package me.gtacraft.plugins.hub.task;

import com.google.common.collect.Lists;
import me.gtacraft.plugins.hub.GTAHub;
import me.gtacraft.plugins.hub.util.GTAUtil;
import me.vaqxine.GTAMultiServer.utils.BungeeUtils;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Connor on 7/8/14. Designed for the GTA-Hub project.
 */

public class ScanLocationTask implements Runnable {

    public static List<String> ports = Lists.newArrayList();

    public void run() {
        for (Player player : GTAHub.async_player_map.values()) {
            String region = GTAUtil.getRegionName(player.getLocation());
            if (ports.contains(region)) {
                BungeeUtils.transferServer(player, region, true);
            }
        }
    }

}
