/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.hooks;

/**
 *
 * @author devan_000
 */
public class MinecraftServerHook {

    public static void disableAutoSave() {
        net.minecraft.server.v1_7_R3.MinecraftServer.getServer().autosavePeriod = 0;
    }

}
