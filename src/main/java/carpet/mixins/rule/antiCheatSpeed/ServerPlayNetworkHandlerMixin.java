package carpet.mixins.rule.antiCheatSpeed;

import carpet.CarpetSettings;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Redirect(
            method = "handlePlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;isInTeleportationState()Z"
            )
    )
    private boolean antiCheatSpeed(ServerPlayerEntity instance) {
        return instance.isInTeleportationState() || CarpetSettings.antiCheatSpeed;
    }
}
