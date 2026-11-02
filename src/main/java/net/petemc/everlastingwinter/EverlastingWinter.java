package net.petemc.everlastingwinter;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

	public EverlastingWinter(FMLJavaModLoadingContext context) {
		LOGGER.info("Initializing the {} Mod", MOD_NAME);

		IEventBus modEventBus = context.getModEventBus();

		MainConfig.init();
		ModBiomesModifiers.MODIFIER_CODECS.register(modEventBus);

		MinecraftForge.EVENT_BUS.addListener(EverlastingWinter::onRegisterCommands);
	}

	private static void onRegisterCommands(RegisterCommandsEvent event) {
		EverlastingWinterCommand.register(event.getDispatcher());
	}
}
