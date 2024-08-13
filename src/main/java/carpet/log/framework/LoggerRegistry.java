package carpet.log.framework;

import carpet.CarpetServer;
import carpet.SharedConstants;
import carpet.api.log.HudLogger;
import carpet.api.log.Logger;
import com.google.common.base.Charsets;
import com.google.gson.*;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
    // List of default loggers
    private static final Map<String, LoggerOptions> defaultSubscriptions = new HashMap<>();

    //statics to quickly access if its worth even to call each one
    public static boolean __tnt;
    public static boolean __projectiles;
    public static boolean __fallingBlocks;
    public static boolean __kills;
    public static boolean __autosave;
    public static boolean __tps;
    public static boolean __counter;
    public static boolean __mobcaps;
    public static boolean __damage;
    public static boolean __packets;
    public static boolean __weather;
    public static boolean __tileTickLimit;
    public static boolean __portalCaching;
    public static boolean __instantComparators;
    public static boolean __items;
    public static boolean __rng;
    public static boolean __explosions;
    public static boolean __recipes;
    public static boolean __damageDebug;
    public static boolean __invisDebug;
    public static boolean __carefulBreak;

    public static void initLoggers() {
        stopLoggers();
        registerLoggers();
        CarpetServer.registerExtensionLoggers();
    }

    public static void registerLoggers() {
        registerLogger("tnt", Logger.standardLogger("tnt", "brief", new String[]{"brief", "full"}, true));
        registerLogger("projectiles", Logger.standardLogger("projectiles", "brief", new String[]{"brief", "full"}));
        registerLogger("fallingBlocks", Logger.standardLogger("fallingBlocks", "brief", new String[]{"brief", "full"}));
        registerLogger("kills", Logger.standardLogger("kills", null, null));
        registerLogger("weather", Logger.standardLogger("weather", null, null));
        registerLogger("tileTickLimit", Logger.standardLogger("tileTickLimit", null, null));
        registerLogger("portalCaching", Logger.standardLogger("portalCaching", "brief", new String[]{"brief", "full"}));
        registerLogger("instantComparators", Logger.standardLogger("instantComparators", "all", new String[]{"all", "tileTick", "buggy"}));
        registerLogger("items", Logger.standardLogger("items", "brief", new String[]{"brief", "full"}));
        registerLogger("rng", Logger.standardLogger("rng", null, null));
        registerLogger("explosions", Logger.standardLogger("explosions", "compact", new String[]{"brief", "full", "compact"}));

        registerLogger("autosave", HudLogger.standardHudLogger("autosave", null, null));
        registerLogger("tps", HudLogger.standardHudLogger("tps", null, null));
        registerLogger("mobcaps", HudLogger.standardHudLogger("mobcaps", "dynamic", new String[]{"dynamic", "overworld", "nether", "end"}));
        registerLogger("counter", HudLogger.standardHudLogger("counter", "all", new String[]{"all", "cactus", "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"}));
        registerLogger("packets", HudLogger.standardHudLogger("packets", null, null));
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

    private static File getSaveFile(MinecraftServer server) {
        return server.getWorldStorageSource().getFile(server.getWorldSaveName(), "loggerData.json");
    }

    public static void readSaveFile(MinecraftServer server) {
        File logData = getSaveFile(server);
        if (!logData.isFile()) {
            return;
        }
        try {
            JsonElement root = (new JsonParser()).parse(FileUtils.readFileToString(logData, Charsets.UTF_8));
            if (!root.isJsonObject()) {
                return;
            }
            JsonObject rootObj = root.getAsJsonObject();
            JsonArray defaultList = rootObj.getAsJsonArray("defaultList");
            for (JsonElement entryElement : defaultList) {
                LoggerOptions options = new LoggerOptions();
                options.add(entryElement);

                defaultSubscriptions.put(options.logger, options);
            }

            JsonObject playerList = rootObj.getAsJsonObject("players");
            for (Map.Entry<String, JsonElement> playerEntry : playerList.entrySet()) {
                String username = playerEntry.getKey();
                Map<String, String> subs = new HashMap<>();

                JsonArray loggerEntries = playerEntry.getValue().getAsJsonArray();
                for (JsonElement entryElement : loggerEntries) {
                    LoggerOptions options = new LoggerOptions();
                    options.add(entryElement);

                    subs.put(options.logger, options.option);
                }

                playerSubscriptions.put(username, subs);
            }

        } catch (IOException ioexception) {
            SharedConstants.LOG.error("Couldn't read default logger file {}", logData, ioexception);
        } catch (JsonParseException jsonparseexception) {
            SharedConstants.LOG.error("Couldn't parse default logger file {}", logData, jsonparseexception);
        }
    }

    public static void writeConf(MinecraftServer server) {
        File logData = getSaveFile(server);
        try {
            JsonObject root = new JsonObject();

            JsonArray defaultList = new JsonArray();
            for (Map.Entry<String, LoggerOptions> logger : defaultSubscriptions.entrySet()) {
                defaultList.add(logger.getValue().toJson());
            }
            root.add("defaultList", defaultList);

            JsonObject playerList = new JsonObject();
            for (Map.Entry<String, Map<String, String>> playerEntry : playerSubscriptions.entrySet()) {
                JsonArray playerLoggers = new JsonArray();

                for (Map.Entry<String, String> logger : playerEntry.getValue().entrySet()) {
                    LoggerOptions loggerOptions = new LoggerOptions(logger.getKey(), logger.getValue());

                    playerLoggers.add(loggerOptions.toJson());
                }

                playerList.add(playerEntry.getKey(), playerLoggers);
            }
            root.add("players", playerList);

            FileUtils.writeStringToFile(logData, root.toString(), Charsets.UTF_8);
        } catch (IOException ioexception) {
            SharedConstants.LOG.error("Couldn't save stats", (Throwable) ioexception);
        }
    }

    /**
     * Sets a log as a default log with the specified option and handler
     */
    public static void setDefault(MinecraftServer server, String logName, String option) {
        defaultSubscriptions.put(logName, new LoggerOptions(logName, option));
        writeConf(server);

        // Subscribe all players who have no customized subscription list
        for (PlayerEntity player : server.getPlayerManager().getAll()) {
            if (!hasSubscriptions(player.getName())) {
                unsubscribePlayer(player.getName(), logName);
            }
        }
    }

    /**
     * Removes a log from the list of default logs
     */
    public static void removeDefault(MinecraftServer server, String logName) {
        if (defaultSubscriptions.containsKey(logName)) {
            defaultSubscriptions.remove(logName);
            writeConf(server);

            // Unsubscribe all players who have no customized subscription list
            for (PlayerEntity player : server.getPlayerManager().getAll()) {
                if (!hasSubscriptions(player.getName())) {
                    unsubscribePlayer(player.getName(), logName);
                }
            }
        }
    }

    /**
     * Checks if a player is actively subscribed to anything
     */
    public static boolean hasSubscriptions(String playerName) {
        return playerSubscriptions.containsKey(playerName);
    }

    /**
     * Subscribes the player with name playerName to the log with name logName.
     */
    public static void subscribePlayer(String playerName, String logName, String option) {
        if (!playerSubscriptions.containsKey(playerName)) {
            playerSubscriptions.put(playerName, new HashMap<>());
        }
        Logger log = loggerRegistry.get(logName);
        if (option == null) {
            option = log.getDefault();
        }
        playerSubscriptions.get(playerName).put(logName, option);
        log.addPlayer(playerName, option);
        writeConf(CarpetServer.minecraftServer);
    }

    /**
     * Unsubscribes the player with name playerName from the log with name logName.
     */
    public static void unsubscribePlayer(String playerName, String logName) {
        if (playerSubscriptions.containsKey(playerName)) {
            Map<String, String> subscriptions = playerSubscriptions.get(playerName);
            subscriptions.remove(logName);
            loggerRegistry.get(logName).removePlayer(playerName);
            if (subscriptions.isEmpty()) {
                playerSubscriptions.remove(playerName);
            }
            writeConf(CarpetServer.minecraftServer);
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
        return playerSubscriptions.getOrDefault(playerName, new HashMap<>());
    }

    public static Map<String, LoggerOptions> getDefaultSubscriptions() {
        return defaultSubscriptions;
    }

    public static void setAccess(Logger logger) {
        boolean value = logger.hasOnlineSubscribers();
        try {
            Field f = logger.getField();
            f.setBoolean(null, value);
        } catch (IllegalAccessException e) {
            SharedConstants.LOG.error("Cannot change logger quick access field");
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
