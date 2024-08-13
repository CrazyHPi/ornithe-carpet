package carpet.network;

import carpet.CarpetServer;
import carpet.SharedConstants;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import carpet.CarpetSettings;
import carpet.utils.PacketHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiConsumer;

public class ServerNetworkHandler {
    private static final Map<ServerPlayerEntity, String> remoteCarpetPlayers = new HashMap<>();
    private static final Set<ServerPlayerEntity> validCarpetPlayers = new HashSet<>();

//    private static final Map<String, BiConsumer<ServerPlayerEntity, NbtElement>> dataHandlers = new HashMap<String, BiConsumer<ServerPlayerEntity, NbtElement>>() {{
//        put(CarpetClient.HELLO, (p, t) -> onHello(p, t.toString()));
//        put("clientCommand", (p, t) -> handleClientCommand(p, (NbtCompound) t));
//    }};

    private static final Map<String, BiConsumer<ServerPlayerEntity, NbtElement>> dataHandlers = new HashMap<>();

    static {
        dataHandlers.put(CarpetClient.HELLO, (p, t) -> onHello(p, (NbtString) t));
        dataHandlers.put("clientCommand", (p, t) -> handleClientCommand(p, (NbtCompound) t));
    }

    public static void onPlayerJoin(ServerPlayerEntity player) {
        if (!player.networkHandler.connection.isLocal()) {
            NbtCompound data = new NbtCompound();
            data.putString(CarpetClient.HI, SharedConstants.carpetVersion);
            player.networkHandler.sendPacket(PacketHelper.s2CPacket(data));
        } else {
            validCarpetPlayers.add(player);
        }
    }

    public static void onHello(ServerPlayerEntity player, NbtString version) {
        validCarpetPlayers.add(player);
        remoteCarpetPlayers.put(player, version.asString());
        if (SharedConstants.carpetVersion.equals(version.asString())) {
            SharedConstants.LOG.info("Player " + player.getName() + " joined with a matching carpet client");
        } else {
            SharedConstants.LOG.warn("Player " + player.getName() + " joined with another carpet version: " + version);
        }
        DataBuilder data = DataBuilder.create(player.server);
        CarpetServer.forEachManager(sm -> sm.getCarpetRules().forEach(data::withRule));
        player.networkHandler.sendPacket(data.build());
    }

    // backbone for client API commands, not sure what this is for, scarpet?
    private static void handleClientCommand(ServerPlayerEntity player, NbtCompound commandData) {
        String command = commandData.getString("command");
        String id = commandData.getString("id");
        List<Text> output = new ArrayList<>();
        Text[] error = {null};
        if (player.getServer() == null) {
            error[0] = new LiteralText("No Server");
        } else {
            // scarpet
        }
        NbtCompound result = new NbtCompound();
        result.putString("id", id);
        if (error[0] != null) {
            result.putString("error", error[0].getContent());
        }
        NbtList outputResult = new NbtList();
        for (Text line : output) {
            outputResult.add(new NbtString(Text.Serializer.toJson(line)));
        }
        if (!output.isEmpty()) {
            result.put("output", outputResult);
        }
        player.networkHandler.sendPacket(DataBuilder.create(player.server).withCustomNbt("clientCommand", result).build());
    }

    public static void onClientData(ServerPlayerEntity player, NbtCompound compound) {
        for (String key : compound.getKeys()) {
            if (dataHandlers.containsKey(key)) {
                dataHandlers.get(key).accept(player, compound.get(key));
            } else {
                SharedConstants.LOG.warn("Unknown carpet client data: " + key);
            }
        }
    }

    public static void updateRuleWithConnectedClients(CarpetRule<?> rule) {
        if (CarpetSettings.superSecretSetting) {
            return;
        }
        for (ServerPlayerEntity player : remoteCarpetPlayers.keySet()) {
            player.networkHandler.sendPacket(DataBuilder.create(player.server).withRule(rule).build());
        }
    }

    // scarpet
    public static void sendCustomCommand(ServerPlayerEntity player, String command, NbtElement tag) {

    }

    public static void sendPlayerWorldData(ServerPlayerEntity player, World world) {
        // idk why this is here
        // Kahzerx put it here
    }

    public static void onPlayerLoggedOut(ServerPlayerEntity player) {
        validCarpetPlayers.remove(player);
        if (!player.networkHandler.getConnection().isLocal()) {
            remoteCarpetPlayers.remove(player);
        }
    }

    public static void updateTickRate(float tps) {
        for (ServerPlayerEntity player : remoteCarpetPlayers.keySet()) {
            player.networkHandler.sendPacket(DataBuilder.create(player.server).withTickRate(tps).build());
        }
    }

    public static void updateFrozenState(boolean frozen) {
        for (ServerPlayerEntity player : remoteCarpetPlayers.keySet()) {
            player.networkHandler.sendPacket(DataBuilder.create(player.server).withFrozenState(frozen).build());
        }
    }

    public static void close() {
        remoteCarpetPlayers.clear();
        validCarpetPlayers.clear();
    }

    // scarpet
    public static boolean idValidCarpetPlayer(ServerPlayerEntity player) {
        if (CarpetSettings.superSecretSetting) {
            return false;
        }
        return validCarpetPlayers.contains(player);
    }

    // scarpet
    public static String getPlayerStatus(ServerPlayerEntity player) {
        if (remoteCarpetPlayers.containsKey(player)) {
            return "carpet " + remoteCarpetPlayers.get(player);
        }
        if (validCarpetPlayers.contains(player)) {
            return "carpet " + SharedConstants.carpetVersion;
        }
        return "vanilla";
    }

    private static class DataBuilder {
        private NbtCompound tag;
        // unused now, but hey
        private MinecraftServer server;

        private static DataBuilder create(final MinecraftServer server) {
            return new DataBuilder(server);
        }

        private DataBuilder(MinecraftServer server) {
            tag = new NbtCompound();
            this.server = server;
        }

        private DataBuilder withRule(CarpetRule<?> rule) {
            NbtCompound rules = (NbtCompound) tag.get("Rules");
            if (rules == null) {
                rules = new NbtCompound();
                tag.put("Rules", rules);
            }
            String identifier = rule.settingsManager().identifier();
            String key = rule.name();
            while (rules.contains(key)) {
                key = key + "2";
            }
            NbtCompound ruleNBT = new NbtCompound();
            ruleNBT.putString("Value", RuleHelper.toRuleString(rule.value()));
            ruleNBT.putString("Manager", identifier);
            ruleNBT.putString("Rule", rule.name());
            rules.put(key, ruleNBT);

            return this;
        }

        public DataBuilder withTickRate(float tps) {
            tag.putFloat("TickRate", tps);
            return this;
        }

        public DataBuilder withFrozenState(boolean frozen) {
            NbtCompound freezeCompound = new NbtCompound();
            freezeCompound.putBoolean("is_frozen", frozen);
            freezeCompound.putBoolean("deepFreeze", false);
            tag.put("TickingState", freezeCompound);
            return this;
        }

        public DataBuilder withCustomNbt(String key, NbtElement value) {
            tag.put(key, value);
            return this;
        }

        private CustomPayloadS2CPacket build() {
            return PacketHelper.s2CPacket(CarpetClient.CHANNEL, tag);
        }
    }
}
