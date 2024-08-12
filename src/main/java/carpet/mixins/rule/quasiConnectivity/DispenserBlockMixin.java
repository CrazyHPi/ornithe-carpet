package carpet.mixins.rule.quasiConnectivity;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.DispenserBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Definition(id = "world", local = @Local(type = World.class, argsOnly = true))
    @Definition(id = "hasNeighborSignal", method = "Lnet/minecraft/world/World;hasNeighborSignal(Lnet/minecraft/util/math/BlockPos;)Z")
    @Definition(id = "up", method = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;")
    @Expression("world.hasNeighborSignal(?.up())")
    @ModifyExpressionValue(method = "neighborChanged", at = @At("MIXINEXTRAS:EXPRESSION"))
    public boolean modifyQuasiConnectivity(boolean original) {
		return CarpetSettings.quasiConnectivity && original;
	}
}
