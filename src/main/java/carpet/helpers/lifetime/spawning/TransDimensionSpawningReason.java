package carpet.helpers.lifetime.spawning;

import carpet.helpers.lifetime.utils.TextUtil;
import carpet.utils.Messenger;
import net.minecraft.text.Formatting;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class TransDimensionSpawningReason extends SpawningReason {
    private final DimensionType oldDimension;

    public TransDimensionSpawningReason(DimensionType oldDimension) {
        this.oldDimension = Objects.requireNonNull(oldDimension);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransDimensionSpawningReason that = (TransDimensionSpawningReason) o;
        return Objects.equals(this.oldDimension, that.oldDimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.oldDimension);
    }

    @Override
    public Text toText() {
        Text dimText = TextUtil.getDimensionNameText(this.oldDimension);
        dimText.getStyle().setColor(Formatting.GRAY);
        return Messenger.c("w Trans-dimension", "g  (from ", dimText, "g )");
    }
}
