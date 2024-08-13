package carpet.mixins.rule.tntDoNotUpdate;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.TntBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin {
    @ModifyExpressionValue(
            method = "onAdded",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;hasNeighborSignal(Lnet/minecraft/util/math/BlockPos;)Z"
            )
    )
    private boolean onTNTPlaced(boolean original) {
        return !CarpetSettings.tntDoNotUpdate && original;
    }
}
