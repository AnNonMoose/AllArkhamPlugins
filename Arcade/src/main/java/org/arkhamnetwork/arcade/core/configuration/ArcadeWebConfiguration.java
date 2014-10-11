/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.core.configuration;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author devan_000
 */
public class ArcadeWebConfiguration {

    @Getter
    @Setter
    private static String bungeeLobbyServerName = null;

    @Getter
    @Setter
    private static List<String> lobbySignLines = null;

}
