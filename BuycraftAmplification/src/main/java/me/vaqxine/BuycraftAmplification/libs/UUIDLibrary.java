package me.vaqxine.BuycraftAmplification.libs;
 
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.UUID;

import net.minecraft.util.com.google.gson.Gson;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.base.Charsets;
 
public class UUIDLibrary {
       
        /*
         * Class made by BigTeddy98.
         *
         * UUIDLibrary is class to convert UUID <-> Playername
         *
         * 1. No warranty is given or implied.
         * 2. All damage is your own responsibility.
         * 3. If you want to use this in your plugins, a credit would we appreciated.
         */
 
        private static Gson gson = new Gson();
 
        public static String getNameFromUUID(String uuid) {
                String name = null;
                try {
                        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
                        URLConnection connection = url.openConnection();
                        Scanner jsonScanner = new Scanner(connection.getInputStream(), "UTF-8");
                        String json = jsonScanner.next();
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(json);
                        name = (String) ((JSONObject) obj).get("name");
                        jsonScanner.close();
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
                return name;
        }
 
        public static String getUUIDFromName(String name) {
                try {
                        ProfileData profC = new ProfileData(name);
                        String UUID = null;
                        int i = 1;
                                PlayerProfile[] result = post(new URL("https://api.mojang.com/profiles/page/" + i), Proxy.NO_PROXY, gson.toJson(profC).getBytes());
                                if (result.length == 0) {
                                        return null;
                                }
                                UUID = result[0].getId();
                        
                        
                        UUID = UUID.replaceAll(                                            
                                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",                            
                                "$1-$2-$3-$4-$5"); 
                        
                        return UUID;
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return null;
        }

        public static UUID getSpoofedUUIDFromName(String name){
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        }
        
        private static PlayerProfile[] post(URL url, Proxy proxy, byte[] bytes) throws IOException {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);
 
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.write(bytes);
                out.flush();
                out.close();
 
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer response = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                }
                reader.close();
                return gson.fromJson(response.toString(), SearchResult.class).getProfiles();
        }
 
        private static class PlayerProfile {
                private String id;
 
                public String getId() {
                        return id;
                }
        }
 
        private static class SearchResult {
                private PlayerProfile[] profiles;
 
                public PlayerProfile[] getProfiles() {
                        return profiles;
                }
        }
 
        private static class ProfileData {
 
                @SuppressWarnings("unused")
                private String name;
                @SuppressWarnings("unused")
                private String agent = "minecraft";
 
                public ProfileData(String name) {
                        this.name = name;
                }
        }
}