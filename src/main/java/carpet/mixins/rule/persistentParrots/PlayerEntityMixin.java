package carpet.mixins.rule.persistentParrots;

import carpet.CarpetSettings;
import carpet.SharedConstants;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Shadow
	protected abstract void dropShoulderEntities();

	public PlayerEntityMixin() {
		super(SharedConstants.absurd());
	}


	@WrapWithCondition(method = "tickAi", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;dropShoulderEntities()V"))
	private boolean onTickMovement(PlayerEntity instance) {
        return !CarpetSettings.persistentParrots;
    }

	@WrapWithCondition(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;dropShoulderEntities()V"))
	private boolean onDamage(PlayerEntity instance, DamageSource source, float amount) {
        return !CarpetSettings.persistentParrots && this.random.nextFloat() < (amount / 15.0);
    }
}
