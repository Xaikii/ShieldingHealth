package net.perpetualeve.shieldinghealth.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.perpetualeve.shieldinghealth.ShieldingHealth;

public class TaintedPotion extends MobEffect {

	public TaintedPotion( ) {
		super(MobEffectCategory.HARMFUL, 23423243);
		addAttributeModifier(ShieldingHealth.SHIELD_VALUE_ATTRIBUTE, "beecdea2-8fa3-46bc-a109-90cf0f6e8440", -0.1d,
			Operation.MULTIPLY_TOTAL);
	}

	public double getAttributeModifierValue(int p_19457_, AttributeModifier p_19458_) {
		return calculate(p_19457_, p_19458_.getAmount( ));
	}

	public float calculate(int level, double scalar) {
		return (float) ((level + 1) * ShieldingHealth.POTION_SHIELD_EFFECT.get( ) * scalar);
	}
}
