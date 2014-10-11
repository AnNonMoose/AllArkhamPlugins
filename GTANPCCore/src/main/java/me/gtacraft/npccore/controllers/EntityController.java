package me.gtacraft.npccore.controllers;

import me.gtacraft.npccore.struct.Controller;
import me.gtacraft.npccore.struct.Gang;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class EntityController implements Controller {

    public final CopyOnWriteArrayList<UUID> entityUUIDs = new CopyOnWriteArrayList<UUID>();
    public final ConcurrentHashMap<UUID, Gang> gangs = new ConcurrentHashMap<UUID, Gang>();

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
