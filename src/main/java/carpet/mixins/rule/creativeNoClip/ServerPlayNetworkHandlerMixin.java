package carpet.mixins.rule.creativeNoClip;

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
                    value = "FIELD",
                    target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;noClip:Z"
            )
    )
    private boolean creativeNoClip(ServerPlayerEntity instance) {
        return instance.noClip || (CarpetSettings.creativeNoClip && instance.isCreative());
    }
}
