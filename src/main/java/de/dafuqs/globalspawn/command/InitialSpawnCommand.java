package de.dafuqs.globalspawn.command;

import com.mojang.brigadier.arguments.*;
import de.dafuqs.globalspawn.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class InitialSpawnCommand {
	
	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("initialspawnpoint")
				.requires((source) -> source.hasPermissionLevel(GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.commandPermissionLevel))
				.executes((commandContext) -> InitialSpawnCommand.executeQuery(commandContext.getSource()))
				.then(CommandManager.literal("query")
						.executes((context) -> InitialSpawnCommand.executeQuery(context.getSource())))
				.then(CommandManager.literal("remove")
						.executes((context) -> InitialSpawnCommand.executeRemove(context.getSource())))
				.then(CommandManager.literal("set")
						.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
								.then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
										.then(CommandManager.argument("angle", AngleArgumentType.angle())
												.then(CommandManager.argument("spread", IntegerArgumentType.integer())
														.executes((context) -> InitialSpawnCommand.executeSet(
																context.getSource(),
																DimensionArgumentType.getDimensionArgument(context, "dimension"),
																BlockPosArgumentType.getBlockPos(context, "position"),
																AngleArgumentType.getAngle(context, "angle"),
																IntegerArgumentType.getInteger(context, "spread"))
														))))))
		));
	}
	
	static int executeQuery(ServerCommandSource source) {
		GlobalSpawnPoint initialSpawnPoint = GlobalSpawnManager.getInitialSpawnPoint();
		if (initialSpawnPoint == null) {
			source.sendFeedback(() -> Text.translatable("commands.globalspawn.initialspawnpoint.query_not_set"), false);
		} else {
			BlockPos spawnBlockPos = initialSpawnPoint.getPos();
			RegistryKey<World> spawnWorld = initialSpawnPoint.getDimension();
			float angle = initialSpawnPoint.getAngle();
			
			source.sendFeedback(() -> Text.translatable("commands.globalspawn.initialspawnpoint.query_set_at", spawnWorld.getValue().toString(), spawnBlockPos.getX(), spawnBlockPos.getY(), spawnBlockPos.getZ(), angle), false);
		}
		return 1;
	}
	
	static int executeSet(ServerCommandSource source, ServerWorld serverWorld, BlockPos blockPos, float angle, int spread) {
		GlobalSpawnPoint initialSpawnPoint = new GlobalSpawnPoint(serverWorld.getRegistryKey(), blockPos, angle, spread);
		GlobalSpawnManager.setInitialSpawnPoint(initialSpawnPoint);
		source.sendFeedback(() -> Text.translatable("commands.globalspawn.initialspawnpoint.set_to", serverWorld.getRegistryKey().getValue().toString(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), angle), true);
		return 1;
	}
	
	static int executeRemove(ServerCommandSource source) {
		GlobalSpawnManager.unsetInitialSpawnPoint();
		source.sendFeedback(() -> Text.translatable("commands.globalspawn.initialspawnpoint.unset"), true);
		return 1;
	}
	
}