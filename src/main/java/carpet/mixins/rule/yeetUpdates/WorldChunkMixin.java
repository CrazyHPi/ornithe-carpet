package carpet.mixins.rule.yeetUpdates;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityProvider;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
    @WrapWithCondition(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onAdded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;)V"
            )
    )
    public boolean yeetInitialUpdates(Block block, World world, BlockPos pos, BlockState state) {
        return !CarpetSettings.yeetInitialUpdates;
    }

    @WrapOperation(
        method = "setBlockState",
        at = @At(value = "INVOKE",
                target = "Lnet/minecraft/block/Block;onRemoved(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;)V"
        )
    )
    public void yeetRemovalUpdates(Block block, World world, BlockPos pos, BlockState state, Operation<Void> original) {
        if (!CarpetSettings.yeetRemovalUpdates) {
            original.call(block, world, pos, state);
        } else if (block instanceof BlockEntityProvider) {
            // Prevent block entities remaining behind (CCE)
            world.removeBlockEntity(pos);
        }
    }
}
