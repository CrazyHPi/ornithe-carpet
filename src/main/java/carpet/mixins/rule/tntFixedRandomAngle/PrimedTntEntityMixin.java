package carpet.mixins.rule.tntFixedRandomAngle;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.PrimedTntEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PrimedTntEntity.class, priority = 300)
public abstract class PrimedTntEntityMixin {
	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/living/LivingEntity;)V",
		at = @At(value = "INVOKE", target = "Ljava/lang/Math;random()D"))
	public double tryFixRandomAngle(double original) {
		double d = CarpetSettings.tntFixedRandomAngle;
		return d < 0 ? original : (d / (2 * Math.PI));
	}
}
