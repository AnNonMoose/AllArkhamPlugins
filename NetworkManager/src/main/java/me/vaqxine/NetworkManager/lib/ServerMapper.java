package me.vaqxine.NetworkManager.lib;

import java.util.HashMap;

public class ServerMapper extends HashMap<String, String>{
    public HashMap<String, Integer[]> server_player_counts = new HashMap<String, Integer[]>();
    
    public int getCurrentPlayers(String server_prefix){
        return this.server_player_counts.containsKey(server_prefix) ? this.server_player_counts.get(server_prefix)[0] : 0;
    }
    
    public int getMaxPlayers(String server_prefix){
        return this.server_player_counts.containsKey(server_prefix) ? this.server_player_counts.get(server_prefix)[1] : 0;
    }
    
    public void setCurrentAndMaxPlayers(String server_prefix, Integer[] current_and_max_counts){
        this.server_player_counts.put(server_prefix, current_and_max_counts);
    }
}
