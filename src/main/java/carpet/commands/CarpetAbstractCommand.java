package carpet.commands;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.exception.InvalidNumberException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
        msg(source, texts.toArray(texts.toArray(new Text[0])));
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

    /**
     * Returns whether the the {@link CommandSource} can execute
     * a command given the required permission level, according to
     * Carpet's standard for permissions.
     *
     * @param source       The origin {@link CommandSource}
     * @param commandLevel A {@link String} being the permission level (either 0-4, a
     *                     {@link boolean} value or "ops".
     * @return Whether or not the {@link CommandSource} meets the required level
     */
    public static boolean canUseCommand(CommandSource source, Object commandLevel) {
        if (commandLevel instanceof Boolean) {
            return (Boolean) commandLevel;
        }
        String commandLevelString = commandLevel.toString();
        switch (commandLevelString) {
            case "true": return true;
            case "false": return false;
            case "ops": return source.canUseCommand(2, source.getName()); // typical for other cheaty commands
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
                return source.canUseCommand(Integer.parseInt(commandLevelString), source.getName());
        }
        return false;
    }

    public static List<String> suggestMatchingContains(List<String> stream, String key) {
        List<String> regularSuggestionList = new ArrayList<>();
        List<String> smartSuggestionList = new ArrayList<>();
        stream.forEach(listItem -> {
            List<String> words = Arrays.stream(listItem.split("(?<!^)(?=[A-Z])")).map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
            List<String> prefixes = new ArrayList<>(words.size());
            for (int i = 0; i < words.size(); i++) {
                prefixes.add(String.join("", words.subList(i, words.size())));
            }
            if (prefixes.stream().anyMatch(s -> s.startsWith(key))) {
                smartSuggestionList.add(listItem);
            }
            if (matchesSubStr(key, listItem.toLowerCase(Locale.ROOT))) {
                regularSuggestionList.add(listItem);
            }
        });

        return regularSuggestionList;
    }

    public static  boolean matchesSubStr(String string, String string2) {
        for(int i = 0; !string2.startsWith(string, i); ++i) {
            i = string2.indexOf(95, i);
            if (i < 0) {
                return false;
            }
        }

        return true;
    }
}
