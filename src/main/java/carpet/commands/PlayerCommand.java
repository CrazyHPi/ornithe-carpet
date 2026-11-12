package carpet.commands;

import carpet.CarpetSettings;
import carpet.fakes.ServerPlayerEntityF;
import carpet.patches.ServerPlayerEntityFake;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerCommand extends CarpetAbstractCommand {
    @Override
    public String getName() {
        return "player";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "player <spawn|kill|stop|drop|dropStack|swapHands|mount|dismount> <player_name>  OR /player <use|attack|jump> <player_name> <once|continuous|interval.. ticks>";
    }

    @Override
    public boolean canUse(MinecraftServer server, CommandSource source) {
        return canUseCommand(source, CarpetSettings.commandPlayer);
    }

    @Override
    public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new IncorrectUsageException("player <x> action");
        }
        String playerName = args[0];
        String action = args[1];
        ServerPlayerEntity player = server.getPlayerManager().get(playerName);
        if (source instanceof PlayerEntity) {
            ServerPlayerEntity sendingPlayer = (ServerPlayerEntity) source;
            if (!server.getPlayerManager().isOp(sendingPlayer.getGameProfile())) {
                if (!(sendingPlayer == player || player == null || player instanceof ServerPlayerEntityFake)) {
                    throw new IncorrectUsageException("Non OP players can't control other players");
                }
            }
        }
        if (player == null && !action.equalsIgnoreCase("spawn") && !action.equalsIgnoreCase("respawn")) {
            throw new IncorrectUsageException("player doesn't exist");
        }
        if ("use".equalsIgnoreCase(action) || "attack".equalsIgnoreCase(action) || "jump".equalsIgnoreCase(action)) {
            String option = "once";
            int interval = 0;
            if (args.length > 2) {
                option = args[2];
                if (args.length > 3 && option.equalsIgnoreCase("interval")) {
                    interval = parseInt(args[3], 2, 72000);
                }
            }
            if (action.equalsIgnoreCase("use")) {
                if (option.equalsIgnoreCase("once"))
                    ((ServerPlayerEntityF) player).getActionPack().useOnce();
                if (option.equalsIgnoreCase("continuous"))
                    ((ServerPlayerEntityF) player).getActionPack().setUseForever();
                if (option.equalsIgnoreCase("interval") && interval > 1)
                    ((ServerPlayerEntityF) player).getActionPack().setUse(interval, 0);
            }
            if (action.equalsIgnoreCase("attack")) {
                if (option.equalsIgnoreCase("once"))
                    ((ServerPlayerEntityF) player).getActionPack().attackOnce();
                if (option.equalsIgnoreCase("continuous"))
                    ((ServerPlayerEntityF) player).getActionPack().setAttackForever();
                if (option.equalsIgnoreCase("interval") && interval > 1)
                    ((ServerPlayerEntityF) player).getActionPack().setAttack(interval, 0);
            }
            if (action.equalsIgnoreCase("jump")) {
                if (option.equalsIgnoreCase("once"))
                    ((ServerPlayerEntityF) player).getActionPack().jumpOnce();
                if (option.equalsIgnoreCase("continuous"))
                    ((ServerPlayerEntityF) player).getActionPack().setJumpForever();
                if (option.equalsIgnoreCase("interval") && interval > 1)
                    ((ServerPlayerEntityF) player).getActionPack().setJump(interval, 0);
            }
            return;
        }
        if ("stop".equalsIgnoreCase(action)) {
            ((ServerPlayerEntityF) player).getActionPack().stop();
            return;
        }
        if ("drop".equalsIgnoreCase(action)) {
            int slot = -1;
            if (args.length > 2) {
                switch (args[2]) {
                    case "all": slot = -2; break;
                    case "mainhand": slot = -1; break;
                    case "offhand": slot = 40; break;
                    case "slot": {
                        if (args.length > 3) {
                            slot = parseInt(args[3], 0, 40);
                        }
                        break;
                    }
                }
            }
            ((ServerPlayerEntityF) player).getActionPack().dropItem(slot, false);
            return;
        }
        if ("dropStack".equalsIgnoreCase(action)) {
            int slot = -1;
            if (args.length > 2) {
                switch (args[2]) {
                    case "all": slot = -2; break;
                    case "mainhand": slot = -1; break;
                    case "offhand": slot = 40; break;
                    case "slot": {
                        if (args.length > 3) {
                            slot = parseInt(args[3], 0, 40);
                        }
                        break;
                    }
                }
            }
            ((ServerPlayerEntityF) player).getActionPack().dropItem(slot, true);
            return;
        }
        if ("swapHands".equalsIgnoreCase(action)) {
            ((ServerPlayerEntityF) player).getActionPack().swapHands();
            return;
        }
        if ("hotbar".equalsIgnoreCase(action)) {
            if (args.length > 2) {
                int slot = parseInt(args[2], 1, 9);
                ((ServerPlayerEntityF) player).getActionPack().setSlot(slot);
            } else {
                throw new IncorrectUsageException("/player " + playerName + " hotbar <1-9>");
            }
            return;
        }
        if ("spawn".equalsIgnoreCase(action)) {
            if (player != null) {
                throw new IncorrectUsageException("player " + playerName + " already exists");
            }
            if (playerName.length() < 3 || playerName.length() > 16) {
                throw new IncorrectUsageException("player names can only be 3 to 16 chars long");
            }
            if (isWhitelistedPlayer(server, playerName) && !source.canUseCommand(2, "gamemode")) {
                throw new CommandException("You are not allowed to spawn a whitelisted player");
            }
            Vec3d vec3d = source.getSourcePos();
            double x = vec3d.x;
            double y = vec3d.y;
            double z = vec3d.z;
            double yaw = 0.0D;
            double pitch = 0.0D;
            World world = source.getSourceWorld();
            int dimension = world.dimension.getType().getId();
            int gamemode = server.getDefaultGameMode().getId();

            if (source instanceof ServerPlayerEntity) {
                ServerPlayerEntity entity = asPlayer(source);
                yaw = entity.yaw;
                pitch = entity.pitch;
                gamemode = entity.interactionManager.getGameMode().getId();
            }
            if (args.length >= 5) {
                x = parseTeleportCoordinate(x, args[2], true).getCoordinate();
                y = parseTeleportCoordinate(y, args[3], -4096, 4096, false).getCoordinate();
                z = parseTeleportCoordinate(z, args[4], true).getCoordinate();
                yaw = parseTeleportCoordinate(yaw, args.length > 5 ? args[5] : "~", false).getCoordinate();
                pitch = parseTeleportCoordinate(pitch, args.length > 6 ? args[6] : "~", false).getCoordinate();
            }
            if (args.length >= 8) {
                String dimension_string = args[7];
                dimension = 0;
                if ("nether".equalsIgnoreCase(dimension_string)) {
                    dimension = -1;
                }
                if ("end".equalsIgnoreCase(dimension_string)) {
                    dimension = 1;
                }
            }
            if (args.length >= 9) {
                gamemode = parseInt(args[8], 0, 3);
                if (gamemode == 1 && !source.canUseCommand(2, "gamemode")) {
                    throw new CommandException("You are not allowed to spawn a creative player");
                }
            }
            ServerPlayerEntityFake.createFake(playerName, server, x, y, z, yaw, pitch, dimension, gamemode);
            return;
        }
        if ("kill".equalsIgnoreCase(action)) {
            if (!(player instanceof ServerPlayerEntityFake)) {
                throw new IncorrectUsageException("use /kill or /kick on regular players");
            }
            player.discard();
            return;
        }
        if ("shadow".equalsIgnoreCase(action)) {
            if (player instanceof ServerPlayerEntityFake) {
                throw new IncorrectUsageException("cannot shadow server side players");
            }
            ServerPlayerEntityFake.createShadow(server, player);
            return;
        }
        if ("mount".equalsIgnoreCase(action)) {
            ((ServerPlayerEntityF) player).getActionPack().mount();
            return;
        }
        if ("dismount".equalsIgnoreCase(action)) {
            ((ServerPlayerEntityF) player).getActionPack().dismount();
            return;
        }

        //FP only
        if (action.matches("^(?:move|sneak|sprint|unsneak|unsprint|look|turn)$")) {
            if (player != null && !(player instanceof ServerPlayerEntityFake))
                throw new IncorrectUsageException(action + " action could only be run on existing fake players");

            if ("move".equalsIgnoreCase(action)) {
                if (args.length < 3)
                    throw new IncorrectUsageException("/player " + playerName + " go <forward|backward|left|right>");
                String where = args[2];
                if ("forward".equalsIgnoreCase(where)) {
                    ((ServerPlayerEntityF) player).getActionPack().setForward(1.0F);
                    return;
                }
                if ("backward".equalsIgnoreCase(where)) {
                    ((ServerPlayerEntityF) player).getActionPack().setForward(-1.0F);
                    return;
                }
                if ("left".equalsIgnoreCase(where)) {
                    ((ServerPlayerEntityF) player).getActionPack().setStrafing(1.0F);
                    return;
                }
                if ("right".equalsIgnoreCase(where)) {
                    ((ServerPlayerEntityF) player).getActionPack().setStrafing(-1.0F);
                    return;
                }
                throw new IncorrectUsageException("/player " + playerName + " go <forward|backward|left|right>");
            }
            if ("sneak".equalsIgnoreCase(action)) {
                ((ServerPlayerEntityF) player).getActionPack().setSneaking(true);
                return;
            }
            if ("unsneak".equalsIgnoreCase(action)) {
                ((ServerPlayerEntityF) player).getActionPack().setSneaking(false);
                return;
            }
            if ("sprint".equalsIgnoreCase(action)) {
                ((ServerPlayerEntityF) player).getActionPack().setSprinting(true);
                return;
            }
            if ("unsprint".equalsIgnoreCase(action)) {
                ((ServerPlayerEntityF) player).getActionPack().setSprinting(false);
                return;
            }
            if ("look".equalsIgnoreCase(action)) {
                if (args.length < 3)
                    throw new IncorrectUsageException("/player " + playerName + " look <left|right|north|south|east|west|up|down| yaw .. pitch>");
                if (args[2].charAt(0) >= 'A' && args[2].charAt(0) <= 'z') {
                    if (!((ServerPlayerEntityF) player).getActionPack().look(args[2].toLowerCase())) {
                        throw new IncorrectUsageException("look direction is north, south, east, west, up or down");
                    }
                } else if (args.length > 3) {
                    float yawVal = (float) parseTeleportCoordinate(player.yaw, args[2], false).getCoordinate();
                    float pitchVal = (float) parseTeleportCoordinate(player.pitch, args[3], false).getCoordinate();
                    ((ServerPlayerEntityF) player).getActionPack().look(yawVal, pitchVal);
                } else {
                    throw new IncorrectUsageException("/player " + playerName + " look <north|south|east|west|up|down| yaw .. pitch>");
                }
                return;
            }
            if ("turn".equalsIgnoreCase(action)) {
                if (args.length < 3) {
                    throw new IncorrectUsageException("/player " + playerName + " turn <left|right|back| yaw .. pitch>");
                }
                if (args[2].charAt(0) >= 'A' && args[2].charAt(0) <= 'z') {
                    if (!((ServerPlayerEntityF) player).getActionPack().turn(args[2].toLowerCase())) {
                        throw new IncorrectUsageException("turn direction is left, right, or back");
                    }
                } else if (args.length > 3) {
                    float yawVal = (float) parseTeleportCoordinate(player.yaw, args[2], false).getCoordinate();
                    float pitchVal = (float) parseTeleportCoordinate(player.pitch, args[3], false).getCoordinate();
                    ((ServerPlayerEntityF) player).getActionPack().turn(yawVal, pitchVal);
                } else {
                    throw new IncorrectUsageException("/player " + playerName + " turn <left|right|back| yaw .. pitch>");
                }
                return;
            }
        }
        if ("despawn".equalsIgnoreCase(action)) {
            if (!(player instanceof ServerPlayerEntityFake)) {
                throw new IncorrectUsageException("this is a bot only command");
            }
            ((ServerPlayerEntityFake) player).despawn();
            return;
        }
        if ("respawn".equalsIgnoreCase(action)) {
            if (player != null) {
                throw new IncorrectUsageException("player " + playerName + " already exists");
            }
            if (playerName.length() < 3 || playerName.length() > 16) {
                throw new IncorrectUsageException("player names can only be 3 to 16 chars long");
            }
            if (isWhitelistedPlayer(server, playerName) && !source.canUseCommand(2, "gamemode")) {
                throw new CommandException("You are not allowed to spawn a whitelisted player");
            }
            ServerPlayerEntityFake.create(playerName, server);
            return;
        }
        throw new IncorrectUsageException("unknown action: " + action);
    }

    private boolean isWhitelistedPlayer(MinecraftServer server, String playerName) {
        for (String s : server.getPlayerManager().getWhitelistNames()) {
            if (s.equalsIgnoreCase(playerName)) return true;
        }
        return false;
    }

    @Override
    public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, @Nullable BlockPos targetPos) {
        if (!CarpetSettings.commandPlayer.equalsIgnoreCase("true") && !CarpetSettings.commandPlayer.equalsIgnoreCase("ops")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            Set<String> players = new HashSet<>(Arrays.asList(server.getPlayerNames()));
            players.add("Steve");
            players.add("Alex");
            return suggestMatching(args, players.toArray(new String[0]));
        }
        if (args.length == 2) {
            return suggestMatching(args,
                    "spawn", "kill", "attack", "use", "jump", "stop", "shadow",
                    "swapHands", "drop", "dropStack", "hotbar", "mount", "dismount",
                    "move", "sneak", "sprint", "unsneak", "unsprint", "look", "turn",
                    "despawn", "respawn");
        }
        if (args.length == 3 && (args[1].matches("^(?:use|attack|jump)$"))) {
            return suggestMatching(args, "once", "continuous", "interval");
        }
        if (args.length == 3 && args[1].matches("^(?:drop|dropStack)$")) {
            return suggestMatching(args, "all", "mainhand", "offhand", "slot");
        }
        if (args.length == 4 && (args[2].equalsIgnoreCase("interval"))) {
            return suggestMatching(args, "20");
        }
        if (args.length == 3 && (args[1].equalsIgnoreCase("move"))) {
            return suggestMatching(args, "left", "right", "forward", "backward");
        }
        if (args.length == 3 && (args[1].equalsIgnoreCase("look"))) {
            return suggestMatching(args, "north", "south", "east", "west", "up", "down");
        }
        if (args.length == 3 && (args[1].equalsIgnoreCase("turn"))) {
            return suggestMatching(args, "left", "right", "back", "rotation");
        }
        if (args.length > 2 && (args[1].equalsIgnoreCase("spawn"))) {
            if (args.length <= 5) {
                return suggestCoordinate(args, 2, targetPos);
            } else if (args.length <= 7) {
                return suggestMatching(args, "0.0");
            } else if (args.length == 8) {
                return suggestMatching(args, "overworld", "end", "nether");
            } else if (args.length == 9) {
                return suggestMatching(args, "0", "1", "2", "3");
            }
        }
        return Collections.emptyList();
    }
}
