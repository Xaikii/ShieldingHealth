package net.perpetualeve.shieldinghealth.mixin.world.damagesource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.damagesource.CombatTracker;
import net.perpetualeve.shieldinghealth.ShieldingHealth;

@Mixin(value = CombatTracker.class, priority = 123287)
public class CombatTrackerMixin {

	@ModifyConstant(method = "Lnet/minecraft/world/damagesource/CombatTracker;recheckStatus()V", constant = @Constant(intValue = 300))
	public int sh$modifyCombatDuration(int value) {
		return ShieldingHealth.COMBAT_TIME.get( );
	}
}
