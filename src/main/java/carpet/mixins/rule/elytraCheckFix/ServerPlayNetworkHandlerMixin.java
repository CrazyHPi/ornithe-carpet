package carpet.mixins.rule.elytraCheckFix;

import carpet.CarpetSettings;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Redirect(
            method = "handlePlayerMovementAction",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;velocityY:D"
            )
    )
    private double elytraCheckFix(ServerPlayerEntity instance) {
        return CarpetSettings.elytraCheckFix ? -1 : instance.velocityY;
    }
}
