package carpet.mixins.accessor;

import net.minecraft.server.ChunkHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkHolder.class)
public interface ChunkHolderA {
    @Accessor("blocksChanged")
    int getBlocksChanged();
}
