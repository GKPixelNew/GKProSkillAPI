/**
 * SkillAPI
 * com.sucy.skill.dynamic.TempEntity
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.skill.dynamic;

import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.particle.target.EffectTarget;
import com.sucy.skill.api.particle.target.EntityTarget;
import com.sucy.skill.api.particle.target.FixedTarget;
import com.sucy.skill.api.util.Nearby;
import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Temporary dummy entity used for targeting a location in the dynamic system
 */
@SuppressWarnings("ConstantConditions")
public class TempEntity implements LivingEntity {

    private EffectTarget target;

    /**
     * Sets up a new dummy entity
     *
     * @param loc location to represent
     */
    public TempEntity(Location loc) {
        this(new FixedTarget(loc));
    }

    public TempEntity(final EffectTarget target) {
        this.target = target;
    }

    public double getEyeHeight() {
        return 0.2;
    }

    public double getEyeHeight(boolean b) {
        return 0.2;
    }

    @NotNull
    public Location getEyeLocation() {
        return getLocation().add(0, 0, 0);
    }

    @NotNull
    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return null;
    }

    @NotNull
    public Block getTargetBlock(Set<Material> set, int i) {
        return null;
    }

    @Override
    public @Nullable Block getTargetBlock(int maxDistance, TargetBlockInfo.FluidMode fluidMode) {
        return null;
    }

    @Override
    public @Nullable BlockFace getTargetBlockFace(int maxDistance, TargetBlockInfo.FluidMode fluidMode) {
        return null;
    }

    @Override
    public @Nullable BlockFace getTargetBlockFace(int maxDistance, @NotNull FluidCollisionMode fluidMode) {
        return null;
    }

    @Override
    public @Nullable TargetBlockInfo getTargetBlockInfo(int maxDistance, TargetBlockInfo.FluidMode fluidMode) {
        return null;
    }

    @Override
    public @Nullable Entity getTargetEntity(int maxDistance, boolean ignoreBlocks) {
        return null;
    }

    @Override
    public @Nullable TargetEntityInfo getTargetEntityInfo(int maxDistance, boolean ignoreBlocks) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceEntities(int maxDistance, boolean ignoreBlocks) {
        return null;
    }

    @NotNull
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return null;
    }

    @Override
    public @Nullable Block getTargetBlockExact(int maxDistance) {
        return null;
    }

    @Override
    public @Nullable Block getTargetBlockExact(int maxDistance, @NotNull FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceBlocks(double maxDistance) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceBlocks(double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    public int getRemainingAir() {
        return 0;
    }

    public void setRemainingAir(int i) {

    }

    public int getMaximumAir() {
        return 0;
    }

    public void setMaximumAir(int i) {

    }

    @Override
    public int getArrowCooldown() {
        return 0;
    }

    @Override
    public void setArrowCooldown(int ticks) {
    }

    @Override
    public int getArrowsInBody() {
        return 0;
    }

    @Override
    public void setArrowsInBody(int count) {
    }

    @Override
    public void setArrowsInBody(int count, boolean fireEvent) {

    }

    @Override
    public int getBeeStingerCooldown() {
        return 0;
    }

    @Override
    public void setBeeStingerCooldown(int ticks) {

    }

    @Override
    public int getBeeStingersInBody() {
        return 0;
    }

    @Override
    public void setBeeStingersInBody(int count) {

    }

    public int getMaximumNoDamageTicks() {
        return 0;
    }

    public void setMaximumNoDamageTicks(int i) {
    }

    public double getLastDamage() {
        return 0;
    }

    public void setLastDamage(double v) {
    }

    public int getNoDamageTicks() {
        return 0;
    }

    public void setNoDamageTicks(int i) {
    }

    public Player getKiller() {
        return null;
    }

    @Override
    public void setKiller(@Nullable Player killer) {

    }

    public boolean addPotionEffect(@NotNull PotionEffect potionEffect) {
        return false;
    }

    public boolean addPotionEffect(@NotNull PotionEffect potionEffect, boolean b) {
        return false;
    }

    public boolean addPotionEffects(@NotNull Collection<PotionEffect> collection) {
        return false;
    }

    public boolean hasPotionEffect(@NotNull PotionEffectType potionEffectType) {
        return false;
    }

    public PotionEffect getPotionEffect(@NotNull PotionEffectType potionEffectType) {
        return null;
    }

    public void removePotionEffect(@NotNull PotionEffectType potionEffectType) {

    }

    @NotNull
    public Collection<PotionEffect> getActivePotionEffects() {
        return ImmutableList.of();
    }

    public boolean hasLineOfSight(@NotNull Entity entity) {
        return false;
    }

    @Override
    public boolean hasLineOfSight(@NotNull Location location) {
        return false;
    }

    public boolean getRemoveWhenFarAway() {
        return false;
    }

    public void setRemoveWhenFarAway(boolean b) {

    }

    public EntityEquipment getEquipment() {
        return null;
    }

    public boolean getCanPickupItems() {
        return false;
    }

    public void setCanPickupItems(boolean b) {

    }

    @Override
    public @Nullable Component customName() {
        return null;
    }

    @Override
    public void customName(@Nullable Component customName) {

    }

    public String getCustomName() {
        return null;
    }

    public void setCustomName(String s) {

    }

    public boolean isCustomNameVisible() {
        return false;
    }

    public void setCustomNameVisible(boolean b) {

    }

    @Override
    public boolean isVisibleByDefault() {
        return true;
    }

    @Override
    public void setVisibleByDefault(boolean b) {
    }

    public boolean isGlowing() {
        return false;
    }

    public void setGlowing(boolean b) {

    }

    public boolean isInvulnerable() {
        return false;
    }

    public void setInvulnerable(boolean b) {

    }

    public boolean isSilent() {
        return false;
    }

    public void setSilent(boolean b) {

    }

    public boolean hasGravity() {
        return false;
    }

    public void setGravity(boolean b) {

    }

    public int getPortalCooldown() {
        return 0;
    }

    public void setPortalCooldown(int i) {

    }

    @NotNull
    public Set<String> getScoreboardTags() {
        return null;
    }

    public boolean addScoreboardTag(@NotNull String s) {
        return false;
    }

    public boolean removeScoreboardTag(@NotNull String s) {
        return false;
    }

    @NotNull
    public PistonMoveReaction getPistonMoveReaction() {
        return null;
    }

    @Override
    public @NotNull BlockFace getFacing() {
        return null;
    }

    @Override
    public @NotNull Pose getPose() {
        return null;
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public void setSneaking(boolean sneak) {

    }

    @Override
    public @NotNull Spigot spigot() {
        return null;
    }

    @Override
    public @NotNull Component name() {
        return null;
    }

    @Override
    public @NotNull Component teamDisplayName() {
        return null;
    }

    @Override
    public @Nullable Location getOrigin() {
        return null;
    }

    @Override
    public boolean fromMobSpawner() {
        return false;
    }

    @Override
    public @NotNull CreatureSpawnEvent.SpawnReason getEntitySpawnReason() {
        return null;
    }

    @Override
    public boolean isUnderWater() {
        return false;
    }

    @Override
    public boolean isInRain() {
        return false;
    }

    @Override
    public boolean isInBubbleColumn() {
        return false;
    }

    @Override
    public boolean isInWaterOrRain() {
        return false;
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        return false;
    }

    @Override
    public boolean isInWaterOrRainOrBubbleColumn() {
        return false;
    }

    @Override
    public boolean isInLava() {
        return false;
    }

    @Override
    public boolean isTicking() {
        return false;
    }

    @Override
    public @NotNull Set<Player> getTrackedPlayers() {
        return null;
    }

    @Override
    public boolean spawnAt(@NotNull Location location, @NotNull CreatureSpawnEvent.SpawnReason reason) {
        return false;
    }

    @Override
    public boolean isInPowderedSnow() {
        return false;
    }

    @Override
    public boolean collidesAt(@NotNull Location location) {
        return false;
    }

    @Override
    public boolean wouldCollideUsing(@NotNull BoundingBox boundingBox) {
        return false;
    }

    public boolean isLeashed() {
        return false;
    }

    @NotNull
    public Entity getLeashHolder() throws IllegalStateException {
        return null;
    }

    public boolean setLeashHolder(Entity entity) {
        return false;
    }

    public boolean isGliding() {
        return false;
    }

    public void setGliding(boolean b) {

    }

    public boolean isSwimming() {
        return false;
    }

    public void setSwimming(final boolean b) {
    }

    @Override
    public boolean isRiptiding() {
        return false;
    }

    @Override
    public boolean isSleeping() {
        return false;
    }

    public boolean isClimbing() {
        return false;
    }

    public void setAI(boolean b) {
    }

    public boolean hasAI() {
        return false;
    }

    @Override
    public void attack(@NotNull Entity target) {

    }

    @Override
    public void swingMainHand() {

    }

    @Override
    public void swingOffHand() {

    }

    public boolean isCollidable() {
        return false;
    }

    public void setCollidable(boolean b) {

    }

    @Override
    public @NotNull Set<UUID> getCollidableExemptions() {
        return null;
    }

    @Override
    public <T> @Nullable T getMemory(@NotNull MemoryKey<T> memoryKey) {
        return null;
    }

    @Override
    public <T> void setMemory(@NotNull MemoryKey<T> memoryKey, @Nullable T memoryValue) {
    }

    @Override
    public @Nullable Sound getHurtSound() {
        return null;
    }

    @Override
    public @Nullable Sound getDeathSound() {
        return null;
    }

    @Override
    public @NotNull Sound getFallDamageSound(int fallHeight) {
        return null;
    }

    @Override
    public @NotNull Sound getFallDamageSoundSmall() {
        return null;
    }

    @Override
    public @NotNull Sound getFallDamageSoundBig() {
        return null;
    }

    @Override
    public @NotNull Sound getDrinkingSound(@NotNull ItemStack itemStack) {
        return null;
    }

    @Override
    public @NotNull Sound getEatingSound(@NotNull ItemStack itemStack) {
        return null;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    public @NotNull EntityCategory getCategory() {
        return EntityCategory.NONE;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void setInvisible(boolean invisible) {
    }

    @Override
    public int getArrowsStuck() {
        return 0;
    }

    @Override
    public void setArrowsStuck(int arrows) {

    }

    @Override
    public int getShieldBlockingDelay() {
        return 0;
    }

    @Override
    public void setShieldBlockingDelay(int delay) {

    }

    @Override
    public @NotNull ItemStack getActiveItem() {
        return null;
    }

    @Override
    public void clearActiveItem() {

    }

    @Override
    public int getItemUseRemainingTime() {
        return 0;
    }

    @Override
    public int getHandRaisedTime() {
        return 0;
    }

    @Override
    public boolean isHandRaised() {
        return false;
    }

    @Override
    public @NotNull EquipmentSlot getHandRaised() {
        return null;
    }

    @Override
    public boolean isJumping() {
        return false;
    }

    @Override
    public void setJumping(boolean jumping) {

    }

    @Override
    public void playPickupItemAnimation(@NotNull Item item, int quantity) {

    }

    @Override
    public float getHurtDirection() {
        return 0;
    }

    @Override
    public void setHurtDirection(float hurtDirection) {

    }

    @Override
    public void knockback(double strength, double directionX, double directionZ) {

    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot slot) {

    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot slot, @NotNull Collection<Player> players) {

    }

    @Override
    public @NotNull ItemStack damageItemStack(@NotNull ItemStack stack, int amount) {
        return null;
    }

    @Override
    public void damageItemStack(@NotNull EquipmentSlot slot, int amount) {

    }

    @Override
    public float getBodyYaw() {
        return 0;
    }

    @Override
    public void setBodyYaw(float bodyYaw) {

    }

    public void damage(double v) {
    }

    public void damage(double v, Entity entity) {
    }

    public double getHealth() {
        return 1;
    }

    public void setHealth(double v) {
    }

    @Override
    public double getAbsorptionAmount() {
        return 0;
    }

    @Override
    public void setAbsorptionAmount(double v) {
    }

    public double getMaxHealth() {
        return 1;
    }

    public void setMaxHealth(double v) {
    }

    public void resetMaxHealth() {
    }

    @NotNull
    public Location getLocation() {
        return target.getLocation().clone();
    }

    public Location getLocation(final Location location) {
        final Location loc = target.getLocation();
        location.setX(loc.getX());
        location.setY(loc.getY());
        location.setZ(loc.getZ());
        location.setWorld(loc.getWorld());
        location.setYaw(loc.getYaw());
        location.setPitch(loc.getPitch());
        return location;
    }

    @NotNull
    public Vector getVelocity() {
        return new Vector(0, 0, 0);
    }

    public void setVelocity(@NotNull Vector vector) {
    }

    public double getHeight() {
        return 0;
    }

    public double getWidth() {
        return 0;
    }

    @Override
    public @NotNull BoundingBox getBoundingBox() {
        return null;
    }

    public boolean isOnGround() {
        return true;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @NotNull
    public World getWorld() {
        return target.getLocation().getWorld();
    }

    @Override
    public void setRotation(float yaw, float pitch) {
    }

    @Override
    public boolean teleport(@NotNull Location location, PlayerTeleportEvent.TeleportCause cause, @NotNull TeleportFlag... teleportFlags) {
        return false;
    }

    public boolean teleport(@NotNull Location location) {
        target = new FixedTarget(location);
        return true;
    }

    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause teleportCause) {
        target = new FixedTarget(location);
        return true;
    }

    public boolean teleport(@NotNull Entity entity) {
        target = new EntityTarget(entity);
        return true;
    }

    public boolean teleport(@NotNull Entity entity, @NotNull PlayerTeleportEvent.TeleportCause teleportCause) {
        target = new EntityTarget(entity);
        return true;
    }

    @NotNull
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return Nearby.getNearby(target.getLocation(), x);
    }

    public int getEntityId() {
        return 0;
    }

    public int getFireTicks() {
        return 0;
    }

    public void setFireTicks(int i) {
    }

    public int getMaxFireTicks() {
        return 0;
    }

    @Override
    public boolean isVisualFire() {
        return false;
    }

    @Override
    public void setVisualFire(boolean b) {

    }

    @Override
    public int getFreezeTicks() {
        return 0;
    }

    @Override
    public void setFreezeTicks(int i) {

    }

    @Override
    public int getMaxFreezeTicks() {
        return 0;
    }

    @Override
    public boolean isFrozen() {
        return false;
    }

    @Override
    public boolean isFreezeTickingLocked() {
        return false;
    }

    @Override
    public void lockFreezeTicks(boolean locked) {

    }

    public void remove() {
    }

    public boolean isDead() {
        return false;
    }

    public boolean isValid() {
        return true;
    }

    public void sendMessage(@NotNull String s) {
    }

    public void sendMessage(@NotNull String[] strings) {
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String[] messages) {
    }

    @NotNull
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void setPersistent(boolean persistent) {
    }

    @NotNull
    public String getName() {
        return "Location";
    }

    public Entity getPassenger() {
        return null;
    }

    public boolean setPassenger(@NotNull Entity entity) {
        return false;
    }

    @NotNull
    public List<Entity> getPassengers() {
        return null;
    }

    public boolean addPassenger(@NotNull final Entity entity) {
        return false;
    }

    public boolean removePassenger(@NotNull final Entity entity) {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean eject() {
        return false;
    }

    public float getFallDistance() {
        return 0;
    }

    public void setFallDistance(float v) {

    }

    public EntityDamageEvent getLastDamageCause() {
        return null;
    }

    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {
    }

    @NotNull
    public UUID getUniqueId() {
        return null;
    }

    public int getTicksLived() {
        return 0;
    }

    public void setTicksLived(int i) {
    }

    @NotNull
    @Override
    public SpawnCategory getSpawnCategory() {
        return SpawnCategory.MISC;
    }

    public void playEffect(@NotNull EntityEffect entityEffect) {
    }

    @NotNull
    public EntityType getType() {
        return EntityType.CHICKEN;
    }

    @Override
    public @NotNull Sound getSwimSound() {
        return null;
    }

    @Override
    public @NotNull Sound getSwimSplashSound() {
        return null;
    }

    @Override
    public @NotNull Sound getSwimHighSpeedSplashSound() {
        return null;
    }

    public boolean isInsideVehicle() {
        return false;
    }

    public boolean leaveVehicle() {
        return false;
    }

    public Entity getVehicle() {
        return null;
    }

    public void setMetadata(@NotNull String s, @NotNull MetadataValue metadataValue) {

    }

    @NotNull
    public List<MetadataValue> getMetadata(@NotNull String s) {
        return null;
    }

    public boolean hasMetadata(@NotNull String s) {
        return false;
    }

    public void removeMetadata(@NotNull String s, @NotNull Plugin plugin) {

    }

    @NotNull
    public <T extends Projectile> T launchProjectile(@NotNull Class<? extends T> aClass) {
        return null;
    }

    @NotNull
    public <T extends Projectile> T launchProjectile(@NotNull Class<? extends T> aClass, Vector vector) {
        return null;
    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> projectile, @Nullable Vector velocity, @Nullable Consumer<T> function) {
        return null;
    }

    public boolean isPermissionSet(@NotNull String s) {
        return false;
    }

    public boolean isPermissionSet(@NotNull Permission permission) {
        return false;
    }

    public boolean hasPermission(@NotNull String s) {
        return false;
    }

    public boolean hasPermission(@NotNull Permission permission) {
        return false;
    }

    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return null;
    }

    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return null;
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return null;
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return null;
    }

    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {

    }

    public void recalculatePermissions() {

    }

    @NotNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    public boolean isOp() {
        return false;
    }

    public void setOp(boolean b) {

    }

    public AttributeInstance getAttribute(@NotNull Attribute attribute) {
        return null;
    }

    @Override
    public void registerAttribute(@NotNull Attribute attribute) {

    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return null;
    }

    @Override
    public @NotNull TriState getFrictionState() {
        return null;
    }

    @Override
    public void setFrictionState(@NotNull TriState state) {

    }
}
