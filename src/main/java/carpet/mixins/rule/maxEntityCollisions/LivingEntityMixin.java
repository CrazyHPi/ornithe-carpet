package carpet.mixins.rule.maxEntityCollisions;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.living.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyExpressionValue(
        method = "pushAwayCollidingEntities",
        at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/living/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
        )
    )
    private int limitPushedEntities(int collidingEntities) {
        int maxEntityCollisions = CarpetSettings.maxEntityCollisions;
        return maxEntityCollisions > 0 ? Math.min(collidingEntities, maxEntityCollisions) : collidingEntities;
    }
}
