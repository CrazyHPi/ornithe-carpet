package carpet.mixins.rule.explosionNoBlockDamage;

import carpet.CarpetSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Redirect(
            method = "damageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/BlockState;",
                    remap = false
            )
    )
    private BlockState explosionNoDamage(World instance, BlockPos pos) {
        return CarpetSettings.explosionNoBlockDamage ? Blocks.BEDROCK.defaultState() : instance.getBlockState(pos);
    }
}
