package carpet.mixins.tick.rate.client;

import carpet.CarpetSettings;
import carpet.fakes.MinecraftF;
import carpet.helpers.TickRateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.TickTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(TickTimer.class)
public abstract class TickTimerMixin {
	@Shadow
	private float mspt;

	@Inject(
		method = "advance",
		at = @At(
			"HEAD"
		)
	)
	public void advance(CallbackInfo ci) {
		if (CarpetSettings.smoothClientAnimations) {
			Optional<TickRateManager> trm = ((MinecraftF) Minecraft.getInstance()).getTickRateManager();
			if (trm.isPresent() && trm.get().runsNormally()) {
				this.mspt = Math.max(50.0f, trm.get().mspt());
			}
		} else
			this.mspt = 50.0f;
	}
}
