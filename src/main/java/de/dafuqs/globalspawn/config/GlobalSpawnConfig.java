package de.dafuqs.globalspawn.config;

import me.shedaniel.autoconfig.*;
import me.shedaniel.autoconfig.annotation.*;

@Config(name = "GlobalSpawn")
public class GlobalSpawnConfig implements ConfigData {
	
	public int commandPermissionLevel = 2;
	public boolean spawnAtGlobalSpawnOnEveryJoin = false;
	
	public boolean globalRespawnPointActive = false;
	public String globalRespawnDimension = "minecraft:overworld";
	public int globalRespawnPositionX = 50;
	public int globalRespawnPositionY = 80;
	public int globalRespawnPositionZ = 50;
	public int globalRespawnPositionSpread = 0;
	public String globalRespawnCriterion = "SAFE_WITH_SKY_ACCESS";
	public float globalRespawnAngle = 0;

	public boolean initialSpawnPointActive = false;
	public String initialSpawnPointDimension = "minecraft:overworld";
	public int initialSpawnPositionX = 50;
	public int initialSpawnPositionY = 80;
	public int initialSpawnPositionZ = 50;
	public int initialSpawnPositionSpread = 0;
	public String initialSpawnCriterion = "SAFE_WITH_SKY_ACCESS";
	public float initialSpawnAngle = 0;
}
