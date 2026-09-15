package net.petemc.everlastingwinter.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * In-memory override overlay for config values that can be changed at runtime
 * via the /everlastingwinter command. Values held here (exact, camelCase option
 * name) are never written to the config file and are lost when the game exits.
 * {@link MainConfig} getters consult this first and fall back to the persisted
 * config value when no override is present.
 */
public class RuntimeOverrides {

	public enum Type { BOOLEAN, INT, FLOAT }

	private static final Map<String, Entry> REGISTRY = new LinkedHashMap<>();
	private static final Map<String, Object> OVERRIDES = new HashMap<>();

	private static final class Entry {
		final String name;
		final Type type;
		final Supplier<Object> defaultValue;

		Entry(String name, Type type, Supplier<Object> defaultValue) {
			this.name = name;
			this.type = type;
			this.defaultValue = defaultValue;
		}
	}

	static {
		register("constantSnowfall", Type.BOOLEAN, MainConfig::rawConstantSnowfall);
		register("layerDepth", Type.INT, MainConfig::rawLayerDepth);
		register("snowTickChance", Type.INT, MainConfig::rawSnowTickChance);
		register("shouldReplaceFlowersAndGrass", Type.BOOLEAN, MainConfig::rawShouldReplaceFlowersAndGrass);
		register("replaceSmallMushrooms", Type.BOOLEAN, MainConfig::rawReplaceSmallMushrooms);
		register("replaceTallFlowers", Type.BOOLEAN, MainConfig::rawReplaceTallFlowers);
		register("breakCropsAndFarmland", Type.BOOLEAN, MainConfig::rawBreakCropsAndFarmland);
		register("replacePaths", Type.BOOLEAN, MainConfig::rawReplacePaths);
		register("replaceTorches", Type.BOOLEAN, MainConfig::rawReplaceTorches);
		register("enablePowderSnow", Type.BOOLEAN, MainConfig::rawEnablePowderSnow);
		register("powderSnowChance", Type.INT, MainConfig::rawPowderSnowChance);
		// biomeTemperature and listOfSnowBiomes are intentionally NOT registered:
		// biomeTemperature only takes effect at server start, the biome list is read-only.
	}

	public static class ParseResult {
		public final boolean ok;
		public final String message;

		private ParseResult(boolean ok, String message) {
			this.ok = ok;
			this.message = message;
		}

		static ParseResult ok(String message) {
			return new ParseResult(true, message);
		}

		static ParseResult fail(String message) {
			return new ParseResult(false, message);
		}
	}

	private static void register(String name, Type type, Supplier<Object> defaultValue) {
		REGISTRY.put(name, new Entry(name, type, defaultValue));
	}

	public static boolean isKnown(String name) {
		return name != null && REGISTRY.containsKey(name);
	}

	public static Entry getEntry(String name) {
		return name == null ? null : REGISTRY.get(name);
	}

	public static Collection<String> knownNames() {
		return new ArrayList<>(REGISTRY.keySet());
	}

	public static boolean hasOverride(String name) {
		return name != null && OVERRIDES.containsKey(name);
	}

	public static void reset(String name) {
		if (name != null) {
			OVERRIDES.remove(name);
		}
	}

	public static void resetAll() {
		OVERRIDES.clear();
	}

	public static Object currentValue(String name) {
		Object o = OVERRIDES.get(name);
		if (o != null) {
			return o;
		}
		Entry e = getEntry(name);
		return e == null ? null : e.defaultValue.get();
	}

	public static Object defaultValue(String name) {
		Entry e = getEntry(name);
		return e == null ? null : e.defaultValue.get();
	}

	public static String format(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Float) {
			return String.valueOf((float) value);
		}
		return String.valueOf(value);
	}

	/**
	 * Getters used by {@link MainConfig}. Return {@code null} when no override is
	 * present for the given option so the caller falls back to the config value.
	 */
	public static Boolean getBoolean(String name) {
		Object v = OVERRIDES.get(name);
		return v instanceof Boolean ? (Boolean) v : null;
	}

	public static Integer getInt(String name) {
		Object v = OVERRIDES.get(name);
		return v instanceof Integer ? (Integer) v : null;
	}

	public static Float getFloat(String name) {
		Object v = OVERRIDES.get(name);
		return v instanceof Float ? (Float) v : null;
	}

	public static ParseResult set(String name, String rawValue) {
		Entry e = getEntry(name);
		if (e == null) {
			return ParseResult.fail("Unknown option '" + name + "'. Use Tab to list valid options.");
		}
		String value = rawValue == null ? "" : rawValue.trim();
		try {
			switch (e.type) {
				case BOOLEAN: {
					Boolean b = parseBoolean(value);
					if (b == null) {
						return ParseResult.fail("Expected 'true' or 'false' for '" + e.name + "'.");
					}
					OVERRIDES.put(name, b);
					return ParseResult.ok(e.name + " set to " + b + " (until game exit).");
				}
				case INT: {
					int i = (int) Math.round(Double.parseDouble(value));
					OVERRIDES.put(name, i);
					return ParseResult.ok(e.name + " set to " + i + " (until game exit).");
				}
				case FLOAT: {
					float f = (float) Double.parseDouble(value);
					OVERRIDES.put(name, f);
					return ParseResult.ok(e.name + " set to " + f + " (until game exit).");
				}
				default:
					return ParseResult.fail("Unsupported option type for '" + e.name + "'.");
			}
		} catch (NumberFormatException ex) {
			return ParseResult.fail("Invalid number '" + value + "' for '" + e.name + "'.");
		}
	}

	private static Boolean parseBoolean(String value) {
		if ("true".equals(value)) {
			return Boolean.TRUE;
		}
		if ("false".equals(value)) {
			return Boolean.FALSE;
		}
		return null;
	}
}
