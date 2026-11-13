package carpet.helpers.lifetime.utils;

import carpet.utils.Messenger;
import com.google.common.collect.Maps;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

import java.util.Map;

public class TextUtil {
    public static Text attachHoverEvent(Text text, HoverEvent hoverEvent) {
        text.getStyle().setHoverEvent(hoverEvent);
        return text;
    }

    public static Text attachHoverText(Text text, Text hoverText) {
        return attachHoverEvent(text, new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
    }

    private static final Map<DimensionType, Text> DIMENSION_NAME = Maps.newHashMap();

    static {
        DIMENSION_NAME.put(DimensionType.OVERWORLD, new TranslatableText("createWorld.customize.preset.overworld"));
        DIMENSION_NAME.put(DimensionType.NETHER, new TranslatableText("advancements.nether.root.title"));
        DIMENSION_NAME.put(DimensionType.THE_END, new TranslatableText("advancements.end.root.title"));
    }

    public static String getTeleportCommand(Vec3d pos, DimensionType dimensionType) {
        // no "execute in <dimension>" in 1.12
        return String.format("/tp %s %s %s", pos.x, pos.y, pos.z);
    }

    public static Text getFancyText(String style, Text displayText, Text hoverText, ClickEvent clickEvent) {
        Text text = displayText.copy();
        if (style != null) {
            text.setStyle(Messenger.c(style + "  ").getSiblings().get(0).getStyle());
        }
        if (hoverText != null) {
            text.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        }
        if (clickEvent != null) {
            text.getStyle().setClickEvent(clickEvent);
        }
        return text;
    }

    public static String getCoordinateString(Vec3d pos) {
        return String.format("[%.1f, %.1f, %.1f]", pos.x, pos.y, pos.z);
    }

    public static Text getDimensionNameText(DimensionType dim) {
        return DIMENSION_NAME.getOrDefault(dim, Messenger.s(null, dim.toString())).copy();
    }
}
