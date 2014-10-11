/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

/**
 *
 * @author devan_000
 */
public class URLUtils {

    public static String toWorkingURL(String string) {
        if (!string.endsWith("/")) {
            string = string + "/";
        }

        return string;
    }

}
