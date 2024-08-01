package carpet;

import carpet.api.settings.SettingsManager;
import carpet.commands.LogCommand;
import carpet.logging.HUDController;
import carpet.logging.LoggerRegistry;
import carpet.network.ServerNetworkHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.handler.CommandRegistry;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CarpetServer {
    public static MinecraftServer minecraftServer;
    public static SettingsManager settingsManager;
    public static final List<CarpetExtension> extensions = new ArrayList<>();

    /**
     * Registers a {@link CarpetExtension} to be managed by Carpet.<br>
     * Should be called before Carpet's startup, like in Fabric Loader's
     * {@link net.fabricmc.api.ModInitializer} entrypoint
     *
     * @param extension The instance of a {@link CarpetExtension} to be registered
     */
    public static void manageExtension(CarpetExtension extension) {
        extensions.add(extension);
    }

    public static void onGameStarted() {
        settingsManager = new SettingsManager(CarpetSettings.carpetVersion, "carpet", "Carpet Mod");
        settingsManager.parseSettingsClass(CarpetSettings.class);
        extensions.forEach(CarpetExtension::onGameStarted);
    }

    public static void onServerLoaded(MinecraftServer server) {
        CarpetServer.minecraftServer = server;
        forEachManager(sm -> sm.attachServer(server));
        extensions.forEach(e -> e.onServerLoaded(server));
        LoggerRegistry.initLoggers();
    }

    public static void onServerLoadedWorlds(MinecraftServer minecraftServer) {
        extensions.forEach(e -> e.onServerLoadedWorlds(minecraftServer));
    }

    public static void tick(MinecraftServer server) {
        // todo tickrate

        HUDController.updateHUD(server);
        extensions.forEach(e -> e.onTick(server));
    }

    public static void registerCarpetCommands(CommandRegistry registry) {
        if (settingsManager == null) {
            return;
        }
        registry.register(new SettingsManager.CarpetCommand(settingsManager));

        extensions.forEach(e -> e.registerCommands(registry));
    }

    public static void onPlayerLoggedIn(ServerPlayerEntity player) {
        ServerNetworkHandler.onPlayerJoin(player);
        extensions.forEach(e -> e.onPlayerLoggedIn(player));
    }

    public static void onPlayerLoggedOut(ServerPlayerEntity player) {
        ServerNetworkHandler.onPlayerLoggedOut(player);
        extensions.forEach(e -> e.onPlayerLoggedOut(player));
    }

    // scarpet
    public static void clientPreClosing() {
    }

    public static void onServerClosed(MinecraftServer server) {
        if (minecraftServer != null) {
            ServerNetworkHandler.close();
            extensions.forEach(e -> e.onServerClosed(server));
            minecraftServer = null;
        }
    }

    public static void onServerDoneClosing(MinecraftServer server) {
        forEachManager(SettingsManager::detachServer);
    }

    // not API
    // carpet's included
    public static void forEachManager(Consumer<SettingsManager> consumer) {
        consumer.accept(settingsManager);
        for (CarpetExtension e : extensions) {
            SettingsManager manager = e.extensionSettingsManager();
            if (manager != null) {
                consumer.accept(manager);
            }
        }
    }

    public static void registerExtensionLoggers() {
        extensions.forEach(CarpetExtension::registerLoggers);
    }

    // not used in 1.12
    public static void onReload(MinecraftServer server) {
        extensions.forEach(e -> e.onReload(server));
    }
}
