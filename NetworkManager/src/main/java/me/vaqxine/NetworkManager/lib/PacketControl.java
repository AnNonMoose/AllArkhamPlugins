package me.vaqxine.NetworkManager.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PacketControl extends HashMap<String, ArrayList<String>> {

    private static final long serialVersionUID = 8363216519688341890L;

    public void addPacket(String target_server, String data){
        if(this.containsKey(target_server))
            this.get(target_server).add(data);
        else
            this.put(target_server, new ArrayList<String>(Arrays.asList(data)));
    }
    
    public String[] getPackets(String target_server){
        if(this.containsKey(target_server))
            return this.get(target_server).toArray(new String[this.get(target_server).size()]);
        return new String[0];
    }
    
    public String[] getServers(){
        return this.keySet().toArray(new String[this.keySet().size()]);
    }
    
    public void destroy(){
        this.clear();
        return;
    }
}
