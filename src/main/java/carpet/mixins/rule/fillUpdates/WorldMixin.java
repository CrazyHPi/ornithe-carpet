package carpet.mixins.rule.fillUpdates;

import carpet.SharedConstants;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(World.class)
public abstract class WorldMixin {
    @WrapWithCondition(
            method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;I)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;updateObservers(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"
            )
    )
    private boolean skipObserverUpdate(World instance, BlockPos pos, Block block, @Local(argsOnly = true) int flags) {
        return (flags & 128) == 0;
    }

    @WrapOperation(
            method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;I)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/WorldChunk;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;)Lnet/minecraft/block/state/BlockState;"
            )
    )
    private BlockState skipUpdates(WorldChunk instance, BlockPos pos, BlockState state, Operation<BlockState> original, @Local(argsOnly = true) int flags) {
        if ((flags & 128) != 0) {
            boolean temp = SharedConstants.shouldSkipUpdates.get();
            SharedConstants.shouldSkipUpdates.set(true);
            try {
                return original.call(instance, pos, state);
            } finally {
                SharedConstants.shouldSkipUpdates.set(temp);
            }
        }

        return original.call(instance, pos, state);
    }
}
