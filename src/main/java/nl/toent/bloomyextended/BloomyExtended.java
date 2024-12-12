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
		NbtCompound tag = bookStack.getOrCreateNbt();

		tag.putString("title", "The Bloomy Modpack");
		tag.putString("author", "toent");

		// Create pages
		NbtList pages = new NbtList();
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("===================\n The Bloomy Modpack\n        By toent\n===================Welcome to The Bloomy Modpack!\nIn this book I (toent) will run you through some simple instructions, tips and configuration recommendations to get you started in this Vanilla++ Pack!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("===================\n         LevelZ\n     By Globox1997\n===================The biggest change over vanilla in the pack is the LevelZ mod by Globox1997.\nIt is the reason why you are starting with so little Hearts."))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("The main reason why it was added to slow down game progression, since too many people rush through the game nowadays.\nYou can find and level your stats in the LevelZ Tab in you inventory!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("On this tab you can find the EXP you need until level up and your current stats, you will also see that you get 5 free points to get started.\nTo see what levelling a class unlocks, you can click on the icon of a class."))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("You might have spotted other tabs besides the LevelZ menu as well.\nThese are for the support mods: JobsAddon and PartyAddon, these mods allow you to gain LevelZ EXP faster!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("I have also developed and added an extra mod that grants LevelZ EXP for breaking blocks with the right tools to speedup things even more!\nIf the LevelZ experience is still not satisfactory for you, you can always modify the settings in the Mod Menu!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("===================\n   Distant Horizons\n     By jeseibel(+)\n===================\nWondering why you can see this far? (or why your computer is (not) lagging) This is because of The Distant Horizons mod that lets you see far distances without taking up too much performance!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("Distant Horizons can be a bit rough to run on some computers, as it is only a visual mod, you can safely turn it off in the button next to the FOV in the settings! Set Enable Rendering to False to turn it off, or lower the LOD Render Distance Radius to make the mod run better!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("If you decide to turn Distant Horizons off, skip this section.\nIf you decide to turn it on, let me instruct you how to get the most out of it. Using the Chunky Mod (By pop4959) you can preload chunks so that Distant Horizons actually knows what to show you and saving you resources!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("To use Chunky, type /Chunky in chat!\nTo further improve performance using Distant Horizons, you can lower your actual Minecraft Render Distance, since Distant Horizons Render Distance is easier to run than Minecraft Render Distance."))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("===================\nBloomy Settings Import\n        By toent\n===================\nSick of always having to change your settings to the ones you are used to when running a new Modpack or Modded Instance? Import/Export them with this mod!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("Importing and Exporting is super easy! Just find the folder file path the contains your options.txt file (EXCLUDING the file) to import it. Or just choose a file directory to save it to!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("The Bloomy Settings Import Mod works together well with Options Profiles Mod (By AxolotlMaid), since you can load multiple different Options.txt files into different profiles without them interfering."))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("===================\n      Other Stuff!\n\n===================\nSome other stuff I recommend looking into before getting started are the keybinds, as there are many conflicting mod keybinds by default!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("There are also plenty of other cool mods included in the pack, which you really should check out!\nThese mods are, but are not limited to Better Archeology (By Pandarix), Essential (By SparkUniverse_), Friends&Foes (By Faboslav) and a lot of structure mods by talented creators!"))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("===================\n         Epilogue\n\n===================\nFinally, I want to thank you for playing The Bloomy Modpack! All the mods should work great in servers or with Essential's multiplayer sessions as well as of course, Singleplayer Gameplay."))));
		pages.add(NbtString.of(Text.Serializer.toJson(Text.literal("I could go more indepth about more mods and how multiple other mods work, however I believe that the beauty of Minecraft lies in exploration, and thus I will let you discover most stuff yourself!\n\n       Have Fun!\n       xx toent"))));
		// Set the pages in the book's NBT data

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