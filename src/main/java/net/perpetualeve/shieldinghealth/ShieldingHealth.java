package net.perpetualeve.shieldinghealth;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.api.ConfigType;
import carbonconfiglib.config.Config;
import carbonconfiglib.config.ConfigEntry.BoolValue;
import carbonconfiglib.config.ConfigEntry.DoubleValue;
import carbonconfiglib.config.ConfigEntry.IntValue;
import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.config.ConfigSection;
import carbonconfiglib.config.ConfigSettings;
import carbonconfiglib.impl.ReloadMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.perpetualeve.shieldinghealth.attributes.ShieldDelayAttribute;
import net.perpetualeve.shieldinghealth.attributes.ShieldRegenAttribute;
import net.perpetualeve.shieldinghealth.attributes.ShieldValueAttribute;
import net.perpetualeve.shieldinghealth.enchantments.BastionOfFlesh;
import net.perpetualeve.shieldinghealth.enchantments.ShieldRiever;
import net.perpetualeve.shieldinghealth.enchantments.Shielding;
import net.perpetualeve.shieldinghealth.item.PowerToken;
import net.perpetualeve.shieldinghealth.item.ShieldPower;
import net.perpetualeve.shieldinghealth.mixin.world.damagesource.ICombatTrackerMixin;
import net.perpetualeve.shieldinghealth.potions.InterferencePotion;
import net.perpetualeve.shieldinghealth.potions.TaintedPotion;

@Mod(ShieldingHealth.MODID)
public class ShieldingHealth {
	public static final String	MODID	= "shieldinghealth";
	private static final Logger	LOGGER	= LogUtils.getLogger( );

	public static ConfigHandler	CONFIG;
	public static IntValue		SHIELD_VALUE;
	public static IntValue		SHIELD_VALUE_PER_ABSORPTION;
	public static BoolValue		SHIELD_PERCENT;
	public static BoolValue		SHIELD_REGEN_OUT_COMBAT;
	public static BoolValue		PLAYER_SHIELD_DEFAULT;
	public static IntValue		SHIELD_DELAY;
	public static IntValue		SHIELDREGEN_DEFAULT;
	public static DoubleValue	POTION_SHIELD_EFFECT;
	public static DoubleValue	POTION_AMOUNT_EFFECT;
	public static DoubleValue	ENCHANTMENT_STEAL;
	public static BoolValue		ENCHANTMENT_STEAL_PERCENT;
	public static BoolValue		ENCHANTMENT_BENEFIT;
	public static IntValue		COMBAT_TIME;
	public static DoubleValue	SHIELD_POWER_BONUS;
	public static DoubleValue	SHIELD_POWER_MAX;

	public static Attribute	SHIELD_VALUE_ATTRIBUTE;
	public static Attribute	SHIELD_REGEN_ATTRIBUTE;
	public static Attribute	SHIELD_DELAY_ATTRIBUTE;

	public static MobEffect	INTERFERENCE;
	public static MobEffect	TAINTED;

	public static Enchantment	SHIELD_RIEVER;
	public static Enchantment	BASTION_OF_FLESH;
	public static Enchantment	SHIELDING;

	public static Item	POWER_TOKEN;
	public static Item	SHIELD_POWER;

	public static final String	SHIELD_TAG			= "sh_shielded";
	public static final String	SHIELD_STRONG_TAG	= "sh_pshielded";

	public ShieldingHealth( ) {
		IEventBus modEventBus = FMLJavaModLoadingContext.get( ).getModEventBus( );

		Config			config	= new Config("shielding_health");
		ConfigSection	server	= new ConfigSection("server");

		CONFIG						= CarbonConfig.CONFIGS.createConfig(config,
			ConfigSettings.withConfigType(ConfigType.SERVER));
		SHIELD_VALUE				= server.addInt("Shield Value", 8, "How many Health Points the Shield should be by default").setMin(0)
			.setMax(65520).setRequiredReload(ReloadMode.GAME);
		SHIELD_VALUE_PER_ABSORPTION	= server.addInt("Shield Value Per Absorption", 4, "Bonus Health per Absorption Level").setMin(0)
			.setMax(65520);
		SHIELD_PERCENT				= server.addBool("Shield Percent", false,
			"Should the Shield scale with MaxHP and be percentual instead?");
		SHIELD_REGEN_OUT_COMBAT		= server.addBool("Shield regen when outOfCombat", true,
			"If true will only start regen the Shield when no Combat Entries are found. When false will regen after some time of not attacking or being attacked");
		PLAYER_SHIELD_DEFAULT		= server.addBool("Player has Shield by default", true,
			"Should the Player have said Shield by default, set false if you want to handle this externally e.g. KubeJS");
		SHIELD_DELAY				= server.addInt("Shield regen start", 100,
			"How many ticks after trigger should start the regen process. Only works when <" + SHIELD_REGEN_OUT_COMBAT.getKey( )
				+ "> is false")
			.setMin(0).setMax(6000).setRequiredReload(ReloadMode.GAME);
		SHIELDREGEN_DEFAULT			= server.addInt("Shield regen default time", 100,
			"How many ticks are required to fully restore the shield").setMin(20).setMax(1200).setRequiredReload(ReloadMode.GAME);
		POTION_SHIELD_EFFECT		= server
			.addDouble("Shield regen time by Potion", 1.0d, "How much the Interference Potion Effect should extend the necessary time")
			.setMin(0.01d)
			.setMax(100.d);
		POTION_AMOUNT_EFFECT		= server
			.addDouble("Shield amount by Potion", 1.0d, "How much the Tainted Potion Effect should reduce of the shield").setMin(0.01d)
			.setMax(100.d);
		ENCHANTMENT_STEAL			= server.addDouble("Enchantment Shield Steal", 4, "How much Absorption Shield should be removed?");
		ENCHANTMENT_STEAL_PERCENT	= server.addBool("Enchantment Shield Steal Percent", false,
			"Should the stealing be in percent instead?");
		ENCHANTMENT_BENEFIT			= server.addBool("Enchantment Shield Gain", false,
			"Should the removed amount by added on your own Shield?");
		COMBAT_TIME					= server.addInt("CombatDurationOverwrite", 300, "How long is an entity considered in combat?");
		SHIELD_POWER_BONUS			= server
			.addDouble("Shield Power gain", 1, "How much max Shield you gain per Shield Power items consumed").setMax(65520).setMin(0);
		SHIELD_POWER_MAX			= server.addDouble("Shield Power max", 10, "How much max Shield you can gain via Shield Power items")
			.setMax(65520).setMin(1);

		config.add(server);

		CONFIG.register( );

		SHIELD_DELAY_ATTRIBUTE	= new ShieldDelayAttribute( );
		SHIELD_VALUE_ATTRIBUTE	= new ShieldValueAttribute( );
		SHIELD_REGEN_ATTRIBUTE	= new ShieldRegenAttribute( );

		INTERFERENCE	= new InterferencePotion( );
		TAINTED			= new TaintedPotion( );

		SHIELD_RIEVER		= new ShieldRiever( );
		BASTION_OF_FLESH	= new BastionOfFlesh( );
		SHIELDING			= new Shielding( );

		MinecraftForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::entityAttribute);
		modEventBus.addListener(this::register);
		modEventBus.addListener(this::creativeTabBuild);

	}

	public void register(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.ATTRIBUTES, T ->
		{
			T.register(ResourceLocation.tryParse(MODID + ":" + ShieldDelayAttribute.ID), SHIELD_DELAY_ATTRIBUTE);
			T.register(ResourceLocation.tryParse(MODID + ":" + ShieldValueAttribute.ID), SHIELD_VALUE_ATTRIBUTE);
			T.register(ResourceLocation.tryParse(MODID + ":" + ShieldRegenAttribute.ID), SHIELD_REGEN_ATTRIBUTE);
		});
		event.register(ForgeRegistries.Keys.MOB_EFFECTS, T ->
		{
			T.register(ResourceLocation.tryParse(MODID + ":interference"), INTERFERENCE);
			T.register(ResourceLocation.tryParse(MODID + ":tainted"), TAINTED);
		});
		event.register(ForgeRegistries.Keys.ENCHANTMENTS, T ->
		{
			T.register(ResourceLocation.tryParse(MODID + ":shield_reaver"), SHIELD_RIEVER);
			T.register(ResourceLocation.tryParse(MODID + ":bastion_of_flesh"), BASTION_OF_FLESH);
			T.register(ResourceLocation.tryParse(MODID + ":shielding"), SHIELDING);
		});
		event.register(ForgeRegistries.Keys.ITEMS, T ->
		{
			T.register(ResourceLocation.tryParse(MODID + ":power_token"), POWER_TOKEN = new PowerToken( ));
			T.register(ResourceLocation.tryParse(MODID + ":shield_power"), SHIELD_POWER = new ShieldPower( ));
		});
		event.register(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, T ->
		{
			T.register("shielding_health_glm", SHLootModifier.CODEC);
		});
	}

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void lootTableLoad(LootTableLoadEvent event) {
		LootTable table = event.getTable( );
		if (BuiltInLootTables.PIGLIN_BARTERING.equals(table.getLootTableId( ))) {
			try {
//				LootPool pool = table.getPool("main");
//				if(pool == null) return;

				Field field = LootTable.class.getDeclaredField("pools");
				field.setAccessible(true);
				List<LootPool> pools = (List<LootPool>) field.get(table);
				if (pools.size( ) > 0) {
					LootPool	pool	= pools.get(0);
					Field		field2	= LootPool.class.getDeclaredField("entries");
					field2.setAccessible(true);
					LootPoolEntryContainer[]		entries	= (LootPoolEntryContainer[]) field2.get(pool);
					List<LootPoolEntryContainer>	list	= new ArrayList<>(Arrays.asList(entries));
					list.add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
						.apply((new SetEnchantmentsFunction.Builder( )).withEnchantment(BASTION_OF_FLESH, ConstantValue.exactly(1.0f)))
						.setWeight(3).setQuality(2).build( ));

					field2.set(pool, list.toArray(new LootPoolEntryContainer[0]));
				}
			}
			catch (Exception e) {
				LOGGER.error("Shielding Health couldn't insert Loot into Pigling Bartering");
			}
		}
	}

	@SuppressWarnings({
		"unchecked", "rawtypes"
	})
	public void entityAttribute(EntityAttributeModificationEvent event) {
		for (EntityType type : event.getTypes( )) {
			event.add(type, SHIELD_DELAY_ATTRIBUTE, SHIELD_DELAY_ATTRIBUTE.getDefaultValue( ));
			event.add(type, SHIELD_REGEN_ATTRIBUTE, SHIELD_REGEN_ATTRIBUTE.getDefaultValue( ));
			event.add(type, SHIELD_VALUE_ATTRIBUTE, SHIELD_VALUE_ATTRIBUTE.getDefaultValue( ));
		}
	}

	public void creativeTabBuild(BuildCreativeModeTabContentsEvent event) {
		if (CreativeModeTabs.INGREDIENTS.equals(event.getTabKey( ))) {
			event.accept(POWER_TOKEN);
			event.accept(SHIELD_POWER);
		}
	}

	@SubscribeEvent
	public void entityConstruct(EntityConstructing event) {
		String tag = SHIELD_PERCENT.getValue( ) ? SHIELD_STRONG_TAG : SHIELD_TAG;
		if (event.getEntity( ) instanceof Player entity && PLAYER_SHIELD_DEFAULT.getValue( ) && !entity.getTags( ).contains(tag))
			entity.addTag(tag);
	}

	@SubscribeEvent
	public void entityJoin(EntityJoinLevelEvent event) {
		if (event.getEntity( ) instanceof LivingEntity entity) {
			AttributeInstance inst = entity.getAttribute(SHIELD_DELAY_ATTRIBUTE);
			if (inst != null && inst.getModifier(ShieldPower.SHIELD_UUID) != null) {
				double val = Math.min(inst.getModifier(ShieldPower.SHIELD_UUID).getAmount( ),
					getMaxAbsorption(entity, SHIELD_PERCENT.get( )));
				inst.removeModifier(ShieldPower.SHIELD_UUID);
				inst.addPermanentModifier(new AttributeModifier(ShieldPower.SHIELD_UUID, "shield_power", val, Operation.ADDITION));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingHurt(LivingDamageEvent event) {
		if (event.getEntity( ).getAbsorptionAmount( ) > event.getAmount( ))
			event.getEntity( ).getCombatTracker( ).recordDamage(event.getSource( ), event.getAmount( ));
	}

	public ICombatTrackerMixin getMixin(CombatTracker tracker) {
		return (ICombatTrackerMixin) (Object) tracker;
	}

	@SubscribeEvent
	public void equipSwap(LivingEquipmentChangeEvent event) {
		LivingEntity							entity		= event.getEntity( );
		AttributeMap							attribute	= entity.getAttributes( );
		Multimap<Attribute, AttributeModifier>	mods		= createModifiersFromStack(event.getFrom( ), entity, event.getSlot( ), true);
		if (!mods.isEmpty( )) {
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getTo( ), entity, event.getSlot( ), false);
		if (!mods.isEmpty( )) {
			attribute.addTransientAttributeModifiers(mods);
		}
	}

	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(ItemStack stack, LivingEntity living, EquipmentSlot slot,
		boolean remove) {
		Multimap<Attribute, AttributeModifier>	mods	= HashMultimap.create( );
		int										level	= stack.getEnchantmentLevel(BASTION_OF_FLESH);
		if (level > 0) {
			mods.put(SHIELD_VALUE_ATTRIBUTE,
				new AttributeModifier(BastionOfFlesh.SHIELD_UUID, "bastion_of_flesh_shield", level, Operation.MULTIPLY_BASE));
			mods.put(Attributes.ARMOR, new AttributeModifier(BastionOfFlesh.ARMOR_UUID, "bastion_of_flesh_armor", (1d / (level + 1d)) - 1d,
				Operation.MULTIPLY_TOTAL));
			mods.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(BastionOfFlesh.TOUGHNESS_UUID, "bastion_of_flesh_toughness",
				(1d / (level + 1d)) - 1d, Operation.MULTIPLY_TOTAL));
		}
		level = stack.getEnchantmentLevel(SHIELDING);
		if (level > 0) {
			mods.put(SHIELD_VALUE_ATTRIBUTE,
				new AttributeModifier(Shielding.SHIELD_UUID[slot.getFilterFlag( )], "shielding_" + slot.getFilterFlag( ), level,
					Operation.ADDITION));
		}
		return mods;
	}

	@SubscribeEvent
	public void entityTick(LivingTickEvent event) {
		LivingEntity	entity	= event.getEntity( );
		Level			level	= entity.level( );
		if (level.getGameTime( ) % 10 != 0 || level.isClientSide( )) return;

		boolean percent = entity.getTags( ).contains(SHIELD_STRONG_TAG);

		if (!percent && !entity.getTags( ).contains(SHIELD_TAG)) return;

		ICombatTrackerMixin	tracker	= getMixin(entity.getCombatTracker( ));
		boolean				flag	= !tracker.isInCombat( ) && (SHIELD_REGEN_OUT_COMBAT.getValue( ) ? true
			: ((entity.tickCount - tracker.getLastDamageTime( )) >= entity.getAttributeValue(SHIELD_DELAY_ATTRIBUTE)));
		float				val		= getMaxAbsorption(entity, percent);
		if (flag)
			regen(entity, percent);
		if (entity.getAbsorptionAmount( ) > val && !tracker.isInCombat( ))
			entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount( ) - 0.5f, val));
	}

	public static float getMaxAbsorption(LivingEntity entity, boolean flag) {
		return (float) (entity.getAttributeValue(ShieldingHealth.SHIELD_VALUE_ATTRIBUTE)
			+ (entity.hasEffect(MobEffects.ABSORPTION) ? entity.getEffect(MobEffects.ABSORPTION).getAmplifier( ) + 1 : 0)
				* ShieldingHealth.SHIELD_VALUE_PER_ABSORPTION.getValue( ))
			* (flag ? entity.getMaxHealth( ) * 0.01f : 1);
	}

	public void regen(LivingEntity entity, boolean percent) {
		regen(entity, percent, getMaxAbsorption(entity, percent));
	}

	public void regen(LivingEntity entity, boolean percent, float val) {
		double missing = val - entity.getAbsorptionAmount( );
		if (missing > 0) {
			ShieldRegenEvent event = new ShieldRegenEvent((10d * missing) / entity.getAttributeValue(SHIELD_REGEN_ATTRIBUTE));
			if (!MinecraftForge.EVENT_BUS.post(event)) entity.setAbsorptionAmount((float) Math.min(
				entity.getAbsorptionAmount( ) + Math.max(event.heal, 0.25f), val));
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
		}
	}
}
