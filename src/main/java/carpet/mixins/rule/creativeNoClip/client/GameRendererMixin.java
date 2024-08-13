package carpet.mixins.rule.creativeNoClip.client;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyExpressionValue(
            method = "render(IFJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;isSpectator()Z"
            )
    )
    private boolean fixSpec(boolean original) {
        return original || (CarpetSettings.creativeNoClip && minecraft.player.isCreative());
    }
}
