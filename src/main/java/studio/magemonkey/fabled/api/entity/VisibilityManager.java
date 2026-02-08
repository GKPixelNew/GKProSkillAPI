package studio.magemonkey.fabled.api.entity;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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
        private final UUID casterUuid;
        @Getter
        private final VisibilityMode mode;
        
        public VisibilityData(Entity entity, UUID casterUuid, VisibilityMode mode) {
            this.entity = entity;
            this.casterUuid = casterUuid;
            this.mode = mode;
        }
    }
    
    private static final Map<UUID, VisibilityData> trackedEntities = new ConcurrentHashMap<>();
    
    /**
     * Registers an entity with visibility restrictions
     * 
     * @param entity the entity to manage visibility for
     * @param caster the caster who created/controls the entity
     * @param mode the visibility mode to apply
     */
    public static void register(Entity entity, LivingEntity caster, VisibilityMode mode) {
        if (mode == VisibilityMode.EVERYONE) {
            // No need to track entities visible to everyone
            unregister(entity);
            return;
        }
        
        UUID casterUuid = caster != null ? caster.getUniqueId() : null;
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
        VisibilityData data = trackedEntities.remove(entity.getUniqueId());
        if (data != null) {
            // Show entity to all players again
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showEntity(Fabled.inst(), entity);
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
        if (canSee(player, data)) {
            player.showEntity(Fabled.inst(), data.getEntity());
        } else {
            player.hideEntity(Fabled.inst(), data.getEntity());
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
            if (data.getEntity().isValid()) {
                applyVisibilityTo(player, data);
            }
        }
    }
    
    /**
     * Cleans up invalid entities from the tracker
     */
    public static void cleanUp() {
        trackedEntities.entrySet().removeIf(entry -> !entry.getValue().getEntity().isValid());
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
