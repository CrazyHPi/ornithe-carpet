package carpet.mixins.rule.tntFixedRandomRange;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
	@Final @Shadow
	private Entity source;

    @Definition(id = "world", field = "Lnet/minecraft/world/explosion/Explosion;world:Lnet/minecraft/world/World;")
    @Definition(id = "random", field = "Lnet/minecraft/world/World;random:Ljava/util/Random;")
    @Definition(id = "nextFloat", method = "Ljava/util/Random;nextFloat()F")
    @Expression("0.7 + @(this.world.random.nextFloat()) * 0.6")
    @ModifyExpressionValue(method = "damageEntities", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    public float fixRandomRange(float original) {
		float v;
		if (source instanceof PrimedTntEntity
			&& (v = CarpetSettings.tntFixedRandomRange) >= 0) return v;
		return original;
	}
}
