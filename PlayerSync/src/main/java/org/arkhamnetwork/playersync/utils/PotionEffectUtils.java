/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync.utils;

import java.util.Collection;
import org.apache.commons.lang.SerializationException;
import org.bukkit.potion.PotionEffect;
import org.json.simple.parser.ParseException;

/**
 *
 * @author devan_000
 */
public class PotionEffectUtils {

      public static byte[] effectsToBytes(Collection<PotionEffect> effects) {
            int i = 0;
            PotionEffect[] ef = new PotionEffect[effects.size()];
            for (PotionEffect effect : effects) {
                  ef[i++] = effect;
            }

            return CompressionUtils.compress(SerializationUtils.serializePotionEffects(ef));
      }

      public static PotionEffect[] effectsFromBytes(byte[] bytes) throws SerializationException, ParseException {
            return SerializationUtils.deserializePotionEffects(CompressionUtils.uncompress(bytes));
      }

}
