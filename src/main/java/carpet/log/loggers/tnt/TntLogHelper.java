package carpet.log.loggers.tnt;

import carpet.log.framework.LoggerRegistry;
import carpet.utils.Messenger;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class TntLogHelper {
    private double primedX, primedY, primedZ;
    public boolean initialized = false;
    private Vec3d primedAngle;

    private static long lastGametime = 0;
    private static int tntCount = 0;

    /**
     * Runs when the TNT is primed. Expects the position and motion angle of the TNT.
     */
    public void onPrimed(double x, double y, double z, Vec3d angle) {
        primedX = x;
        primedY = y;
        primedZ = z;
        primedAngle = angle;
        initialized = true;
    }

    /**
     * Runs when the TNT explodes. Expects the position of the TNT.
     */
    public void onExploded(double x, double y, double z, long gametime) {
        if (!(lastGametime == gametime)) {
            tntCount = 0;
            lastGametime = gametime;
        }
        tntCount++;
        LoggerRegistry.getLogger("tnt").log((option) -> {
            switch (option) {
                case "brief":
                    return new Text[]{Messenger.c(
                            "l P ", Messenger.dblt("l", primedX, primedY, primedZ),
                            "w  ", Messenger.dblt("l", primedAngle.x, primedAngle.y, primedAngle.z),
                            "r  E ", Messenger.dblt("r", x, y, z))};
                case "full":
                    return new Text[]{Messenger.c(
                            "r #" + tntCount,
                            "m @" + gametime,
                            "g : ",
                            "l P ", Messenger.dblf("l", primedX, primedY, primedZ),
                            "w  ", Messenger.dblf("l", primedAngle.x, primedAngle.y, primedAngle.z),
                            "r  E ", Messenger.dblf("r", x, y, z))};
                default:
                    return null;
            }
        });
    }
}
