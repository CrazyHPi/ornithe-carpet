package carpet.network;

import carpet.CarpetSettings;
import carpet.utils.PacketHelper;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ClientNetworkHandler {
    private static final Map<String, BiConsumer<LocalClientPlayerEntity, NbtElement>> dataHandlers = new HashMap<>();

    static {
        // init dataHandler
        dataHandlers.put(CarpetClient.HI, (p, t) -> onHi((NbtString) t));
        dataHandlers.put("Rules", (p, t) -> {});
        dataHandlers.put("clientCommand", (p, t) -> CarpetClient.onClientCommand(t));
    }

    private static void onHi(NbtString version) {
        CarpetClient.setCarpet();
        CarpetClient.serverCarpetVersion = version.asString();
        if (CarpetSettings.carpetVersion.equals(CarpetClient.serverCarpetVersion)) {
            CarpetSettings.LOG.info("Joined carpet server with matching carpet version");
        } else {
            CarpetSettings.LOG.warn("Joined carpet server with another carpet version: " + CarpetClient.serverCarpetVersion);
        }
        // We can ensure that this packet is
        // processed AFTER the player has joined
        respondHello();
    }

    public static void respondHello() {
        NbtCompound data = new NbtCompound();
        data.putString(CarpetClient.HELLO, CarpetSettings.carpetVersion);
        // need to wait for carpet.network.CarpetClient.gameJoined called
        System.out.println("wait");
        while (CarpetClient.getPlayer() == null) {
            System.out.println("wait");
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }
        CarpetClient.getPlayer().networkHandler.sendPacket(PacketHelper.c2SPacket(data));
    }

    public static void onServerData(NbtCompound compound, LocalClientPlayerEntity player) {
        for (String key : compound.getKeys()) {
            if (dataHandlers.containsKey(key)) {
                try {
                    dataHandlers.get(key).accept(player, compound.get(key));
                } catch (Exception e) {
                    CarpetSettings.LOG.info("Corrupt carpet data for " + key);
                }
            } else {
                CarpetSettings.LOG.error("Unknown carpet data: " + key);
            }
        }
    }

    public static void clientCommand(String command) {
        NbtCompound tag = new NbtCompound();
        tag.putString("id", command);
        tag.putString("command", command);
        NbtCompound outer = new NbtCompound();
        outer.put("clientCommand", tag);
        CarpetClient.getPlayer().networkHandler.sendPacket(PacketHelper.c2SPacket(outer));
    }
}
