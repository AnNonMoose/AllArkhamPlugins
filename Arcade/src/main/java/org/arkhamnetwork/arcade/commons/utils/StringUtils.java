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
public class StringUtils {

    public static String passwordToHashes(String password) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < password.length(); i++) {
            builder.append("*");
        }

        return builder.toString();
    }

}
