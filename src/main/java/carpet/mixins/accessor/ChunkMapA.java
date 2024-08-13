package carpet.mixins.accessor;

import net.minecraft.server.ChunkHolder;
import net.minecraft.server.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ChunkMap.class)
public interface ChunkMapA {
    @Accessor("dirty")
    Set<ChunkHolder> getDirty();

    @Accessor("dirty")
    void setDirty(Set<ChunkHolder> dirty);
}
