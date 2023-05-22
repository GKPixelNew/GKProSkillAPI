package com.sucy.skill.hook;

import com.sucy.skill.api.event.BlockChangeEvent;
import dev.robothanzo.gk.replay.api.IReplayHook;
import dev.robothanzo.gk.replay.api.ReplayAPI;
import dev.robothanzo.gk.replay.replaysystem.data.ActionData;
import dev.robothanzo.gk.replay.replaysystem.data.types.*;
import dev.robothanzo.gk.replay.replaysystem.replaying.Replayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public class GKReplayHook implements Listener, IReplayHook {
    private volatile List<PacketData> records = new LinkedList<>();

    public GKReplayHook(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ReplayAPI.getInstance().registerHook(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockChange(BlockChangeEvent event) {
        records.add(new BlockChangeData(LocationData.fromLocation(event.getTo().getLocation()),
                new ItemData(SerializableItemStack.fromMaterial(event.getFrom().getType())),
                new ItemData(SerializableItemStack.fromMaterial(event.getTo().getType()))));
    }

    @Override
    public List<PacketData> onRecord(String playerName) {
        var r = records;
        records = new LinkedList<>();
        return r;
    }

    @Override
    public void onPlay(ActionData data, Replayer replayer) {

    }
}
