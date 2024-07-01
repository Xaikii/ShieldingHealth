package net.perpetualeve.shieldinghealth.attributes;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.perpetualeve.shieldinghealth.ShieldingHealth;

public class ShieldDelayAttribute extends RangedAttribute {

	public static final String ID = "shield_delay";

	public ShieldDelayAttribute( ) {
		super(ID, ShieldingHealth.SHIELD_DELAY.getValue( ), 0, 6000);
		setSyncable(true);
	}
}
