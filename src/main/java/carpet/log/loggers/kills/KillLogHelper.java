package carpet.log.loggers.kills;

import carpet.log.framework.LoggerRegistry;
import carpet.utils.Messenger;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.text.Text;

public class KillLogHelper {

    public static void onSweep(PlayerEntity player, int count) {
        LoggerRegistry.getLogger("kills").log(() -> new Text[]{
                Messenger.c("g " + player.getGameProfile().getName() + " smacked ", "r " + count, "g  entities with sweeping")
        });
    }

    public static void onNonSweepAttack(PlayerEntity player) {
        LoggerRegistry.getLogger("kills").log(() -> new Text[]{
                Messenger.c("g " + player.getGameProfile().getName() + " smacked ", "r 1", "g  (no sweeping)")
        });
    }

    // dont think this will be called, removed
    public static void onDudHit(PlayerEntity player) {
        LoggerRegistry.getLogger("kills").log(() -> new Text[]{
                Messenger.c("g " + player.getGameProfile().getName() + " dud hot = no one affected")
        });
    }
}
