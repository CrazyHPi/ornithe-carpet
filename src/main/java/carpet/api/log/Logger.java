package carpet.api.log;

import carpet.CarpetServer;
import carpet.SharedConstants;
import carpet.log.framework.LoggerOptions;
import carpet.log.framework.LoggerRegistry;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

public class Logger {
    // The set of subscribed and online players.
    private Map<String, String> subscribedOnlinePlayers;

    // The set of subscribed and offline players.
    private Map<String, String> subscribedOfflinePlayers;

    // The logName of this log. Gets prepended to logged messages.
    private String logName;

    private String default_option;

    private String[] options;

    private Field acceleratorField;

    private boolean strictOptions;

    public static Logger standardLogger(String logName, String def, String[] options) {
        return standardLogger(logName, def, options, false);
    }

    public static Logger standardLogger(String logName, String def, String[] options, boolean strictOptions) {
        try {
            return new Logger(LoggerRegistry.class.getField("__" + logName), logName, def, options, strictOptions);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create logger " + logName);
        }
    }

    public Logger(Field acceleratorField, String logName, String def, String[] options) {
        this(acceleratorField, logName, def, options, false);
    }

    public Logger(Field acceleratorField, String logName, String def, String[] options, boolean strictOptions) {
        subscribedOnlinePlayers = new HashMap<>();
        subscribedOfflinePlayers = new HashMap<>();
        this.acceleratorField = acceleratorField;
        this.logName = logName;
        this.default_option = def;
        this.options = options == null ? new String[0] : options;
        this.strictOptions = strictOptions;
        if (acceleratorField == null) {
            SharedConstants.LOG.error("[CM] Logger " + getLogName() + " is missing a specified accelerator");
        }
    }

    public String getDefault() {
        return default_option;
    }

    public String[] getOptions() {
        return options;
    }

    public String getLogName() {
        return logName;
    }

    /**
     * Subscribes the player with the given logName to the logger.
     */
    public void addPlayer(String playerName, String option) {
        if (playerFromName(playerName) != null) {
            subscribedOnlinePlayers.put(playerName, option);
        } else {
            subscribedOfflinePlayers.put(playerName, option);
        }
        LoggerRegistry.setAccess(this);
    }

    /**
     * Unsubscribes the player with the given logName from the logger.
     */
    public void removePlayer(String playerName) {
        subscribedOnlinePlayers.remove(playerName);
        subscribedOfflinePlayers.remove(playerName);
        LoggerRegistry.setAccess(this);
    }

    /**
     * Returns true if there are any online subscribers for this log.
     */
    public boolean hasOnlineSubscribers() {
        return subscribedOnlinePlayers.size() > 0;
    }

    public void serverStopped() {
        subscribedOnlinePlayers.clear();
        subscribedOfflinePlayers.clear();
    }

    public Field getField() {
        return acceleratorField;
    }

    /**
     * serves messages to players fetching them from the promise
     * will repeat invocation for players that share the same option
     */
    @FunctionalInterface
    public interface lMessage {
        Text[] get(String playerOption, PlayerEntity player);
    }
    public void log(lMessage messagePromise) {
        for (Map.Entry<String, String> en : subscribedOnlinePlayers.entrySet()) {
            ServerPlayerEntity player = playerFromName(en.getKey());
            if (player != null) {
                Text[] messages = messagePromise.get(en.getValue(), player);
                if (messages != null)
                    sendPlayerMessage(player, messages);
            }
        }
    }

    /**
     * guarantees that each message for each option will be evaluated once from the promise
     * and served the same way to all other players subscribed to the same option
     */
    @FunctionalInterface
    public interface lMessageIgnorePlayer {
        Text[] get(String playerOption);
    }
    public void log(lMessageIgnorePlayer messagePromise) {
        Map<String, Text[]> cannedMessages = new HashMap<>();
        for (Map.Entry<String, String> en : subscribedOnlinePlayers.entrySet()) {
            ServerPlayerEntity player = playerFromName(en.getKey());
            if (player != null) {
                String option = en.getValue();
                if (!cannedMessages.containsKey(option)) {
                    cannedMessages.put(option, messagePromise.get(option));
                }
                Text[] messages = cannedMessages.get(option);
                if (messages != null)
                    sendPlayerMessage(player, messages);
            }
        }
    }

    /**
     * guarantees that message is evaluated once, so independent from the player and chosen option
     */
    public void log(Supplier<Text[]> messagePromise) {
        Text[] cannedMessages = null;
        for (Map.Entry<String, String> en : subscribedOnlinePlayers.entrySet()) {
            ServerPlayerEntity player = playerFromName(en.getKey());
            if (player != null) {
                if (cannedMessages == null) cannedMessages = messagePromise.get();
                sendPlayerMessage(player, cannedMessages);
            }
        }
    }

    public void sendPlayerMessage(ServerPlayerEntity player, Text... messages) {
        Arrays.stream(messages).forEach(player::sendMessage);
    }

    /**
     * Gets the {@code PlayerEntity} instance for a player given their UUID. Returns null if they are offline.
     */
    protected ServerPlayerEntity playerFromName(String name) {
        return CarpetServer.minecraftServer.getPlayerManager().get(name);
    }

    // ----- Event Handlers ----- //

    public void onPlayerConnect(PlayerEntity player, boolean firstTime) {
        // If the player was subscribed to the log and offline, move them to the set of online subscribers.
        String playerName = player.getName();
        if (subscribedOfflinePlayers.containsKey(playerName)) {
            subscribedOnlinePlayers.put(playerName, subscribedOfflinePlayers.get(playerName));
            subscribedOfflinePlayers.remove(playerName);
        } else if (firstTime) {
            Set<Map.Entry<String, LoggerOptions>> defaultLoggers = new HashSet<>(LoggerRegistry.getDefaultSubscriptions().entrySet());
            String logName = getLogName();
            for (Map.Entry<String, LoggerOptions> logger : defaultLoggers) {
                if (logger.getKey().equals(logName)) {
                    LoggerRegistry.subscribePlayer(playerName, getLogName(), logger.getValue().option == null ? getDefault() : logger.getValue().option);
                    break;
                }
            }

            Set<Map.Entry<String, String>> savedLoggers = new HashSet<>(LoggerRegistry.getPlayerSubscriptions(playerName).entrySet());
            for (Map.Entry<String, String> logger : savedLoggers) {
                if (logger.getKey().equals(logName)) {
                    LoggerRegistry.subscribePlayer(playerName, getLogName(), logger.getValue() == null ? getDefault() : logger.getValue());
                    break;
                }
            }
        }
        LoggerRegistry.setAccess(this);
    }

    public void onPlayerDisconnect(PlayerEntity player) {
        // If the player was subscribed to the log, move them to the set of offline subscribers.
        String playerName = player.getName();
        if (subscribedOnlinePlayers.containsKey(playerName)) {
            subscribedOfflinePlayers.put(playerName, subscribedOnlinePlayers.get(playerName));
            subscribedOnlinePlayers.remove(playerName);
        }
        LoggerRegistry.setAccess(this);
    }

    public String getAcceptedOption(String arg) {
        if (Arrays.asList(this.getOptions()).contains(arg)) {
            return arg;
        }
        return null;
    }

    public boolean isOptionValid(String option) {
        if (strictOptions) {
            return Arrays.asList(this.getOptions()).contains(option);
        }
        return option != null;
    }
}
