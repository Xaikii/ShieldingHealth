package net.perpetualeve.shieldinghealth.mixin.world.damagesource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.damagesource.CombatTracker;

@Mixin(value = CombatTracker.class)
public interface CombatTrackerMixin {

	@Accessor
	public int getLastDamageTime();

	@Accessor
	public int getCombatStartTime();

	@Accessor
	public int getCombatEndTime();

	@Accessor
	public boolean isInCombat();

	@Accessor
	public boolean isTakingDamage();

}
