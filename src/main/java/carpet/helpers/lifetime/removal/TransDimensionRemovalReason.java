package carpet.helpers.lifetime.removal;

import carpet.helpers.lifetime.utils.TextUtil;
import carpet.utils.Messenger;
import net.minecraft.text.Formatting;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class TransDimensionRemovalReason extends RemovalReason {
    private final DimensionType newDimension;

    public TransDimensionRemovalReason(DimensionType newDimension) {
        this.newDimension = Objects.requireNonNull(newDimension);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransDimensionRemovalReason that = (TransDimensionRemovalReason) o;
        return Objects.equals(this.newDimension, that.newDimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.newDimension);
    }

    @Override
    public Text toText() {
        Text dimText = TextUtil.getDimensionNameText(this.newDimension);
        dimText.getStyle().setColor(Formatting.GRAY);
        return Messenger.c("w Trans-dimension", "g  (to ", dimText, "g )");
    }
}
