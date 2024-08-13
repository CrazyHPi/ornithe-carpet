package carpet;

import carpet.api.settings.SettingsManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.handler.CommandRegistry;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import java.util.Collections;
import java.util.Map;

public interface CarpetExtension {
    /**
     * When game started before world is loaded
     */
    default void onGameStarted() {
    }

    /**
     * Runs once per loaded world once the server / gamerules etc are fully loaded
     * but before worlds are loaded
     * Can be loaded multiple times in SinglePlayer
     *
     * @param server The {@link MinecraftServer} instance that loaded
     */
    default void onServerLoaded(MinecraftServer server) {
    }

    /**
     * Runs once per loaded world once the World files are fully loaded
     * Can be loaded multiple times in SinglePlayer
     *
     * @param server The current {@link MinecraftServer} instance
     */
    default void onServerLoadedWorlds(MinecraftServer server) {
    }

    /**
     * Runs once per game tick, as a first thing in the tick
     *
     * @param server The current {@link MinecraftServer} instance
     */
    default void onTick(MinecraftServer server) {
    }

    /**
     * Register your own commands right after vanilla commands are added
     * If that matters for you
     *
     * @param registry The CommandRegistry you use to register your commands
     */
    default void registerCommands(CommandRegistry registry) {
    }

    default SettingsManager extensionSettingsManager() {
        return null;
    }

    /**
     * Event that gets called when a player logs in
     *
     * @param player The {@link ServerPlayerEntity} that logged in
     */
    default void onPlayerLoggedIn(ServerPlayerEntity player) {
    }

    /**
     * Event that gets called when a player logs out
     *
     * @param player The {@link ServerPlayerEntity} that logged out
     */
    default void onPlayerLoggedOut(ServerPlayerEntity player) {
    }

    /**
     * Event that gets called when the server closes.
     * Can be called multiple times in singleplayer
     *
     * @param server The {@link MinecraftServer} that is closing
     */
    default void onServerClosed(MinecraftServer server) {
    }

    /**
     * Event that gets called when the server reloads (usually
     * when running the /reload command)
     *
     * @param server The {@link MinecraftServer} that is reloading
     */
    default void onReload(MinecraftServer server) {
    }

    /**
     * @return A {@link String} usually being the extension's id
     */
    default String version() {
        return null;
    }

    /**
     * Called when registering custom loggers
     */
    default void registerLoggers() {
    }

    /**
     * Method for Carpet to get the translations for extension's
     * rules.
     *
     * @param lang A {@link String} being the language id selected by the user
     * @return A {@link Map<String, String>} containing the string key with its
     * respective translation {@link String} or an empty map if not available
     */
    default Map<String, String> canHasTranslations(String lang) {
        return Collections.emptyMap();
    }

    /**
     * Handles each call that creates / parses the scarpet expression.
     * Extensions can add their own built-in functions here.
     * <p>
     * Events such as generic events or entity events, can be added statically
     * by creating new events as
     * <p>
     * CarpetEventServer.Event class: to handle `__on_foo()` type of call definitions
     * <p>
     * or
     * <p>
     * EntityEventsGroup.Event class: to handle `entity_event('foo', ...)` type of events
     *
     * @param expression Passed {@link CarpetExpression} to add built-in functions to
     */
    // todo scarpet
//	default void scarpetApi(CarpetExpression expression) {
//	}

}
