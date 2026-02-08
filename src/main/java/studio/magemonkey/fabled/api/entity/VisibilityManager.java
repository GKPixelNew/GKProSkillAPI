package studio.magemonkey.fabled.api.entity;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import studio.magemonkey.fabled.Fabled;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized manager for entity visibility.
 * Handles hiding/showing entities to specific players based on visibility modes.
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
        private final Entity entity;
        @Getter
        private final UUID entityUuid;
        @Getter
        private final EntityType entityType;
        @Getter
        private final UUID casterUuid;
        @Getter
        private final VisibilityMode mode;
        
        public VisibilityData(Entity entity, UUID casterUuid, VisibilityMode mode) {
            this.entity = entity;
            this.entityUuid = entity.getUniqueId();
            this.entityType = entity.getType();
            this.casterUuid = casterUuid;
            this.mode = mode;
        }
        
        /**
         * Validates that the entity is still the same one we registered.
         * Protects against UUID reuse when entities despawn and new ones spawn.
         */
        public boolean isStillValid() {
            if (!entity.isValid()) return false;
            // Double-check UUID and type match (protection against UUID reuse)
            return entity.getUniqueId().equals(entityUuid) && entity.getType() == entityType;
        }
    }
    
    private static final Map<UUID, VisibilityData> trackedEntities = new ConcurrentHashMap<>();
    
    /**
     * Registers an entity with visibility restrictions.
     * Only accepts Display entities (TextDisplay, ItemDisplay, BlockDisplay) and ArmorStand.
     * Never allows regular mobs/players to prevent breaking name tags.
     * 
     * @param entity the entity to manage visibility for (must be Display or ArmorStand)
     * @param caster the caster who created/controls the entity
     * @param mode the visibility mode to apply
     */
    public static void register(Entity entity, LivingEntity caster, VisibilityMode mode) {
        // Only allow Display entities and ArmorStand - never regular mobs
        if (!(entity instanceof Display) && !(entity instanceof ArmorStand)) {
            if (entity instanceof LivingEntity) {
                Fabled.inst().getLogger().warning("VisibilityManager.register() called with LivingEntity (" 
                        + entity.getType() + ") - ignoring to prevent breaking name tags");
            }
            return;
        }
        
        if (mode == VisibilityMode.EVERYONE) {
            // No need to track entities visible to everyone
            unregister(entity);
            return;
        }
        
        UUID casterUuid = caster != null ? caster.getUniqueId() : null;
        
        // Check if already registered with same settings - skip to prevent flickering
        VisibilityData existing = trackedEntities.get(entity.getUniqueId());
        if (existing != null && existing.getMode() == mode 
                && java.util.Objects.equals(existing.getCasterUuid(), casterUuid)) {
            return; // No change needed
        }
        
        VisibilityData data = new VisibilityData(entity, casterUuid, mode);
        trackedEntities.put(entity.getUniqueId(), data);
        
        // Apply visibility to all online players
        applyVisibilityToAll(data);
    }
    
    /**
     * Unregisters an entity, making it visible to everyone again
     * 
     * @param entity the entity to unregister
     */
    public static void unregister(Entity entity) {
        if (entity == null) return;
        
        VisibilityData data = trackedEntities.remove(entity.getUniqueId());
        if (data != null && entity.isValid()) {
            // Only show Display and ArmorStand entities
            if (entity instanceof Display || entity instanceof ArmorStand) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.showEntity(Fabled.inst(), entity);
                }
            }
        }
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
     * Applies visibility settings to all online players for a specific entity
     * 
     * @param data the visibility data
     */
    private static void applyVisibilityToAll(VisibilityData data) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyVisibilityTo(player, data);
        }
    }
    
    /**
     * Applies visibility setting for a specific player and entity
     * 
     * @param player the player to apply visibility to
     * @param data the visibility data
     */
    private static void applyVisibilityTo(Player player, VisibilityData data) {
        // Use isStillValid() to ensure this is the same entity we registered
        // This protects against UUID reuse when chunks unload/reload
        if (!data.isStillValid()) {
            return;
        }
        
        Entity entity = data.getEntity();
        // Safety check - only allow Display and ArmorStand, never regular mobs
        if (!(entity instanceof Display) && !(entity instanceof ArmorStand)) {
            return;
        }
        
        if (canSee(player, data)) {
            player.showEntity(Fabled.inst(), entity);
        } else {
            player.hideEntity(Fabled.inst(), entity);
        }
    }
    
    /**
     * Called when a player joins to apply visibility for all tracked entities.
     * Should be called from MainListener.init()
     * 
     * @param player the player who joined
     */
    public static void applyVisibilityForPlayer(Player player) {
        for (VisibilityData data : trackedEntities.values()) {
            if (data.isStillValid()) {
                applyVisibilityTo(player, data);
            }
        }
    }
    
    /**
     * Cleans up invalid entities from the tracker.
     * Removes entries where the entity is no longer valid or UUID was reused.
     */
    public static void cleanUp() {
        trackedEntities.entrySet().removeIf(entry -> !entry.getValue().isStillValid());
    }
    
    /**
     * Clears all tracked entities
     */
    public static void clearAll() {
        trackedEntities.clear();
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
