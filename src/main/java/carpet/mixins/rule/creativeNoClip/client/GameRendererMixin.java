package carpet.mixins.rule.creativeNoClip.client;

import carpet.CarpetSettings;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Redirect(
            method = "render(IFJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;isSpectator()Z"
            )
    )
    private boolean fixSpec(LocalClientPlayerEntity instance) {
        return instance.isSpectator() || (CarpetSettings.creativeNoClip && instance.isCreative());
    }
}
