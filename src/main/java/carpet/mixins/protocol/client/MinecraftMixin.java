package carpet.mixins.protocol.client;

import carpet.network.CarpetClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "setWorld(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At(value = "HEAD"))
    private void disconnectCarpetClient(ClientWorld clientWorld, String string, CallbackInfo ci) {
        if (clientWorld == null) {
            CarpetClient.disconnect();
        }
    }
}
