package me.gtacraft.ai.util;

import com.google.common.collect.Lists;
import me.gtacraft.ai.GTANpc;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by Connor on 6/20/14. Designed for the GTA-AI project.
 */

public class Util {

    private static LinkedList<String> CHARACTER_NAMES = Lists.newLinkedList();

    public static void fillNames() throws IOException {
        try {
            File file = new File(GTANpc.getInstance().getDataFolder(), "names.txt");
            BufferedReader read = new BufferedReader(new FileReader(file));

            String line = "";

            while ((line = read.readLine()) != null) {
                CHARACTER_NAMES.add(line);
                System.out.println(line);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String randomName() {
        //fetch random name pls
        return null;
    }

    public static void main(String[] args) {
        try {
            File file = new File("C:\\Users\\Connor\\Desktop\\GTA Server\\GTA-AI\\src\\main\\resources\\names.txt");
            BufferedReader read = new BufferedReader(new FileReader(file));

            String line = "";

            while ((line = read.readLine()) != null) {
                CHARACTER_NAMES.add(line);
                System.out.println(line);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
