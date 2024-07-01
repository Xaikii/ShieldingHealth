package net.perpetualeve.shieldinghealth.item;

import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.perpetualeve.shieldinghealth.ShieldingHealth;

public class ShieldPower extends Item {

	public static final UUID SHIELD_UUID = UUID.fromString("ff405f4e-1dc0-4d17-9e7b-5113006daeac");

	public ShieldPower( ) {
		super(new Item.Properties( ).stacksTo(8)
			.food((new FoodProperties.Builder( )).alwaysEat( ).fast( ).nutrition(2).saturationMod(10.0f).build( )));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
		AttributeInstance instance = pLivingEntity.getAttribute(ShieldingHealth.SHIELD_VALUE_ATTRIBUTE);
		if (instance != null) {
			double				amount		= ShieldingHealth.SHIELD_POWER_BONUS.get( );
			double				maxC		= ShieldingHealth.SHIELD_POWER_MAX.get( );
			AttributeModifier	modifier	= instance.getModifier(SHIELD_UUID);
			if (modifier != null) {
				instance.removeModifier(SHIELD_UUID);

				amount = Math.min(modifier.getAmount( ) + amount, maxC);
			}
			Minecraft.getInstance( ).player
				.displayClientMessage(Component.translatable("item.shieldinghealth.shield_power_message", amount, maxC)
					.withStyle(Style.EMPTY.applyFormat(ChatFormatting.GOLD)), true);
			instance.addPermanentModifier(new AttributeModifier(SHIELD_UUID, "shield_power", amount, Operation.ADDITION));
		}
		pLivingEntity.setAbsorptionAmount(Math.min(pLivingEntity.getAbsorptionAmount( ) + 10, 10 + ShieldingHealth.getMaxAbsorption(
			pLivingEntity,
			ShieldingHealth.SHIELD_PERCENT.get( ) || pLivingEntity.getTags( ).contains(ShieldingHealth.SHIELD_STRONG_TAG))));
		return super.finishUsingItem(pStack, pLevel, pLivingEntity);
	}
}
