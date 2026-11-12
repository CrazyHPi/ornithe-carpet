package carpet.patches;

import net.minecraft.network.Connection;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;

public class ServerPlayNetworkHandlerFake extends ServerPlayNetworkHandler {
    public ServerPlayNetworkHandlerFake(MinecraftServer server, Connection connection, ServerPlayerEntity player) {
        super(server, connection, player);
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        //noop
    }
}
