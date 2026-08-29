package net.petemc.everlastingwinter;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
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
	}
}