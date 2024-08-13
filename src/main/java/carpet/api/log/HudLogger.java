package carpet.api.log;

import carpet.log.framework.HudController;
import carpet.log.framework.LoggerRegistry;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

public class HudLogger extends Logger {
    public static Logger standardHudLogger(String logName, String def, String[] options) {
        return standardHudLogger(logName, def, options, false);
    }

    public static Logger standardHudLogger(String logName, String def, String[] options, boolean strictOptions) {
        // should convert to factory method if more than 2 classes are here
        try {
            return new HudLogger(LoggerRegistry.class.getField("__" + logName), logName, def, options, strictOptions);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create logger " + logName);
        }
    }

    public HudLogger(Field field, String logName, String def, String[] options, boolean strictOptions) {
        super(field, logName, def, options, strictOptions);
    }

    @Deprecated
    public HudLogger(Field field, String logName, String def, String[] options) {
        super(field, logName, def, options, false);
    }

    @Override
    public void removePlayer(String playerName) {
        ServerPlayerEntity player = playerFromName(playerName);
        if (player != null) HudController.clearPlayer(player);
        super.removePlayer(playerName);
    }

    @Override
    public void sendPlayerMessage(ServerPlayerEntity player, Text... messages) {
        for (Text m : messages) HudController.addMessage(player, m);
    }
}
