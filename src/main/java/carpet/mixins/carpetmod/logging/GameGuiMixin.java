package carpet.mixins.carpetmod.logging;

import carpet.fakes.carpetmod.PlayerTabOverlayInterface;
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

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;isIntegratedServerRunning()Z"
            )
    )
    private boolean isLogging(Minecraft instance) {
        return instance.isIntegratedServerRunning() && !((PlayerTabOverlayInterface) playerTabOverlay).ornithe_carpet$hasFooterOrHeader();
    }
}
