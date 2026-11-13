package carpet.helpers.lifetime.trackeddata;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

public class ItemTrackedData extends ExtraCountTrackedData {
    @Override
    protected long getExtraCount(Entity entity) {
        return entity instanceof ItemEntity ? ((ItemEntity) entity).getItemStack().getSize() : 0L;
    }

    @Override
    protected String getCountDisplayString() {
        return "Item Count";
    }
}
