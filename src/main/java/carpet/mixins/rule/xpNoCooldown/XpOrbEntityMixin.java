package carpet.mixins.rule.xpNoCooldown;

import carpet.CarpetSettings;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(XpOrbEntity.class)
public abstract class XpOrbEntityMixin {
    @Inject(method = "onPlayerCollision", at = @At(value = "HEAD"))
    private void removeDelay(PlayerEntity playerEntity, CallbackInfo ci) {
        if (CarpetSettings.xpNoCooldown) {
            playerEntity.xpCooldown = 0;
        }
    }
}
