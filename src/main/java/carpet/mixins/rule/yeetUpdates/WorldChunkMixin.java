package carpet.mixins.rule.yeetUpdates;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
    @WrapWithCondition(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onAdded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;)V"))
    public boolean yeetInitialUpdates(Block block, World world, BlockPos pos, BlockState state) {
        return !CarpetSettings.yeetInitialUpdates;
    }

    @WrapWithCondition(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onRemoved(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;)V"))
    public boolean yeetRemovalUpdates(Block block, World world, BlockPos pos, BlockState state) {
        return !CarpetSettings.yeetRemovalUpdates;
    }
}
