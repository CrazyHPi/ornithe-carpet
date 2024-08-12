package carpet.network;

import carpet.CarpetServer;
import carpet.SharedConstants;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.Identifier;

public class CarpetClient {
    public static final String HI = "69";
    public static final String HELLO = "420";

    // scarpet
//    public static ShapesRenderer shapes = null;

    private static LocalClientPlayerEntity clientPlayer = null;
    private static boolean isServerCarpet = false;
    public static String serverCarpetVersion;
    public static final Identifier CARPET_CHANNEL = new Identifier("carpet:hello");
    public static final String CHANNEL = CARPET_CHANNEL.toString();

    public static void gameJoined(LocalClientPlayerEntity player) {
        clientPlayer = player;
    }

    public static void disconnect() {
        if (isServerCarpet) {
            isServerCarpet = false;
            clientPlayer = null;
            CarpetServer.onServerClosed(null);
            CarpetServer.onServerDoneClosing(null);
        } else {
            CarpetServer.clientPreClosing();
        }
    }

    public static void setCarpet() {
        isServerCarpet = true;
    }

    public static LocalClientPlayerEntity getPlayer() {
        return clientPlayer;
    }

    public static boolean isCarpet() {
        return isServerCarpet;
    }

    // backbone for client API commands, not sure what this is for
    public static boolean sendClientCommand(String command) {
        if (!isServerCarpet && CarpetServer.minecraftServer == null) {
            return false;
        }
        ClientNetworkHandler.clientCommand(command);
        return true;
    }

    public static void onClientCommand(NbtElement t) {
        SharedConstants.LOG.info("Server Response:");
        NbtCompound tag = (NbtCompound) t;
        SharedConstants.LOG.info(" - id: " + tag.getString("id"));
        if (tag.contains("error")) {
            SharedConstants.LOG.warn(" - error: " + tag.getString("error"));
        }
        if (tag.contains("output")) {
            NbtList outputTag = (NbtList) tag.get("output");
            for (int i = 0; i < outputTag.size(); i++) {
                SharedConstants.LOG.info(" - response: " + outputTag.getString(i));
            }
        }
    }
}
