package carpet.mixins.logging.client;

import carpet.duck.PlayerTabOverlay$;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.overlay.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameGui.class)
public abstract class GameGuiMixin {
    @Shadow
    @Final
    private PlayerTabOverlay playerTabOverlay;

    @ModifyExpressionValue(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;isIntegratedServerRunning()Z"
            )
    )
    private boolean isLogging(boolean original) {
        return original && !((PlayerTabOverlay$) playerTabOverlay).hasFooterOrHeader();
    }
}
