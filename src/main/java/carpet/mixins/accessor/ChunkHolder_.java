package carpet.mixins.accessor;

import net.minecraft.server.ChunkHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkHolder.class)
public interface ChunkHolder_ {
	@Accessor
	int getBlocksChanged();
}
