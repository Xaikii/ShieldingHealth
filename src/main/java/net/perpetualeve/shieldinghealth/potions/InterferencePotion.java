package net.perpetualeve.shieldinghealth.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.perpetualeve.shieldinghealth.ShieldingHealth;

public class InterferencePotion extends MobEffect {

	public InterferencePotion( ) {
		super(MobEffectCategory.HARMFUL, 13212947);
		addAttributeModifier(ShieldingHealth.SHIELD_REGEN_ATTRIBUTE, "201ca8fd-eb7f-444c-b6a1-ff4e4d3fa4d6", 1.0d, Operation.MULTIPLY_TOTAL);
		addAttributeModifier(ShieldingHealth.SHIELD_DELAY_ATTRIBUTE, "b1157f16-f2fd-47fd-aabf-c0da6b27a847", 1.0d, Operation.MULTIPLY_TOTAL);
	}

	public double getAttributeModifierValue(int p_19457_, AttributeModifier p_19458_) {
		return (p_19457_ + 1) * ShieldingHealth.POTION_SHIELD_EFFECT.get( ) * p_19458_.getAmount( );
	}
}
