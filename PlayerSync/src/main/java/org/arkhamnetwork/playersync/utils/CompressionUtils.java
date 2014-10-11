/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import lombok.Cleanup;

public class CompressionUtils {

      public static byte[] compress(byte[] uncomp) {
            if (uncomp == null) {
                  return new byte[0];
            }

            try {
                  @Cleanup
                  ByteArrayOutputStream os = new ByteArrayOutputStream();
                  try (GZIPOutputStream compressor = new GZIPOutputStream(os)) {
                        compressor.write(uncomp);
                  }
                  return os.toByteArray();
            } catch (IOException ex) {
            }
            return null;
      }

      public static byte[] uncompress(byte[] comp) {
            if (comp == null) {
                  return new byte[0];
            }

            GZIPInputStream decompressor = null;
            try {
                  @Cleanup
                  ByteArrayInputStream is = new ByteArrayInputStream(comp);
                  decompressor = new GZIPInputStream(is);
                  ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                  byte[] b = new byte[256];
                  int tmp;
                  while ((tmp = decompressor.read(b)) != -1) {
                        buffer.write(b, 0, tmp);
                  }
                  buffer.close();
                  return buffer.toByteArray();
            } catch (IOException ex) {
            } finally {
                  try {
                        if (decompressor != null) {
                              decompressor.close();
                        }
                  } catch (IOException ex) {
                  }
            }
            return null;
      }

}
