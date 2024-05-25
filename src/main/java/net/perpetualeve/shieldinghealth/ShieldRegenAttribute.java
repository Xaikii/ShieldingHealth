package net.perpetualeve.shieldinghealth;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ShieldRegenAttribute extends RangedAttribute {

	public static final String ID = "shield_regen";

	public ShieldRegenAttribute( ) {
		super(ID, ShieldingHealth.SHIELDREGEN_DEFAULT.getValue( ), 20, 1200);
		setSyncable(true);
	}

}
