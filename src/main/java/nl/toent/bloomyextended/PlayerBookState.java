package nl.toent.bloomyextended;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerBookState extends PersistentState {
    private Set<UUID> playersWithBook = new HashSet<>();

    // Default constructor
    public PlayerBookState() {
        // This constructor is used when creating a new instance
    }

    // Constructor that initializes the state from NBT data
    public PlayerBookState(NbtCompound nbt) {
        readNbt(nbt);
    }

    public boolean hasReceivedBook(UUID playerUUID) {
        return playersWithBook.contains(playerUUID);
    }

    public void givePlayerBook(UUID playerUUID) {
        playersWithBook.add(playerUUID);
        markDirty(); // Mark the state as dirty to ensure it gets saved
    }


    public void readNbt(NbtCompound nbt) {
        // Read the UUIDs from the NBT data
        NbtList uuidList = nbt.getList("playersWithBook", 8); // 8 is the tag type for String
        playersWithBook = new HashSet<>();
        for (int i = 0; i < uuidList.size(); i++) {
            String uuidString = uuidList.getString(i); // Get each UUID as a String
            playersWithBook.add(UUID.fromString(uuidString)); // Convert String to UUID
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // Write the UUIDs to the NBT data
        NbtList uuidList = new NbtList();
        for (UUID uuid : playersWithBook) {
            uuidList.add(NbtString.of(uuid.toString())); // Store UUID as String
        }
        nbt.put("playersWithBook", uuidList);
        return nbt;
    }

    public static PlayerBookState get(MinecraftServer server) {
        // Retrieve or create the PlayerBookState
        return server.getWorld(ServerWorld.OVERWORLD)
                .getPersistentStateManager()
                .getOrCreate(PlayerBookState::new, PlayerBookState::new, "player_book_state");
    }
}