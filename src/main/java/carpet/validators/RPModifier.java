package carpet.validators;

import carpet.api.settings.Validators;
import carpet.mixins.accessor.RedstoneWireBlockA;
import net.minecraft.block.Blocks;

public class RPModifier extends Validators.SideEffectValidator<Boolean> {
    @Override
    public Boolean parseValue(Boolean newValue) {
        return true;
    }

    @Override
    public void performEffect(Boolean newValue) {
        ((RedstoneWireBlockA) Blocks.REDSTONE_WIRE).setShouldSignal(newValue);
    }
}
