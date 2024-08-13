package carpet.settings.validation;

import carpet.api.settings.Validators;
import carpet.mixins.accessor.RedstoneWireBlock_;
import net.minecraft.block.Blocks;

public class RPModifier extends Validators.SideEffectValidator<Boolean> {
    @Override
    public Boolean parseValue(Boolean newValue) {
        return true;
    }

    @Override
    public void performEffect(Boolean newValue) {
        ((RedstoneWireBlock_) Blocks.REDSTONE_WIRE).setShouldSignal(newValue);
    }
}
