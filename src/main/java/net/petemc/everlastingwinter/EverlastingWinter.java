package net.petemc.everlastingwinter;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.petemc.everlastingwinter.command.EverlastingWinterCommand;
import net.petemc.everlastingwinter.config.MainConfig;
import net.petemc.everlastingwinter.util.ModBiomesModifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(EverlastingWinter.MOD_ID)
public class EverlastingWinter {
	public static final String MOD_ID = "everlastingwinter";
	public static final String MOD_NAME = "Everlasting Winter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public EverlastingWinter(IEventBus modEventBus, ModContainer modContainer) {
		LOGGER.info("Initializing the {} Mod", MOD_NAME);

		modContainer.registerConfig(ModConfig.Type.SERVER, MainConfig.SPEC);
		ModBiomesModifiers.MODIFIER_CODECS.register(modEventBus);

		NeoForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::addCreative);

		NeoForge.EVENT_BUS.addListener(EverlastingWinter::onRegisterCommands);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
		});
	}

	// Add the example block item to the building blocks tab
	private void addCreative(BuildCreativeModeTabContentsEvent event) {

	}

	private static void onRegisterCommands(RegisterCommandsEvent event) {
		EverlastingWinterCommand.register(event.getDispatcher());
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		LOGGER.info("Initializing the {} Mod (server starting)", MOD_NAME);

	}

	// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
	@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {

		}
	}
}
