package org.arkhamnetwork.playersync.utils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

      public static String convertLocationToString(Location l) {
            // world_name,0,0,0:yaw$pitch
            DecimalFormat df = new DecimalFormat("#.####");
            return (l.getWorld().getName() + "," + df.format(l.getBlockX()) + "," + df.format(l.getBlockY()) + "," + df.format(l.getBlockZ()) + ":" + df.format(l.getYaw()) + "$" + df.format(l.getPitch()));
      }

      public static Location convertStringToLocation(String location_string) {
            return convertStringToLocation(location_string, false);
      }

      public static Location convertStringToLocation(String location_string, boolean letWorldBeNull) {
            // world_name,0,0,0:yaw$pitch
            if (location_string == null) {
                  return null;
            }

            if (location_string.split(",").length == 4) {
                  String world_name = location_string.split(",")[0];
                  double x = Double.parseDouble(location_string.split(",")[1]);
                  double y = Double.parseDouble(location_string.split(",")[2]);
                  double z = Double.parseDouble(location_string.split(",")[3].split(":")[0]);
                  float yaw = Float.parseFloat(location_string.substring(location_string.indexOf(":") + 1, location_string.indexOf("$")));
                  float pitch = Float.parseFloat(location_string.substring(location_string.indexOf("$") + 1, location_string.length()));

                  // 'world' could be null. - vaq
                  // Harry - we want it to be null, we check if its null where this is implemented and tp to spawn if it is.
                  // Harry - On a second note, ill add a method
                  Location loc;

                  if (letWorldBeNull) {
                        loc = new Location(Bukkit.getWorld(world_name), x, y, z, yaw, pitch);
                  } else {
                        if (Bukkit.getWorld(world_name) != null) {
                              loc = new Location(Bukkit.getWorld(world_name), x, y, z, yaw, pitch);
                        } else {
                              Location l_spawn = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
                              l_spawn.setPitch(2);
                              l_spawn.setYaw(-179F);
                              loc = l_spawn;
                        }
                  }
                  return loc;
            }

            return null;
      }

      public static String convertHomeMapToString(HashMap<String, Location> hmap) {
            String s = "";
            for (Entry<String, Location> data : hmap.entrySet()) {
                  s += data.getKey() + "@" + convertLocationToString(data.getValue()) + "@HOME_SPLIT@";
            }

            return s;
      }

      public static HashMap<String, Location> convertStringToHomeMap(String s) {
            HashMap<String, Location> home_map = new HashMap<>();

            if (s.contains("@HOME_SPLIT@")) {
                  for (String data : s.split("@HOME_SPLIT@")) {
                        if (!(data.contains("@"))) {
                              continue;
                        }
                        String hname = data.split("@")[0];
                        Location hloc = convertStringToLocation(data.split("@")[1]);
                        home_map.put(hname, hloc);
                  }
            }

            return home_map;
      }
}
