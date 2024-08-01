package carpet.logging;

import carpet.CarpetServer;
import carpet.CarpetSettings;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoggerRegistry {
    // Map from logger names to loggers.
    private static final Map<String, Logger> loggerRegistry = new HashMap<>();
    // Map from player names to the set of names of the logs that player is subscribed to.
    private static final Map<String, Map<String, String>> playerSubscriptions = new HashMap<>();
    //statics to quickly access if its worth even to call each one
    public static boolean __tnt;
    public static boolean __projectiles;
    public static boolean __fallingBlocks;
    public static boolean __tps;
    public static boolean __counter;
    public static boolean __mobcaps;
    public static boolean __packets;
    public static boolean __pathfinding;
    public static boolean __explosions;

    public static void initLoggers() {
        stopLoggers();
        registerLoggers();
        CarpetServer.registerExtensionLoggers();
    }

    public static void registerLoggers() {
        registerLogger("tps", HUDLogger.standardHUDLogger("tps", null, null));
    }

    /**
     * Gets the logger with the given name. Returns null if no such logger exists.
     */
    public static Logger getLogger(String name) {
        return loggerRegistry.get(name);
    }

    /**
     * Gets the set of logger names.
     */
    public static Set<String> getLoggerNames() {
        return loggerRegistry.keySet();
    }

    /**
     * Subscribes the player with name playerName to the log with name logName.
     */
    public static void subscribePlayer(String playerName, String logName, String option) {
        if (!playerSubscriptions.containsKey(playerName)) playerSubscriptions.put(playerName, new HashMap<>());
        Logger log = loggerRegistry.get(logName);
        if (option == null) option = log.getDefault();
        playerSubscriptions.get(playerName).put(logName, option);
        log.addPlayer(playerName, option);
    }

    /**
     * Unsubscribes the player with name playerName from the log with name logName.
     */
    public static void unsubscribePlayer(String playerName, String logName) {
        if (playerSubscriptions.containsKey(playerName)) {
            Map<String, String> subscriptions = playerSubscriptions.get(playerName);
            subscriptions.remove(logName);
            loggerRegistry.get(logName).removePlayer(playerName);
            if (subscriptions.size() == 0) playerSubscriptions.remove(playerName);
        }
    }

    /**
     * If the player is not subscribed to the log, then subscribe them. Otherwise, unsubscribe them.
     */
    public static boolean togglePlayerSubscription(String playerName, String logName) {
        if (playerSubscriptions.containsKey(playerName) && playerSubscriptions.get(playerName).containsKey(logName)) {
            unsubscribePlayer(playerName, logName);
            return false;
        } else {
            subscribePlayer(playerName, logName, null);
            return true;
        }
    }

    /**
     * Get the set of logs the current player is subscribed to.
     */
    public static Map<String, String> getPlayerSubscriptions(String playerName) {
        if (playerSubscriptions.containsKey(playerName)) {
            return playerSubscriptions.get(playerName);
        }
        return null;
    }

    protected static void setAccess(Logger logger) {
        boolean value = logger.hasOnlineSubscribers();
        try {
            Field f = logger.getField();
            f.setBoolean(null, value);
        } catch (IllegalAccessException e) {
            CarpetSettings.LOG.error("Cannot change logger quick access field");
        }
    }

    /**
     * Called when the server starts. Creates the logs used by Carpet mod.
     */
    public static void registerLogger(String name, Logger logger) {
        loggerRegistry.put(name, logger);
        setAccess(logger);
    }

    private final static Set<String> seenPlayers = new HashSet<>();

    public static void stopLoggers() {
        for (Logger log : loggerRegistry.values()) {
            log.serverStopped();
        }
        seenPlayers.clear();
        loggerRegistry.clear();
        playerSubscriptions.clear();
    }

    public static void playerConnected(PlayerEntity player) {
        boolean firstTime = false;
        if (!seenPlayers.contains(player.getName())) {
            seenPlayers.add(player.getName());
            firstTime = true;
            //subscribe them to the defualt loggers
        }
        for (Logger log : loggerRegistry.values()) {
            log.onPlayerConnect(player, firstTime);
        }
    }

    public static void playerDisconnected(PlayerEntity player) {
        for (Logger log : loggerRegistry.values()) {
            log.onPlayerDisconnect(player);
        }
    }
}
