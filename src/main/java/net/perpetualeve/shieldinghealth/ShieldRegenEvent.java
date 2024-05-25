package net.perpetualeve.shieldinghealth;

import net.minecraftforge.eventbus.api.Event;

public class ShieldRegenEvent extends Event {

	double heal = 0.0d;

	public ShieldRegenEvent(double heal) {
		this.heal = heal;
	}

}
