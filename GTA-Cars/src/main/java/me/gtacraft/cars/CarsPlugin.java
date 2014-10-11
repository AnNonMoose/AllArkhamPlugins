package me.gtacraft.cars;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.gtacraft.cars.controllers.MotionController;
import me.gtacraft.cars.events.CarEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Created by tacticalsk8er on 4/27/14.
 */
public class CarsPlugin extends JavaPlugin {

    private static CarsPlugin instance;
    private ProtocolManager protocolManager;

    public static HashMap<Player, Minecart> playersInCars = new HashMap<>();
    public static HashMap<Player, Float> playersUsingNos = new HashMap<>();

    @Override
    public void onLoad() {
        setupProtocolLib();
    }

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new CarEvents(), this);
    }

    private void setupProtocolLib() {
        protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                    PacketContainer packetContainer = event.getPacket();
                    float sideways = packetContainer.getFloat().read(0);
                    float forwards = packetContainer.getFloat().read(1);
                    MotionController.moveCar(event.getPlayer(), forwards, sideways);
                }
            }
        });
    }

    public static CarsPlugin getInstance() {
        return instance;
    }
}
