package net.perpetualeve.shieldinghealth.enchantments;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Shielding extends Enchantment {
	
	public static final UUID[] SHIELD_UUID = new UUID[] {
		UUID.fromString("46ae0da5-6c9c-498f-805e-71a0ecd771c9"), UUID.fromString("4ed639a0-1a32-4aa3-b7d6-994fe53c8459"), UUID.fromString("ee536bd8-855e-463e-a751-7cb02e7d2762"),
		UUID.fromString("d6efa3c0-8820-4e40-9d2a-624652d78f32"), UUID.fromString("ba5164aa-ef0f-4988-abef-a790a042dace"), UUID.fromString("2c7ea957-011a-4136-a5f0-d0d8a1c55780")};

	public Shielding( ) {
		super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[] {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
		});
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return true;
	}

	@Override
	public int getMaxLevel( ) {
		return 2;
	}

	@Override
	public boolean isAllowedOnBooks( ) {
		return true;
	}

	@Override
	public boolean isDiscoverable( ) {
		return false;
	}

	@Override
	public boolean isTradeable( ) {
		return false;
	}

}
