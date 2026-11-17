package carpet.mixins.rule.fillUpdates;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.state.BlockState;
import net.minecraft.server.command.CloneCommand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CloneCommand.class)
public abstract class CloneCommandMixin {
    @WrapOperation(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;I)Z"
            )
    )
    private boolean noCloneUpdates(World instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
        return original.call(instance, pos, state, flags | (CarpetSettings.fillUpdates? 0 : 128));
    }

    @ModifyExpressionValue(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;hasNext()Z",
                    ordinal = 5
            )
    )
    private boolean noCloneNCUpdates(boolean original) {
        if (!CarpetSettings.fillUpdates) {
            return false;
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;hasNext()Z",
                    ordinal = 6
            )
    )
    private boolean noCloneScheduledTick(boolean original) {
        if (!CarpetSettings.fillUpdates) {
            return false;
        }
        return original;
    }
}
