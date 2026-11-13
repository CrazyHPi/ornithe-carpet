package carpet.mixins.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.TargetSelector;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Exposes {@link TargetSelector}'s private selector-parsing helpers so the
 * lifetime tracker's entity filter can build a {@link Predicate} from an
 * {@code @e[...]} selector token.
 */
@Mixin(TargetSelector.class)
public interface TargetSelectorA {
    @Accessor("TARGET_SELECTOR_PATTERN")
    static Pattern getTargetSelectorPattern() {
        throw new AssertionError();
    }

    @Invoker("parseArgs")
    static Map<String, String> invokeParseArgs(String s) {
        throw new AssertionError();
    }

    @Invoker("parsePos")
    static Vec3d invokeParsePos(Map<String, String> args, Vec3d defaultValue) {
        throw new AssertionError();
    }

    @Invoker("getTypePredicates")
    static List<Predicate<Entity>> invokeGetTypePredicates(Map<String, String> args, String s) {
        throw new AssertionError();
    }

    @Invoker("getXpPredicates")
    static List<Predicate<Entity>> invokeGetXpPredicates(Map<String, String> args) {
        throw new AssertionError();
    }

    @Invoker("getGameModePredicates")
    static List<Predicate<Entity>> invokeGetGameModePredicates(Map<String, String> args) {
        throw new AssertionError();
    }

    @Invoker("getTeamPredicates")
    static List<Predicate<Entity>> invokeGetTeamPredicates(Map<String, String> args) {
        throw new AssertionError();
    }

    @Invoker("getScoreboardPredicates")
    static List<Predicate<Entity>> invokeGetScoreboardPredicates(CommandSource source, Map<String, String> args) {
        throw new AssertionError();
    }

    @Invoker("getNamePredicates")
    static List<Predicate<Entity>> invokeGetNamePredicates(Map<String, String> args) {
        throw new AssertionError();
    }

    @Invoker("getNbtPredicates")
    static List<Predicate<Entity>> invokeGetNbtPredicates(Map<String, String> args) {
        throw new AssertionError();
    }

    @Invoker("getDistancePredicates")
    static List<Predicate<Entity>> invokeGetDistancePredicates(Map<String, String> args, Vec3d pos) {
        throw new AssertionError();
    }

    @Invoker("getRotationPredicates")
    static List<Predicate<Entity>> invokeGetRotationPredicates(Map<String, String> args) {
        throw new AssertionError();
    }
}
