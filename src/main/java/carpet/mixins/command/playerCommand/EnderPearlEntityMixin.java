package carpet.mixins.command.playerCommand;

import carpet.patches.ServerPlayerEntityFake;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin {
    @ModifyExpressionValue(
            method = "onCollision",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/network/Connection.isConnected()Z"
            )
    )
    private boolean handleFakePlayerPearlHit(boolean original, @Local ServerPlayerEntity serverPlayerEntity) {
        return original || serverPlayerEntity instanceof ServerPlayerEntityFake;
    }
}
