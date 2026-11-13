package carpet.mixins.rule.persistentParrots;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    public PlayerEntityMixin(World world) {
        super(world);
    }

    @WrapWithCondition(
            method = "mobTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/living/player/PlayerEntity;dropShoulderEntities()V"
            )
    )
    private boolean onTickMovement(PlayerEntity instance) {
        return !CarpetSettings.persistentParrots;
    }

    @WrapWithCondition(
            method = "takeDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/living/player/PlayerEntity;dropShoulderEntities()V"
            )
    )
    private boolean onDamage(PlayerEntity instance, @Local(argsOnly = true) float amount) {
        return !CarpetSettings.persistentParrots && random.nextFloat() < (amount / 15.0);
    }
}
