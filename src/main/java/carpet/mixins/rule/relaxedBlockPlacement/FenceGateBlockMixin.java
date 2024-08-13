package carpet.mixins.rule.relaxedBlockPlacement;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.FenceGateBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FenceGateBlock.class)
public abstract class FenceGateBlockMixin {
    @ModifyExpressionValue(method = "canSurvive", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/material/Material;isSolid()Z"))
    public boolean relaxBlockSurvival(boolean original) {
        return CarpetSettings.relaxedBlockPlacement || original;
    }
}
