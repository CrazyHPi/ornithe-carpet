package carpet.mixins.rule.fillUpdates;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SetBlockCommand.class)
public abstract class SetBlockCommandMixin {
    @WrapOperation(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;I)Z"
            )
    )
    private boolean noSetUpdates(World instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
        return original.call(instance, pos, state, flags | (CarpetSettings.fillUpdates? 0 : 128));
    }

    @WrapWithCondition(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;onBlockChanged(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V"
            )
    )
    private boolean noSetObserverUpdates(World instance, BlockPos pos, Block block, boolean updateObservers) {
        return CarpetSettings.fillUpdates;
    }
}
