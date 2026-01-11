package carpet.mixins.tick.freeze.client;

import carpet.fakes.MinecraftF;
import carpet.fakes.WorldF;
import carpet.helpers.TickRateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Minecraft.class)
public class MinecraftMixin implements MinecraftF {
    @Shadow
    public ClientWorld world;

    @Inject(
            method = "tick",
            at = @At(
                    "HEAD"
            )
    )
    private void onClientTick(CallbackInfo info) {
        if (this.world != null) {
            getTickRateManager().ifPresent(TickRateManager::tick);
        }
    }

    @Override
    public Optional<TickRateManager> getTickRateManager() {
        if (this.world != null) {
            return Optional.of(((WorldF) this.world).tickRateManager());
        }
        return Optional.empty();
    }
}
