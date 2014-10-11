/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.core.configuration;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.arkhamnetwork.arcade.commons.server.ServerType;

/**
 *
 * @author devan_000
 */
public class ArcadeConfiguration {

    @Getter
    @Setter
    private static ServerType serverType;

    @Getter
    @Setter
    private static List<String> gamemodes;

    @Getter
    @Setter
    private static String webServerURL;

    @Getter
    @Setter
    private static MySQLCredentials mysqlCredentials;

    @Getter
    @Setter
    private static String serverName;
}
