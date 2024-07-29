package carpet.utils;

import carpet.network.CarpetClient;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

public class PacketHelper {
    public static CustomPayloadS2CPacket s2CPacket(NbtCompound data) {
        return s2CPacket(CarpetClient.CHANNEL, data);
    }

    public static CustomPayloadS2CPacket s2CPacket(String channel, NbtCompound data) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeNbtCompound(data);
        return new CustomPayloadS2CPacket(channel, buf);
    }

    public static CustomPayloadC2SPacket c2SPacket(NbtCompound data) {
        return c2SPacket(CarpetClient.CHANNEL, data);
    }

    public static CustomPayloadC2SPacket c2SPacket(String channel, NbtCompound data) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeNbtCompound(data);
        return new CustomPayloadC2SPacket(channel, buf);
    }
}
