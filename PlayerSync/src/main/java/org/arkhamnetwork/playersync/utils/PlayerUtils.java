/*
 * Copyright (C) 2014 Harry Devane
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.arkhamnetwork.playersync.utils;

import java.io.File;
import java.util.UUID;
import org.arkhamnetwork.playersync.PlayerSync;
import org.bukkit.World;

/**
 * https://www.github.com/Harry5573OP
 *
 * @author Harry5573OP
 */
public class PlayerUtils {

      private static final PlayerSync plugin = PlayerSync.get();

      /**
       * I CHANGED THIS
       * @param uuid
       * @param playerName
       */
      public static void deletePlayerdataASync(UUID uuid, String playerName) {
            for (World world : plugin.worlds) {
                  String worldname = world.getName();
                  new File(worldname + "/playerdata/" + uuid + ".dat").delete();
                  new File(worldname + "/stats/" + uuid + ".json").delete();
            }
            new File("plugins/Essentials/userdata/" + uuid + ".yml").delete();
            new File("plugins/ArkhamColorChat/users/" + playerName + ".yml").delete();
            new File("plugins/Marriage/playerdata/" + playerName + ".yml").delete();
            new File("plugins/RedeemMCMMO/users/" + playerName + ".yml").delete();
            new File("plugins/MagicCrates/Users/" + playerName + ".yml").delete();
      }

}
