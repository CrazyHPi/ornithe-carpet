package carpet.commands;

import carpet.api.log.Logger;
import carpet.log.framework.LoggerOptions;
import carpet.log.framework.LoggerRegistry;
import carpet.CarpetSettings;
import carpet.utils.Messenger;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LogCommand extends CarpetAbstractCommand {
    private final String USAGE = "/log (interactive menu) OR /log <logName> [?option] [player] [handler ...] OR /log <logName> clear [player] OR /log defaults (interactive menu) OR /log setDefault <logName> [?option] [handler ...] OR /log removeDefault <logName>";

    @Override
    public String getName() {
        return "log";
    }

    @Override
    public String getUsage(CommandSource source) {
        return USAGE;
    }

    @Override
    public boolean canUse(MinecraftServer server, CommandSource source) {
        return canUseCommand(source, CarpetSettings.commandLog);
    }

    @Override
    public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
        if (args.length == 0) {
            listLogs(source);
            return;
        }

        if ("clear".equalsIgnoreCase(args[0])) {
            if (args.length == 1) {
                unsubFromAll(source, source.getName());
                return;
            }
            if (args.length == 2) {
                unsubFromAll(source, args[1]);
                return;
            }
        }
        if ("defaults".equalsIgnoreCase(args[0])) {
            listDefaults(source);
            return;
        }
        if ("setDefault".equalsIgnoreCase(args[0])) {
            if (args.length < 2) {
                throw new IncorrectUsageException("No logger specified.");
            }
            if (args.length == 2) {
                setDefault(source, args[1], null);
                return;
            }
            if (args.length == 3) {
                setDefault(source, args[1], args[2]);
                return;
            }
            throw new IncorrectUsageException("Too many arguments");
        }
        if ("removeDefault".equalsIgnoreCase(args[0])) {
            if (args.length != 2) {
                throw new IncorrectUsageException("No logger specified.");
            }
            removeDefault(source, args[1]);
            return;
        }
        // set logger
        if (args.length == 1) {
            toggleSubscription(source, source.getName(), args[0]);
            return;
        }
        if (args.length == 2 && "clear".equalsIgnoreCase(args[1])) {
            unsubFromLogger(source, source.getName(), args[0]);
            return;
        }
        if (args.length == 2) {
            subscribePlayer(source, source.getName(), args[0], args[1]);
            return;
        }
        if (args.length == 3) {
            subscribePlayer(source, args[2], args[0], args[1]);
            return;
        }
        throw new IncorrectUsageException(USAGE);
    }

    @Override
    public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            Set<String> options = new HashSet<>((LoggerRegistry.getLoggerNames()));
            options.add("clear");
            options.add("defaults");
            options.add("setDefault");
            options.add("removeDefault");
            return suggestMatching(args, options);
        }
        if ("clear".equalsIgnoreCase(args[0]) && args.length == 2) {
            List<String> players = Arrays.asList(server.getPlayerNames());
            return suggestMatching(args, players.toArray(new String[0]));
        }
        if ("setDefault".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                Set<String> options = new HashSet<>((LoggerRegistry.getLoggerNames()));
                return suggestMatching(args, options);
            }
            if (args.length == 3) {
                return suggestMatching(args, getLoggerOptions(args[1]));
            }
        }
        if ("removeDefault".equalsIgnoreCase(args[0]) && args.length == 2) {
            Set<String> options = new HashSet<>((LoggerRegistry.getLoggerNames()));
            return suggestMatching(args, options);
        }
        if (args.length == 2) {
            return suggestMatching(args, getLoggerOptions(args[0]));
        }

        return Collections.emptyList();
    }

    private static void listLogs(CommandSource source) {
        PlayerEntity player;
        try {
            player = (PlayerEntity) source;
        } catch (Exception e) {
            Messenger.m(source, "For players only");
            return;
        }

        Map<String, String> subs = LoggerRegistry.getPlayerSubscriptions(source.getName());
        if (subs == null) {
            subs = new HashMap<>();
        }
        List<String> all_logs = new ArrayList<>(LoggerRegistry.getLoggerNames());
        Collections.sort(all_logs);
        Messenger.m(player, "w _____________________");
        Messenger.m(player, "w Available logging options:");
        for (String lname : all_logs) {
            List<Object> comp = new ArrayList<>();
            String color = subs.containsKey(lname) ? "w" : "g";
            comp.add("w  - " + lname + ": ");
            Logger logger = LoggerRegistry.getLogger(lname);
            String[] options = logger.getOptions();
            if (options.length == 0) {
                if (subs.containsKey(lname)) {
                    comp.add("l Subscribed ");
                } else {
                    comp.add(color + " [Subscribe] ");
                    comp.add("^w subscribe to " + lname);
                    comp.add("!/log " + lname);
                }
            } else {
                for (String option : logger.getOptions()) {
                    if (subs.containsKey(lname) && subs.get(lname).equalsIgnoreCase(option)) {
                        comp.add("l [" + option + "] ");
                    } else {
                        comp.add(color + " [" + option + "] ");
                        comp.add("^w subscribe to " + lname + " " + option);
                        comp.add("!/log " + lname + " " + option);
                    }

                }
            }
            if (subs.containsKey(lname)) {
                comp.add("nb [X]");
                comp.add("^w Click to unsubscribe");
                comp.add("!/log " + lname);
            }
            Messenger.m(player, comp.toArray(new Object[0]));
        }
    }

    private static void unsubFromAll(CommandSource source, String playerName) {
        PlayerEntity player = source.getServer().getPlayerManager().get(playerName);
        if (player == null) {
            Messenger.m(source, "r No player specified");
            return;
        }
        for (String logName : LoggerRegistry.getLoggerNames()) {
            LoggerRegistry.unsubscribePlayer(playerName, logName);
        }
        Messenger.m(source, "gi Unsubscribed from all logs");
    }

    private static void unsubFromLogger(CommandSource source, String playerName, String logName) {
        PlayerEntity player = source.getServer().getPlayerManager().get(playerName);
        if (player == null) {
            Messenger.m(source, "r No player specified");
            return;
        }
        if (LoggerRegistry.getLogger(logName) == null) {
            Messenger.m(source, "r Unknown logger: ", "rb " + logName);
            return;
        }
        LoggerRegistry.unsubscribePlayer(playerName, logName);
        Messenger.m(source, "gi Unsubscribed from " + logName);
    }

    private static void toggleSubscription(CommandSource source, String playerName, String logName) {
        PlayerEntity player = source.getServer().getPlayerManager().get(playerName);
        if (player == null) {
            Messenger.m(source, "r No player specified");
            return;
        }
        if (LoggerRegistry.getLogger(logName) == null) {
            Messenger.m(source, "r Unknown logger: ", "rb " + logName);
            return;
        }
        boolean subscribed = LoggerRegistry.togglePlayerSubscription(playerName, logName);
        if (subscribed) {
            Messenger.m(source, "gi " + playerName + " subscribed to " + logName + ".");
        } else {
            Messenger.m(source, "gi " + playerName + " unsubscribed from " + logName + ".");
        }
    }

    private static void subscribePlayer(CommandSource source, String playerName, String logName, String option) {
        PlayerEntity player = source.getServer().getPlayerManager().get(playerName);
        if (player == null) {
            Messenger.m(source, "r No player specified");
            return;
        }
        if (LoggerRegistry.getLogger(logName) == null) {
            Messenger.m(source, "r Unknown logger: ", "rb " + logName);
            return;
        }
        if (!LoggerRegistry.getLogger(logName).isOptionValid(option)) {
            Messenger.m(source, "r Invalid option: ", "rb " + option);
            return;
        }
        LoggerRegistry.subscribePlayer(playerName, logName, option);
        if (option != null) {
            Messenger.m(source, "gi Subscribed to " + logName + "(" + option + ")");
        } else {
            Messenger.m(source, "gi Subscribed to " + logName);
        }
    }

    // todo less dupe codes...
    private static void listDefaults(CommandSource source) {
        PlayerEntity player;
        try {
            player = (PlayerEntity) source;
        } catch (Exception e) {
            Messenger.m(source, "For players only");
            return;
        }

        Map<String, LoggerOptions> subs = LoggerRegistry.getDefaultSubscriptions();
        if (subs == null) {
            subs = new HashMap<>();
        }
        List<String> all_logs = new ArrayList<>(LoggerRegistry.getLoggerNames());
        Collections.sort(all_logs);
        Messenger.m(player, "w _____________________");
        Messenger.m(player, "w Available Default logging options:");
        for (String lname : all_logs) {
            List<Object> comp = new ArrayList<>();
            String color = subs.containsKey(lname) ? "w" : "g";
            comp.add("w  - " + lname + ": ");
            Logger logger = LoggerRegistry.getLogger(lname);
            String[] options = logger.getOptions();
            if (options.length == 0) {
                if (subs.containsKey(lname)) {
                    comp.add("l Subscribed ");
                } else {
                    comp.add(color + " [Subscribe] ");
                    comp.add("^w set default subscription to " + lname);
                    comp.add("!/log setDefault " + lname);
                }
            } else {
                for (String option : logger.getOptions()) {
                    if (subs.containsKey(lname) && subs.get(lname).option.equalsIgnoreCase(option)) {
                        comp.add("l [" + option + "] ");
                    } else {
                        comp.add(color + " [" + option + "] ");
                        comp.add("^w set default subscription to " + lname + " " + option);
                        comp.add("!/log setDefault " + lname + " " + option);
                    }

                }
            }
            if (subs.containsKey(lname)) {
                comp.add("nb [X]");
                comp.add("^w Click to remove default subscription");
                comp.add("!/log removeDefault " + lname);
            }
            Messenger.m(player, comp.toArray(new Object[0]));
        }
    }

    private static void setDefault(CommandSource source, String logName, String option) throws IncorrectUsageException {
        Logger logger = LoggerRegistry.getLogger(logName);
        if (logger == null) {
            throw new IncorrectUsageException("No logger named " + logger + ".");
        }
        option = option == null ? logger.getDefault() : logger.getAcceptedOption(option);

        LoggerRegistry.setDefault(source.getServer(), logName, option);
        Messenger.m(source, "gi Added " + logger.getLogName() + " to default subscriptions.");
    }

    private static void removeDefault(CommandSource source, String logName) throws IncorrectUsageException {
        Logger logger = LoggerRegistry.getLogger(logName);
        if (logger == null) {
            throw new IncorrectUsageException("No logger named " + logger + ".");
        }
        LoggerRegistry.removeDefault(source.getServer(), logName);
        Messenger.m(source, "gi Removed " + logger.getLogName() + " from default subscriptions.");
    }

    private static List<String> getLoggerOptions(String logName) {
        Logger logger = LoggerRegistry.getLogger(logName);
        String[] opts = logger.getOptions();
        List<String> options = new ArrayList<>();
        if (opts != null) {
            options.addAll(Arrays.asList(opts));
        }
        return options;
    }
}
