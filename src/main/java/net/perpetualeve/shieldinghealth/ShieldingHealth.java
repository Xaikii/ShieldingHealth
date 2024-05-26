package net.perpetualeve.shieldinghealth;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.api.ConfigType;
import carbonconfiglib.config.Config;
import carbonconfiglib.config.ConfigEntry.BoolValue;
import carbonconfiglib.config.ConfigEntry.DoubleValue;
import carbonconfiglib.config.ConfigEntry.IntValue;
import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.config.ConfigSection;
import carbonconfiglib.config.ConfigSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.perpetualeve.shieldinghealth.mixin.world.damagesource.CombatTrackerMixin;

@Mod(ShieldingHealth.MODID)
public class ShieldingHealth {
	public static final String MODID = "shieldinghealth";
//	private static final Logger	LOGGER	= LogUtils.getLogger( );

	public static ConfigHandler	CONFIG;
	public static IntValue		SHIELD_VALUE;
	public static IntValue		SHIELD_VALUE_PER_ABSORPTION;
	public static BoolValue		SHIELD_PERCENT;
	public static BoolValue		SHIELD_REGEN_OUT_COMBAT;
	public static BoolValue		PLAYER_SHIELD_DEFAULT;
	public static IntValue		SHIELD_DELAY;
	public static IntValue		SHIELDREGEN_DEFAULT;
	public static DoubleValue	POTION_SHIELD_EFFECT;
	public static DoubleValue	ENCHANTMENT_STEAL;
	public static BoolValue		ENCHANTMENT_STEAL_PERCENT;
	public static BoolValue		ENCHANTMENT_BENEFIT;

	public static Attribute	SHIELD_VALUE_ATTRIBUTE;
	public static Attribute	SHIELD_REGEN_ATTRIBUTE;
	public static Attribute	SHIELD_DELAY_ATTRIBUTE;

	public static MobEffect	INTERFERENCE;
	public static MobEffect	TAINTED;

	public static Enchantment SHIELD_RIEVER;

	public static final String	SHIELD_TAG			= "sh_shielded";
	public static final String	SHIELD_STRONG_TAG	= "sh_pshielded";

	public ShieldingHealth( ) {
		IEventBus modEventBus = FMLJavaModLoadingContext.get( ).getModEventBus( );

		Config			config	= new Config("shielding_health");
		ConfigSection	server	= new ConfigSection("server");

		CONFIG						= CarbonConfig.CONFIGS.createConfig(config,
			ConfigSettings.withConfigType(ConfigType.SERVER));
		SHIELD_VALUE				= server.addInt("Shield Value", 8, "How many Health Points the Shield should be by default").setMin(0)
			.setMax(65520);
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
			.setMin(0).setMax(6000);
		SHIELDREGEN_DEFAULT			= server.addInt("Shield regen default time", 100,
			"How many ticks are required to fully restore the shield").setMin(20).setMax(1200);
		POTION_SHIELD_EFFECT		= server
			.addDouble("Shield regen time by Potion", 1.0d, "How much the Effect should extend the necessary time").setMin(0.01d)
			.setMax(100.d);
		ENCHANTMENT_STEAL			= server.addDouble("Enchantment Shield Steal", 4, "How much Absorption Shield should be removed?");
		ENCHANTMENT_STEAL_PERCENT	= server.addBool("Enchantment Shield Steal Percent", false,
			"Should the stealing be in percent instead?");
		ENCHANTMENT_BENEFIT			= server.addBool("Enchantment Shield Gain", false,
			"Should the removed amount by added on your own Shield?");

		config.add(server);

		SHIELD_DELAY_ATTRIBUTE	= new ShieldDelayAttribute( );
		SHIELD_VALUE_ATTRIBUTE	= new ShieldValueAttribute( );
		SHIELD_REGEN_ATTRIBUTE	= new ShieldRegenAttribute( );

		INTERFERENCE	= new InterferencePotion( );
		TAINTED			= new TaintedPotion( );
		
		SHIELD_RIEVER = new ShieldRiever();

		CONFIG.register( );

		MinecraftForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::entityAttribute);
		modEventBus.addListener(this::register);
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
		event.register(ForgeRegistries.Keys.ENCHANTMENTS, T -> {
			T.register(ResourceLocation.tryParse(MODID + ":shield_reaver"), SHIELD_RIEVER);
		});
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

	@SubscribeEvent
	public void entityConstruct(EntityConstructing event) {
		if (event.getEntity( ) instanceof LivingEntity entity) {
			String tag = SHIELD_PERCENT.getValue( ) ? SHIELD_STRONG_TAG : SHIELD_TAG;
			if (entity instanceof Player && PLAYER_SHIELD_DEFAULT.getValue( ) && !entity.getTags( ).contains(tag))
				entity.addTag(tag);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingHurt(LivingDamageEvent event) {
		if (event.getEntity( ).getAbsorptionAmount( ) > event.getAmount( ))
			event.getEntity( ).getCombatTracker( ).recordDamage(event.getSource( ), event.getAmount( ));
	}

	public CombatTrackerMixin getMixin(CombatTracker tracker) {
		return (CombatTrackerMixin) (Object) tracker;
	}

	@SubscribeEvent
	public void entityTick(LivingTickEvent event) {
		LivingEntity	entity	= event.getEntity( );
		Level			level	= entity.level( );
		if (level.getGameTime( ) % 10 != 0 || level.isClientSide( )) return;

		boolean percent = entity.getTags( ).contains(SHIELD_STRONG_TAG);

		if (!percent && !entity.getTags( ).contains(SHIELD_TAG)) return;

		CombatTrackerMixin	tracker	= getMixin(entity.getCombatTracker( ));
		boolean				flag	= !tracker.isInCombat( ) && (SHIELD_REGEN_OUT_COMBAT.getValue( ) ? true
			: ((entity.tickCount - tracker.getLastDamageTime( )) >= entity.getAttributeValue(SHIELD_DELAY_ATTRIBUTE)));
		if (flag) regen(entity, percent);
	}

	public float getMaxAbsorption(LivingEntity entity, boolean flag) {
		return (float) (entity.getAttributeValue(ShieldingHealth.SHIELD_VALUE_ATTRIBUTE)
			+ (entity.hasEffect(MobEffects.ABSORPTION) ? entity.getEffect(MobEffects.ABSORPTION).getAmplifier( ) + 1 : 0)
				* ShieldingHealth.SHIELD_VALUE_PER_ABSORPTION.getValue( ))
			* (flag ? entity.getMaxHealth( ) : 1);
	}

	public void regen(LivingEntity entity, boolean percent) {
		double	val		= getMaxAbsorption(entity, percent);
		double	missing	= val - entity.getAbsorptionAmount( );
		if (missing > 0) {
			ShieldRegenEvent event = new ShieldRegenEvent((10d * missing) / entity.getAttributeValue(SHIELD_REGEN_ATTRIBUTE));
			if (!MinecraftForge.EVENT_BUS.post(event)) entity.setAbsorptionAmount((float) Math.min(
				entity.getAbsorptionAmount( ) + Math.max(event.heal, 0.25f),
				val));
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
		}
	}
}
