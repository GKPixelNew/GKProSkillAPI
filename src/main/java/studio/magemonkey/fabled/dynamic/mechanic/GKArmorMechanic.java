/**
 * Fabled
 * studio.magemonkey.fabled.dynamic.mechanic.GKArmorMechanic
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
package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.util.ItemStackReader;
import studio.magemonkey.fabled.dynamic.DynamicSkill;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Sets the specified armor slot of the target to the item defined by the settings.
 * Extended version with support for using block type from cast data.
 */
public class GKArmorMechanic extends MechanicComponent {
    private static final String SLOT               = "slot";
    private static final String OVERWRITE          = "overwrite";
    private static final String USE_BLOCK_TYPE_KEY = "use-block-type-key";
    private static final String BLOCK_TYPE_KEY     = "block-type-key";

    @Override
    public String getKey() {
        return "gk armor";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        EquipmentSlot slot;
        try {
            slot = EquipmentSlot.valueOf(settings.getString(SLOT, "HAND").toUpperCase(Locale.US).replace(" ", "_"));
        } catch (IllegalArgumentException exception) {
            return false;
        }
        
        boolean useBlockTypeKey = settings.getBool(USE_BLOCK_TYPE_KEY, false);
        boolean overwrite       = settings.getBool(OVERWRITE, false);
        
        ItemStack item;
        if (useBlockTypeKey) {
            String blockTypeKey = settings.getString(BLOCK_TYPE_KEY, "blockType");
            CastData data = DynamicSkill.getCastData(caster);
            Object stored = data.getRaw(blockTypeKey);
            
            if (stored == null) {
                return false;
            }
            
            Material material;
            if (stored instanceof BlockData) {
                material = ((BlockData) stored).getMaterial();
            } else if (stored instanceof String) {
                // Handle persistent data stored as string
                try {
                    BlockData blockData = org.bukkit.Bukkit.createBlockData((String) stored);
                    material = blockData.getMaterial();
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } else {
                return false;
            }
            
            // Skip if the material is not a valid item (like AIR, WATER, etc.)
            if (!material.isItem()) {
                return false;
            }
            
            item = new ItemStack(material);
        } else {
            item = ItemStackReader.read(settings);
        }

        boolean success = false;
        for (LivingEntity target : targets) {
            EntityEquipment equipment = Objects.requireNonNull(target.getEquipment());
            boolean         proceed   = overwrite;
            if (!overwrite) {
                switch (slot) {
                    case FEET:
                        proceed = equipment.getBoots() == null || equipment.getBoots().getType().equals(Material.AIR);
                        break;
                    case HAND:
                        proceed = equipment.getItemInMainHand().getType().equals(Material.AIR);
                        break;
                    case HEAD:
                        proceed = equipment.getHelmet() == null || equipment.getHelmet().getType().equals(Material.AIR);
                        break;
                    case LEGS:
                        proceed = equipment.getLeggings() == null || equipment.getLeggings().getType().equals(Material.AIR);
                        break;
                    case CHEST:
                        proceed = equipment.getChestplate() == null || equipment.getChestplate().getType().equals(Material.AIR);
                        break;
                    case OFF_HAND:
                        proceed = equipment.getItemInOffHand().getType().equals(Material.AIR);
                        break;
                }
            }
            if (proceed) {
                switch (slot) {
                    case FEET:
                        equipment.setBoots(item);
                        break;
                    case HAND:
                        equipment.setItemInMainHand(item);
                        break;
                    case HEAD:
                        equipment.setHelmet(item);
                        break;
                    case LEGS:
                        equipment.setLeggings(item);
                        break;
                    case CHEST:
                        equipment.setChestplate(item);
                        break;
                    case OFF_HAND:
                        equipment.setItemInOffHand(item);
                        break;
                }
                success = true;
            }
        }
        return success;
    }
}
