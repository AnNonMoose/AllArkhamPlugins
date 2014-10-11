package org.arkhamnetwork.playersync.utils;

import java.util.HashMap;
import java.util.Map.Entry;

public class LogicUtils {

      public static HashMap<String, Long> getKitCooldownsFromString(String kit_data) {
            HashMap<String, Long> kit_map = new HashMap<>();
            if (kit_data.contains(",")) {
                  for (String s : kit_data.split(",")) {
                        String kit_name = s.split("=")[0];
                        long kit_use = Long.parseLong(s.split("=")[1]);
                        kit_map.put(kit_name, kit_use);
                  }
            }

            return kit_map;
      }

      public static String getStringFromKitCooldowns(HashMap<String, Long> kit_map) {
            String kit_string = "";
            for (Entry<String, Long> kit_data : kit_map.entrySet()) {
                  kit_string += kit_data.getKey() + "=" + kit_data.getValue() + ",";
            }

            if (kit_string.endsWith(",")) {
                  kit_string = kit_string.substring(0, kit_string.length() - 1);
            }

            return kit_string;
      }
}
