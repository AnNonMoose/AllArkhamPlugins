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
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * https://www.github.com/Harry5573OP
 *
 * @author Harry5573OP
 */
public class FileUtils {

      public static void saveBytesToFile(byte[] bytes, File file) {
            if (file.exists()) {
                  file.delete();
            }

            if (bytes == null || bytes.length == 0) {
                  return;
            }

            try {
                  file.createNewFile();
            } catch (IOException ex) {
                  ex.printStackTrace();
                  return;
            }

            try {
                  try (FileOutputStream stream = new FileOutputStream(file)) {
                        stream.write(bytes);
                  }
            } catch (IOException ex) {
                  ex.printStackTrace();
            }
      }

}
