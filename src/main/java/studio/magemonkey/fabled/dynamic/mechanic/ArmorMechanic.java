package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.fabled.api.util.ItemStackReader;
import studio.magemonkey.fabled.dynamic.DynamicSkill;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Sets the specified armor slot of the target to the item defined by the settings.
 * Can optionally use a block type stored by ValueBlockTypeMechanic.
 */
public class ArmorMechanic extends MechanicComponent {
    private static final String SLOT               = "slot";
    private static final String OVERWRITE          = "overwrite";
    private static final String USE_BLOCK_TYPE_KEY = "use-block-type-key";
    private static final String BLOCK_TYPE_KEY     = "block-type-key";

    @Override
    public String getKey() {
        return "armor";
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

        // Determine the item to use
        ItemStack item;
        boolean useBlockTypeKey = settings.getBool(USE_BLOCK_TYPE_KEY, false);
        if (useBlockTypeKey) {
            String key = settings.getString(BLOCK_TYPE_KEY, "blockType");
            Object stored = DynamicSkill.getCastData(caster).get(key);
            if (!(stored instanceof BlockData)) {
                return false;
            }
            BlockData blockData = (BlockData) stored;
            Material material = blockData.getMaterial();
            if (!material.isItem()) {
                return false;
            }
            item = new ItemStack(material);
        } else {
            item = ItemStackReader.read(settings);
        }

        boolean overwrite = settings.getBool(OVERWRITE, false);

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
