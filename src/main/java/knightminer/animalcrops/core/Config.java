package knightminer.animalcrops.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import knightminer.animalcrops.AnimalCrops;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {

	public static List<ResourceLocation> animals = Collections.emptyList();
	private static String[] animalDefaults = {
			"minecraft:chicken",
			"minecraft:cow",
			"minecraft:donkey",
			"minecraft:horse",
			"minecraft:llama",
			"minecraft:mooshroom",
			"minecraft:ocelot",
			"minecraft:parrot",
			"minecraft:pig",
			"minecraft:polar_bear",
			"minecraft:rabbit",
			"minecraft:sheep",
			"minecraft:villager",
			"minecraft:wolf",
	};
	public static boolean canBonemeal = true;
	public static boolean fancyCropRendering = true;
	public static boolean rightClickHarvest = true;
	public static boolean animalBush = true;
	public static int animalBushChance = 20;

	static Configuration configFile;
	public static void preInit(FMLPreInitializationEvent event) {
		configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

		canBonemeal = configFile.getBoolean("canBonemeal", "general", canBonemeal,
				"Determines if bonemeal can be applied to the animal crop");
		rightClickHarvest = configFile.getBoolean("rightClickHarvest", "general", rightClickHarvest,
				"Harvests the crop on right click (which is really the same as just breaking it). Added because people cannot write their right click harvest mods right.");
		animalBush = configFile.getBoolean("animalBush", "general", animalBush,
				"Adds the animal bush: a block that when broken drops a random animal seed.");
		animalBushChance = configFile.getInt("animalBushChance", "general", animalBushChance, 0, 500,
				"Chance for an animal bush to generate per chunk. Formula is a 1 in <chance> chance of generating. Set to 0 to disable generation.");

		fancyCropRendering = configFile.getBoolean("fancyCropRendering", "client", fancyCropRendering,
				"Makes the animal crop render the entity model. If false will just render a tinted texture based on the spawn egg colors");

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

	public static void init(FMLInitializationEvent event) {
		animalDefaults = configFile.get("general", "animals", animalDefaults,
				"List of animals to add as animal seeds. Must extend EntityLiving").getStringList();

		// ensure all the animals are valid
		animals = new ArrayList<>();
		for(String animal : animalDefaults) {
			// ensure the entity is registered
			ResourceLocation location = new ResourceLocation(animal);
			if(!EntityList.ENTITY_EGGS.containsKey(location)) {
				AnimalCrops.log.error("Invalid entity {}, must have a spawn egg", animal);
				continue;
			}
			if(EntityList.isRegistered(location)) {
				// insure the entity type is valid, we only allow entity creature
				if(EntityLiving.class.isAssignableFrom(EntityList.getClass(location))) {
					animals.add(location);
				} else {
					AnimalCrops.log.error("Invalid entity type for {}, must extend EntityLiving", animal);
				}
			} else {
				AnimalCrops.log.debug("Could not find entity {}, either entity is missing or the ID is incorrect", animal);
			}
		}

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}

}
