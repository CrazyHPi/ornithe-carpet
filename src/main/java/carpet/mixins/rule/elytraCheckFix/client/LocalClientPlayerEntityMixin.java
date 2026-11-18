package carpet.mixins.rule.elytraCheckFix.client;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalClientPlayerEntity.class)
public abstract class LocalClientPlayerEntityMixin {
    // clientside check to allow elytra deployment with positive y-velocity
    @ModifyExpressionValue(
            method = "mobTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;velocityY:D",
                    ordinal = 0
            )
    )
    private double elytraCheckFix(double velocityY) {
        return CarpetSettings.elytraCheckFix ? -1 : velocityY;
    }
}
