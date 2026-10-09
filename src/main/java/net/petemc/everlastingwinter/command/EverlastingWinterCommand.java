package net.petemc.everlastingwinter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.petemc.everlastingwinter.EverlastingWinter;
import net.petemc.everlastingwinter.config.MainConfig;
import net.petemc.everlastingwinter.config.RuntimeOverrides;

import java.util.concurrent.CompletableFuture;

public class EverlastingWinterCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> root = LiteralArgumentBuilder.<CommandSourceStack>literal("everlastingwinter");

		root.then(LiteralArgumentBuilder.<CommandSourceStack>literal("status").executes(EverlastingWinterCommand::status));

		root.then(RequiredArgumentBuilder
				.<CommandSourceStack, String>argument("option", StringArgumentType.word())
				.suggests(EverlastingWinterCommand::suggestOptions)
				.then(RequiredArgumentBuilder
						.<CommandSourceStack, String>argument("value", StringArgumentType.word())
						.executes(EverlastingWinterCommand::setGeneric)));

		root.then(LiteralArgumentBuilder.<CommandSourceStack>literal("reset").executes(EverlastingWinterCommand::resetAll));

		dispatcher.register(root);
	}

	private static void fail(CommandSourceStack source, String msg) {
		source.sendSuccess(() -> Component.literal(msg).withStyle(ChatFormatting.RED), false);
	}

	private static void ok(CommandSourceStack source, String msg) {
		source.sendSuccess(() -> Component.literal(msg).withStyle(ChatFormatting.GREEN), false);
	}

	private static int status(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		source.sendSuccess(() -> Component.literal("=== " + EverlastingWinter.MOD_NAME + " runtime state ==="), false);
		for (String name : RuntimeOverrides.knownNames()) {
			Object cur = RuntimeOverrides.currentValue(name);
			String suffix = "";
			if (RuntimeOverrides.hasOverride(name)) {
				suffix = " (config: " + RuntimeOverrides.format(RuntimeOverrides.defaultValue(name)) + ")";
			}
			String line = "  " + name + " = " + RuntimeOverrides.format(cur) + suffix;
			source.sendSuccess(() -> Component.literal(line), false);
		}
		var biomes = MainConfig.getListOfSnowBiomes();
		String biomeLine = "  listOfSnowBiomes = " + (biomes == null ? 0 : biomes.size()) + " biomes (read-only, edit the config file)";
		source.sendSuccess(() -> Component.literal(biomeLine), false);
		return 1;
	}

	private static int setGeneric(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		String option = StringArgumentType.getString(ctx, "option");
		String value = StringArgumentType.getString(ctx, "value");

		if ("listOfSnowBiomes".equals(option)) {
			fail(source, "listOfSnowBiomes is read-only. Edit the config file to change the biome list.");
			return 0;
		}

		if (!RuntimeOverrides.isKnown(option)) {
			fail(source, "Unknown option '" + option + "'. Use Tab to list valid options.");
			return 0;
		}

		RuntimeOverrides.ParseResult r = RuntimeOverrides.set(option, value);
		if (r.ok) {
			ok(source, r.message);
			return 1;
		} else {
			fail(source, r.message);
			return 0;
		}
	}

	private static int resetAll(CommandContext<CommandSourceStack> ctx) {
		RuntimeOverrides.resetAll();
		ctx.getSource().sendSuccess(() -> Component.literal(EverlastingWinter.MOD_NAME + ": all runtime overrides cleared (back to config-file values)."), false);
		return 1;
	}

	private static CompletableFuture<Suggestions> suggestOptions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
		for (String n : RuntimeOverrides.knownNames()) {
			builder.suggest(n);
		}
		return builder.buildFuture();
	}
}
