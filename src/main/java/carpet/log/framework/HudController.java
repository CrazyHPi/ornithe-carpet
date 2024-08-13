package carpet.log.framework;

import carpet.helpers.HopperCounter;
import carpet.log.loggers.packets.PacketCounter;
import carpet.mixins.accessor.TabListS2CPacketA;
import carpet.CarpetSettings;
import carpet.utils.Messenger;
import carpet.utils.SpawnReporter;
import net.minecraft.entity.living.mob.MobCategory;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.TabListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.*;
import java.util.function.Consumer;

public class HudController {
    public static final Map<ServerPlayerEntity, List<Text>> playerHuds = new HashMap<>();

    private static final List<Consumer<MinecraftServer>> hudListeners = new ArrayList<>();

    /**
     * for extension
     * Adds listener to be called when HUD is updated for logging information
     *
     * @param listener - a method to be called when new HUD inforation are collected
     */
    public static void register(Consumer<MinecraftServer> listener) {
        hudListeners.add(listener);
    }

    public static void addMessage(ServerPlayerEntity player, Text hudMessage) {
        if (player == null) {
            return;
        }
        if (!playerHuds.containsKey(player)) {
            playerHuds.put(player, new ArrayList<>());
        } else {
            playerHuds.get(player).add(new LiteralText("\n"));
        }
        playerHuds.get(player).add(hudMessage);
    }

    public static void clearPlayer(ServerPlayerEntity player) {
        TabListS2CPacket packet = new TabListS2CPacket();
        ((TabListS2CPacketA) packet).setHeader(new LiteralText(""));
        ((TabListS2CPacketA) packet).setFooter(new LiteralText(""));
        player.networkHandler.sendPacket(packet);
    }

    public static void updateHud(MinecraftServer server) {
        if (server.getTicks() % CarpetSettings.hudUpdateInterval != 0) {
            return;
        }
        playerHuds.clear();

        if (LoggerRegistry.__autosave) {
            LoggerRegistry.getLogger("autosave").log(() -> send_autosave(server));
        }

        if (LoggerRegistry.__tps) {
            LoggerRegistry.getLogger("tps").log(() -> send_tps_display(server));
        }

        if (LoggerRegistry.__mobcaps) {
            LoggerRegistry.getLogger("mobcaps").log(HudController::send_mobcap_display);
        }

        if (LoggerRegistry.__counter) {
            LoggerRegistry.getLogger("counter").log((option) -> send_counter(option, server));
        }

        if (LoggerRegistry.__packets) {
            LoggerRegistry.getLogger("packets").log(HudController::packetCounter);
        }

        hudListeners.forEach(l -> l.accept(server));

        for (PlayerEntity player : playerHuds.keySet()) {
            TabListS2CPacket packet = new TabListS2CPacket();
            ((TabListS2CPacketA) packet).setHeader(new LiteralText(""));
            ((TabListS2CPacketA) packet).setFooter(Messenger.c(playerHuds.get(player).toArray(new Object[0])));
            ((ServerPlayerEntity) player).networkHandler.sendPacket(packet);
        }
    }

    private static Text[] send_autosave(MinecraftServer server) {
        int gametick = server.getTicks();
        int previous = gametick % 900;
        if (gametick != 0 && previous == 0) {
            previous = 900;
        }
        int next = 900 - previous;
        String color = Messenger.heatmap_color(previous, 860);

        return new Text[]{
                Messenger.c(
                        "g Prev: ", String.format(Locale.US, "%s %d", color, previous),
                        "g  Next: ", String.format(Locale.US, "%s %d", color, next))
        };
    }

    private static Text[] send_tps_display(MinecraftServer server) {
        double MSPT = MathHelper.average(server.averageTickTimes) * 1.0E-6D;
        // todo tick rate
//        double TPS = 1000.0D / Math.max((TickSpeed.time_warp_start_time != 0)?0.0:TickSpeed.mspt, MSPT);
        double TPS = 50;
        String color = Messenger.heatmap_color(MSPT, 50);

        return new Text[]{Messenger.c(
                "g TPS: ", String.format(Locale.US, "%s %.1f", color, TPS),
                "g  MSPT: ", String.format(Locale.US, "%s %.1f", color, MSPT))};
    }

    private static Text[] send_mobcap_display(String option, PlayerEntity player) {
        int dim;
        switch (option) {
            case "overworld":
                dim = 0;
                break;
            case "nether":
                dim = -1;
                break;
            case "end":
                dim = 1;
                break;
            case "dynamic":
            default:
                dim = player.dimensionId;
                break;
        }
        List<Text> text = new ArrayList<>();
        for (MobCategory type : MobCategory.values()) {
            Pair<Integer, Integer> counts = SpawnReporter.mobcaps.get(dim).getOrDefault(type, new Pair<>(0, 0));
            int actual = counts.getLeft();
            int limit = counts.getRight();
            text.add(Messenger.c((actual + limit == 0) ? "g -" : Messenger.heatmap_color(actual, limit) + " " + actual,
                    Messenger.creatureTypeColor(type) + " /" + ((actual + limit == 0) ? "-" : limit)
            ));
            text.add(Messenger.c("w  "));
        }
        text.remove(text.size() - 1);
        return new Text[]{Messenger.c(text.toArray(new Object[0]))};
    }

    private static Text[] send_counter(String option, MinecraftServer server) {
        if (option.equals("all")) {
            return HopperCounter.formatAll(server, false, true).toArray(new Text[0]);
        } else {
            HopperCounter counter = HopperCounter.getCounter(option);
            if (counter != null) {
                return counter.format(server, false, true).toArray(new Text[0]);
            }
        }
        return null;
    }

    private static Text[] packetCounter() {
        Text[] ret = new Text[]{
                Messenger.c("w I/" + PacketCounter.totalIn + " O/" + PacketCounter.totalOut)
        };
        PacketCounter.reset();
        return ret;
    }
}
