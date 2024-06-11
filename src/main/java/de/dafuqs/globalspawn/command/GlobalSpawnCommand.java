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

public class GlobalSpawnCommand {

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("globalspawnpoint")
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
								0,
								GlobalSpawnPoint.SpawnCriterion.SAFE_SKY_ACCESS_NOT_REQUIRED,
								0))
						.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
								.then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
										.then(CommandManager.argument("spread", IntegerArgumentType.integer())
												.then(CommandManager.argument("criterion", SpawnCriterionArgumentType.criterion())
														.then(CommandManager.argument("angle", AngleArgumentType.angle())
																.executes((context) -> executeSet(
																		context.getSource(),
																		DimensionArgumentType.getDimensionArgument(context, "dimension"),
																		BlockPosArgumentType.getBlockPos(context, "position"),
																		IntegerArgumentType.getInteger(context, "spread"),
																		SpawnCriterionArgumentType.getCriterion(context, "criterion"),
																		AngleArgumentType.getAngle(context, "angle"))
																)))))))));
	}

	static int executeQuery(ServerCommandSource source) {
		GlobalSpawnPoint globalSpawnPoint = GlobalSpawnManager.getGlobalRespawnPoint();
		if (globalSpawnPoint == null) {
			source.sendFeedback(() -> Text.translatable("commands.globalspawn.globalspawnpoint.query_not_set"), false);
		} else {
			BlockPos spawnBlockPos = globalSpawnPoint.getPos();
			RegistryKey<World> spawnWorld = globalSpawnPoint.getDimension();
			float angle = globalSpawnPoint.getAngle();
			GlobalSpawnPoint.SpawnCriterion criterion = globalSpawnPoint.getCriterion();

			source.sendFeedback(() -> Text.translatable("commands.globalspawn.globalspawnpoint.query_set_at", spawnWorld.getValue().toString(), spawnBlockPos.getX(), spawnBlockPos.getY(), spawnBlockPos.getZ(), angle, criterion), false);
		}
		return 1;
	}

	static int executeSet(ServerCommandSource source, ServerWorld serverWorld, BlockPos blockPos, int spread, GlobalSpawnPoint.SpawnCriterion criterion, float angle) {
		GlobalSpawnPoint globalSpawnPoint = new GlobalSpawnPoint(serverWorld.getRegistryKey(), blockPos, spread, criterion, angle);
		GlobalSpawnManager.setGlobalSpawnPoint(globalSpawnPoint);
		source.sendFeedback(() -> Text.translatable("commands.globalspawn.globalspawnpoint.set_to", serverWorld.getRegistryKey().getValue().toString(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), angle), true);
		return 1;
	}

	static int executeRemove(ServerCommandSource source) {
		GlobalSpawnManager.unsetGlobalSpawnPoint();
		source.sendFeedback(() -> Text.translatable("commands.globalspawn.globalspawnpoint.removed"), true);
		return 1;
	}

}