package carpet.mixins.rule.relaxedBlockPlacement;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.PumpkinBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PumpkinBlock.class)
public abstract class PumpkinBlockMixin {
    @ModifyExpressionValue(method = "canSurvive", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/BlockState;isFullBlock()Z"))
    public boolean relaxBlockSurvival(boolean original) {
        return CarpetSettings.relaxedBlockPlacement || original;
    }
}
