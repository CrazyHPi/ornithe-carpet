package carpet.mixins.carpetmod.network;

import carpet.CarpetServer;
import net.minecraft.network.Connection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "onLogin", at = @At("RETURN"))
    private void onPlayerConnected(Connection connection, ServerPlayerEntity player, CallbackInfo ci) {
        CarpetServer.onPlayerLoggedIn(player);
    }

}
