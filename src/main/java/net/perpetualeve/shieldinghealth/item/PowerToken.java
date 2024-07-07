package net.perpetualeve.shieldinghealth.item;

import java.util.List;
import java.util.Set;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.perpetualeve.shieldinghealth.ShieldingHealth;

public class PowerToken extends Item {

	public PowerToken( ) {
		super(new Item.Properties( ).stacksTo(8));
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		pTooltipComponents.add(Component.translatable("item.shieldinghealth.power_token_info")
			.withStyle(Style.EMPTY.applyFormats(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC)));
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget,
		InteractionHand pUsedHand) {
		boolean		hasMending	= pStack.getEnchantmentLevel(Enchantments.MENDING) > 0;
		Set<String>	tags		= pInteractionTarget.getTags( );
		if (tags.contains(ShieldingHealth.SHIELD_TAG) || tags.contains(ShieldingHealth.SHIELD_STRONG_TAG)) {
			return InteractionResult.PASS;
		}
		pInteractionTarget.addTag(hasMending ? ShieldingHealth.SHIELD_STRONG_TAG : ShieldingHealth.SHIELD_TAG);
		pStack.setCount(pStack.getCount( ) - 1);
		return InteractionResult.CONSUME;
	}

}
