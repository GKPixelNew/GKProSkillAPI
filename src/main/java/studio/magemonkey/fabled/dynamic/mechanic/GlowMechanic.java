package studio.magemonkey.fabled.dynamic.mechanic;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import studio.magemonkey.fabled.Fabled;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.GlowMechanic
 */
public class GlowMechanic extends MechanicComponent {
    private static final String DURATION = "duration";
    private static final byte GLOWING_FLAG = 0x40;
    
    // Track active glow effects: viewer UUID -> Set of target entity IDs
    private static final Map<UUID, Set<Integer>> activeGlowTargets = new ConcurrentHashMap<>();
    // Track active glow tasks: viewer UUID -> (target entity ID -> task)
    private static final Map<UUID, Map<Integer, BukkitTask>> activeGlowTasks = new ConcurrentHashMap<>();
    
    private static boolean listenerRegistered = false;
    
    private static void ensureListenerRegistered() {
        if (listenerRegistered) return;
        listenerRegistered = true;
        
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListenerAbstract(PacketListenerPriority.HIGH) {
            @Override
            public void onPacketSend(PacketSendEvent event) {
                if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) return;
                if (!(event.getPlayer() instanceof Player player)) return;
                
                var playerUuid = player.getUniqueId();
                var glowingTargets = activeGlowTargets.get(playerUuid);
                if (glowingTargets == null || glowingTargets.isEmpty()) return;
                
                var packet = new WrapperPlayServerEntityMetadata(event);
                int entityId = packet.getEntityId();
                
                if (!glowingTargets.contains(entityId)) return;
                
                // Find and modify the entity flags (index 0)
                List<EntityData<?>> metadata = new ArrayList<>(packet.getEntityMetadata());
                boolean foundFlags = false;
                
                for (int i = 0; i < metadata.size(); i++) {
                    EntityData<?> data = metadata.get(i);
                    if (data.getIndex() == 0 && data.getType() == EntityDataTypes.BYTE) {
                        // Add glowing flag to existing flags
                        byte currentFlags = (byte) data.getValue();
                        byte newFlags = (byte) (currentFlags | GLOWING_FLAG);
                        metadata.set(i, new EntityData(0, EntityDataTypes.BYTE, newFlags));
                        foundFlags = true;
                        break;
                    }
                }
                
                // If flags weren't in the packet, we need to add them
                if (!foundFlags) {
                    // Get the entity and compute flags
                    Entity entity = null;
                    for (var world : Bukkit.getWorlds()) {
                        for (var e : world.getEntities()) {
                            if (e.getEntityId() == entityId) {
                                entity = e;
                                break;
                            }
                        }
                        if (entity != null) break;
                    }
                    
                    if (entity != null) {
                        byte flags = GLOWING_FLAG;
                        if (entity.getFireTicks() > 0) flags |= 0x01;
                        if (entity instanceof LivingEntity le && le.isSneaking()) flags |= 0x02;
                        if (entity instanceof Player p && p.isSprinting()) flags |= 0x08;
                        if (entity instanceof LivingEntity le && le.isSwimming()) flags |= 0x10;
                        if (entity instanceof LivingEntity le && le.isInvisible()) flags |= 0x20;
                        metadata.add(new EntityData(0, EntityDataTypes.BYTE, flags));
                    }
                }
                
                packet.setEntityMetadata(metadata);
            }
        });
    }

    @Override
    public String getKey() {
        return "glow";
    }

    @Override
    public boolean execute(final LivingEntity caster,
                           final int level,
                           final List<LivingEntity> targets,
                           boolean force) {
        if (caster instanceof Player player) {
            ensureListenerRegistered();
            
            var duration = (int) parseValues(caster, DURATION, level, 5) * 20;
            var playerUuid = player.getUniqueId();
            
            for (var target : targets) {
                int entityId = target.getEntityId();
                
                // Cancel any existing glow removal task for this player-target pair
                var playerTasks = activeGlowTasks.computeIfAbsent(playerUuid, k -> new ConcurrentHashMap<>());
                var existingTask = playerTasks.remove(entityId);
                if (existingTask != null) {
                    existingTask.cancel();
                }
                
                // Add to active glow targets
                var glowingTargets = activeGlowTargets.computeIfAbsent(playerUuid, k -> ConcurrentHashMap.newKeySet());
                glowingTargets.add(entityId);
                
                // Get current entity flags and add glowing
                byte flags = GLOWING_FLAG;
                if (target.getFireTicks() > 0) flags |= 0x01;
                if (target.isSneaking()) flags |= 0x02;
                if (target instanceof Player p && p.isSprinting()) flags |= 0x08;
                if (target.isSwimming()) flags |= 0x10;
                if (target.isInvisible()) flags |= 0x20;
                
                // Send metadata packet with glowing flag
                var metadata = new EntityData(0, EntityDataTypes.BYTE, flags);
                var packet = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(metadata));
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
                
                // Schedule removal of glow effect
                var task = Bukkit.getScheduler().runTaskLater(Fabled.inst(), () -> {
                    // Remove from tracking
                    var tasks = activeGlowTasks.get(playerUuid);
                    if (tasks != null) {
                        tasks.remove(entityId);
                        if (tasks.isEmpty()) {
                            activeGlowTasks.remove(playerUuid);
                        }
                    }
                    
                    // Remove from active glow targets
                    var targets2 = activeGlowTargets.get(playerUuid);
                    if (targets2 != null) {
                        targets2.remove(entityId);
                        if (targets2.isEmpty()) {
                            activeGlowTargets.remove(playerUuid);
                        }
                    }
                    
                    // Remove glowing flag
                    byte resetFlags = 0;
                    if (target.getFireTicks() > 0) resetFlags |= 0x01;
                    if (target.isSneaking()) resetFlags |= 0x02;
                    if (target instanceof Player p && p.isSprinting()) resetFlags |= 0x08;
                    if (target.isSwimming()) resetFlags |= 0x10;
                    if (target.isInvisible()) resetFlags |= 0x20;
                    if (target.isGlowing()) resetFlags |= GLOWING_FLAG; // Keep if actually glowing
                    
                    var resetMetadata = new EntityData(0, EntityDataTypes.BYTE, resetFlags);
                    var resetPacket = new WrapperPlayServerEntityMetadata(entityId, Collections.singletonList(resetMetadata));
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, resetPacket);
                }, duration);
                
                // Track the new task
                playerTasks.put(entityId, task);
            }
            return true;
        }
        return false;
    }
}
