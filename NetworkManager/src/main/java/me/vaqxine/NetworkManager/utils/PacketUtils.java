package me.vaqxine.NetworkManager.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import me.vaqxine.NetworkManager.NetworkManager;

public class PacketUtils {

      public static void saveUrl(final String filename, final String urlString) throws MalformedURLException, IOException {
            NetworkManager.log.debug("Downloading " + filename + " from " + urlString + "...", PacketUtils.class);
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            try {
                  in = new BufferedInputStream(new URL(urlString).openStream());
                  fout = new FileOutputStream(filename);

                  final byte data[] = new byte[1024];
                  int count;
                  while ((count = in.read(data, 0, 1024)) != -1) {
                        fout.write(data, 0, count);
                  }
            } finally {
                  if (in != null) {
                        in.close();
                  }
                  if (fout != null) {
                        fout.close();
                  }
            }
            NetworkManager.log.debug("Download of " + filename + " complete!", PacketUtils.class);
      }
}
