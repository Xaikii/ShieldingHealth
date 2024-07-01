package net.perpetualeve.shieldinghealth.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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

}
