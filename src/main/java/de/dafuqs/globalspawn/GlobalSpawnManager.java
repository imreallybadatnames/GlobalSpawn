package de.dafuqs.globalspawn;

import net.minecraft.registry.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class GlobalSpawnManager {
	
	private static @Nullable GlobalSpawnPoint globalRespawnPoint;
	private static @Nullable GlobalSpawnPoint initialSpawnPoint;
	
	// GENERAL
	public static void initialize(MinecraftServer server) {
		boolean shouldRespawnPointBeActive = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPointActive;
		RegistryKey<World> globalSpawnWorldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnDimension));
		if (shouldRespawnPointBeActive && existsWorld(server, globalSpawnWorldKey)) {
			int x = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPositionX;
			int y = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPositionY;
			int z = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPositionZ;
			int spread = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPositionSpread;
			GlobalSpawnPoint.SpawnCriterion criterion = GlobalSpawnPoint.SpawnCriterion.valueOf(GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnCriterion);
			float angle = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnAngle;
			globalRespawnPoint = new GlobalSpawnPoint(globalSpawnWorldKey, new BlockPos(x, y, z), spread, criterion, angle);
		}
		
		boolean shouldInitialSpawnPointBeActive = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPointActive;
		RegistryKey<World> initialSpawnWorldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPointDimension));
		if (shouldInitialSpawnPointBeActive && existsWorld(server, initialSpawnWorldKey)) {
			int x = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPositionX;
			int y = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPositionY;
			int z = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPositionZ;
			int spread = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPositionSpread;
			GlobalSpawnPoint.SpawnCriterion criterion = GlobalSpawnPoint.SpawnCriterion.valueOf(GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnCriterion);
			float angle = GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnAngle;
			initialSpawnPoint = new GlobalSpawnPoint(initialSpawnWorldKey, new BlockPos(x, y, z), spread, criterion, angle);
		}
	}
	
	private static boolean existsWorld(MinecraftServer server, RegistryKey<World> registryKey) {
		return server.getWorld(registryKey) != null;
	}
	
	private static void updateConfigFile() {
		// respawn point
		if (globalRespawnPoint != null) {
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPointActive = true;
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnDimension = globalRespawnPoint.getDimension().getValue().toString();
			
			BlockPos globalRespawnPointSpawnBlockPos = globalRespawnPoint.getPos();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPositionX = globalRespawnPointSpawnBlockPos.getX();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPositionY = globalRespawnPointSpawnBlockPos.getY();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPositionZ = globalRespawnPointSpawnBlockPos.getZ();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPositionSpread = globalRespawnPoint.getHorizontalSpread();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnCriterion = initialSpawnPoint.getCriterion().toString();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnAngle = globalRespawnPoint.getAngle();
		} else {
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.globalRespawnPointActive = false;
		}
		
		// initial spawn point
		if (initialSpawnPoint != null) {
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPointActive = true;
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPointDimension = initialSpawnPoint.getDimension().getValue().toString();
			
			BlockPos initialSpawnPointSpawnBlockPos = initialSpawnPoint.getPos();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPositionX = initialSpawnPointSpawnBlockPos.getX();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPositionY = initialSpawnPointSpawnBlockPos.getY();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPositionZ = initialSpawnPointSpawnBlockPos.getZ();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPositionSpread = initialSpawnPoint.getHorizontalSpread();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnCriterion = initialSpawnPoint.getCriterion().toString();
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnAngle = initialSpawnPoint.getAngle();
		} else {
			GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG.initialSpawnPointActive = false;
		}
		
		GlobalSpawnCommon.GLOBAL_SPAWN_CONFIG_HOLDER.save();
	}
	
	// RESPAWN
	public static void setGlobalSpawnPoint(GlobalSpawnPoint globalRespawnPoint) {
		GlobalSpawnManager.globalRespawnPoint = globalRespawnPoint;
		updateConfigFile();
	}
	
	public static @Nullable GlobalSpawnPoint getGlobalRespawnPoint() {
		return globalRespawnPoint;
	}
	
	public static void unsetGlobalSpawnPoint() {
		globalRespawnPoint = null;
		updateConfigFile();
	}
	
	public static boolean isGlobalSpawnPointActive(MinecraftServer server) {
		if (globalRespawnPoint == null) {
			return false;
		}
		
		if (existsWorld(server, globalRespawnPoint.getDimension())) {
			return true;
		} else {
            GlobalSpawnCommon.LOGGER.warn("Respawn dimension {} is not loaded. GlobalRespawn is disabled", globalRespawnPoint.getDimension());
			return false;
		}
	}
	
	// INITIAL SPAWN
	public static void setInitialSpawnPoint(GlobalSpawnPoint initialSpawnPoint) {
		GlobalSpawnManager.initialSpawnPoint = initialSpawnPoint;
		updateConfigFile();
	}
	
	public static @Nullable GlobalSpawnPoint getInitialSpawnPoint() {
		return initialSpawnPoint;
	}
	
	public static void unsetInitialSpawnPoint() {
		initialSpawnPoint = null;
		updateConfigFile();
	}
	
	public static boolean isInitialSpawnPointActive(MinecraftServer server) {
		if (initialSpawnPoint == null) {
			return false;
		}
		
		if (existsWorld(server, initialSpawnPoint.getDimension())) {
			return true;
		} else {
            GlobalSpawnCommon.LOGGER.warn("Initial spawn dimension {} is not loaded. InitialSpawn is disabled", initialSpawnPoint.getDimension());
			return false;
		}
	}
	
}
