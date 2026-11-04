package net.petemc.everlastingwinter;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
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

		modContainer.registerConfig(ModConfig.Type.SYNCED, MainConfig.SPEC);
		ModBiomesModifiers.MODIFIER_CODECS.register(modEventBus);

		NeoForge.EVENT_BUS.addListener(EverlastingWinter::onRegisterCommands);
	}

	private static void onRegisterCommands(RegisterCommandsEvent event) {
		EverlastingWinterCommand.register(event.getDispatcher());
	}
}
