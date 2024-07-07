package net.perpetualeve.shieldinghealth.enchantments;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BastionOfFlesh extends Enchantment {
	
	public static final UUID SHIELD_UUID = UUID.fromString("9ccceb0a-9651-4343-a8d4-cac1651613ba");
	public static final UUID ARMOR_UUID = UUID.fromString("7c374002-900e-4c60-8ca1-6f2b2ba5f873");
	public static final UUID TOUGHNESS_UUID = UUID.fromString("792528ab-9f76-4b03-8e84-9b858dbcfa3d");

	public BastionOfFlesh( ) {
		super(Rarity.RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[] {
			EquipmentSlot.CHEST
		});
	}

	@Override
	public int getMaxLevel( ) {
		return 1;
	}

	@Override
	public boolean isAllowedOnBooks( ) {
		return true;
	}

	@Override
	public boolean isDiscoverable( ) {
		return true;
	}

	@Override
	public boolean isTradeable( ) {
		return false;
	}

}
