package carpet.mixins.carpetmod.network;

import carpet.network.CarpetClient;
import carpet.network.ClientNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.LoginS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomCarpetPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (CarpetClient.CHANNEL.equals(packet.getChannel())) {
            NbtCompound data = packet.getData().readNbtCompound();
            if (data != null) {
                ClientNetworkHandler.onServerData(data, this.minecraft.player);
            }
            ci.cancel();
        }
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onGameJoined(LoginS2CPacket packet, CallbackInfo ci) {
        CarpetClient.gameJoined(minecraft.player);
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onCMDisconnected(Text reason, CallbackInfo ci) {
        CarpetClient.disconnect();
    }

}
