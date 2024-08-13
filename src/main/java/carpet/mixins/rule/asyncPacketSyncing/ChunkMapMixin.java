package carpet.mixins.rule.asyncPacketSyncing;

import carpet.mixins.accessor.ChunkHolder_;
import carpet.CarpetSettings;
import net.minecraft.server.ChunkHolder;
import net.minecraft.server.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V"))
    public void redirectClearDirtyChunks(Set<ChunkHolder> dirty) {
        if (CarpetSettings.asyncPacketSyncing) {
            // Iterator remove
            // Let's hope that no chunk is added during this, shall we?
            dirty.removeIf(chunkHolder -> ((ChunkHolder_) chunkHolder).getBlocksChanged() == 0);
        } else dirty.clear();
    }
}
