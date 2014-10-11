package me.gtacraft.plugins.chests.util;

import org.fusesource.jansi.Ansi;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class Logger {

    private final String ansi_green = Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString();
    private final String ansi_yellow = Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString();
    private final String ansi_red = Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString();

    private final String ansi_underline = Ansi.ansi().a(Ansi.Attribute.UNDERLINE).boldOff().toString();
    private final String ansi_reset = Ansi.ansi().a(Ansi.Attribute.RESET).boldOff().toString();

    public void notice(String s, Class<?> c){
        System.out.println(ansi_green + "[GTAChests (" + c.getSimpleName() + ")] " + s + ansi_reset);
    }

    public void debug(String s, Class<?> c){
        System.out.println(ansi_yellow + "(DE) " + ansi_reset + "[GTAChests (" + c.getSimpleName() + ")] " + s + ansi_reset);
    }

    public void log(String s, Class<?> c){
        System.out.println("[GTAChests (" + c.getSimpleName() + ")] " + s);
    }

    public void warning(String s, Class<?> c){
        System.out.println(ansi_yellow + "[GTAChests (" + c.getSimpleName() + ")] " + s + ansi_reset);
    }

    public void error(String s, Class<?> c){
        System.out.println(ansi_red + "[GTAChests (" + c.getSimpleName() + ")] " + s + ansi_reset);
    }
}