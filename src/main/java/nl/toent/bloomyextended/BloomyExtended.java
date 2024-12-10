package nl.toent.bloomyextended;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BloomyExtended implements ModInitializer {
	public static final String MOD_ID = "bloomyextended";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static boolean initializeServerCommands = false;

	@Override
	public void onInitialize() {
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			MinecraftServer server = world.getServer();
			if (world.getRegistryKey() == ServerWorld.OVERWORLD && !initializeServerCommands) {
				executeVanillaReloadCommands(server);
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->{
			client.execute(() -> {
				initializeServerCommands = false;
			});
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, world) -> {
			giveTutorialBook(handler.getPlayer(), handler.getPlayer().getServer());
		});

		LOGGER.info("BloomyExtended Initialized!");
	}

	private void executeVanillaReloadCommands(MinecraftServer server) {
		server.execute(() -> {
			ServerCommandSource commandSource = server.getCommandSource();
			if (commandSource != null) {
				// Disable Vanilla Reload Entity health bars.
				server.getCommandManager().executeWithPrefix(commandSource, "function vanilla_refresh:other/actions/mob/mob_health_disable");
				initializeServerCommands = true; // Prevent further executions
			}
		});
	}

	public void giveTutorialBook(ServerPlayerEntity player, MinecraftServer server) {
		PlayerBookState state = PlayerBookState.get(server);

		if (state == null) {
			LOGGER.error("PlayerBookState is null! Cannot give book to player.");
			return;
		}

		if (state.hasReceivedBook(player.getUuid())) {
			return; // Player has already received the book
		}

		// Create book item
		ItemStack bookStack = new ItemStack(Items.WRITTEN_BOOK);

		// Create pages
		NbtList pages = new NbtList();
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("Welcome to the modpack! Here's how to use it..."))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("Page 2: Instructions go here..."))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("Page 3: More instructions..."))));

		// Set the pages in the book's NBT data
		NbtCompound tag = bookStack.getOrCreateNbt();
		tag.put("pages", pages);
		bookStack.setNbt(tag);

		// Give the book to the player
		if (!player.getInventory().insertStack(bookStack)) {
			player.dropItem(bookStack, false);
			LOGGER.info("Dropped book for player: {}", player.getName().getString());
		} else {
			LOGGER.info("Gave book to player: {}", player.getName().getString());
		}

		state.givePlayerBook(player.getUuid());
		state.markDirty();
	}
}