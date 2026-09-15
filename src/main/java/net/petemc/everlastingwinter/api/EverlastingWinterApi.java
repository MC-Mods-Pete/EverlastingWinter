package net.petemc.everlastingwinter.api;

import net.petemc.everlastingwinter.config.RuntimeOverrides;

import java.util.Collection;

/**
 * Public API for other mods to read and set the in-memory runtime config values
 * of Everlasting Winter. Values are equivalent to the /everlastingwinter command:
 * they take effect immediately, are never written to the config file, and are
 * lost when the game exits.
 *
 * <p>This API does not cover {@code biomeTemperature} (only applies at server
 * start) or {@code listOfSnowBiomes} (read-only, edit via the config file).</p>
 */
public class EverlastingWinterApi {

	private EverlastingWinterApi() {
	}

	/** The option names supported by this API (see {@link #hasOverride} to check changes). */
	public static Collection<String> getKnownOptions() {
		return RuntimeOverrides.knownNames();
	}

	/** Whether a runtime override is currently active for the given option. */
	public static boolean hasOverride(String option) {
		return RuntimeOverrides.hasOverride(option);
	}

	/** Remove the runtime override for the given option (falls back to the config value). */
	public static void reset(String option) {
		RuntimeOverrides.reset(option);
	}

	/** Remove all runtime overrides (fall back to the config values). */
	public static void resetAll() {
		RuntimeOverrides.resetAll();
	}

	// --- constantSnowfall ---

	public static boolean isConstantSnowfall() {
		Boolean v = RuntimeOverrides.getBoolean("constantSnowfall");
		return v != null ? v : (Boolean) RuntimeOverrides.currentValue("constantSnowfall");
	}

	public static void setConstantSnowfall(boolean value) {
		RuntimeOverrides.set("constantSnowfall", String.valueOf(value));
	}

	// --- layerDepth ---

	public static int getLayerDepth() {
		Integer v = RuntimeOverrides.getInt("layerDepth");
		return v != null ? v : ((Number) RuntimeOverrides.currentValue("layerDepth")).intValue();
	}

	public static void setLayerDepth(int value) {
		RuntimeOverrides.set("layerDepth", String.valueOf(Math.max(1, value)));
	}

	// --- snowTickChance ---

	public static int getSnowTickChance() {
		Integer v = RuntimeOverrides.getInt("snowTickChance");
		return v != null ? v : ((Number) RuntimeOverrides.currentValue("snowTickChance")).intValue();
	}

	public static void setSnowTickChance(int value) {
		RuntimeOverrides.set("snowTickChance", String.valueOf(value));
	}

	// --- shouldReplaceFlowersAndGrass ---

	public static boolean isShouldReplaceFlowersAndGrass() {
		Boolean v = RuntimeOverrides.getBoolean("shouldReplaceFlowersAndGrass");
		return v != null ? v : (Boolean) RuntimeOverrides.currentValue("shouldReplaceFlowersAndGrass");
	}

	public static void setShouldReplaceFlowersAndGrass(boolean value) {
		RuntimeOverrides.set("shouldReplaceFlowersAndGrass", String.valueOf(value));
	}

	// --- replaceSmallMushrooms ---

	public static boolean isReplaceSmallMushrooms() {
		Boolean v = RuntimeOverrides.getBoolean("replaceSmallMushrooms");
		return v != null ? v : (Boolean) RuntimeOverrides.currentValue("replaceSmallMushrooms");
	}

	public static void setReplaceSmallMushrooms(boolean value) {
		RuntimeOverrides.set("replaceSmallMushrooms", String.valueOf(value));
	}

	// --- replaceTallFlowers ---

	public static boolean isReplaceTallFlowers() {
		Boolean v = RuntimeOverrides.getBoolean("replaceTallFlowers");
		return v != null ? v : (Boolean) RuntimeOverrides.currentValue("replaceTallFlowers");
	}

	public static void setReplaceTallFlowers(boolean value) {
		RuntimeOverrides.set("replaceTallFlowers", String.valueOf(value));
	}

	// --- breakCropsAndFarmland ---

	public static boolean isBreakCropsAndFarmland() {
		Boolean v = RuntimeOverrides.getBoolean("breakCropsAndFarmland");
		return v != null ? v : (Boolean) RuntimeOverrides.currentValue("breakCropsAndFarmland");
	}

	public static void setBreakCropsAndFarmland(boolean value) {
		RuntimeOverrides.set("breakCropsAndFarmland", String.valueOf(value));
	}

	// --- replacePaths ---

	public static boolean isReplacePaths() {
		Boolean v = RuntimeOverrides.getBoolean("replacePaths");
		return v != null ? v : (Boolean) RuntimeOverrides.currentValue("replacePaths");
	}

	public static void setReplacePaths(boolean value) {
		RuntimeOverrides.set("replacePaths", String.valueOf(value));
	}

	// --- replaceTorches ---

	public static boolean isReplaceTorches() {
		Boolean v = RuntimeOverrides.getBoolean("replaceTorches");
		return v != null ? v : (Boolean) RuntimeOverrides.currentValue("replaceTorches");
	}

	public static void setReplaceTorches(boolean value) {
		RuntimeOverrides.set("replaceTorches", String.valueOf(value));
	}

	// --- enablePowderSnow ---

	public static boolean isEnablePowderSnow() {
		Boolean v = RuntimeOverrides.getBoolean("enablePowderSnow");
		return v != null ? v : (Boolean) RuntimeOverrides.currentValue("enablePowderSnow");
	}

	public static void setEnablePowderSnow(boolean value) {
		RuntimeOverrides.set("enablePowderSnow", String.valueOf(value));
	}

	// --- powderSnowChance ---

	public static int getPowderSnowChance() {
		Integer v = RuntimeOverrides.getInt("powderSnowChance");
		return v != null ? v : ((Number) RuntimeOverrides.currentValue("powderSnowChance")).intValue();
	}

	public static void setPowderSnowChance(int value) {
		RuntimeOverrides.set("powderSnowChance", String.valueOf(Math.max(0, Math.min(100, value))));
	}
}
