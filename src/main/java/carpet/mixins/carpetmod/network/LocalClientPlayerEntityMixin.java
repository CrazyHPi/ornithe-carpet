package carpet.mixins.carpetmod.network;

import carpet.CarpetServer;
import carpet.network.CarpetClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalClientPlayerEntity.class)
public abstract class LocalClientPlayerEntityMixin {
    @Shadow
    protected Minecraft minecraft;

    @Inject(method = "sendChat", at = @At("HEAD"))
    private void inspectMessage(String message, CallbackInfo ci) {
        if (message.startsWith("/call ")) {
            String command = message.substring(6);
            CarpetClient.sendClientCommand(command);
        }
        if (CarpetServer.minecraftServer == null && !CarpetClient.isCarpet() && minecraft.player != null) {
            LocalClientPlayerEntity playerSource = minecraft.player;
            CarpetServer.forEachManager(sm -> sm.inspectClientsideCommand(playerSource, message));
        }
    }
}
