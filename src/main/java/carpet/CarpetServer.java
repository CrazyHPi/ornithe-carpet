package carpet;

import carpet.api.settings.SettingsManager;
import carpet.commands.*;
import carpet.log.framework.HudController;
import carpet.log.framework.LoggerRegistry;
import carpet.network.ServerNetworkHandler;
import carpet.patches.ServerPlayerEntityFake;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.handler.CommandRegistry;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

import java.io.*;
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
     * {@link net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint} entrypoint.<br>
     * 1.12 Ornithe Carpet use Mixin to init the mod right when Minecraft start.<br>
     * Set extension's entry point to preLunch and call this method <br>
     * in fabric.mod.json:
     * "preLaunch": ["extension::init"] <br>
     * if OSL is not used
     *
     * @param extension The instance of a {@link CarpetExtension} to be registered
     */
    public static void manageExtension(CarpetExtension extension) {
        extensions.add(extension);
    }

    public static void preLaunch() {
        // init mixin extras
//        MixinExtrasBootstrap.init();
    }

    public static void onGameStarted() {
        settingsManager = new SettingsManager(SharedConstants.carpetVersion, "carpet", "Carpet Mod");
        settingsManager.parseSettingsClass(CarpetSettings.class);
        extensions.forEach(CarpetExtension::onGameStarted);
    }

    public static void onServerLoaded(MinecraftServer server) {
        CarpetServer.minecraftServer = server;
        forEachManager(sm -> sm.attachServer(server));
        extensions.forEach(e -> e.onServerLoaded(server));
        LoggerRegistry.initLoggers();
        LoggerRegistry.readSaveFile(server);
    }

    public static void onServerLoadedWorlds(MinecraftServer minecraftServer) {
        loadBots(minecraftServer);

        extensions.forEach(e -> e.onServerLoadedWorlds(minecraftServer));
    }

    public static void tick(MinecraftServer server) {
        HudController.updateHud(server);
        extensions.forEach(e -> e.onTick(server));
    }

    public static void registerCarpetCommands(CommandRegistry registry) {
        if (settingsManager == null) {
            return;
        }
        registry.register(new SettingsManager.CarpetCommand(settingsManager));
        registry.register(new CounterCommand());
        registry.register(new LogCommand());
        registry.register(new ChunkCommand());
        registry.register(new TickCommand());
        registry.register(new PlayerCommand());

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            registry.register(new TestCommand());
        }

        extensions.forEach(e -> e.registerCommands(registry));
    }

    public static void onPlayerLoggedIn(ServerPlayerEntity player) {
        ServerNetworkHandler.onPlayerJoin(player);
        LoggerRegistry.playerConnected(player);
        extensions.forEach(e -> e.onPlayerLoggedIn(player));
    }

    public static void onPlayerLoggedOut(ServerPlayerEntity player) {
        ServerNetworkHandler.onPlayerLoggedOut(player);
        LoggerRegistry.playerDisconnected(player);
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

    public static void loadBots(MinecraftServer server) {
        try {
            File settings_file = server.getWorldStorageSource().getFile(server.getWorldSaveName(), "bot.conf");
            BufferedReader b = new BufferedReader(new FileReader(settings_file));
            String line = "";
            boolean temp = CarpetSettings.removeFakePlayerSkins;
            CarpetSettings.removeFakePlayerSkins = true;
            while ((line = b.readLine()) != null) {
                ServerPlayerEntityFake.create(line, server);
            }
            b.close();
            CarpetSettings.removeFakePlayerSkins = temp;
        } catch (IOException e) {
            SharedConstants.LOG.error(e.getStackTrace());
        }
    }

    public static void saveBots(MinecraftServer server, ArrayList<String> names) {
        try {
            File settings_file = server.getWorldStorageSource().getFile(server.getWorldSaveName(), "bot.conf");
            if (names != null) {
                FileWriter fw = new FileWriter(settings_file);
                for (String name : names) {
                    fw.write(name + "\n");
                }
                fw.close();
            } else {
                settings_file.delete();
            }
        } catch (IOException e) {
            SharedConstants.LOG.error(e.getStackTrace());
        }
    }
}
