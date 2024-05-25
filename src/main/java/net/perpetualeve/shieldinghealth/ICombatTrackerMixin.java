package net.perpetualeve.shieldinghealth;

public interface ICombatTrackerMixin {

	int sh$getLastDamageTime();

	int sh$getCombatStartTime();

	int sh$getCombatEndTime();

	boolean sh$isInCombat();

	boolean sh$isTakingDamage();
}
