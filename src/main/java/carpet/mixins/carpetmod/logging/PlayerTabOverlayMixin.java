package carpet.mixins.carpetmod.logging;

import carpet.fakes.carpetmod.PlayerTabOverlayInterface;
import net.minecraft.client.gui.overlay.PlayerTabOverlay;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin implements PlayerTabOverlayInterface {
    @Shadow
    private Text footer;

    @Shadow
    private Text header;

    @Override
    public boolean ornithe_carpet$hasFooterOrHeader() {
        return footer != null || header != null;
    }
}
