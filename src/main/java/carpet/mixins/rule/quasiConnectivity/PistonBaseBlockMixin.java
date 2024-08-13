package carpet.mixins.rule.quasiConnectivity;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {
    @Definition(id = "world", local = @Local(type = World.class, argsOnly = true))
    @Definition(id = "hasSignal", method = "Lnet/minecraft/world/World;hasSignal(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
    @Definition(id = "blockPos", local = @Local(type = BlockPos.class, ordinal = 1))
    @Definition(id = "offset", method = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;")
    @Definition(id = "direction2", local = @Local(type = Direction.class, ordinal = 1))
    @Expression("world.hasSignal(blockPos.offset(direction2), direction2)")
    @ModifyExpressionValue(method = "shouldExtend", at = @At("MIXINEXTRAS:EXPRESSION"))
    public boolean modifyQuasiConnectivity(boolean original) {
        return CarpetSettings.quasiConnectivity && original;
    }
}
