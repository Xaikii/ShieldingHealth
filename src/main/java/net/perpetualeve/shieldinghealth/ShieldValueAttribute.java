package net.perpetualeve.shieldinghealth;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ShieldValueAttribute extends RangedAttribute {

	public static final String ID = "shield_value";

	public ShieldValueAttribute( ) {
		super(ID, ShieldingHealth.SHIELD_VALUE.getValue( ), 0, 65520);
		setSyncable(true);
	}

}
