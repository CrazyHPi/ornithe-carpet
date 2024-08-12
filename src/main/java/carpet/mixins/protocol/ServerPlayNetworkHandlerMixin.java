package carpet.mixins.protocol;

import carpet.CarpetServer;
import carpet.network.CarpetClient;
import carpet.network.ServerNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketUtils;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomCarpetPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        if (CarpetClient.CHANNEL.equals(packet.getChannel())) {
            // We should force onto the main thread here
            // ServerNetworkHandler.handleData can possibly mutate data that isn't
            // thread safe, and also allows for client commands to be executed
            PacketUtils.ensureOnSameThread(packet, (ServerPlayPacketHandler) this, this.player.getServerWorld());
            NbtCompound data = packet.getData().readNbtCompound();
            if (data != null) {
                ServerNetworkHandler.onClientData(this.player, data);
            }
            ci.cancel();
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onPlayerDisconnect(Text reason, CallbackInfo ci) {
        CarpetServer.onPlayerLoggedOut(this.player);
    }
}
