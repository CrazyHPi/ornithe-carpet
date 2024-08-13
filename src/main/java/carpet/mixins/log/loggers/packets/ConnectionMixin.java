package carpet.mixins.log.loggers.packets;

import carpet.log.loggers.packets.PacketCounter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Inject(method = "doSend", at = @At("HEAD"))
    private void countPacketOut(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>>[] listeners, CallbackInfo ci) {
        PacketCounter.totalOut++;
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void countPacketIn(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        PacketCounter.totalIn++;
    }
}
