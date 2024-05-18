/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.TempEntity
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
package studio.magemonkey.fabled.dynamic;

import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import com.google.common.collect.ImmutableList;
import io.papermc.paper.entity.TeleportFlag;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.damage.DamageSource;
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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import studio.magemonkey.fabled.api.particle.target.EffectTarget;
import studio.magemonkey.fabled.api.particle.target.EntityTarget;
import studio.magemonkey.fabled.api.particle.target.FixedTarget;
import studio.magemonkey.fabled.api.util.Nearby;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Temporary dummy entity used for targeting a location in the dynamic system
 */
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

    public Location getEyeLocation() {
        return getLocation().add(0, 0, 0);
    }

    public List<Block> getLineOfSight(HashSet<Byte> hashSet, int i) {
        return null;
    }

    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return null;
    }

    public Block getTargetBlock(HashSet<Byte> hashSet, int i) {
        return null;
    }

    @NotNull
    public Block getTargetBlock(Set<Material> set, int i) {
        return null;
    }

    @Override
    public @Nullable Block getTargetBlock(int i, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        return null;
    }

    @Override
    public @Nullable BlockFace getTargetBlockFace(int i, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        return null;
    }

    @Override
    public @Nullable BlockFace getTargetBlockFace(int i, @NotNull FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Override
    public @Nullable TargetBlockInfo getTargetBlockInfo(int i, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        return null;
    }

    @Override
    public @Nullable Entity getTargetEntity(int i, boolean b) {
        return null;
    }

    @Override
    public @Nullable TargetEntityInfo getTargetEntityInfo(int i, boolean b) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceEntities(int i, boolean b) {
        return null;
    }

    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hashSet, int i) {
        return null;
    }

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

    public Egg throwEgg() {
        return null;
    }

    public Snowball throwSnowball() {
        return null;
    }

    public Arrow shootArrow() {
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

    @Nullable
    @Override
    public ItemStack getItemInUse() {
        return null;
    }

    @Override
    public int getItemInUseTicks() {
        return 0;
    }

    @Override
    public void setItemInUseTicks(int i) {

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
    public void setArrowsInBody(int i, boolean b) {

    }

    @Override
    public int getNextArrowRemoval() {
        return 0;
    }

    @Override
    public void setNextArrowRemoval(@Range(from = 0L, to = 2147483647L) int i) {

    }

    @Override
    public int getBeeStingerCooldown() {
        return 0;
    }

    @Override
    public void setBeeStingerCooldown(int i) {

    }

    @Override
    public int getBeeStingersInBody() {
        return 0;
    }

    @Override
    public void setBeeStingersInBody(int i) {

    }

    @Override
    public int getNextBeeStingerRemoval() {
        return 0;
    }

    @Override
    public void setNextBeeStingerRemoval(@Range(from = 0L, to = 2147483647L) int i) {

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

    public int _INVALID_getLastDamage() {
        return 0;
    }

    public void _INVALID_setLastDamage(int i) {
    }

    public int getNoDamageTicks() {
        return 0;
    }

    public void setNoDamageTicks(int i) {
    }

    @Override
    public int getNoActionTicks() {
        return 0;
    }

    @Override
    public void setNoActionTicks(int i) {
    }

    public Player getKiller() {
        return null;
    }

    @Override
    public void setKiller(@Nullable Player player) {

    }

    public boolean addPotionEffect(PotionEffect potionEffect) {
        return false;
    }

    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return false;
    }

    public boolean addPotionEffects(Collection<PotionEffect> collection) {
        return false;
    }

    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return false;
    }

    public PotionEffect getPotionEffect(PotionEffectType potionEffectType) {
        return null;
    }

    public void removePotionEffect(PotionEffectType potionEffectType) {

    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return ImmutableList.of();
    }

    @Override
    public boolean clearActivePotionEffects() {
        return false;
    }

    public boolean hasLineOfSight(Entity entity) {
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
    public void customName(@Nullable Component component) {

    }

    public String getCustomName() {
        return null;
    }

    public void setCustomName(String s) {

    }

    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public void setVisibleByDefault(boolean b) {}

    @Override
    public boolean isVisibleByDefault() {return true;}

    @NotNull
    @Override
    public Set<Player> getTrackedBy() {
        return Set.of();
    }

    public void setCustomNameVisible(boolean b) {

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

    public Set<String> getScoreboardTags() {
        return null;
    }

    public boolean addScoreboardTag(String s) {
        return false;
    }

    public boolean removeScoreboardTag(String s) {
        return false;
    }

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
    public void setSneaking(boolean b) {

    }

    @Override
    public void setPose(@NotNull Pose pose, boolean b) {

    }

    @Override
    public boolean hasFixedPose() {
        return false;
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

    @NotNull
    @Override
    public CreatureSpawnEvent.SpawnReason getEntitySpawnReason() {
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
        return Set.of();
    }

    @Override
    public boolean spawnAt(@NotNull Location location, @NotNull CreatureSpawnEvent.SpawnReason spawnReason) {
        return false;
    }

    @Override
    public boolean isInPowderedSnow() {
        return false;
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getZ() {
        return 0;
    }

    @Override
    public float getPitch() {
        return 0;
    }

    @Override
    public float getYaw() {
        return 0;
    }

    @Override
    public boolean collidesAt(@NotNull Location location) {
        return false;
    }

    @Override
    public boolean wouldCollideUsing(@NotNull BoundingBox boundingBox) {
        return false;
    }

    @Override
    public @NotNull EntityScheduler getScheduler() {
        return null;
    }

    @Override
    public @NotNull String getScoreboardEntryName() {
        return "";
    }

    public boolean isLeashed() {
        return false;
    }

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

    @Override
    public void playHurtAnimation(float v) {

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

    @Nullable
    @Override
    public Sound getHurtSound() {
        return null;
    }

    @Nullable
    @Override
    public Sound getDeathSound() {
        return null;
    }

    @NotNull
    @Override
    public Sound getFallDamageSound(int i) {
        return null;
    }

    @NotNull
    @Override
    public Sound getFallDamageSoundSmall() {
        return null;
    }

    @NotNull
    @Override
    public Sound getFallDamageSoundBig() {
        return null;
    }

    @NotNull
    @Override
    public Sound getDrinkingSound(@NotNull ItemStack itemStack) {
        return null;
    }

    @NotNull
    @Override
    public Sound getEatingSound(@NotNull ItemStack itemStack) {
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
    public void setNoPhysics(boolean b) {

    }

    @Override
    public boolean hasNoPhysics() {
        return false;
    }

    @Override
    public boolean isFreezeTickingLocked() {
        return false;
    }

    @Override
    public void lockFreezeTicks(boolean b) {

    }

    @Override
    public int getArrowsStuck() {
        return 0;
    }

    @Override
    public void setArrowsStuck(int i) {

    }

    @Override
    public int getShieldBlockingDelay() {
        return 0;
    }

    @Override
    public void setShieldBlockingDelay(int i) {

    }

    @Override
    public float getSidewaysMovement() {
        return 0;
    }

    @Override
    public float getUpwardsMovement() {
        return 0;
    }

    @Override
    public float getForwardsMovement() {
        return 0;
    }

    @Override
    public void startUsingItem(@NotNull EquipmentSlot equipmentSlot) {

    }

    @Override
    public void completeUsingActiveItem() {

    }

    @Override
    public @NotNull ItemStack getActiveItem() {
        return null;
    }

    @Override
    public void clearActiveItem() {

    }

    @Override
    public int getActiveItemRemainingTime() {
        return 0;
    }

    @Override
    public void setActiveItemRemainingTime(@Range(from = 0L, to = 2147483647L) int i) {

    }

    @Override
    public boolean hasActiveItem() {
        return false;
    }

    @Override
    public int getActiveItemUsedTime() {
        return 0;
    }

    @Override
    public @NotNull EquipmentSlot getActiveItemHand() {
        return null;
    }

    @Override
    public boolean isJumping() {
        return false;
    }

    @Override
    public void setJumping(boolean b) {

    }

    @Override
    public void playPickupItemAnimation(@NotNull Item item, int i) {

    }

    @Override
    public float getHurtDirection() {
        return 0;
    }

    @Override
    public void setHurtDirection(float v) {

    }

    @Override
    public void knockback(double v, double v1, double v2) {

    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot equipmentSlot) {

    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot equipmentSlot, @NotNull Collection<Player> collection) {

    }

    @Override
    public @NotNull ItemStack damageItemStack(@NotNull ItemStack itemStack, int i) {
        return null;
    }

    @Override
    public void damageItemStack(@NotNull EquipmentSlot equipmentSlot, int i) {

    }

    @Override
    public float getBodyYaw() {
        return 0;
    }

    @Override
    public void setBodyYaw(float v) {

    }

    @Override
    public void setInvisible(boolean invisible) {
    }

    public void damage(double v) {
    }

    public void _INVALID_damage(int i) {
    }

    public void damage(double v, Entity entity) {
    }

    @Override
    public void damage(double v, @NotNull DamageSource damageSource) {
    }

    public void _INVALID_damage(int i, Entity entity) {
    }

    public double getHealth() {
        return 1;
    }

    public void setHealth(double v) {
    }

    public int _INVALID_getHealth() {
        return 0;
    }

    @Override
    public double getAbsorptionAmount() {
        return 0;
    }

    @Override
    public void setAbsorptionAmount(double v) {
    }

    public void _INVALID_setHealth(int i) {
    }

    public double getMaxHealth() {
        return 1;
    }

    public void setMaxHealth(double v) {
    }

    public int _INVALID_getMaxHealth() {
        return 0;
    }

    public void _INVALID_setMaxHealth(int i) {
    }

    public void resetMaxHealth() {
    }

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

    public Vector getVelocity() {
        return new Vector(0, 0, 0);
    }

    public void setVelocity(Vector vector) {
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

    public World getWorld() {
        return target.getLocation().getWorld();
    }

    @Override
    public void setRotation(float yaw, float pitch) {
    }

    @Override
    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause teleportCause, @NotNull TeleportFlag... teleportFlags) {
        return false;
    }

    public boolean teleport(Location location) {
        target = new FixedTarget(location);
        return true;
    }

    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        target = new FixedTarget(location);
        return true;
    }

    public boolean teleport(Entity entity) {
        target = new EntityTarget(entity);
        return true;
    }

    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
        target = new EntityTarget(entity);
        return true;
    }

    @Override
    public @NotNull CompletableFuture<Boolean> teleportAsync(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause teleportCause, @NotNull TeleportFlag... teleportFlags) {
        return null;
    }

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

    public void remove() {
    }

    public boolean isDead() {
        return false;
    }

    public boolean isValid() {
        return true;
    }

    public void sendMessage(String s) {
    }

    public void sendMessage(String[] strings) {
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String[] messages) {
    }

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

    public String getName() {
        return "Location";
    }

    public Entity getPassenger() {
        return null;
    }

    public boolean setPassenger(Entity entity) {
        return false;
    }

    public List<Entity> getPassengers() {
        return null;
    }

    public boolean addPassenger(final Entity entity) {
        return false;
    }

    public boolean removePassenger(final Entity entity) {
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
    public SpawnCategory getSpawnCategory() {return SpawnCategory.MISC;}

    @Override
    public boolean isInWorld() {
        return false;
    }

    @Nullable
    @Override
    public EntitySnapshot createSnapshot() {
        return null;
    }

    @NotNull
    @Override
    public Entity copy() {
        return null;
    }

    @NotNull
    @Override
    public Entity copy(@NotNull Location location) {
        return null;
    }

    public void playEffect(EntityEffect entityEffect) {
    }

    public EntityType getType() {
        return EntityType.CHICKEN;
    }

    @NotNull
    @Override
    public Sound getSwimSound() {
        return null;
    }

    @NotNull
    @Override
    public Sound getSwimSplashSound() {
        return null;
    }

    @NotNull
    @Override
    public Sound getSwimHighSpeedSplashSound() {
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

    public void setMetadata(String s, MetadataValue metadataValue) {

    }

    public List<MetadataValue> getMetadata(String s) {
        return null;
    }

    public boolean hasMetadata(String s) {
        return false;
    }

    public void removeMetadata(String s, Plugin plugin) {

    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return null;
    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        return null;
    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> aClass, @Nullable Vector vector, @Nullable Consumer<? super T> consumer) {
        return null;
    }

    public boolean isPermissionSet(String s) {
        return false;
    }

    public boolean isPermissionSet(Permission permission) {
        return false;
    }

    public boolean hasPermission(String s) {
        return false;
    }

    public boolean hasPermission(Permission permission) {
        return false;
    }

    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;
    }

    public void removeAttachment(PermissionAttachment permissionAttachment) {

    }

    public void recalculatePermissions() {

    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    public boolean isOp() {
        return false;
    }

    public void setOp(boolean b) {

    }

    public AttributeInstance getAttribute(Attribute attribute) {
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
    public void setFrictionState(@NotNull TriState triState) {

    }
}
