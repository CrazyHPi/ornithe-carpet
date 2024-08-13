package carpet.mixins.rule.portalDelay;

import carpet.CarpetSettings;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
	@Shadow
	public PlayerAbilities abilities;

	@Inject(method = "getMaxNetherPortalTime", at = @At(value = "HEAD"), cancellable = true)
	private void onPortalDelay(CallbackInfoReturnable<Integer> cir) {
		if (CarpetSettings.portalCreativeDelay != -1 && this.abilities.invulnerable) {
			cir.setReturnValue(CarpetSettings.portalCreativeDelay);
		} else if (CarpetSettings.portalSurvivalDelay != 80 && !this.abilities.invulnerable) {
			cir.setReturnValue(CarpetSettings.portalSurvivalDelay);
		}
	}
}
