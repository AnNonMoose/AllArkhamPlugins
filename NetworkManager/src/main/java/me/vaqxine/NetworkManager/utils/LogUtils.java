/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.vaqxine.NetworkManager.utils;

import com.google.gson.Gson;
import me.vaqxine.NetworkManager.NetworkManager;

/**
 *
 * @author devan_000
 */
public class LogUtils {

      private static final NetworkManager plugin = NetworkManager.getPlugin();
      private static final Gson gson = new Gson();

      public static String getAndClearLogLines() {
            try {
                  return gson.toJson(plugin.loggedConsoleLines);
            } finally {
                  plugin.loggedConsoleLines.clear();
            }
      }

}
