package nl.toent.bloomyextended;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BloomyExtended implements ModInitializer {
	public static final String MOD_ID = "bloomyextended";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static boolean initializeServerCommands = false;
	private static String serverName = "";

	@Override
	public void onInitialize() {

		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			MinecraftServer server = world.getServer();
				if (world.getRegistryKey() == ServerWorld.OVERWORLD && !initializeServerCommands) {
					executeVanillaReloadCommands(server);
				}
		});

		//reset initialization boolean to fix an issue where the mod would work properly in single player
		ServerTickEvents.END_SERVER_TICK.register((world) -> {
			initializeServerCommands = false;
		});

		LOGGER.info("BloomyExtended Initialized!");
	}

	private void executeVanillaReloadCommands(MinecraftServer server) {
		server.execute(() -> {
			ServerCommandSource commandSource = server.getCommandSource();
			if (commandSource != null) {

				//Disable Vanilla Reload Entity health bars.
				server.getCommandManager().executeWithPrefix(commandSource, "function vanilla_refresh:other/actions/mob/mob_health_disable");



				initializeServerCommands = true; // Prevent further executions
			}
		});
	}
}