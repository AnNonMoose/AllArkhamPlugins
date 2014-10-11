/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync.utils;

import java.io.IOException;
import org.apache.commons.lang.SerializationException;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

/**
 *
 * @author devan_000
 */
public class InventoryUtils {

      public static byte[] inventoryToBytes(ItemStack[] contents) {
            if (contents == null) {
                  return null;
            }
            try {
                  return CompressionUtils.compress(SerializationUtils.serializeItemStacks(contents));
            } catch (SerializationException | IOException ex) {
                  if (!(ex instanceof NullPointerException)) {
                        ex.printStackTrace();
                  }
            }
            return null;
      }

      public static ItemStack[] inventoryFromBytes(byte[] bytes) {
            if (bytes == null) {
                  return null;
            }
            try {
                  return SerializationUtils.deserializeItemStacks(CompressionUtils.uncompress(bytes));
            } catch (SerializationException | ParseException | IOException | ClassNotFoundException ex) {
                  ex.printStackTrace();
            }
            return null;
      }

}
