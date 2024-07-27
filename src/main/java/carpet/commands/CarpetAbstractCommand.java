package carpet.commands;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.exception.InvalidNumberException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Text;

import java.util.List;

public abstract class CarpetAbstractCommand extends AbstractCommand {
    @Override
    public boolean canUse(MinecraftServer server, CommandSource source) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public void msg(CommandSource source, List<Text> texts) {
        msg(source, texts);
    }

    public void msg(CommandSource source, Text... texts) {
        if (source instanceof PlayerEntity) {
            for (Text t : texts) source.sendMessage(t);
        } else {
            for (Text t : texts) sendSuccess(source, this, t.getString());
        }
    }

    protected int parseChunkPosition(String arg, int base) throws InvalidNumberException {
        return arg.equals("~") ? base >> 4 : parseInt(arg);
    }
}
