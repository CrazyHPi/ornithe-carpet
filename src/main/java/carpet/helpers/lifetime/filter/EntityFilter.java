package carpet.helpers.lifetime.filter;

import carpet.helpers.lifetime.utils.TextUtil;
import carpet.mixins.accessor.TargetSelectorA;
import carpet.utils.Messenger;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.CommandSyntaxException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public class EntityFilter implements Predicate<Entity> {
    private final String token;
    private final CommandSource serverCommandSource;
    private final Predicate<Entity> predicate;
    private final Vec3d anchorPos;

    public EntityFilter(CommandSource sender, String token) throws CommandException {
        this.token = token;
        this.serverCommandSource = sender;

        // reference: net.minecraft.server.command.TargetSelector.select
        Matcher matcher = TargetSelectorA.getTargetSelectorPattern().matcher(token);
        if (!matcher.matches()) {
            throw new CommandSyntaxException("Invalid entity selector: " + token);
        }
        String s = matcher.group(1);
        Map<String, String> params = TargetSelectorA.invokeParseArgs(matcher.group(2));
        this.anchorPos = TargetSelectorA.invokeParsePos(params, sender.getSourcePos());

        List<Predicate<Entity>> allPredicates = Lists.newArrayList();
        allPredicates.addAll(TargetSelectorA.invokeGetTypePredicates(params, s));
        allPredicates.addAll(TargetSelectorA.invokeGetXpPredicates(params));
        allPredicates.addAll(TargetSelectorA.invokeGetGameModePredicates(params));
        allPredicates.addAll(TargetSelectorA.invokeGetTeamPredicates(params));
        allPredicates.addAll(TargetSelectorA.invokeGetScoreboardPredicates(sender, params));
        allPredicates.addAll(TargetSelectorA.invokeGetNamePredicates(params));
        allPredicates.addAll(TargetSelectorA.invokeGetNbtPredicates(params));
        allPredicates.addAll(TargetSelectorA.invokeGetDistancePredicates(params, this.anchorPos));
        allPredicates.addAll(TargetSelectorA.invokeGetRotationPredicates(params));

        this.predicate = allPredicates.stream().reduce(x -> true, Predicate::and);
    }

    private Vec3d getAnchorPos() {
        return this.anchorPos;
    }

    @Override
    public boolean test(Entity testEntity) {
        return testEntity != null && this.predicate.test(testEntity);
    }

    public Text toText() {
        return TextUtil.getFancyText(
                "y",
                Messenger.s(this.token),
                Messenger.c(
                        "w Dimension: ",
                        TextUtil.getDimensionNameText(this.serverCommandSource.getSourceWorld().dimension.getType()),
                        String.format("w \nAnchor Pos: %s", this.getAnchorPos())
                ),
                new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, this.token)
        );
    }
}
