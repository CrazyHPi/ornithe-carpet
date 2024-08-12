package carpet.mixins.carpetmod.logging.client;

import carpet.fakes.PlayerTabOverlayInterface;
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
    public boolean carpet$hasFooterOrHeader() {
        return footer != null || header != null;
    }
}
