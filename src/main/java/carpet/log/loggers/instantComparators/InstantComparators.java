package carpet.log.loggers.instantComparators;

import carpet.log.framework.LoggerRegistry;
import carpet.utils.Messenger;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InstantComparators {

    public static void onNoTileEntity(World world, BlockPos pos) {
        if (LoggerRegistry.__instantComparators) {
            LoggerRegistry.getLogger("instantComparators").log(option -> new Text[]{
                    Messenger.c("y Comparator has no tile entity ", Messenger.tp("y", pos))
            });
        }
    }

    public static void onInstantComparator(World world, BlockPos pos, boolean buggy) {
        if (LoggerRegistry.__instantComparators) {
            LoggerRegistry.getLogger("instantComparators").log(option -> {
                if ((!buggy && !"buggy".equals(option)) || (buggy && !"tileTick".equals(option))) {
                    return new Text[]{
                            Messenger.c("l " + (buggy ? "Buggy" : "Tile tick") + " instant comparator detected ", Messenger.tp("y", pos))
                    };
                } else {
                    return null;
                }
            });
        }
    }

}
