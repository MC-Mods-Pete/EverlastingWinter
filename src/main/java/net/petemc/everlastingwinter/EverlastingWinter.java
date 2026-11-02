package net.petemc.everlastingwinter;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.petemc.everlastingwinter.command.EverlastingWinterCommand;
import net.petemc.everlastingwinter.config.MainConfig;
import net.petemc.everlastingwinter.util.ModBiomesModifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EverlastingWinter implements ModInitializer {
	public static final String MOD_ID = "everlastingwinter";
	public static final String MOD_NAME = "Everlasting Winter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing the {} Mod", MOD_NAME);
		MainConfig.load();
		ModBiomesModifiers.load();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				EverlastingWinterCommand.register(dispatcher));
	}
}