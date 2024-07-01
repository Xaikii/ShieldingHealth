package net.perpetualeve.shieldinghealth;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.loot.IGlobalLootModifier;

public class SHLootModifier implements IGlobalLootModifier {

	public static final Codec<SHLootModifier> CODEC = Codec.unit(SHLootModifier::new);

	@Override
	public Codec<? extends IGlobalLootModifier> codec( ) {
		return CODEC;
	}

	@Override
	public @NotNull ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		Entity	killed	= context.getParamOrNull(LootContextParams.THIS_ENTITY);
		Player	player	= context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);

		if (context.getQueriedLootTableId( ).getPath( ).startsWith("chests") && context.getRandom( ).nextDouble( ) < 0.1) {
			generatedLoot.add(ShieldingHealth.POWER_TOKEN.getDefaultInstance( ));
		}
		else if (killed == null || player == null) {
			return generatedLoot;
		}
		if (player instanceof ServerPlayer sPlayer && killed instanceof Enemy) {
			int value = sPlayer.getStats( ).getValue(Stats.ENTITY_KILLED, killed.getType( ));
			if (value == 1) generatedLoot.add(ShieldingHealth.POWER_TOKEN.getDefaultInstance( ));
		}
		return generatedLoot;
	}
}
