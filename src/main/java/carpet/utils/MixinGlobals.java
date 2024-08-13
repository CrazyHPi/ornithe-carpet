package carpet.utils;

import carpet.CarpetSettings;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;

import java.util.EnumSet;

/**
 * This class contains global variables and utilities used by multiple mixin classes
 */
public class MixinGlobals {
    // fillUpdate
    public static final IntStack fillUpdateStack = new IntArrayList();

    public static void pushYeetUpdateFlags() {
        fillUpdateStack.push(
                (CarpetSettings.yeetRemovalUpdates ? 16 : 0) |
                        (CarpetSettings.yeetInitialUpdates ? 8 : 0) |
                        (CarpetSettings.yeetComparatorUpdates ? 4 : 0) |
                        (CarpetSettings.yeetNeighborUpdates ? 2 : 0) |
                        (CarpetSettings.yeetObserverUpdates ? 1 : 0)
        );
        CarpetSettings.yeetRemovalUpdates = true;
        CarpetSettings.yeetInitialUpdates = true;
        CarpetSettings.yeetComparatorUpdates = true;
        CarpetSettings.yeetNeighborUpdates = true;
        CarpetSettings.yeetObserverUpdates = true;
    }

    public static void restoreYeetUpdateFlags() {
        int flags = fillUpdateStack.pop();
        CarpetSettings.yeetRemovalUpdates = (flags & 16) != 0;
        CarpetSettings.yeetInitialUpdates = (flags & 8) != 0;
        CarpetSettings.yeetComparatorUpdates = (flags & 4) != 0;
        CarpetSettings.yeetNeighborUpdates = (flags & 2) != 0;
        CarpetSettings.yeetObserverUpdates = (flags & 1) != 0;
    }
}
