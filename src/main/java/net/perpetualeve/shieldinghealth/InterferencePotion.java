package net.perpetualeve.shieldinghealth;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class InterferencePotion extends MobEffect {

	public InterferencePotion( ) {
		super(MobEffectCategory.HARMFUL, 13212947);
		addAttributeModifier(ShieldingHealth.SHIELD_REGEN_ATTRIBUTE, "interference_regen_potion", 1.0d, Operation.MULTIPLY_TOTAL);
		addAttributeModifier(ShieldingHealth.SHIELD_DELAY_ATTRIBUTE, "interference_delay_potion", 1.0d, Operation.MULTIPLY_TOTAL);
	}

	public double getAttributeModifierValue(int p_19457_, AttributeModifier p_19458_) {
		return (p_19457_ + 1) * ShieldingHealth.POTION_SHIELD_EFFECT.get( ) * p_19458_.getAmount( );
	}
}
