package carpet.validators;

import carpet.api.settings.Validators;
import carpet.mixins.accessor.ChunkMap_;
import carpet.mixins.accessor.ServerWorld_;
import carpet.CarpetServer;
import com.google.common.collect.Sets;
import net.minecraft.server.ChunkHolder;
import net.minecraft.server.ChunkMap;
import net.minecraft.server.world.ServerWorld;

import java.util.Set;

public class ChunkMapCrashFixModifier extends Validators.SideEffectValidator<Boolean> {
    @Override
    public Boolean parseValue(Boolean newValue) {
        return newValue;
    }

    @Override
    public void performEffect(Boolean newValue) {
        if (CarpetServer.minecraftServer == null || CarpetServer.minecraftServer.worlds == null) return;
        ServerWorld[] worlds = CarpetServer.minecraftServer.worlds;
        for (ServerWorld world : worlds) {
            if (world == null) continue;
            fixChunkMap(((ServerWorld_) world).getChunkMap(), newValue);
        }
    }

    private static void fixChunkMap(ChunkMap chunkMap, boolean crashFix) {
        Set<ChunkHolder> oldDirty = ((ChunkMap_) chunkMap).getDirty();
        Set<ChunkHolder> newDirty = crashFix ? Sets.newConcurrentHashSet() : Sets.newHashSet();
        newDirty.addAll(oldDirty);
        ((ChunkMap_) chunkMap).setDirty(newDirty);
    }
}
