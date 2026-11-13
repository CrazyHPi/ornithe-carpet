package carpet.helpers.lifetime.trackeddata;

import net.minecraft.entity.Entity;
import net.minecraft.entity.XpOrbEntity;

public class ExperienceOrbTrackedData extends ExtraCountTrackedData {
    @Override
    protected long getExtraCount(Entity entity) {
        return entity instanceof XpOrbEntity ? ((XpOrbEntity) entity).getXp() : 0L;
    }

    @Override
    protected String getCountDisplayString() {
        return "Experience Amount";
    }
}
