package carpet.mixins.rule.explosionNoBlockDamage;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @ModifyExpressionValue(
            method = "damageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/BlockState;",
                    remap = false
            )
    )
    private BlockState explosionNoDamage(BlockState original) {
        return CarpetSettings.explosionNoBlockDamage ? Blocks.BEDROCK.defaultState() : original;
    }
}
