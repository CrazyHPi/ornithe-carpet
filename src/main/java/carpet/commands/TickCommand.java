package carpet.commands;

import carpet.CarpetSettings;
import carpet.fakes.MinecraftServerF;
import carpet.helpers.ServerTickRateManager;
import carpet.network.ServerNetworkHandler;
import carpet.utils.Messenger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TickCommand extends CarpetAbstractCommand {
    @Override
    public String getName() {
        return "tick";
    }

    @Override
    public String getUsage(CommandSource commandSource) {
        return this.getName() + " <option>";
    }

    @Override
    public boolean canUse(MinecraftServer server, CommandSource source) {
        return canUseCommand(source, CarpetSettings.commandTick);
    }

    @Override
    public void run(MinecraftServer minecraftServer, CommandSource commandSource, String[] strings) throws CommandException {
        if (strings.length == 1) {
            String action = strings[0].toLowerCase();
            switch (action) {
                case "freeze":
                    toggleFreeze(commandSource, false);
                    break;
                case "step":
                    step(commandSource, 1);
                    break;
                case "rate":
                    queryTps(commandSource);
                    break;
                case "superhot":
                    toggleSuperHot(commandSource);
                    break;
                case "warp":
                    setWarp(commandSource, 0, null);
                    break;
                default:
                    break;
            }
        }

        if (strings.length == 2 && "freeze".equalsIgnoreCase(strings[0])) {
            if ("status".equalsIgnoreCase(strings[1])) {
                freezeStatus(commandSource);
            } else if ("deep".equalsIgnoreCase(strings[1])) {
                toggleFreeze(commandSource, true);
            } else if ("on".equalsIgnoreCase(strings[1])) {
                setFreeze(commandSource, false, true);
            } else if ("off".equalsIgnoreCase(strings[1])) {
                setFreeze(commandSource, false, false);
            }
        }

        if (strings.length == 2 && "step".equalsIgnoreCase(strings[0])) {
            step(commandSource, MathHelper.clamp(Integer.parseInt(strings[1]), 1, 72000));
        }

        if (strings.length == 2 && "rate".equalsIgnoreCase(strings[0])) {
            setTps(commandSource, MathHelper.clamp(Float.parseFloat(strings[1]), 0.1F, 500.0F));
        }

        if (strings.length == 2 && "warp".equalsIgnoreCase(strings[0])) {
            setWarp(commandSource, Math.max(Integer.parseInt(strings[1]), 1), null);
        }

        if (strings.length == 3 && "freeze".equalsIgnoreCase(strings[0]) && "on".equalsIgnoreCase(strings[1]) && "deep".equalsIgnoreCase(strings[2])) {
            setFreeze(commandSource, true, true);
        }

        if (strings.length == 3 && "warp".equalsIgnoreCase(strings[0])) {
            setWarp(commandSource, Math.max(Integer.parseInt(strings[1]), 1), strings[2]);
        }
    }

    @Override
    public List<String> getSuggestions(MinecraftServer minecraftServer, CommandSource commandSource, String[] strings, @Nullable BlockPos blockPos) {
        if (strings.length == 1) {
            return suggestMatching(strings, Arrays.asList("freeze", "step", "rate", "superhot", "warp"));
        } else if (strings.length == 2) {
            if ("freeze".equalsIgnoreCase(strings[0]))
                return suggestMatching(strings, Arrays.asList("status", "deep", "on", "off"));
            if ("step".equalsIgnoreCase(strings[0]))
                return suggestMatching(strings, Collections.singletonList("20"));
            if ("rate".equalsIgnoreCase(strings[0]))
                return suggestMatching(strings, Collections.singletonList("20"));
            if ("warp".equalsIgnoreCase(strings[0]))
                return suggestMatching(strings, Arrays.asList("3600", "72000"));
        } else {
            return strings.length == 3 && "freeze".equalsIgnoreCase(strings[0]) && "on".equalsIgnoreCase(strings[1]) ? Collections.singletonList("deep") : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    private static int freezeStatus(CommandSource source) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        if (trm.gameIsPaused()) {
            Messenger.m(source, "gi Freeze Status: Game is " + (trm.deeplyFrozen() ? "deeply " : "") + "frozen");
        } else {
            Messenger.m(source, "gi Freeze Status: Game runs normally");
        }
        return 1;
    }

    private static int setFreeze(CommandSource source, boolean isDeep, boolean freeze) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        trm.setFrozenState(freeze, isDeep);
        if (trm.gameIsPaused()) {
            Messenger.m(source, "gi Game is " + (isDeep ? "deeply " : "") + "frozen");
        } else {
            Messenger.m(source, "gi Game runs normally");
        }
        return 1;
    }

    private static int toggleFreeze(CommandSource source, boolean isDeep) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        return setFreeze(source, isDeep, !trm.gameIsPaused());
    }

    private static int step(CommandSource source, int advance) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        trm.stepGameIfPaused(advance);
        Messenger.m(source, "gi Stepping " + advance + " tick" + (advance != 1 ? "s" : ""));
        return 1;
    }

    private static int setTps(CommandSource source, float tps) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        trm.setTickRate(tps, true);
        queryTps(source);
        return (int) tps;
    }

    private static int queryTps(CommandSource source) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();

        Messenger.m(source, "w Current tps is: ", String.format("wb %.1f", trm.tickrate()));
        return (int) trm.tickrate();
    }

    private static int toggleSuperHot(CommandSource source) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        trm.setSuperHot(!trm.isSuperHot());
        ServerNetworkHandler.updateSuperHotStateToConnectedPlayers(source.getServer());
        if (trm.isSuperHot()) {
            Messenger.m(source, "gi Superhot enabled");
        } else {
            Messenger.m(source, "gi Superhot disabled");
        }
        return 1;
    }

    private static int setWarp(CommandSource source, int advance, String tail_command) {
        ServerPlayerEntity player;
        if (source.asEntity() instanceof ServerPlayerEntity)
            player = ((ServerPlayerEntity) source.asEntity());
        else
            player = null; // may be null
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        Text message = trm.requestGameToWarpSpeed(player, advance, tail_command, source);
        source.sendMessage(message);
        return 1;
    }
}
