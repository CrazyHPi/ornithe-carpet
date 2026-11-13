package carpet.helpers;

import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class TeleportHelper {

    /**
     * Moves a player to another player's position, even if they are in different dimensions.
     * Adapted from the spectator teleport code (ServerPlayNetworkHandler#handlePlayerSpectate).
     */
    public static void changeDimensions(ServerPlayerEntity player, ServerPlayerEntity target) {
        double x = target.x;
        double y = target.y;
        double z = target.z;
        MinecraftServer server = player.getServer();
        assert server != null;

        ServerWorld worldFrom = player.getServerWorld();
        ServerWorld worldTo = target.getServerWorld();
        int dimension = target.dimension;
        player.dimension = dimension;

        player.networkHandler.sendPacket(new PlayerRespawnS2CPacket(dimension, worldFrom.getDifficulty(), worldFrom.getData().getGeneratorType(), player.interactionManager.getGameMode()));
        server.getPlayerManager().updatePermissions(player);
        worldFrom.removeEntityNow(player);
        player.removed = false;
        player.setPositionAndAngles(x, y, z, target.yaw, target.pitch);

        worldFrom.tickEntity(player, false);
        worldTo.addEntity(player);
        worldTo.tickEntity(player, false);

        player.setWorld(worldTo);
        server.getPlayerManager().onChangedDimension(player, worldFrom);

        player.teleport(x, y, z);
        player.interactionManager.setWorld(worldTo);
        server.getPlayerManager().sendWorldInfo(player, worldTo);
        server.getPlayerManager().sendPlayerInfo(player);
    }
}
