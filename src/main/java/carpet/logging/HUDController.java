package carpet.logging;

import carpet.CarpetSettings;
import carpet.mixins.carpetmod.logging.TabListS2CPacketAccessor;
import carpet.utils.Messenger;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.TabListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.*;
import java.util.function.Consumer;

public class HUDController {
    public static final Map<ServerPlayerEntity, List<Text>> playerHUDs = new HashMap<>();

    private static final List<Consumer<MinecraftServer>> HUDListeners = new ArrayList<>();

    /**
     * for extension
     * Adds listener to be called when HUD is updated for logging information
     *
     * @param listener - a method to be called when new HUD inforation are collected
     */
    public static void register(Consumer<MinecraftServer> listener) {
        HUDListeners.add(listener);
    }

    public static void addMessage(ServerPlayerEntity player, Text hudMessage) {
        if (player == null) {
            return;
        }
        if (!playerHUDs.containsKey(player)) {
            playerHUDs.put(player, new ArrayList<>());
        } else {
            playerHUDs.get(player).add(new LiteralText("\n"));
        }
        playerHUDs.get(player).add(hudMessage);
    }

    public static void clearPlayer(ServerPlayerEntity player) {
        TabListS2CPacket packet = new TabListS2CPacket();
        ((TabListS2CPacketAccessor) packet).setHeader(new LiteralText(""));
        ((TabListS2CPacketAccessor) packet).setFooter(new LiteralText(""));
        player.networkHandler.sendPacket(packet);
    }

    public static void updateHUD(MinecraftServer server) {
        if (server.getTicks() % CarpetSettings.HUDUpdateInterval != 0) {
            return;
        }
        playerHUDs.clear();

        if (LoggerRegistry.__tps) {
            LoggerRegistry.getLogger("tps").log(() -> send_tps_display(server));
        }
        if (LoggerRegistry.__packets) {

        }

        HUDListeners.forEach(l -> l.accept(server));

        for (PlayerEntity player : playerHUDs.keySet()) {
            TabListS2CPacket packet = new TabListS2CPacket();
            ((TabListS2CPacketAccessor) packet).setHeader(new LiteralText(""));
            ((TabListS2CPacketAccessor) packet).setFooter(Messenger.c(playerHUDs.get(player).toArray(new Object[0])));
            ((ServerPlayerEntity) player).networkHandler.sendPacket(packet);
        }
    }

    private static Text[] send_tps_display(MinecraftServer server) {
        double MSPT = MathHelper.average(server.averageTickTimes) * 1.0E-6D;
        // todo tick rate
//        double TPS = 1000.0D / Math.max((TickSpeed.time_warp_start_time != 0)?0.0:TickSpeed.mspt, MSPT);
        double TPS = 50;
        String color = Messenger.heatmap_color(MSPT, 50);

        return new Text[] {Messenger.c(
                "g TPS: ", String.format(Locale.US, "%s %.1f",color, TPS),
                "g  MSPT: ", String.format(Locale.US,"%s %.1f", color, MSPT))};
    }
}
