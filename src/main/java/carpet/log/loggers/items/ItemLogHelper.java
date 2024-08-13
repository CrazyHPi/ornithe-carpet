package carpet.log.loggers.items;

import carpet.api.log.Logger;
import carpet.log.framework.LoggerRegistry;
import carpet.utils.Messenger;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class ItemLogHelper {
    private boolean doLog;
    private Logger logger;

    private ArrayList<Vec3d> positions = new ArrayList<>();
    private ArrayList<Vec3d> motions = new ArrayList<>();
    private int sentLogs;

    public ItemLogHelper(String logName) {
        this.logger = LoggerRegistry.getLogger(logName);
        this.doLog = this.logger.hasOnlineSubscribers();
        sentLogs = 0;
    }

    public void onTick(double x, double y, double z, double motionX, double motionY, double motionZ) {
        if (!doLog) return;
        positions.add(new Vec3d(x, y, z));
        motions.add(new Vec3d(motionX, motionY, motionZ));
    }

    public void onFinish(String type) {
        if (!doLog) return;
        sentLogs = 0;
        sendUpdateLogs(true, type);
        doLog = false;
    }

    private void sendUpdateLogs(boolean finished, String type) {
        logger.log((option) -> {
            List<Text> comp = new ArrayList<>();
            switch (option) {
                case "brief":
                    Vec3d p = new Vec3d(0, 0, 0);
                    if (positions.size() > 0) {
                        p = positions.get(positions.size() - 1);
                    }
                    comp.add(Messenger.c("w ----" + type + "---- t: " + positions.size() + "  pos: ", Messenger.dblt("w", p.x, p.y, p.z)));
                    return comp.toArray(new Text[0]);
                case "full":
                    comp.add(Messenger.c("w ----" + type + "---- t: " + positions.size()));
                    for (int i = sentLogs; i < positions.size(); i++) {
                        sentLogs++;
                        Vec3d pos = positions.get(i);
                        Vec3d mot = motions.get(i);
                        comp.add(Messenger.c(
                                String.format("w tick: %d pos", (i + 1)), Messenger.dblt("w", pos.x, pos.y, pos.z),
                                "w   mot", Messenger.dblt("w", mot.x, mot.y, mot.z), Messenger.c("w  [tp]", "/tp " + pos.x + " " + pos.y + " " + pos.z)));
                    }
                    break;
            }
            return comp.toArray(new Text[0]);
        });
    }
}
