package studio.magemonkey.fabled.api.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized manager for entity visibility using packet interception.
 * Handles hiding/showing entities to specific players based on visibility modes.
 * Uses PacketEvents to intercept SPAWN_ENTITY packets instead of Bukkit's hideEntity/showEntity
 * which doesn't work properly for passenger entities.
 */
public class VisibilityManager {
    
    /**
     * Visibility mode for entities
     */
    public enum VisibilityMode {
        /** Visible to everyone (default) */
        EVERYONE,
        /** Only visible to the caster */
        CASTER_ONLY,
        /** Visible to caster and allies (same team) */
        CASTER_AND_ALLIES,
        /** Only visible to enemies (not caster, not allies) */
        ENEMIES_ONLY,
        /** Invisible to everyone */
        NONE
    }
    
    /**
     * Data class holding visibility info for an entity
     */
    public static class VisibilityData {
        @Getter
        private final int entityId;
        @Getter
        private final UUID entityUuid;
        @Getter
        private final UUID casterUuid;
        @Getter
        private final VisibilityMode mode;
        
        public VisibilityData(int entityId, UUID entityUuid, UUID casterUuid, VisibilityMode mode) {
            this.entityId = entityId;
            this.entityUuid = entityUuid;
            this.casterUuid = casterUuid;
            this.mode = mode;
        }
    }
    
    // Track entities by entity ID (packets use entity ID, not UUID)
    private static final Map<Integer, VisibilityData> trackedEntities = new ConcurrentHashMap<>();
    // Also track by UUID for quick lookup during registration
    private static final Map<UUID, Integer> uuidToEntityId = new ConcurrentHashMap<>();
    
    private static boolean listenerRegistered = false;
    
    /**
     * Ensures the packet listener is registered. Called lazily on first use.
     */
    private static void ensureListenerRegistered() {
        if (listenerRegistered) return;
        listenerRegistered = true;
        
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListenerAbstract(PacketListenerPriority.HIGH) {
            @Override
            public void onPacketSend(PacketSendEvent event) {
                if (!(event.getPlayer() instanceof Player player)) return;
                
                // Handle entity spawn packets
                if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
                    var packet = new WrapperPlayServerSpawnEntity(event);
                    int entityId = packet.getEntityId();
                    
                    VisibilityData data = trackedEntities.get(entityId);
                    if (data != null && !canSee(player, data)) {
                        event.setCancelled(true);
                    }
                    return;
                }
                
                // Handle entity metadata packets
                if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
                    var packet = new WrapperPlayServerEntityMetadata(event);
                    int entityId = packet.getEntityId();
                    
                    VisibilityData data = trackedEntities.get(entityId);
                    if (data != null && !canSee(player, data)) {
                        event.setCancelled(true);
                    }
                    return;
                }
                
                // Handle set passengers packets - need to filter out hidden passengers
                if (event.getPacketType() == PacketType.Play.Server.SET_PASSENGERS) {
                    var packet = new WrapperPlayServerSetPassengers(event);
                    int[] passengers = packet.getPassengers();
                    
                    // Check if any passenger is hidden from this player
                    boolean hasHiddenPassenger = false;
                    for (int passengerId : passengers) {
                        VisibilityData data = trackedEntities.get(passengerId);
                        if (data != null && !canSee(player, data)) {
                            hasHiddenPassenger = true;
                            break;
                        }
                    }
                    
                    if (hasHiddenPassenger) {
                        // Filter out hidden passengers
                        int[] visiblePassengers = java.util.Arrays.stream(passengers)
                                .filter(id -> {
                                    VisibilityData data = trackedEntities.get(id);
                                    return data == null || canSee(player, data);
                                })
                                .toArray();
                        
                        if (visiblePassengers.length == 0 && passengers.length > 0) {
                            // All passengers hidden - cancel the packet entirely
                            event.setCancelled(true);
                        } else if (visiblePassengers.length != passengers.length) {
                            // Some passengers hidden - modify packet to only show visible ones
                            packet.setPassengers(visiblePassengers);
                        }
                    }
                    return;
                }
                
                // Handle entity movement/position packets
                if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE
                        || event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION
                        || event.getPacketType() == PacketType.Play.Server.ENTITY_ROTATION
                        || event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT
                        || event.getPacketType() == PacketType.Play.Server.ENTITY_HEAD_LOOK
                        || event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY
                        || event.getPacketType() == PacketType.Play.Server.ENTITY_ANIMATION
                        || event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT
                        || event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT
                        || event.getPacketType() == PacketType.Play.Server.ENTITY_SOUND_EFFECT
                        || event.getPacketType() == PacketType.Play.Server.UPDATE_ATTRIBUTES) {
                    // These packets have entity ID as first integer
                    try {
                        // Get entity ID from packet - most entity packets have it at index 0
                        Object rawPacket = event.getLastUsedWrapper();
                        if (rawPacket != null) {
                            var method = rawPacket.getClass().getMethod("getEntityId");
                            int entityId = (int) method.invoke(rawPacket);
                            
                            VisibilityData data = trackedEntities.get(entityId);
                            if (data != null && !canSee(player, data)) {
                                event.setCancelled(true);
                            }
                        }
                    } catch (Exception ignored) {
                        // If we can't get entity ID, let the packet through
                    }
                }
            }
        });
    }
    
    /**
     * Registers an entity with visibility restrictions.
     * 
     * @param entity the entity to manage visibility for
     * @param caster the caster who created/controls the entity
     * @param mode the visibility mode to apply
     */
    public static void register(Entity entity, LivingEntity caster, VisibilityMode mode) {
        ensureListenerRegistered();
        
        if (mode == VisibilityMode.EVERYONE) {
            // No need to track entities visible to everyone
            unregister(entity);
            return;
        }
        
        int entityId = entity.getEntityId();
        UUID entityUuid = entity.getUniqueId();
        UUID casterUuid = caster != null ? caster.getUniqueId() : null;
        
        // Check if already registered with same settings
        VisibilityData existing = trackedEntities.get(entityId);
        if (existing != null && existing.getMode() == mode 
                && java.util.Objects.equals(existing.getCasterUuid(), casterUuid)) {
            return; // No change needed
        }
        
        VisibilityData data = new VisibilityData(entityId, entityUuid, casterUuid, mode);
        trackedEntities.put(entityId, data);
        uuidToEntityId.put(entityUuid, entityId);
        
        // Send destroy packet to players who shouldn't see this entity
        // This handles the case where entity was already spawned before we registered
        sendDestroyToNonViewers(entity, data);
    }
    
    /**
     * Sends DESTROY_ENTITIES packet to all players who shouldn't see this entity.
     * Used to hide an already-spawned entity.
     */
    private static void sendDestroyToNonViewers(Entity entity, VisibilityData data) {
        int entityId = entity.getEntityId();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!canSee(player, data)) {
                var destroyPacket = new WrapperPlayServerDestroyEntities(entityId);
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
            }
        }
    }
    
    /**
     * Unregisters an entity, making it visible to everyone again.
     * 
     * @param entity the entity to unregister
     */
    public static void unregister(Entity entity) {
        if (entity == null) return;
        
        int entityId = entity.getEntityId();
        UUID entityUuid = entity.getUniqueId();
        
        trackedEntities.remove(entityId);
        uuidToEntityId.remove(entityUuid);
        
        // Note: If the entity should now be visible to players who couldn't see it before,
        // the server will naturally send spawn packets when they come into range.
        // For immediate visibility, we'd need to force-send spawn packets, but that's complex.
        // In practice, text displays are short-lived so this shouldn't be an issue.
    }
    
    /**
     * Checks if a player can see an entity based on visibility settings
     * 
     * @param viewer the player viewing
     * @param data the visibility data for the entity
     * @return true if the player can see the entity
     */
    public static boolean canSee(Player viewer, VisibilityData data) {
        if (data == null || data.getMode() == VisibilityMode.EVERYONE) {
            return true;
        }
        
        UUID casterUuid = data.getCasterUuid();
        boolean isCaster = casterUuid != null && viewer.getUniqueId().equals(casterUuid);
        boolean isAlly = false;
        
        if (casterUuid != null) {
            Player caster = Bukkit.getPlayer(casterUuid);
            if (caster != null) {
                isAlly = areAllies(caster, viewer);
            }
        }
        
        return switch (data.getMode()) {
            case EVERYONE -> true;
            case CASTER_ONLY -> isCaster;
            case CASTER_AND_ALLIES -> isCaster || isAlly;
            case ENEMIES_ONLY -> !isCaster && !isAlly;
            case NONE -> false;
        };
    }
    
    /**
     * Checks if two players are allies (on the same scoreboard team)
     * 
     * @param player1 first player
     * @param player2 second player
     * @return true if they are on the same team
     */
    public static boolean areAllies(Player player1, Player player2) {
        if (player1.equals(player2)) {
            return true;
        }
        
        Team team1 = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player1.getName());
        Team team2 = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player2.getName());
        
        return team1 != null && team1.equals(team2);
    }
    
    /**
     * Called when a player joins to send destroy packets for entities they shouldn't see.
     * Should be called from MainListener.init()
     * 
     * @param player the player who joined
     */
    public static void applyVisibilityForPlayer(Player player) {
        for (VisibilityData data : trackedEntities.values()) {
            if (!canSee(player, data)) {
                // Send destroy packet for this entity
                var destroyPacket = new WrapperPlayServerDestroyEntities(data.getEntityId());
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
            }
        }
    }
    
    /**
     * Cleans up entries for entities that no longer exist.
     * Call this periodically or when entities are removed.
     */
    public static void cleanUp() {
        var iterator = trackedEntities.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            VisibilityData data = entry.getValue();
            
            // Check if entity still exists
            boolean exists = false;
            for (var world : Bukkit.getWorlds()) {
                for (var entity : world.getEntities()) {
                    if (entity.getEntityId() == data.getEntityId() 
                            && entity.getUniqueId().equals(data.getEntityUuid())) {
                        exists = true;
                        break;
                    }
                }
                if (exists) break;
            }
            
            if (!exists) {
                iterator.remove();
                uuidToEntityId.remove(data.getEntityUuid());
            }
        }
    }
    
    /**
     * Clears all tracked entities
     */
    public static void clearAll() {
        trackedEntities.clear();
        uuidToEntityId.clear();
    }
    
    /**
     * Parses a visibility mode from a string
     * 
     * @param value the string value
     * @return the visibility mode, defaults to EVERYONE
     */
    public static VisibilityMode parseMode(String value) {
        if (value == null) {
            return VisibilityMode.EVERYONE;
        }
        return switch (value.toLowerCase().replace("_", "-").replace(" ", "-")) {
            case "caster-only", "caster", "only-caster" -> VisibilityMode.CASTER_ONLY;
            case "caster-and-allies", "allies", "caster-allies" -> VisibilityMode.CASTER_AND_ALLIES;
            case "enemies-only", "enemies", "only-enemies" -> VisibilityMode.ENEMIES_ONLY;
            case "none", "invisible", "hidden" -> VisibilityMode.NONE;
            default -> VisibilityMode.EVERYONE;
        };
    }
}
