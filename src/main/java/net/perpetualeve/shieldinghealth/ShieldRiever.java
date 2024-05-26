package net.perpetualeve.shieldinghealth;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ShieldRiever extends Enchantment {

	public ShieldRiever( ) {
		super(Rarity.RARE, EnchantmentCategory.create("weaponry", T ->
		{
			return EnchantmentCategory.BREAKABLE.canEnchant(T) || EnchantmentCategory.WEAPON.canEnchant(T)
				|| EnchantmentCategory.TRIDENT.canEnchant(T) || EnchantmentCategory.BOW.canEnchant(T)
				|| EnchantmentCategory.CROSSBOW.canEnchant(T);
		}), new EquipmentSlot[] {
			EquipmentSlot.MAINHAND
		});
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canEnchant(ItemStack p_44689_) {
		return true;
	}

	@Override
	public int getMaxLevel( ) {
		return 5;
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
		return true;
	}

	@Override
	public void doPostAttack(LivingEntity p_44686_, Entity p_44687_, int p_44688_) {
		if (p_44687_ instanceof LivingEntity entity) {
			float	targetShield	= entity.getAbsorptionAmount( );
			float	amount			= (float) (ShieldingHealth.ENCHANTMENT_STEAL_PERCENT.getValue( )
				? ShieldingHealth.ENCHANTMENT_STEAL.getValue( ) * targetShield * 0.01d
				: ShieldingHealth.ENCHANTMENT_STEAL.getValue( ));
			entity.setAbsorptionAmount(targetShield - amount);
			if (ShieldingHealth.ENCHANTMENT_BENEFIT.getValue( )) p_44686_.setAbsorptionAmount(p_44686_.getAbsorptionAmount( ) + amount);
		}

	}
}
