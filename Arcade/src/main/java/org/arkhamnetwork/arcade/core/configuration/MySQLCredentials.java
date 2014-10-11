/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.core.configuration;

import lombok.Getter;

/**
 *
 * @author devan_000
 */
public class MySQLCredentials {

    public MySQLCredentials(String HOSTNAME, int PORT, String USER,
            String PASSWORD, String DATABASENAME) {
        this.HOSTNAME = HOSTNAME;
        this.PORT = PORT;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        this.DATABASENAME = DATABASENAME;
    }

    @Getter
    private String HOSTNAME;
    @Getter
    private int PORT;
    @Getter
    private String USER;
    @Getter
    private String PASSWORD;
    @Getter
    private String DATABASENAME;

}
