package carpet.utils;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messenger {
    public static final Logger LOG = LogManager.getLogger("Messaging System");

    public static final Pattern colorExtract = Pattern.compile("#([0-9a-fA-F]{6})");

    public enum CarpetFormatting {
        ITALIC      ('i', (s, f) -> s.setItalic(true)),
        STRIKE      ('s', (s, f) -> s.setStrikethrough(true)),
        UNDERLINE   ('u', (s, f) -> s.setUnderlined(true)),
        BOLD        ('b', (s, f) -> s.setBold(true)),
        OBFUSCATE   ('o', (s, f) -> s.setObfuscated(true)),

        WHITE       ('w', (s, f) -> s.setColor(Formatting.WHITE)),
        YELLOW      ('y', (s, f) -> s.setColor(Formatting.YELLOW)),
        LIGHT_PURPLE('m', (s, f) -> s.setColor(Formatting.LIGHT_PURPLE)), // magenta
        RED         ('r', (s, f) -> s.setColor(Formatting.RED)),
        AQUA        ('c', (s, f) -> s.setColor(Formatting.AQUA)), // cyan
        GREEN       ('l', (s, f) -> s.setColor(Formatting.GREEN)), // lime
        BLUE        ('t', (s, f) -> s.setColor(Formatting.BLUE)), // light blue, teal
        DARK_GRAY   ('f', (s, f) -> s.setColor(Formatting.DARK_GRAY)),
        GRAY        ('g', (s, f) -> s.setColor(Formatting.GRAY)),
        GOLD        ('d', (s, f) -> s.setColor(Formatting.GOLD)),
        DARK_PURPLE ('p', (s, f) -> s.setColor(Formatting.DARK_PURPLE)), // purple
        DARK_RED    ('n', (s, f) -> s.setColor(Formatting.DARK_RED)),  // brown
        DARK_AQUA   ('q', (s, f) -> s.setColor(Formatting.DARK_AQUA)),
        DARK_GREEN  ('e', (s, f) -> s.setColor(Formatting.DARK_GREEN)),
        DARK_BLUE   ('v', (s, f) -> s.setColor(Formatting.DARK_BLUE)), // navy
        BLACK       ('k', (s, f) -> s.setColor(Formatting.BLACK)),

        COLOR       ('#', (s, f) -> {
            return s;
        }, s -> {
            Matcher m = colorExtract.matcher(s);
            return m.find() ? m.group(1) : null;
        }),
        ;

        public char code;
        public BiFunction<Style, String, Style> applier;
        public Function<String, String> container;

        CarpetFormatting(char code, BiFunction<Style, String, Style> applier) {
            this(code, applier, s -> s.indexOf(code) >= 0 ? Character.toString(code) : null);
        }

        CarpetFormatting(char code, BiFunction<Style, String, Style> applier, Function<String, String> container) {
            this.code = code;
            this.applier = applier;
            this.container = container;
        }

        public Style apply(String format, Style previous) {
            String fmt;
            if ((fmt = container.apply(format)) != null) return applier.apply(previous, fmt);
            return previous;
        }
    }

    public static Style parseStyle(String style) {
        Style myStyle = new Style().setColor(Formatting.WHITE);
        for (CarpetFormatting cf : CarpetFormatting.values()) {
            myStyle = cf.apply(style, myStyle);
        }
        return myStyle;
    }

    public static BaseText getChatComponentFromDesc(String message, BaseText previousMessage) {
        if (message.equalsIgnoreCase("")) {
            return new LiteralText("");
        }
        if (Character.isWhitespace(message.charAt(0))) {
            message = "w" + message;
        }
        int limit = message.indexOf(' ');
        String desc = message;
        String str = "";
        if (limit >= 0) {
            desc = message.substring(0, limit);
            str = message.substring(limit + 1);
        }
        if (previousMessage == null) {
            BaseText text = new LiteralText(str);
            text.setStyle(parseStyle(desc));
            return text;
        }
        Style previousStyle = previousMessage.getStyle();
        BaseText ret = previousMessage;
        // questionable ?!^@&
        switch (desc.charAt(0)) {
            case '?':
                previousMessage.setStyle(previousStyle.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message.substring(1))));
                break;
            case '!':
                previousMessage.setStyle(previousStyle.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, message.substring(1))));
                break;
            case '^':
                previousMessage.setStyle(previousStyle.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, c(message.substring(1)))));
                break;
            case '@':
                previousMessage.setStyle(previousStyle.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, message.substring(1))));
                break;
            default:
                ret = new LiteralText(str);
                ret.setStyle(parseStyle(desc));
                previousMessage.setStyle(previousStyle);
                break;
        }

        return ret;
    }

    public static void m(CommandSource source, Object... fields) {
        if (source != null) {
            source.sendMessage(Messenger.c(fields));
        }
    }

    public static void m(PlayerEntity player, Object... fields) {
        player.sendMessage(Messenger.c(fields));
    }

    public static Text c(Object... fields) {
        BaseText message = new LiteralText("");
        BaseText previousComponent = null;
        for (Object o : fields) {
            if (o instanceof BaseText) {
                message.append((BaseText) o);
                previousComponent = (BaseText) o;
                continue;
            }
            String txt = o.toString();
            BaseText comp = getChatComponentFromDesc(txt, previousComponent);
            if (comp != previousComponent) {
                message.append(comp);
            }
            previousComponent = comp;
        }
        return message;
    }
}
