package de.dafuqs.globalspawn;

import net.minecraft.nbt.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.stat.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

public class GlobalSpawnMixinHandler {
	
	/**
	 * Sets compound tags for the spawn position of new players
	 * <p>
	 * CompoundTag is null when players first join => modify
	 * The tag is not really set to the player (so not permanent)
	 * but used to position the player in the world on spawn
	 *
	 * @param nbtCompound The NBTag of a connecting player
	 */
	public static NbtCompound modifySpawnRegistryPositionAndDimensionForNewPlayer(MinecraftServer server, NbtCompound nbtCompound) {
		if (GlobalSpawnManager.isInitialSpawnPointActive(server)) {
			return GlobalSpawnManager.getInitialSpawnPoint().getSpawnNbtCompound(server, nbtCompound);
		} else if (GlobalSpawnManager.isGlobalSpawnPointActive(server)) {
			return GlobalSpawnManager.getGlobalRespawnPoint().getSpawnNbtCompound(server, nbtCompound);
		}
		return nbtCompound;
	}
	
	/**
	 * Sets compound tags for the spawn position of existing players
	 * <p>
	 * The tag is not really set to the player (so not permanent)
	 * but used to position the player in the world on spawn
	 *
	 * @param nbtCompound The NBTag of a connecting player
	 * @return CompoundTag with modified spawn position and dimension
	 */
	public static NbtCompound modifySpawnRegistryPositionAndDimensionForExistingPlayer(MinecraftServer server, NbtCompound nbtCompound) {
		if (GlobalSpawnManager.isGlobalSpawnPointActive(server)) {
			return GlobalSpawnManager.getGlobalRespawnPoint().getSpawnNbtCompound(server, nbtCompound);
		} else {
			return nbtCompound;
		}
	}
	
	/**
	 * Moving a newly joined player to the world spawn
	 * @param serverPlayerEntity The player
	 */
	public static boolean movePlayerToSpawn(ServerPlayerEntity serverPlayerEntity) {
		@Nullable BlockPos spawnBlockPos = null;
		float angle = 0.0F;

		if (GlobalSpawnManager.isInitialSpawnPointActive(serverPlayerEntity.server) && isNewPlayer(serverPlayerEntity)) {
			spawnBlockPos = GlobalSpawnManager.getInitialSpawnPoint().getFinalSpawnPos(serverPlayerEntity.server);
			angle = GlobalSpawnManager.getGlobalRespawnPoint().getAngle();
		} else if (GlobalSpawnManager.isGlobalSpawnPointActive(serverPlayerEntity.server)) {
			spawnBlockPos = GlobalSpawnManager.getGlobalRespawnPoint().getFinalSpawnPos(serverPlayerEntity.server);
			angle = GlobalSpawnManager.getGlobalRespawnPoint().getAngle();
		}

		if (spawnBlockPos == null) {
			return false;
		}

		serverPlayerEntity.refreshPositionAndAngles(spawnBlockPos, angle, 0.0F);
		serverPlayerEntity.updatePosition(spawnBlockPos.getX() + 0.5F, spawnBlockPos.getY(), spawnBlockPos.getZ() + 0.5F);

		return true;
	}
	
	public static boolean isNewPlayer(ServerPlayerEntity serverPlayerEntity) {
		return serverPlayerEntity.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS)) == 0
			&& serverPlayerEntity.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.WALK_ONE_CM)) == 0;
	}
	
}
