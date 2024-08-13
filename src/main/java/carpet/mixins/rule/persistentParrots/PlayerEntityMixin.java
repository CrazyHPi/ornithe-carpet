package carpet.mixins.rule.persistentParrots;

import carpet.CarpetSettings;
import carpet.SharedConstants;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    public PlayerEntityMixin(World world) {
        super(world);
    }

    @WrapWithCondition(method = "tickAi", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;dropShoulderEntities()V"))
    private boolean onTickMovement(PlayerEntity instance) {
        return !CarpetSettings.persistentParrots;
    }

    @WrapWithCondition(method = "damage", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/living/player/PlayerEntity;dropShoulderEntities()V"))
    private boolean onDamage(PlayerEntity instance, @Local(argsOnly = true) float amount) {
        return !CarpetSettings.persistentParrots && random.nextFloat() < (amount / 15.0);
    }
}
