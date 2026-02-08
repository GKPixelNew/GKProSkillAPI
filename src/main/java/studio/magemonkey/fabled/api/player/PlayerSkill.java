/**
 * Fabled
 * studio.magemonkey.fabled.api.player.PlayerSkill
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.api.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.enums.SkillStatus;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.manager.AttributeManager;

/**
 * Represents player-specific data for a skill such as the player's
 * current level for the skill, the cooldown, and other related data.
 */
public final class PlayerSkill {

    private Skill       skill;
    private PlayerData  player;
    private PlayerClass parent;
    @Getter
    @Setter
    private long        cooldown;
    /**
     * -- SETTER --
     *  Sets the level of the skill. This will not update passive
     *  effects. To level up/down the skill properly, use the
     *  upgrade and downgrade methods in PlayerData.
     *
     * @param level new level of the skill
     */
    @Setter
    private int         level;
    private boolean     external;

    // Stock system fields
    /**
     * Current available stock charges (-1 means uninitialized, will be set to maxStock)
     */
    @Getter
    @Setter
    private int         currentStock = -1;

    /**
     * Timestamp when the next stock will regenerate (0 if not regenerating)
     */
    @Getter
    @Setter
    private long        stockRegenEndTime = 0;

    /**
     * Timestamp of the last successful cast (for cast-interval check)
     */
    @Getter
    @Setter
    private long        lastCastTime = 0;

    /**
     * Constructs a new PlayerSkill. You should not need to use
     * this constructor as it is provided by the API. Get instances
     * through the PlayerData object.
     *
     * @param player owning player data
     * @param skill  skill template
     * @param parent owning player class
     */
    public PlayerSkill(PlayerData player, Skill skill, PlayerClass parent) {
        this.player = player;
        this.skill = skill;
        this.parent = parent;
        this.external = false;
    }

    /**
     * Constructs a new PlayerSkill. You should not need to use
     * this constructor as it is provided by the API. Get instances
     * through the PlayerData object.
     *
     * @param player   owning player data
     * @param skill    skill template
     * @param parent   owning player class
     * @param external whether the skill was added by an external plugin
     */
    public PlayerSkill(PlayerData player, Skill skill, PlayerClass parent, boolean external) {
        this.player = player;
        this.skill = skill;
        this.parent = parent;
        this.external = external;
    }

    /**
     * Checks whether the skill is currently unlocked
     * for the player. This requires the skill to be at least
     * level 1.
     *
     * @return true if unlocked, false otherwise
     */
    public boolean isUnlocked() {
        return level > 0;
    }

    /**
     * Retrieves the template data for this skill.
     *
     * @return skill template data
     */
    public Skill getData() {
        return skill;
    }

    /**
     * Retrieves the owning player class.
     *
     * @return owning player class
     */
    public PlayerClass getPlayerClass() {
        return parent;
    }

    /**
     * Retrieves the owning player's data.
     *
     * @return owning player's data
     */
    public PlayerData getPlayerData() {
        return player;
    }

    /**
     * Retrieves the material this skill is currently bound to.
     *
     * @return the current material bound to or null if not bound
     */
    public Material getBind() {
        return null;
    }

    /**
     * Retrieves the current level the player has the skill at
     *
     * @return current skill level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Retrieves whether the skill was added by an external plugin
     *
     * @return whether the skill was added by an external plugin
     */
    public boolean isExternal() {return external;}

    /**
     * Retrieves the cost to upgrade the skill to the next level
     *
     * @return cost to upgrade the skill to the next level
     */
    public int getCost() {
        return skill.getCost(level);
    }

    /**
     * @return total invested cost in the skill
     */
    public int getInvestedCost() {
        int total = 0;
        for (int i = 0; i < level; i++)
            total += skill.getCost(i);
        return total;

        /* Could assume the linearly scaling cost, but API allows overrides
        int x0 = skill.getCost(0);
        int dx = skill.getCost(1) - x0;
        return (x0 - dx) * level + dx * (level - 1) * level / 2;
        */
    }

    /**
     * @return mana cost to use the skill
     */
    public double getManaCost() {
        return skill.getManaCost(level, player);
    }

    /**
     * Retrieves the level requirement of the skill to get to the next level
     *
     * @return the level requirement to get to the next level
     */
    public int getLevelReq() {
        return skill.getLevelReq(level);
    }

    /**
     * Checks whether the skill is currently on cooldown
     *
     * @return true if on cooldown, false otherwise
     */
    public boolean isOnCooldown() {
        return cooldown > System.currentTimeMillis();
    }

    /**
     * Checks whether the skill is at its maximum level
     *
     * @return true if at max level, false otherwise
     */
    public boolean isMaxed() {
        return level >= skill.getMaxLevel();
    }

    /**
     * Gets the current cooldown of the skill in seconds.
     * For stock system: returns regen time if no stock, or cast-interval time if within interval.
     *
     * @return current cooldown in seconds or 0 if not on cooldown
     */
    public int getCooldownLeft() {
        if (usesStockSystem()) {
            // No stock: return regen time
            if (!hasStock()) {
                return (int) Math.ceil(getStockRegenTimeLeft());
            }
            // Within cast-interval: return interval time
            if (!isCastIntervalPassed()) {
                return (int) Math.ceil(getCastIntervalTimeLeft());
            }
            return 0;
        }
        
        // Standard cooldown
        if (isOnCooldown()) {
            return (int) ((cooldown - System.currentTimeMillis() + 999) / 1000);
        } else {
            return 0;
        }
    }

    /**
     * Gets the current cooldown of the skill in milliseconds.
     * For stock system: returns regen time if no stock, or cast-interval time if within interval.
     *
     * @return current cooldown in milliseconds or 0 if not on cooldown
     */
    public int getCooldownMillis() {
        if (usesStockSystem()) {
            // No stock: return regen time
            if (!hasStock()) {
                return (int) (getStockRegenTimeLeft() * 1000);
            }
            // Within cast-interval: return interval time
            if (!isCastIntervalPassed()) {
                return (int) (getCastIntervalTimeLeft() * 1000);
            }
            return 0;
        }
        
        // Standard cooldown
        if (isOnCooldown()) {
            return (int) (cooldown - System.currentTimeMillis());
        } else {
            return 0;
        }
    }

    /**
     * Retrieves the current ready status of the skill which could
     * be on cooldown, missing mana, or ready.
     * For skills with maxStock > 1, this checks stock availability and cast-interval instead of cooldown.
     *
     * @return the ready status of the skill
     */
    public SkillStatus getStatus() {
        // Stock system check (maxStock > 1)
        if (usesStockSystem()) {
            // Check if no stock available
            if (!hasStock()) {
                return SkillStatus.ON_COOLDOWN;
            }
            // Check if within cast-interval
            if (!isCastIntervalPassed()) {
                return SkillStatus.ON_COOLDOWN;
            }
        } else {
            // Standard cooldown check (maxStock == 1)
            if (isOnCooldown()) {
                return SkillStatus.ON_COOLDOWN;
            }
        }

        // If mana is enabled, check to see if the player has enough
        if (Fabled.getSettings().isManaEnabled()
                && player.getMana() < getManaCost()) {

            return SkillStatus.MISSING_MANA;
        }

        // The skill is available when both off cooldown and when there's enough mana
        return SkillStatus.READY;
    }

    /**
     * Adds levels to the skill. This will not update passive
     * effects. To level up/down the skill properly, use the
     * upgrade and downgrade methods in PlayerData.
     *
     * @param amount number of levels to add
     */
    public void addLevels(int amount) {
        this.level = Math.min(this.level + amount, skill.getMaxLevel());
    }

    /**
     * Sets the bind material of the skill
     *
     * @param mat new bind material
     */
    @Deprecated
    public void setBind(Material mat) {}

    /**
     * Reverts the skill back to level 0, locking it from
     * casting and refunding invested skill points
     */
    public void revert() {
        parent.givePoints(getInvestedCost());
        level = 0;
    }

    /**
     * Starts the cooldown of the skill.
     * For skills with maxStock > 1, this consumes a stock charge instead.
     */
    public void startCooldown() {
        if (usesStockSystem()) {
            consumeStock();
        } else {
            cooldown = System.currentTimeMillis() + (long) Math.floor(skill.getCooldown(level, player) * 1000L);
        }
    }

    /**
     * Refreshes the cooldown of the skill, allowing the
     * player to cast the skill again.
     * For stock system, this also restores stock to max.
     */
    public void refreshCooldown() {
        cooldown = 0;
        if (usesStockSystem()) {
            restoreStock();
        }
    }

    /**
     * Subtracts from the current cooldown time, shortening
     * the time until it can be cast again.
     *
     * @param seconds number of seconds to subtract from the cooldown
     */
    public void subtractCooldown(double seconds) {
        addCooldown(-seconds);
    }

    /**
     * Adds to the current cooldown time, lengthening
     * the time until it can be cast again.
     *
     * @param seconds number of seconds to add to the cooldown
     */
    public void addCooldown(double seconds) {
        if (isOnCooldown())
            cooldown += (int) (seconds * 1000);
        else
            cooldown = System.currentTimeMillis() + (int) (seconds * 1000);
    }

    /**
     * Starts the skill preview effects
     */
    public void startPreview() {
        skill.playPreview(player, level);
    }

    // ==================== Stock System Methods ====================

    /**
     * Gets the maximum stock charges for this skill at current level
     *
     * @return max stock, minimum 1
     */
    public int getMaxStock() {
        if (player == null) {
            return skill.getMaxStock(level);
        }
        return skill.getMaxStock(level, player);
    }

    /**
     * Gets the cast interval for this skill at current level
     *
     * @return cast interval in seconds
     */
    public double getCastInterval() {
        if (player == null) {
            return skill.getCastInterval(level);
        }
        return skill.getCastInterval(level, player);
    }

    /**
     * Updates and retrieves the current available stock count.
     * This method processes any pending stock regenerations before returning.
     *
     * @return current available stock
     */
    public int getAvailableStock() {
        int maxStock = getMaxStock();

        // Initialize if needed
        if (currentStock < 0) {
            currentStock = maxStock;
            return currentStock;
        }

        // Process any pending regenerations
        if (currentStock < maxStock && stockRegenEndTime > 0) {
            long now = System.currentTimeMillis();
            long cooldownMs = (long) (skill.getCooldown(level, player) * 1000);

            // Catch up on regenerated stocks
            while (now >= stockRegenEndTime && currentStock < maxStock) {
                currentStock++;
                if (currentStock < maxStock) {
                    stockRegenEndTime += cooldownMs;
                } else {
                    stockRegenEndTime = 0; // Full, stop regenerating
                }
            }
        }

        return currentStock;
    }

    /**
     * Checks if the skill has stock available for casting
     *
     * @return true if at least 1 stock is available
     */
    public boolean hasStock() {
        return getAvailableStock() > 0;
    }

    /**
     * Checks if the cast interval has passed since last cast
     *
     * @return true if cast interval has passed (can cast again)
     */
    public boolean isCastIntervalPassed() {
        if (lastCastTime == 0) return true;
        long castIntervalMs = (long) (getCastInterval() * 1000);
        return System.currentTimeMillis() - lastCastTime >= castIntervalMs;
    }

    /**
     * Checks if the skill can be cast considering stock and cast-interval
     *
     * @return true if skill has stock and cast-interval has passed
     */
    public boolean canUseStock() {
        return hasStock() && isCastIntervalPassed();
    }

    /**
     * Consumes one stock charge and starts regeneration if needed.
     * Call this when the skill is successfully cast.
     */
    public void consumeStock() {
        // Ensure stock is up to date
        getAvailableStock();

        int maxStock = getMaxStock();
        boolean wasAtMax = currentStock >= maxStock;

        // Consume one stock
        currentStock = Math.max(0, currentStock - 1);
        lastCastTime = System.currentTimeMillis();

        // Start regeneration if it wasn't running
        if (wasAtMax || stockRegenEndTime <= System.currentTimeMillis()) {
            stockRegenEndTime = System.currentTimeMillis() + (long) (skill.getCooldown(level, player) * 1000);
        }
    }

    /**
     * Gets the remaining time until the next stock regenerates
     *
     * @return time in seconds, or 0 if not regenerating
     */
    public double getStockRegenTimeLeft() {
        if (stockRegenEndTime <= 0) return 0;
        long diff = stockRegenEndTime - System.currentTimeMillis();
        return Math.max(0, diff / 1000.0);
    }

    /**
     * Gets the remaining time until cast interval passes
     *
     * @return time in seconds, or 0 if can cast immediately
     */
    public double getCastIntervalTimeLeft() {
        if (lastCastTime == 0) return 0;
        long castIntervalMs = (long) (getCastInterval() * 1000);
        long elapsed = System.currentTimeMillis() - lastCastTime;
        long remaining = castIntervalMs - elapsed;
        return Math.max(0, remaining / 1000.0);
    }

    /**
     * Checks if the skill uses the stock system (maxStock > 1)
     *
     * @return true if maxStock > 1
     */
    public boolean usesStockSystem() {
        return getMaxStock() > 1;
    }

    /**
     * Restores stock to maximum.
     * Used when refreshing or resetting the skill.
     */
    public void restoreStock() {
        currentStock = getMaxStock();
        stockRegenEndTime = 0;
    }

    /**
     * Sets current stock to a specific value (for loading saved data)
     *
     * @param stock the stock value to set
     */
    public void initializeStock(int stock) {
        this.currentStock = Math.max(0, Math.min(stock, getMaxStock()));
        // If below max, start regeneration
        if (this.currentStock < getMaxStock()) {
            stockRegenEndTime = System.currentTimeMillis() + (long) (skill.getCooldown(level, player) * 1000);
        }
    }
}
