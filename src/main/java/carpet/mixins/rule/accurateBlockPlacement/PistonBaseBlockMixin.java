package carpet.mixins.rule.accurateBlockPlacement;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {
    @WrapWithCondition(method = "onPlaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;I)Z"))
    public boolean skipFixFacing(World world, BlockPos pos, BlockState state, int flags) {
        return !CarpetSettings.accurateBlockPlacement;
    }
}
