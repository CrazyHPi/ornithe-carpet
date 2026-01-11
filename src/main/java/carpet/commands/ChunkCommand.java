package carpet.commands;

import carpet.CarpetSettings;
import carpet.mixins.accessor.ChunkHolderA;
import carpet.mixins.accessor.ServerChunkCacheA;
import carpet.mixins.accessor.ServerWorldA;
import carpet.mixins.accessor.WorldA;
import carpet.utils.Messenger;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.ChunkHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.world.chunk.ServerChunkCache;
import net.minecraft.text.LiteralText;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkGenerator;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChunkCommand extends CarpetAbstractCommand {
    // for chunk command
    public static List<Long> removeChunk = new ArrayList<>();

    /**
     * Gets the name of the command
     */
    @Override
    public String getName() {
        return "chunk";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "Usage: chunk <load | info | unload | regen | repop | asyncrepop | setInvisible | remove> <X> <Z>";
    }

    @Override
    public boolean canUse(MinecraftServer server, CommandSource source) {
        return canUseCommand(source, CarpetSettings.commandChunk);
    }

    protected World world;

    /**
     * Callback for when the command is executed
     */
    @Override
    public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {

        if (args.length != 3) {
            throw new IncorrectUsageException(getUsage(source));
        }

        world = source.getSourceWorld();
        try {
            int chunkX = parseChunkPosition(args[1], source.getSourceBlockPos().getX());
            int chunkZ = parseChunkPosition(args[2], source.getSourceBlockPos().getZ());

            switch (args[0]) {
                case "load":
                    world.getChunkAt(chunkX, chunkZ);
                    source.sendMessage(new LiteralText("Chunk " + chunkX + ", " + chunkZ + " loaded"));
                    return;
                case "unload":
                    unload(source, chunkX, chunkZ);
                    return;
                case "regen":
                    regen(source, chunkX, chunkZ);
                    return;
                case "repop":
                    repop(source, chunkX, chunkZ);
                    return;
                case "asyncrepop":
                    asyncrepop(source, chunkX, chunkZ);
                    return;
                case "setInvisible":
                    setInvisible(source, chunkX, chunkZ);
                    return;
                case "remove":
                    remove(source, chunkX, chunkZ);
                    return;
                case "info":
                default:
                    info(source, chunkX, chunkZ);

            }
        } catch (Exception e) {
            throw new IncorrectUsageException(getUsage(source));
        }
    }

    private boolean checkRepopLoaded(int x, int z) {
        return ((WorldA) world).invokeIsChunkLoadedAt(x, z, false)
                && ((WorldA) world).invokeIsChunkLoadedAt(x + 1, z, false)
                && ((WorldA) world).invokeIsChunkLoadedAt(x, z + 1, false)
                && ((WorldA) world).invokeIsChunkLoadedAt(x + 1, z + 1, false);
    }

    private void regen(CommandSource source, int x, int z) {
        if (!checkRepopLoaded(x, z)) {
            Messenger.m(source, "w Area not loaded for re-population");
        }

        ServerChunkCache chunkProvider = (ServerChunkCache) world.getChunkSource();
        long i = ChunkPos.toLong(x, z);
        ((ServerChunkCacheA) chunkProvider).getChunks().remove(i);
        WorldChunk chunk = ((ServerChunkCacheA) chunkProvider).getGenerator().getChunk(x, z);
        ((ServerChunkCacheA) chunkProvider).getChunks().put(i, chunk);
        chunk.load();
        chunk.setTerrainPopulated(true);
        chunk.tick(false);
        ChunkHolder entry = ((ServerWorldA) world).getChunkMap().getChunk(x, z);
        if (entry != null && ((ChunkHolderA) entry).getChunk() != null) {
            ((ChunkHolderA) entry).setChunk(chunk);
            ((ChunkHolderA) entry).setPopulated(false);
            entry.populate();
        }
    }

    private void repop(CommandSource source, int x, int z) {
        if (!checkRepopLoaded(x, z)) {
            Messenger.m(source, "w Area not loaded for re-population");
        }

        ServerChunkCache chunkProvider = (ServerChunkCache) world.getChunkSource();
        ChunkGenerator chunkGenerator = ((ServerChunkCacheA) chunkProvider).getGenerator();
        WorldChunk chunk = chunkProvider.getGeneratedChunk(x, z);
        chunk.setTerrainPopulated(false);
        chunk.populate(chunkProvider, chunkGenerator);
    }

    private void asyncrepop(CommandSource source, int x, int z) {
        if (!checkRepopLoaded(x, z)) {
            Messenger.m(source, "w Area not loaded for re-population");
        }

        HttpUtil.DOWNLOAD_THREAD_FACTORY.submit(() -> {
            try {
                ServerChunkCache chunkProvider = (ServerChunkCache) world.getChunkSource();
                ChunkGenerator chunkGenerator = ((ServerChunkCacheA) chunkProvider).getGenerator();
                WorldChunk chunk = chunkProvider.getGeneratedChunk(x, z);
                chunk.setTerrainPopulated(false);
                chunk.populate(chunkProvider, chunkGenerator);
                System.out.println("Chunk async repop end.");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    protected void info(CommandSource source, int x, int z) throws NoSuchFieldException, IllegalAccessException {
        if (!((WorldA) world).invokeIsChunkLoadedAt(x, z, false)) {
            Messenger.m(source, "w Chunk is not loaded");
        }

        long i = ChunkPos.toLong(x, z);
        ServerChunkCache provider = (ServerChunkCache) world.getChunkSource();
        int mask = getMask((Long2ObjectOpenHashMap<WorldChunk>) ((ServerChunkCacheA) provider).getChunks());
        long key = HashCommon.mix(i) & mask;
        Messenger.m(source, "w Chunk ideal key is " + key);
        if (world.isSpawnChunk(x, z))
            Messenger.m(source, "w Spawn Chunk");
    }

    protected void unload(CommandSource source, int x, int z) {
        if (!((WorldA) world).invokeIsChunkLoadedAt(x, z, false)) {
            Messenger.m(source, "w Chunk is not loaded");
            return;
        }
        WorldChunk chunk = world.getChunkAt(x, z);
        ServerChunkCache provider = (ServerChunkCache) world.getChunkSource();
        provider.unloadChunk(chunk);
        Messenger.m(source, "w Chunk is queue to unload");
    }

    protected void setInvisible(CommandSource source, int x, int z) {
        if (!((WorldA) world).invokeIsChunkLoadedAt(x, z, false)) {
            Messenger.m(source, "w Chunk is not loaded");
            return;
        }
        WorldChunk chunk = world.getChunkAt(x, z);
        chunk.setTerrainPopulated(false);
    }

    protected void remove(CommandSource source, int x, int z) {
        long chunkPos = ChunkPos.toLong(x, z);
        if (!removeChunk.contains(chunkPos)) removeChunk.add(chunkPos);
        Messenger.m(source, "w Marked chunk for regeneration");
    }

    @Override
    public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, @Nullable BlockPos targetPos) {
        int chunkX = source.getSourceBlockPos().getX() >> 4;
        int chunkZ = source.getSourceBlockPos().getZ() >> 4;

        if (args.length == 1) {
            return suggestMatching(args, "info", "load", "unload", "regen", "repop", "asyncrepop", "setInvisible", "remove");
        } else if (args.length == 2) {
            return suggestMatching(args, Integer.toString(chunkX), "~");
        } else if (args.length == 3) {
            return suggestMatching(args, Integer.toString(chunkZ), "~");
        } else {
            return Collections.emptyList();
        }
    }

    public static int getMask(Long2ObjectOpenHashMap hashMap) throws NoSuchFieldException, IllegalAccessException {
        Field mask = Long2ObjectOpenHashMap.class.getDeclaredField("mask");
        mask.setAccessible(true);
        return (int) mask.get(hashMap);
    }
}
