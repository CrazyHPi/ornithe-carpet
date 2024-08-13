package carpet.mixins.accessor;

import net.minecraft.server.ChunkMap;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerWorld.class)
public interface ServerWorld_ {
    @Accessor
    ChunkMap getChunkMap();
}
