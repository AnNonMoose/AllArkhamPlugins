/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import java.io.File;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 *
 * @author devan_000
 */
public class ZipUtils {

    public static void unZip(File zipFile, File outputDirectory)
            throws ZipException {
        new ZipFile(zipFile).extractAll(outputDirectory.getPath());
    }

}
