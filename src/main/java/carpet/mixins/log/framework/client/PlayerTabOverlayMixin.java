package carpet.mixins.log.framework.client;

import carpet.fakes.PlayerTabOverlayF;
import net.minecraft.client.gui.overlay.PlayerTabOverlay;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin implements PlayerTabOverlayF {
    @Shadow
    private Text footer;

    @Shadow
    private Text header;

    @Override
    public boolean hasFooterOrHeader() {
        return footer != null || header != null;
    }
}
