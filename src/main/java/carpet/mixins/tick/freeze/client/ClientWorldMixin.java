package carpet.mixins.tick.freeze.client;

import carpet.fakes.WorldF;
import carpet.helpers.TickRateManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements WorldF {
    @Unique
    private TickRateManager tickRateManager;

    @Inject(
            method = "<init>",
            at = @At(
                    "RETURN"
            )
    )
    private void onInit(CallbackInfo ci) {
        this.tickRateManager = new TickRateManager();
    }

    @Unique
    @Override
    public TickRateManager tickRateManager() {
        return tickRateManager;
    }
}
