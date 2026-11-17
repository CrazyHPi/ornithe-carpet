package carpet.mixins.rule.fillUpdates;

import carpet.SharedConstants;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
    @Definition(id = "world", field = "Lnet/minecraft/world/chunk/WorldChunk;world:Lnet/minecraft/world/World;")
    @Definition(id = "isClient", field = "Lnet/minecraft/world/World;isClient:Z")
    @Expression("this.world.isClient")
    @ModifyExpressionValue(method = "setBlockState", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean skipUpdates(boolean original) {
        return SharedConstants.shouldSkipUpdates.get() || original;
    }
}
