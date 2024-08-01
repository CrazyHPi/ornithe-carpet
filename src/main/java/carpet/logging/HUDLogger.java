package carpet.logging;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

public class HUDLogger extends Logger {
    static Logger standardHUDLogger(String logName, String def, String[] options) {
        return standardHUDLogger(logName, def, options, false);
    }

    static Logger standardHUDLogger(String logName, String def, String[] options, boolean strictOptions) {
        // should convert to factory method if more than 2 classes are here
        try {
            return new HUDLogger(LoggerRegistry.class.getField("__" + logName), logName, def, options, strictOptions);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create logger " + logName);
        }
    }

    public HUDLogger(Field field, String logName, String def, String[] options, boolean strictOptions) {
        super(field, logName, def, options, strictOptions);
    }

    @Deprecated
    public HUDLogger(Field field, String logName, String def, String[] options) {
        super(field, logName, def, options, false);
    }

    @Override
    public void removePlayer(String playerName) {
        ServerPlayerEntity player = playerFromName(playerName);
        if (player != null) HUDController.clearPlayer(player);
        super.removePlayer(playerName);
    }

    @Override
    public void sendPlayerMessage(ServerPlayerEntity player, Text... messages) {
        for (Text m : messages) HUDController.addMessage(player, m);
    }
}
