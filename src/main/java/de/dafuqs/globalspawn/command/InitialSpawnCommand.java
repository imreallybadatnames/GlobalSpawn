package de.dafuqs.globalspawn.command;

import com.mojang.brigadier.arguments.*;
import de.dafuqs.globalspawn.*;
import net.fabricmc.fabric.api.command.v2.*;
import net.minecraft.command.argument.*;
import net.minecraft.registry.*;
import net.minecraft.server.command.*;
import net.minecraft.server.world.*;
import net.minecraft.text.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class InitialSpawnCommand {

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("initialspawnpoint")
				.requires((source) -> source.hasPermissionLevel(GlobalSpawn.GLOBAL_SPAWN_CONFIG.commandPermissionLevel))
				.executes((commandContext) -> executeQuery(commandContext.getSource()))
				.then(CommandManager.literal("query")
						.executes((context) -> executeQuery(context.getSource())))
				.then(CommandManager.literal("remove")
						.executes((context) -> executeRemove(context.getSource())))
				.then(CommandManager.literal("set")
						.executes((context) -> executeSet(
								context.getSource(),
								context.getSource().getWorld(),
								BlockPos.ofFloored(context.getSource().getPosition()),
								GlobalSpawnPoint.SpawnCriterion.UNOBSTRUCTED_Y,
								0,
								0))
						.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
								.then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
										.then(CommandManager.argument("criterion", SpawnCriterionArgumentType.criterion())
												.then(CommandManager.argument("spread", IntegerArgumentType.integer())
														.then(CommandManager.argument("angle", AngleArgumentType.angle())
																.executes((context) -> InitialSpawnCommand.executeSet(
																		context.getSource(),
																		DimensionArgumentType.getDimensionArgument(context, "dimension"),
																		BlockPosArgumentType.getBlockPos(context, "position"),
																		SpawnCriterionArgumentType.getCriterion(context, "criterion"),
																		IntegerArgumentType.getInteger(context, "spread"),
																		AngleArgumentType.getAngle(context, "angle"))
																)))))))));
	}

	static int executeQuery(ServerCommandSource source) {
		GlobalSpawnPoint initialSpawnPoint = GlobalSpawnManager.getInitialSpawnPoint();
		if (initialSpawnPoint == null) {
			source.sendFeedback(() -> Text.translatable("commands.globalspawn.initialspawnpoint.query_not_set"), false);
		} else {
			BlockPos spawnBlockPos = initialSpawnPoint.getPos();
			RegistryKey<World> spawnWorld = initialSpawnPoint.getDimension();
			float angle = initialSpawnPoint.getAngle();
			GlobalSpawnPoint.SpawnCriterion criterion = initialSpawnPoint.getCriterion();

			source.sendFeedback(() -> Text.translatable("commands.globalspawn.initialspawnpoint.query_set_at", spawnWorld.getValue().toString(), spawnBlockPos.getX(), spawnBlockPos.getY(), spawnBlockPos.getZ(), angle, criterion.toString().toLowerCase(Locale.ROOT)), false);
		}
		return 1;
	}

	static int executeSet(ServerCommandSource source, ServerWorld serverWorld, BlockPos blockPos, GlobalSpawnPoint.SpawnCriterion criterion, int spread, float angle) {
		GlobalSpawnPoint initialSpawnPoint = new GlobalSpawnPoint(serverWorld.getRegistryKey(), blockPos, spread, criterion, angle);
		GlobalSpawnManager.setInitialSpawnPoint(initialSpawnPoint);
		source.sendFeedback(() -> Text.translatable("commands.globalspawn.initialspawnpoint.set_to", serverWorld.getRegistryKey().getValue().toString(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), angle), true);
		return 1;
	}

	static int executeRemove(ServerCommandSource source) {
		GlobalSpawnManager.unsetInitialSpawnPoint();
		source.sendFeedback(() -> Text.translatable("commands.globalspawn.initialspawnpoint.removed"), true);
		return 1;
	}

}